package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.MovieMonthOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieWeekOrderRepository extends JpaRepository<MovieMonthOrder, Integer> {
}
