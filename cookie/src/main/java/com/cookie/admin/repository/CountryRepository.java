package com.cookie.admin.repository;

import com.cookie.domain.country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

    @Query("""
        SELECT c
        FROM Country c
        WHERE c.name = :country
    """)
    Optional<Country> findByCountry(@Param("country") String country);
}
