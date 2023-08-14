package com.maddiesmarket.springadlister.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maddiesmarket.springadlister.models.*;
import com.maddiesmarket.springadlister.repositories.*;
import com.maddiesmarket.springadlister.utils.AdConverter;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.maddiesmarket.springadlister.utils.Lists.findMissingElements;
import static com.maddiesmarket.springadlister.utils.Lists.findNewElements;

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
    public String viewAdsByCategory(@RequestParam("categoryId") String categoryId, Model model){
        List<Ad> allAds = adDao.findByArchivedFalse();
        List<Ad> byCategory = new ArrayList<>();
        for(int i = 0; i < allAds.size(); i++){
            if(allAds.get(i).getCategoryIds().contains(categoryId)) {
                byCategory.add(allAds.get(i));
            }
        }
        model.addAttribute("ads", byCategory);
        model.addAttribute("categories", categoryDao.findAll());
        return "ads/ads";
    }

    @GetMapping("/ads")
    public String viewAds(Model model){
        model.addAttribute("ads", adDao.findByArchivedFalse());
        model.addAttribute("categories", categoryDao.findAll());
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


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                Optional<User> optionalUser = userDao.findById(user.getId());
                if (optionalUser.isEmpty()) {
                    model.addAttribute("AdBelongsToUser", false);
                } else {
                    if(ad.get().getUser().getId() == optionalUser.get().getId()) {
                        model.addAttribute("AdBelongsToUser", true);
                    } else {
                        model.addAttribute("AdBelongsToUser", false);
                    }
                }
            } else {
                model.addAttribute("AdBelongsToUser", false);
            }
        } else {
            model.addAttribute("AdBelongsToUser", false);
        }
        return "ads/viewMore";
    }

    @GetMapping("/create-listing")
    public String newAd(Model model){
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("allStates", allStates);
        return "ads/create";
    }

    @GetMapping("editAd/{id}")
    public String editEditAd(Model model, @PathVariable Long id){
        Optional<Ad> optionalAd = adDao.findById(id);
        if(optionalAd.isEmpty()){
            return "ads/ads";
            //todo error
        }

        //todo check if user owns ad
        model.addAttribute("allStates", allStates);
        model.addAttribute("categories", categoryDao.findAll());

        model.addAttribute("ad", optionalAd.get());
        model.addAttribute("adCategoryIds", optionalAd.get().getCategoryIds());
        return "ads/editAd";
    }

    @Transactional
    @PostMapping("edit-listing")
    public String editListing(@RequestParam("categories") String categoriesJson,
                              @RequestParam("photos") String photosJson,
                              @RequestParam("photosRemove") String photosRemoveJson,
                              @RequestParam("title") String title,
                              @RequestParam("adId") Long id,
                              @RequestParam("description") String description,
                              @RequestParam("price") String price,
                              @RequestParam("condition") String condition,
                              @RequestParam("city") String city,
                              @RequestParam("state") String state,
                              @RequestParam("ad_status") String adStatus) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<String> categories = objectMapper.readValue(categoriesJson, new TypeReference<List<String>>() {});
        List<String> photos = objectMapper.readValue(photosJson, new TypeReference<List<String>>() {});
        List<String> photosRemove = objectMapper.readValue(photosRemoveJson, new TypeReference<List<String>>() {});

        System.out.println(categories);
        System.out.println(photos);
        System.out.println(photosRemove);
        System.out.println(title);
        System.out.println(description);
        System.out.println(price);
        System.out.println(condition);
        System.out.println(city);
        System.out.println(state);
        System.out.println(id);
        System.out.println(adStatus);

        Optional<Ad> currentAd = adDao.findById(id);
        if(currentAd.isEmpty()){
            System.out.println("COULDNT FIND AD");
            return "/";
            //todo error

        }

//        System.out.println(currentAd);
//
        Ad copyOfAd = currentAd.get();

        System.out.println(copyOfAd);

        copyOfAd.setTitle(title);
        copyOfAd.setDescription(description);
        copyOfAd.setPrice(Double.valueOf(price));
        copyOfAd.setAdCondition(Ad.getCorrespondingCondition(condition));
        copyOfAd.setCity(city);
        copyOfAd.setState(state);
        copyOfAd.setAdStatus(Ad.getCorrespondingStatus(adStatus));
        copyOfAd.setId(id);

        System.out.println(copyOfAd);


        adDao.save(copyOfAd);


        List<String> missingCats = findMissingElements( currentAd.get().getCategoryIds(), categories);
        List<String> newCats = findNewElements(currentAd.get().getCategoryIds(), categories);
        System.out.println(missingCats);
        System.out.println(newCats);

        for(int i = 0; i < missingCats.size(); i ++){
            Optional<AdCategories> optionalAdCategories = adsCategoriesDao.findByAdIdAndCategoryId(copyOfAd.getId(), Long.valueOf(missingCats.get(i)));
            if(optionalAdCategories.isEmpty()) {
                System.out.println("error");
                return "redirect:/error";
            }

            AdCategories copyOfOptional = optionalAdCategories.get();
            copyOfOptional.setAd(null);
            adsCategoriesDao.save(copyOfOptional);
        }

        for(int i = 0; i < newCats.size(); i++){
            Optional<Category> optionalCategory = categoryDao.findById(Long.valueOf(newCats.get(i)));
            if (optionalCategory.isEmpty()){
                System.out.println("is empty");
                return "ads/ads";
            }
            AdCategories adCategories = new AdCategories(copyOfAd, optionalCategory.get());
            System.out.println(adCategories.getCategory().getCategory());
            adsCategoriesDao.save(adCategories);
        }

        for(int i = 0; i < photosRemove.size(); i ++){
            Optional<AdImages> optionalAdImages = adsImagesDao.findById(Long.valueOf(photosRemove.get(i)));
            if(optionalAdImages.isEmpty()) {
                System.out.println("error");
                return "redirect:/error";
            }

            AdImages copyOfOptional = optionalAdImages.get();
            copyOfOptional.setAd(null);
            adsImagesDao.save(copyOfOptional);
        }


        for(int i = 0 ; i < photos.size(); i++){
            AdImages adImages = new AdImages(photos.get(i), copyOfAd);
            adsImagesDao.save(adImages);
        }


        return "ads/ads";
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

    @GetMapping("deleteAd/{id}")
    @ResponseBody
    public boolean deleteAd(Model model, @PathVariable Long id){
        //todo check that owner owns ad
        Optional<Ad> optionalAd = adDao.findById(id);
        if(optionalAd.isEmpty()){
            return false;
            //todo error
        }

        optionalAd.get().setArchived(true);
        optionalAd.get().setUser(null);
        adDao.save(optionalAd.get());

        return true;
    }


}
