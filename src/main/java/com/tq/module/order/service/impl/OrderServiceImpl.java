package com.tq.module.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tq.common.api.ErrorCode;
import com.tq.common.api.PageResult;
import com.tq.common.exception.BusinessException;
import com.tq.common.exception.NotFoundException;
import com.tq.common.util.UrlUtil;
import com.tq.config.properties.OrderTimeoutProperties;
import com.tq.module.address.entity.Address;
import com.tq.module.address.service.AddressService;
import com.tq.module.cart.entity.CartItem;
import com.tq.module.cart.mapper.CartItemMapper;
import com.tq.module.order.dto.*;
import com.tq.module.order.entity.AddressSnapshot;
import com.tq.module.order.entity.Order;
import com.tq.module.order.entity.OrderItem;
import com.tq.module.order.mapper.OrderItemMapper;
import com.tq.module.order.mapper.OrderMapper;
import com.tq.module.product.entity.Product;
import com.tq.module.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements com.tq.module.order.service.OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    private final AddressService addressService;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    private final OrderTimeoutProperties orderTimeoutProperties;

    @Override
    @Transactional
    public String create(Long userId, OrderCreateRequest req) {
        Address address = addressService.getByIdForOrder(userId, Long.valueOf(req.getAddressId()));

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        if ("BUY_NOW".equals(req.getSourceType())) {
            Long productId = Long.valueOf(req.getProductId());
            int qty = req.getQuantity();

            Product p = getOnShelfProductOrThrow(productId);
            deductStockOrThrow(p.getId(), qty);

            OrderItem oi = buildOrderItem(null, p, qty);
            items.add(oi);
            total = total.add(oi.getSubtotalAmount());

        } else if ("CART".equals(req.getSourceType())) {
            List<Long> cartIds = req.getCartItemIds().stream().map(Long::valueOf).toList();

            List<CartItem> cartItems = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                    .eq(CartItem::getUserId, userId)
                    .in(CartItem::getId, cartIds));

            if (cartItems.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "cartItemIds无效");
            }

            // 先逐项校验并扣减库存
            for (CartItem ci : cartItems) {
                Product p = getOnShelfProductOrThrow(ci.getProductId());
                deductStockOrThrow(p.getId(), ci.getQuantity());
                OrderItem oi = buildOrderItem(null, p, ci.getQuantity());
                items.add(oi);
                total = total.add(oi.getSubtotalAmount());
            }

            // 下单成功后自动删除购物车记录（逻辑删除即可）
            for (CartItem ci : cartItems) {
                cartItemMapper.deleteById(ci.getId());
            }
        } else {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "sourceType不合法");
        }

        LocalDateTime now = LocalDateTime.now();
        long timeoutSeconds = orderTimeoutProperties.effectiveTimeoutSeconds(); // 900 或 20（测试）
        LocalDateTime expireTime = now.plusSeconds(timeoutSeconds);

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("WAIT_PAY");
        order.setTotalAmount(total);
        order.setRemark(req.getRemark());

        // 地址快照写入订单主表
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setProvince(address.getProvince());
        order.setCity(address.getCity());
        order.setDistrict(address.getDistrict());
        order.setDetailAddress(address.getDetailAddress());

        order.setExpireTime(expireTime);
        order.setDeleted(0);
        order.setCreateTime(now);
        order.setUpdateTime(now);

        // 为了满足数据库 order_no 非空约束，先设置一个临时 UUID
        // 插入成功获取 ID 后，再更新为正式的业务规则订单号
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        orderMapper.insert(order);

        // 生成 orderNo：yyyyMMdd + 订单ID后8位（可按需调整为更严格的规则）
        String day = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String tail = String.format("%08d", order.getId() % 100_000_000L);
        order.setOrderNo(day + tail);
        orderMapper.updateById(order);

        // 插入明细
        for (OrderItem oi : items) {
            oi.setOrderId(order.getId());
            oi.setCreateTime(now);
            oi.setUpdateTime(now);
            oi.setDeleted(0);
            orderItemMapper.insert(oi);
        }

        return String.valueOf(order.getId());
    }

    @Override
    public PageResult<OrderListItemVO> page(Long userId, String status, int pageNum, int pageSize) {
        Page<Order> page = orderMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .eq(status != null && !status.isBlank(), Order::getStatus, status)
                        .orderByDesc(Order::getCreateTime));

        List<OrderListItemVO> list = page.getRecords().stream().map(o -> {
            OrderListItemVO vo = new OrderListItemVO();
            vo.setOrderId(String.valueOf(o.getId()));
            vo.setOrderNo(o.getOrderNo());
            vo.setStatus(o.getStatus());
            vo.setTotalAmount(o.getTotalAmount());
            vo.setCreateTime(o.getCreateTime());
            vo.setPayTime(o.getPayTime());
            return vo;
        }).toList();

        return new PageResult<>(page.getTotal(), list, pageNum, pageSize);
    }

    @Override
    public OrderDetailVO detail(Long userId, Long orderId, String baseUrl) {
        Order o = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        if (o == null) {
            throw new NotFoundException("订单不存在");
        }
        return buildDetail(o, baseUrl);
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long orderId) {
        Order o = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        if (o == null) {
            throw new NotFoundException("订单不存在");
        }
        if (!"WAIT_PAY".equals(o.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "订单当前状态不可取消");
        }

        o.setStatus("CANCELED");
        o.setCancelReason("USER_CANCEL");
        o.setCancelTime(LocalDateTime.now());
        o.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(o);

        rollbackStockByOrderId(o.getId());
    }

    @Override
    @Transactional
    public void pay(Long userId, Long orderId) {
        Order o = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        if (o == null) {
            throw new NotFoundException("订单不存在");
        }
        if (!"WAIT_PAY".equals(o.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "订单当前状态不可支付");
        }
        if (o.getExpireTime() != null && o.getExpireTime().isBefore(LocalDateTime.now())) {
            // 过期则视为已超时（由定时任务或支付时兜底触发取消）
            throw new BusinessException(ErrorCode.PARAM_INVALID, "订单已超时取消，无法支付");
        }

        o.setStatus("PAID");
        o.setPayTime(LocalDateTime.now());
        o.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(o);
    }

    @Override
    public PageResult<AdminOrderListItemVO> adminPage(String orderNo, String status, Long userId, int pageNum, int pageSize) {
        Page<Order> page = orderMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Order>()
                        .eq(userId != null, Order::getUserId, userId)
                        .eq(status != null && !status.isBlank(), Order::getStatus, status)
                        .like(orderNo != null && !orderNo.isBlank(), Order::getOrderNo, orderNo)
                        .orderByDesc(Order::getCreateTime));

        List<AdminOrderListItemVO> list = page.getRecords().stream().map(o -> {
            AdminOrderListItemVO vo = new AdminOrderListItemVO();
            vo.setOrderId(String.valueOf(o.getId()));
            vo.setOrderNo(o.getOrderNo());
            vo.setUserId(String.valueOf(o.getUserId()));
            vo.setStatus(o.getStatus());
            vo.setTotalAmount(o.getTotalAmount());
            vo.setCreateTime(o.getCreateTime());
            vo.setPayTime(o.getPayTime());
            return vo;
        }).toList();

        return new PageResult<>(page.getTotal(), list, pageNum, pageSize);

    }

    @Override
    public OrderDetailVO adminDetail(Long orderId, String baseUrl) {
        Order o = orderMapper.selectById(orderId);
        if (o == null) {
            throw new NotFoundException("订单不存在");
        }
        return buildDetail(o, baseUrl);
    }

    @Override
    @Transactional
    public void adminUpdateStatus(Long orderId, OrderStatusUpdateRequest req) {
        Order o = orderMapper.selectById(orderId);
        if (o == null) {
            throw new NotFoundException("订单不存在");
        }

        String newStatus = req.getStatus();
        // 按接口说明：PAID -> SHIPPED -> COMPLETED，不允许把已取消改回 WAIT_PAY
        if ("CANCELED".equals(o.getStatus()) && "WAIT_PAY".equals(newStatus)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "已取消订单不可改回待支付");
        }
        if ("PAID".equals(o.getStatus()) && !"SHIPPED".equals(newStatus)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "当前状态仅允许变更为SHIPPED");
        }
        if ("SHIPPED".equals(o.getStatus()) && !"COMPLETED".equals(newStatus)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "当前状态仅允许变更为COMPLETED");
        }

        o.setStatus(newStatus);
        if (req.getRemark() != null) {
            o.setRemark(req.getRemark());
        }
        o.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(o);
    }

    @Override
    @Transactional
    public void cancelExpiredOrders() {
        LocalDateTime now = LocalDateTime.now();

        List<Order> expired = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, "WAIT_PAY")
                .le(Order::getExpireTime, now)
                .orderByAsc(Order::getExpireTime)
                .last("LIMIT 200"));

        for (Order o : expired) {
            // 二次校验：仅对 WAIT_PAY 做取消（避免并发支付）
            Order latest = orderMapper.selectById(o.getId());
            if (latest == null || !"WAIT_PAY".equals(latest.getStatus())) {
                continue;
            }

            latest.setStatus("CANCELED");
            latest.setCancelReason("SYSTEM_TIMEOUT");
            latest.setCancelTime(now);
            latest.setUpdateTime(now);
            orderMapper.updateById(latest);

            rollbackStockByOrderId(latest.getId());
        }
    }

    private OrderDetailVO buildDetail(Order o, String baseUrl) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, o.getId()));

        AddressSnapshot snap = new AddressSnapshot();
        snap.setReceiverName(o.getReceiverName());
        snap.setReceiverPhone(o.getReceiverPhone());
        snap.setProvince(o.getProvince());
        snap.setCity(o.getCity());
        snap.setDistrict(o.getDistrict());
        snap.setDetailAddress(o.getDetailAddress());

        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrderId(String.valueOf(o.getId()));
        vo.setOrderNo(o.getOrderNo());
        vo.setStatus(o.getStatus());
        vo.setTotalAmount(o.getTotalAmount());
        vo.setCreateTime(o.getCreateTime());
        vo.setPayTime(o.getPayTime());
        vo.setCancelTime(o.getCancelTime());
        vo.setCancelReason(o.getCancelReason());
        vo.setExpireTime(o.getExpireTime());
        vo.setAddressSnapshot(snap);

        List<OrderDetailVO.ItemVO> itemVos = items.stream().map(oi -> {
            OrderDetailVO.ItemVO iv = new OrderDetailVO.ItemVO();
            iv.setProductId(String.valueOf(oi.getProductId()));
            iv.setProductTitle(oi.getProductTitle());
            iv.setProductImage(UrlUtil.toFullUrl(baseUrl, oi.getProductImage())); // 补全 fullUrl，
            iv.setPrice(oi.getPrice());
            iv.setQuantity(oi.getQuantity());
            iv.setSubtotalAmount(oi.getSubtotalAmount());
            return iv;
        }).toList();

        vo.setItems(itemVos);
        return vo;
    }

    private Product getOnShelfProductOrThrow(Long productId) {
        Product p = productMapper.selectById(productId);
        if (p == null || p.getDeleted() != 0) {
            throw new NotFoundException("商品不存在");
        }
        if (!"ON_SHELF".equals(p.getStatus())) {
            throw new NotFoundException("商品已下架");
        }
        return p;
    }

    private void deductStockOrThrow(Long productId, int qty) {
        int rows = productMapper.deductStock(productId, qty);
        if (rows <= 0) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH, "库存不足");
        }
    }

    private void rollbackStockByOrderId(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem oi : items) {
            productMapper.rollbackStock(oi.getProductId(), oi.getQuantity());
        }
    }

    private OrderItem buildOrderItem(Long orderId, Product p, int qty) {
        OrderItem oi = new OrderItem();
        oi.setOrderId(orderId);
        oi.setProductId(p.getId());
        oi.setProductTitle(p.getTitle());
        oi.setProductImage(p.getMainImage()); // 存库可保留相对路径，对外再补 fullUrl
        oi.setPrice(p.getPrice());
        oi.setQuantity(qty);
        oi.setSubtotalAmount(p.getPrice().multiply(BigDecimal.valueOf(qty)));
        return oi;
    }
}
