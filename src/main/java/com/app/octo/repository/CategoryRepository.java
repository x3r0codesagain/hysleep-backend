package com.app.octo.repository;

import com.app.octo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  Category findByCategoryId(Long id);
  Category findByCategoryName(String category);
}
