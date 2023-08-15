package com.maddiesmarket.springadlister.repositories;

import com.maddiesmarket.springadlister.models.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {

    List<Ad> findByArchivedFalseAndAdStatusNot(Ad.adStatus adStatus);

}
