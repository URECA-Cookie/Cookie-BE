package com.cookie.admin.repository;

import com.cookie.admin.entity.AdminMovieRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<AdminMovieRecommend, Long> {
}
