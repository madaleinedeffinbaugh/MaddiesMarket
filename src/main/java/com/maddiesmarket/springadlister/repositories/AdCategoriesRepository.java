package com.maddiesmarket.springadlister.repositories;

import com.maddiesmarket.springadlister.models.AdCategories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdCategoriesRepository extends JpaRepository<AdCategories, Long> {

}