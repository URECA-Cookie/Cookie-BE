package com.cookie.domain.actor.repository;

import com.cookie.domain.actor.entity.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    @Query("""
            SELECT a
            FROM Actor a
            WHERE a.tmdbCasterId =:tmdbCasterId
           """)
    Optional<Actor> findByTMDBCasterId(@Param("tmdbCasterId") Long tmdbCasterId);

    Page<Actor> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
