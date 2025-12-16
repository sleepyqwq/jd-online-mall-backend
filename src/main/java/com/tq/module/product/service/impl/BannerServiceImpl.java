package com.tq.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tq.common.api.PageResult;
import com.tq.module.product.entity.Banner;
import com.tq.module.product.mapper.BannerMapper;
import com.tq.module.product.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    public List<Banner> listHomeBanners() {
        return bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSortOrder)
                .orderByDesc(Banner::getId));
    }

    @Override
    public PageResult<Banner> pageAdminBanners(int pageNum, int pageSize) {
        Page<Banner> page = bannerMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSortOrder));
        return new PageResult<>(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public void createBanner(Banner banner) {
        banner.setCreateTime(LocalDateTime.now());
        banner.setUpdateTime(LocalDateTime.now());
        banner.setDeleted(0);
        if (banner.getStatus() == null) banner.setStatus(1);
        if (banner.getSortOrder() == null) banner.setSortOrder(1);
        bannerMapper.insert(banner);
    }

    @Override
    public void updateBanner(Banner banner) {
        banner.setUpdateTime(LocalDateTime.now());
        bannerMapper.updateById(banner);
    }

    @Override
    public void deleteBanner(Long id) {
        bannerMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Banner b = new Banner();
        b.setId(id);
        b.setStatus(status);
        b.setUpdateTime(LocalDateTime.now());
        bannerMapper.updateById(b);
    }
}