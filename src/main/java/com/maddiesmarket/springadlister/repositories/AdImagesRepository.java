package com.maddiesmarket.springadlister.repositories;

import com.maddiesmarket.springadlister.models.AdCategories;
import com.maddiesmarket.springadlister.models.AdImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdImagesRepository extends JpaRepository<AdImages, Long> {
    Optional<AdImages> findByAdIdAndUrl(Long adId, String url);

}
