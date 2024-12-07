package com.cookie.admin.service.reward;

import com.cookie.admin.dto.request.AdminBadgeRequest;
import com.cookie.admin.dto.response.AdminBadges;
import com.cookie.admin.exception.AdminBadRequestException;
import com.cookie.domain.badge.entity.Badge;
import com.cookie.domain.badge.repository.BadgeRepository;
import com.cookie.domain.user.repository.UserBadgeRepository;
import com.cookie.global.service.AWSS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminRewardService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final AWSS3Service awss3Service;

    @Transactional(readOnly = true)
    public List<AdminBadges> getRewardBadges() {

        List<Badge> badges = badgeRepository.findAll();

        return badges.stream()
                .map(badge -> AdminBadges.builder()
                        .BadgeId(badge.getId())
                        .genre(badge.getGenre())
                        .badgeImage(badge.getBadgeImage())
                        .badgeName(badge.getName())
                        .needPoint(badge.getNeedPoint()).build())
                .toList();
    }

    @Transactional
    public void createRewardBadges(MultipartFile image, AdminBadgeRequest request) {
        String imageUrl = awss3Service.uploadImage(image);

        Optional<Badge> existingBadge = badgeRepository.findBadgeByName(request.getBadgeName());

        if (existingBadge.isPresent()) {
            throw new AdminBadRequestException("해당 뱃지 이름이 이미 존재합니다.");
        }

        Badge badge = Badge.builder()
                .name(request.getBadgeName())
                .genre(request.getGenre())
                .badgeImage(imageUrl)
                .needPoint(request.getNeedPoint())
                .build();

        badgeRepository.save(badge);
    }

    @Transactional
    public void updateRewardBadge(Long badgeId, MultipartFile image, AdminBadgeRequest update) {

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new AdminBadRequestException("해당 뱃지 Id가 존재하지 않습니다."));


        if (image != null) {
            System.out.println("image = " + image.getName());
            System.out.println("image getContentType = " + image.getContentType());
            String imageUrl = awss3Service.uploadImage(image);
            badge.updateBadgeImage(imageUrl);
        }

        if (update.getBadgeName() != null) {
            if (badgeRepository.findBadgeByName(update.getBadgeName()).isPresent()) {
                throw new AdminBadRequestException("해당 뱃지 이름이 이미 존재합니다.");
            }
            badge.updateBadgeName(update.getBadgeName());
        }

        if ((update.getGenre() != null)) {
            badge.updateGenre(update.getGenre());
        }

        if (update.getNeedPoint() != null) {
            badge.updateNeedPoint(update.getNeedPoint());
        }

        badgeRepository.save(badge);
    }

    @Transactional
    public void deleteRewardBadge(Long badgeId) {

        badgeRepository.findById(badgeId)
                .orElseThrow(() -> new AdminBadRequestException("해당 뱃지 Id가 존재하지 않습니다."));

        userBadgeRepository.deleteUserBadgeByBadgeId(badgeId);

        badgeRepository.deleteById(badgeId);
    }
}
