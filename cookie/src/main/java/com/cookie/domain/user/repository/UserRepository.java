package com.cookie.domain.user.repository;

import com.cookie.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.userCategories uc JOIN FETCH uc.category c WHERE c.mainCategory = '장르' AND c.subCategory IN :genres AND u.id <> :userId")
    List<User> findUsersByFavoriteGenresInAndExcludeUserId(@Param("genres") List<String> genres, @Param("userId") Long userId);

}