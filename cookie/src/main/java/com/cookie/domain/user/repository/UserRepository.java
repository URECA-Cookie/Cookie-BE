package com.cookie.domain.user.repository;

import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.category c " +
            "WHERE c.mainCategory = '장르' " +
            "AND c.subCategory IN :genres " +
            "AND u.id <> :userId")
    List<User> findUsersByFavoriteGenresInAndExcludeUserId(@Param("genres") List<String> genres, @Param("userId") Long userId);

    User findBySocialProviderAndSocialId(SocialProvider socialProvider, String socialId);
    boolean existsBySocialProviderAndSocialId(SocialProvider socialProvider, String socialId);
    boolean existsByNickname(String nickname);
    Optional<User> findByNickname(String nickname);

    @Query("SELECT ft.token FROM User u " +
            "JOIN u.fcmTokens ft " +
            "JOIN u.category c " +
            "WHERE c.subCategoryEn = :genre " +
            "AND u.id != :userId")
    List<String> findTokensByGenreAndExcludeUser(
            @Param("genre") String genre,
            @Param("userId") Long userId
    );

}