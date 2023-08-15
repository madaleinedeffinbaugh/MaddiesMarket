package com.maddiesmarket.springadlister.repositories;


import com.maddiesmarket.springadlister.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
