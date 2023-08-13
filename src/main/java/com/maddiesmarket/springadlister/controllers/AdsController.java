package com.maddiesmarket.springadlister.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maddiesmarket.springadlister.models.*;
import com.maddiesmarket.springadlister.repositories.*;
import com.maddiesmarket.springadlister.utils.AdConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class AdsController {

    private final AdRepository adDao;
    private final CategoryRepository categoryDao;
    private final UserRepository userDao;

    private final AdCategoriesRepository adsCategoriesDao;

    private final AdImagesRepository adsImagesDao;

    List<String> allStates = Arrays.asList(
            "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
            "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky",
            "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri",
            "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
            "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island",
            "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
            "West Virginia", "Wisconsin", "Wyoming"
    );

    public AdsController(AdRepository adDao, CategoryRepository categoryDao, UserRepository userDao, AdCategoriesRepository adsCategoriesDao, AdImagesRepository adsImagesDao){
        this.adDao = adDao;
        this.categoryDao = categoryDao;
        this.userDao = userDao;
        this.adsImagesDao = adsImagesDao;
        this.adsCategoriesDao = adsCategoriesDao;
    }

    @GetMapping("/ads")
    public String viewAds(Model model){
        model.addAttribute("ads", adDao.findAll());
        return "ads/ads";
    }

    @GetMapping("/viewAd/{id}")
    public String viewAd(Model model, @PathVariable Long id){
        Optional<Ad> ad = adDao.findById(id);
        if (ad.isEmpty()) {
            System.out.println("Item with id " + id + " not found!");
            return "/index";
        }
        model.addAttribute("ad", ad.get());
        return "ads/viewMore";
    }

    @GetMapping("create-listing")
    public String newAd(Model model){
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("allStates", allStates);
        return "ads/create";
    }

    @PostMapping("/submit-listing")
    public String createAd(@RequestParam("categories") String categoriesJson,
                           @RequestParam("photos") String photosJson,
                           @RequestParam("title") String title,
                           @RequestParam("description") String description,
                           @RequestParam("price") String price,
                           @RequestParam("condition") String condition,
                           @RequestParam("city") String city,
                           @RequestParam("state") String state) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<String> categories = objectMapper.readValue(categoriesJson, new TypeReference<List<String>>() {});
        List<String> photos = objectMapper.readValue(photosJson, new TypeReference<List<String>>() {});

        System.out.println(categories);
        System.out.println(photos);
        System.out.println(title);
        System.out.println(description);
        System.out.println(price);
        System.out.println(condition);
        System.out.println(city);
        System.out.println(state);

        Optional<User> optionalUser = userDao.findById(1L);
        if (optionalUser.isEmpty()){
            return "ads/ads";
        }

        Ad newAd = new Ad(title, description, Double.parseDouble(price), Ad.getCorrespondingCondition(condition), city, state, optionalUser.get());

        Ad savedAd =adDao.save(newAd);
        long adId = savedAd.getId();

        for(int i = 0 ; i < categories.size(); i++){
            Optional<Category> optionalCategory = categoryDao.findById(Long.valueOf(categories.get(i)));
            if (optionalCategory.isEmpty()){
                return "ads/ads";
            }
            AdCategories adCategories = new AdCategories(savedAd, optionalCategory.get());
            adsCategoriesDao.save(adCategories);
        }

        for(int i = 0 ; i < photos.size(); i++){
            AdImages adImages = new AdImages(photos.get(i), savedAd);
            adsImagesDao.save(adImages);
        }


        return "redirect:/ads";
    }
}
