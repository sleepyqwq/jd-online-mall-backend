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
import com.tq.module.order.dto.AdminOrderListItemVO;
import com.tq.module.order.dto.OrderCreateRequest;
import com.tq.module.order.dto.OrderDetailVO;
import com.tq.module.order.dto.OrderListItemVO;
import com.tq.module.order.dto.OrderStatusUpdateRequest;
import com.tq.module.order.entity.AddressSnapshot;
import com.tq.module.order.entity.Order;
import com.tq.module.order.entity.OrderItem;
import com.tq.module.order.mapper.OrderItemMapper;
import com.tq.module.order.mapper.OrderMapper;
import com.tq.module.order.service.OrderService;
import com.tq.module.product.entity.Product;
import com.tq.module.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final String STATUS_WAIT_PAY = "WAIT_PAY";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_SHIPPED = "SHIPPED";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_CANCELED = "CANCELED";

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

        String sourceType = req.getSourceType();
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        List<Long> cartIdsToDelete = Collections.emptyList();

        if ("BUY_NOW".equals(sourceType)) {
            Long productId = Long.valueOf(req.getProductId());
            int qty = req.getQuantity();

            Product p = getOnShelfProductOrThrow(productId);
            deductStockOrThrow(p.getId(), qty);

            OrderItem oi = buildOrderItem(p, qty);
            items.add(oi);
            total = total.add(oi.getSubtotalAmount());

        } else if ("CART".equals(sourceType)) {
            List<String> rawCartIds = req.getCartItemIds();
            if (rawCartIds == null || rawCartIds.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "cartItemIds无效");
            }

            List<Long> cartIds = rawCartIds.stream().map(Long::valueOf).toList();
            List<CartItem> cartItems = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                    .eq(CartItem::getUserId, userId)
                    .in(CartItem::getId, cartIds));

            if (cartItems.isEmpty() || cartItems.size() != cartIds.size()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "cartItemIds无效");
            }

            // 先逐项校验并扣减库存
            for (CartItem ci : cartItems) {
                Product p = getOnShelfProductOrThrow(ci.getProductId());
                deductStockOrThrow(p.getId(), ci.getQuantity());

                OrderItem oi = buildOrderItem(p, ci.getQuantity());
                items.add(oi);
                total = total.add(oi.getSubtotalAmount());
            }

            cartIdsToDelete = cartIds;

        } else {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "sourceType不合法");
        }

        LocalDateTime now = LocalDateTime.now();
        long timeoutSeconds = orderTimeoutProperties.effectiveTimeoutSeconds();
        LocalDateTime expireTime = now.plusSeconds(timeoutSeconds);

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(STATUS_WAIT_PAY);
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

        // 为了满足数据库 order_no 非空约束，先设置一个临时 UUID，插入拿到 ID 后再生成正式订单号
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        orderMapper.insert(order);

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

        // 下单成功后自动删除购物车记录（逻辑删除）
        if (!cartIdsToDelete.isEmpty()) {
            cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                    .eq(CartItem::getUserId, userId)
                    .in(CartItem::getId, cartIdsToDelete));
        }


        return String.valueOf(order.getId());
    }

    @Override
    public PageResult<OrderListItemVO> page(Long userId, String status, int pageNum, int pageSize) {
        // 1. 查询订单主表分页
        Page<Order> page = orderMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .eq(status != null && !status.isBlank(), Order::getStatus, status)
                        .orderByDesc(Order::getCreateTime));

        // 2. 批量查询当前页所有订单的明细 (避免 N+1)
        List<Long> orderIds = page.getRecords().stream().map(Order::getId).toList();
        Map<Long, List<OrderItem>> itemMap = new HashMap<>();

        if (!orderIds.isEmpty()) {
            List<OrderItem> allItems = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, orderIds)
            );
            itemMap = allItems.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));
        }

        // 3. 组装 VO
        Map<Long, List<OrderItem>> finalItemMap = itemMap;
        List<OrderListItemVO> list = page.getRecords().stream().map(o -> {
            OrderListItemVO vo = new OrderListItemVO();
            vo.setOrderId(String.valueOf(o.getId()));
            vo.setOrderNo(o.getOrderNo());
            vo.setStatus(o.getStatus());
            vo.setTotalAmount(o.getTotalAmount());
            vo.setCreateTime(o.getCreateTime());
            vo.setPayTime(o.getPayTime());

            List<OrderItem> orderItems = finalItemMap.getOrDefault(o.getId(), new ArrayList<>());
            List<OrderDetailVO.ItemVO> itemVOs = orderItems.stream().map(item -> {
                OrderDetailVO.ItemVO itemVO = new OrderDetailVO.ItemVO();
                itemVO.setProductId(String.valueOf(item.getProductId()));
                itemVO.setProductTitle(item.getProductTitle());
                itemVO.setProductImage(item.getProductImage()); // 列表页不补 fullUrl（保持你当前策略）
                itemVO.setPrice(item.getPrice());
                itemVO.setQuantity(item.getQuantity());
                itemVO.setSubtotalAmount(item.getSubtotalAmount());
                return itemVO;
            }).toList();

            vo.setItems(itemVOs);
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
    public void cancel(Long userId, Long orderId, String reason) {
        Order o = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        if (o == null) {
            throw new NotFoundException("订单不存在");
        }
        if (!STATUS_WAIT_PAY.equals(o.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "订单当前状态不可取消");
        }

        LocalDateTime now = LocalDateTime.now();
        o.setStatus(STATUS_CANCELED);
        o.setCancelReason(reason != null ? reason : "USER_CANCEL");
        o.setCancelTime(now);
        o.setUpdateTime(now);
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
        if (!STATUS_WAIT_PAY.equals(o.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "订单当前状态不可支付");
        }

        LocalDateTime now = LocalDateTime.now();
        if (o.getExpireTime() != null && o.getExpireTime().isBefore(now)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "订单已超时取消，无法支付");
        }

        o.setStatus(STATUS_PAID);
        o.setPayTime(now);
        o.setUpdateTime(now);
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
        if (STATUS_CANCELED.equals(o.getStatus()) && STATUS_WAIT_PAY.equals(newStatus)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "已取消订单不可改回待支付");
        }
        if (STATUS_PAID.equals(o.getStatus()) && !STATUS_SHIPPED.equals(newStatus)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "当前状态仅允许变更为SHIPPED");
        }
        if (STATUS_SHIPPED.equals(o.getStatus()) && !STATUS_COMPLETED.equals(newStatus)) {
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
                .eq(Order::getStatus, STATUS_WAIT_PAY)
                .le(Order::getExpireTime, now)
                .orderByAsc(Order::getExpireTime)
                .last("LIMIT 200"));

        for (Order o : expired) {
            // 二次校验：仅对 WAIT_PAY 做取消（避免并发支付）
            Order latest = orderMapper.selectById(o.getId());
            if (latest == null || !STATUS_WAIT_PAY.equals(latest.getStatus())) {
                continue;
            }

            latest.setStatus(STATUS_CANCELED);
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
        vo.setRemark(o.getRemark());
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
            iv.setProductImage(UrlUtil.toFullUrl(baseUrl, oi.getProductImage()));
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

    // 这里去掉 orderId 入参，避免 “orderId 永远为 null” 的静态检查告警；orderId 在插入明细前统一补齐
    private OrderItem buildOrderItem(Product p, int qty) {
        OrderItem oi = new OrderItem();
        oi.setProductId(p.getId());
        oi.setProductTitle(p.getTitle());
        oi.setProductImage(p.getMainImage()); // 存库保留相对路径，对外再补 fullUrl
        oi.setPrice(p.getPrice());
        oi.setQuantity(qty);
        oi.setSubtotalAmount(p.getPrice().multiply(BigDecimal.valueOf(qty)));
        return oi;
    }
}
