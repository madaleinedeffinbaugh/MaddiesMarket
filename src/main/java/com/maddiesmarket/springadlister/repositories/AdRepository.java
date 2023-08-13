package com.maddiesmarket.springadlister.repositories;

import com.maddiesmarket.springadlister.models.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdRepository extends JpaRepository<Ad, Long> {

}
