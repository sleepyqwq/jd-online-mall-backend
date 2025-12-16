package com.tq.module.product.controller;

import com.tq.common.api.PageResult;
import com.tq.common.api.Result;
import com.tq.module.product.entity.Banner;
import com.tq.module.product.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // 注意这里前缀调整，方便分开管理
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    // 前台接口
    @GetMapping("/banners")
    public Result<List<Banner>> list() {
        return Result.ok(bannerService.listHomeBanners());
    }

    // --- 后台管理接口 ---

    @GetMapping("/admin/banners")
    public Result<PageResult<Banner>> page(@RequestParam int pageNum, @RequestParam int pageSize) {
        return Result.ok(bannerService.pageAdminBanners(pageNum, pageSize));
    }

    @PostMapping("/admin/banners")
    public Result<Void> create(@RequestBody Banner banner) {
        bannerService.createBanner(banner);
        return Result.ok();
    }

    @PutMapping("/admin/banners/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        bannerService.updateBanner(banner);
        return Result.ok();
    }

    @DeleteMapping("/admin/banners/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return Result.ok();
    }

    @PutMapping("/admin/banners/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Banner banner) {
        bannerService.updateStatus(id, banner.getStatus());
        return Result.ok();
    }
}