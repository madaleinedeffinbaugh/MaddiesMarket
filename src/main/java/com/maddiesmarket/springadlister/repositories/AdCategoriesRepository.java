package com.maddiesmarket.springadlister.repositories;

import com.maddiesmarket.springadlister.models.AdCategories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdCategoriesRepository extends JpaRepository<AdCategories, Long> {
    Optional<AdCategories> findByAdIdAndCategoryId(Long adId, Long categoryId);

}