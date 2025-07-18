package Oops.backend.domain.category.repository;

import Oops.backend.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameContainingIgnoreCase(String name);   // 일부 + 대소문자 무시 검색
}
