package com.maddiesmarket.springadlister.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "spring_ad_images")
public class AdImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = true)
    private String url;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

    public AdImages(String url, Ad ad) {
        this.url = url;
        this.ad = ad;
    }
}
