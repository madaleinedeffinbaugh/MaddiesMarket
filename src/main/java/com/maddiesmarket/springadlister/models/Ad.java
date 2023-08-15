package com.maddiesmarket.springadlister.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.maddiesmarket.springadlister.models.Ad.adCondition.*;
import static com.maddiesmarket.springadlister.models.Ad.adStatus.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "spring_ads")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = true)
    private String title;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = true)
    private Double price;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private adCondition adCondition;

    @Column(nullable = true)
    private String location;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String state;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private adStatus adStatus;
    @Column(nullable = true)
    private Boolean archived;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ad")
    private List<AdCategories> adCategories;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ad")
    private List<AdImages> adImages;


    public enum adStatus {
        available, pending, sold
    }

    public enum adCondition {
        brand_new, used_like_new, used_good, used_fair, used_poor
    }

    public String getMainPhoto() {
        if (adImages.size() < 1) {
            return "https://cdn.filestackcontent.com/X6wvXICwSlKY6xBgChz4";
        } else {
            return adImages.get(0).getUrl();
        }
    }

    public String getTruncatedDescription(){
        if(description.trim().length() < 111){
            return description;
        } else {
            return description.trim().substring(0,110) + ". . .";
        }
    }

    public static Ad.adCondition getCorrespondingCondition(String condition) {
        if(condition.equals("New")){
            return brand_new;
        } else if(condition.equals("Used - Good")) {
            return used_good;
        } else if(condition.equals("Used - Fair")) {
            return used_fair;
        }
        else if(condition.equals("Used - Like New")) {
            return used_like_new;
        }
        else if(condition.equals("Used - Poor")) {
            return used_poor;
        } else {
            return null;
        }
    }

    public static Ad.adStatus getCorrespondingStatus(String adStatus) {
        if(adStatus.equals("available")){
            return available;
        } else if(adStatus.equals("pending")) {
            return pending;
        } else if(adStatus.equals("sold")) {
            return sold;
        }
        else {
            return null;
        }
    }

    public String styleCondition(){
        if(this.adCondition == brand_new){
            return "New";
        } else if(this.adCondition == used_like_new){
            return "Used - Like New";
        } else if(this.adCondition == used_good){
            return "Used - Good";
        } else if(this.adCondition == used_fair){
            return "Used - Fair";
        }else if(this.adCondition == used_poor){
            return "Used - Poor";
        }else {
            return "";
        }
    }

    public String styleStatus(){
        if(this.adStatus == available){
            return "Available";
        } else if(this.adStatus == pending){
            return "Pending";
        } else if(this.adStatus == sold){
            return "Sold";
        } else {
            return "";
        }
    }

    public Ad(String title, String description, Double price, Ad.adCondition adCondition, String city, String state, User user) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.adCondition = adCondition;
        this.location = "";
        this.city = city;
        this.state = state;
        this.adStatus = available;
        this.archived = false;
        this.user = user;
    }

    public String getFormattedPrice(){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(price);
    }

    public List<String> getCategoryIds(){
        List<String> names = new ArrayList<>();
        for(int i = 0; i < getAdCategories().size(); i++){
            names.add(String.valueOf(getAdCategories().get(i).getCategory().getId()));
        }
        return names;
    }
}
