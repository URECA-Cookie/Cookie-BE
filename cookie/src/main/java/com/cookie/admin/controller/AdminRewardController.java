package com.cookie.admin.controller;

import com.cookie.admin.dto.request.AdminBadgeRequest;
import com.cookie.admin.dto.response.AdminBadges;
import com.cookie.admin.service.reward.AdminRewardService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reward")
public class AdminRewardController {

    private final AdminRewardService adminRewardService;

    @Hidden
    @GetMapping()
    public ApiSuccess<?> getRewardBadges() {
        List<AdminBadges> data = adminRewardService.getRewardBadges();
        return ApiUtil.success(data);
    }

    @Hidden
    @PostMapping
    public ApiSuccess<?> createRewardBadges(@RequestPart("badgeImage") MultipartFile badgeImage,
                                            @RequestPart("request") AdminBadgeRequest request) {
        adminRewardService.createRewardBadges(badgeImage, request);
        return ApiUtil.success("SUCCESS");
    }

    @Hidden
    @PutMapping("/{badgeId}")
    public ApiSuccess<?> updateRewardBadge(@PathVariable("badgeId") Long badgeId,
                                           @RequestPart(value = "badgeImage", required = false) MultipartFile image,
                                           @RequestPart("update") AdminBadgeRequest update) {
        adminRewardService.updateRewardBadge(badgeId, image, update);
        return ApiUtil.success("SUCCESS");
    }

    @Hidden
    @DeleteMapping("/{badgeId}")
    public ApiSuccess<?> deleteRewardBadge(@PathVariable("badgeId") Long badgeId) {
        adminRewardService.deleteRewardBadge(badgeId);
        return ApiUtil.success("SUCCESS");
    }
}
