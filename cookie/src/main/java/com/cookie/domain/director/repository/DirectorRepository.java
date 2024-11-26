package com.cookie.domain.director.repository;

import com.cookie.domain.director.dto.response.DirectorResponse;
import com.cookie.domain.director.entity.Director;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    @Query("SELECT new com.cookie.domain.director.dto.response.DirectorResponse("
            + "d.name, d.profileImage) "
            + "FROM Director d "
            + "JOIN MovieDirector md ON md.director.id = d.id "
            + "WHERE md.movie.id = :movieId")
    List<DirectorResponse> findDirectorsByMovieId(@Param("movieId") Long movieId);

    @Query("""
            SELECT d
            FROM Director d
            WHERE d.tmdbCasterId =:tmdbCasterId
           """)
    Optional<Director> findByTMDBCasterId(@Param("tmdbCasterId") Long tmdbCasterId);

    Page<Director> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("""
            SELECT d
            FROM Director d
            LEFT JOIN FETCH d.movies
            WHERE d.id =:directorId
           """)
    List<Director> findAllMoviesByDirectorId(@Param("directorId")Long directorId);
}
