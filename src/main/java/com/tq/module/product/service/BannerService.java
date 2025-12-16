package com.tq.module.product.service;

import com.tq.common.api.PageResult;
import com.tq.module.product.entity.Banner;
import java.util.List;

public interface BannerService {
    // 首页查询
    List<Banner> listHomeBanners();

    // --- 以下为新增的管理端方法 ---
    PageResult<Banner> pageAdminBanners(int pageNum, int pageSize);
    void createBanner(Banner banner);
    void updateBanner(Banner banner);
    void deleteBanner(Long id);
    void updateStatus(Long id, Integer status);
}