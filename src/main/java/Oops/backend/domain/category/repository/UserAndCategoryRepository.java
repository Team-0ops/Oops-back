package Oops.backend.domain.category.repository;

import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.entity.UserAndCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserAndCategoryRepository extends JpaRepository<UserAndCategory, Long> {
    @Query("SELECT uc.category.id FROM UserAndCategory uc WHERE uc.user = :user")
    List<Long> findCategoryIdsByUser(@Param("user") User user);

    List<UserAndCategory> findByUserId(Long userId);

    boolean existsByUserIdAndCategoryId(Long userId, Long categoryId);

    Optional<UserAndCategory> findByUserIdAndCategoryId(Long userId, Long categoryId);
}
