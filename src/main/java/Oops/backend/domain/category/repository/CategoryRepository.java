package Oops.backend.domain.category.repository;

import Oops.backend.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameContainingIgnoreCase(String name);   // 일부 + 대소문자 무시 검색

    @Query("SELECT c.name FROM Category c WHERE c.id = :id")
    Optional<String> findNameById(@Param("id") Long id);
}
