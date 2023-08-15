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
@Table(name = "spring_ad_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = true)
    private String first_name;

    @Column(nullable = true)
    private String last_name;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String username;

    @Column(nullable = true)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Ad> userAds;


    public User(User copy) {
        id = copy.id; // This line is SUPER important! Many things won't work if it's absent
        email = copy.email;
        username = copy.username;
        password = copy.password;
        first_name = copy.first_name;
        last_name = copy.last_name;
        userAds = copy.userAds;
    }

    public boolean userHasAvailableAds(){
        for (Ad ad : userAds) {
            if ((ad.getAdStatus() == Ad.adStatus.available) && !ad.getArchived()) {
                return true; // Found an available ad
            }
        }
        return false; // No available ads found
    }

    public boolean userHasPendingAds(){
        for (Ad ad : userAds) {
            if ((ad.getAdStatus() == Ad.adStatus.pending) && !ad.getArchived()) {
                return true; // Found a pending ad
            }
        }
        return false; // No available ads found
    }

    public boolean userHasSoldAds(){
        for (Ad ad : userAds) {
            if ((ad.getAdStatus() == Ad.adStatus.sold) && !ad.getArchived()) {
                return true; // Found a sold ad
            }
        }
        return false; // No available ads found
    }
}
