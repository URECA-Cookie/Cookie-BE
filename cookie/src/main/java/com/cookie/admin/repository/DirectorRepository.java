package com.cookie.admin.repository;

import com.cookie.domain.director.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DirectorRepository extends JpaRepository<Director, Long> {

    @Query("""
            SELECT d
            FROM Director d
            WHERE d.tmdbCasterId =:tmdbCasterId
           """)
    Optional<Director> findByTMDBCasterId(@Param("tmdbCasterId") Long tmdbCasterId);
}
