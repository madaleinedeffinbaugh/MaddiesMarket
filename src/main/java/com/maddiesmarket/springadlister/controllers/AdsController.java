package com.maddiesmarket.springadlister.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maddiesmarket.springadlister.models.*;
import com.maddiesmarket.springadlister.repositories.*;
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

    public AdsController(AdRepository adDao, CategoryRepository categoryDao, UserRepository userDao, AdCategoriesRepository adsCategoriesDao, AdImagesRepository adsImagesDao) {
        this.adDao = adDao;
        this.categoryDao = categoryDao;
        this.userDao = userDao;
        this.adsImagesDao = adsImagesDao;
        this.adsCategoriesDao = adsCategoriesDao;
    }

    @GetMapping("/ads")
    public String viewAds(Model model) {
        //find all ads that aren't sold or deleted and return them to the page
        model.addAttribute("ads", adDao.findByArchivedFalseAndAdStatusNot(Ad.adStatus.sold));
        //grab a list of all the categories to fill the "search by category" select
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("pageTitle", "Ads");
        return "ads/ads";
    }
    @GetMapping("/ads-by-category")
    public String viewAdsByCategory(@RequestParam("categoryId") String categoryId, Model model) {
        //find all ads that aren't sold or deleted to begin
        List<Ad> allAds = adDao.findByArchivedFalseAndAdStatusNot(Ad.adStatus.sold);
        //return that list if they selected all (id of 1)
        if (categoryId.equals("1")) {
            model.addAttribute("ads", allAds);
        } else {
            //sort through the ads to find the ones that match the category and return that
            List<Ad> byCategory = new ArrayList<>();
            for (int i = 0; i < allAds.size(); i++) {
                if (allAds.get(i).getCategoryIds().contains(categoryId)) {
                    byCategory.add(allAds.get(i));
                }
            }
            model.addAttribute("ads", byCategory);
        }
        //grab a list of all the categories to fill the "search by category" select
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("pageTitle", "Ads");
        return "ads/ads";
    }

    @GetMapping("/search-ads")
    public String viewAdsBySearch(@RequestParam("search") String search, Model model) {
        //find all ads that aren't sold or deleted to begin
        List<Ad> allAds = adDao.findByArchivedFalseAndAdStatusNot(Ad.adStatus.sold);
        List<Ad> matchingAds = new ArrayList<>();
        //search through them to see of the user entered search term matches anything in the title, description, or location
        for (int i = 0; i < allAds.size(); i++) {
            String location = allAds.get(i).getCity() + ", " + allAds.get(i).getState();
            if (allAds.get(i).getTitle().contains(search) || allAds.get(i).getDescription().contains(search) || location.contains(search)) {
                matchingAds.add(allAds.get(i));
            }
        }
        model.addAttribute("ads", matchingAds);
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("pageTitle", "Ads");
        return "ads/ads";
    }

    @GetMapping("/viewAd/{id}")
    public String viewAd(Model model, @PathVariable Long id) {
        //find the target at
        Optional<Ad> ad = adDao.findById(id);
        if (ad.isEmpty()) {
            return "redirect:/error";
        }
        model.addAttribute("ad", ad.get());

        //find out if the ad belongs the user (to determine if the edit button should be shown or the message user button should be shown)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                Optional<User> optionalUser = userDao.findById(user.getId());
                if (optionalUser.isEmpty()) {
                    model.addAttribute("AdBelongsToUser", false);
                } else {
                    if (ad.get().getUser().getId() == optionalUser.get().getId()) {
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
        model.addAttribute("pageTitle", ad.get().getTitle());
        return "ads/viewMore";
    }

    @GetMapping("/create-listing")
    public String newAd(Model model) {
        //make sure the user is logged in before they can create an ad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                Optional<User> optionalUser = userDao.findById(user.getId());
                if (optionalUser.isEmpty()) {
                    return "redirect:/login";
                } else {
                    model.addAttribute("categories", categoryDao.findAll());
                    model.addAttribute("allStates", allStates);
                    model.addAttribute("pageTitle", "Create a Listing");
                    return "ads/create";
                }
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
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
        //these two lists are the categories and images the user added/chose
        List<String> categories = objectMapper.readValue(categoriesJson, new TypeReference<List<String>>() {
        });
        List<String> photos = objectMapper.readValue(photosJson, new TypeReference<List<String>>() {
        });

        //find the current user logged in so they can be added to the ad
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userDao.findById(currentUser.getId());
        if (optionalUser.isEmpty()) {
            return "redirect:/error";
        }

        //create the new ad
        Ad newAd = new Ad(title, description, Double.parseDouble(price), Ad.getCorrespondingCondition(condition), city, state, optionalUser.get());
        Ad savedAd = adDao.save(newAd);

        //pass through the list of categories and add them to the ad
        for (int i = 0; i < categories.size(); i++) {
            Optional<Category> optionalCategory = categoryDao.findById(Long.valueOf(categories.get(i)));
            if (optionalCategory.isEmpty()) {
                return "redirect:/error";
            }
            AdCategories adCategories = new AdCategories(savedAd, optionalCategory.get());
            adsCategoriesDao.save(adCategories);
        }

        //pass through the images and add them to the ad
        for (int i = 0; i < photos.size(); i++) {
            AdImages adImages = new AdImages(photos.get(i), savedAd);
            adsImagesDao.save(adImages);
        }

        return "redirect:/ads";
    }

    @GetMapping("/editAd/{id}")
    public String editEditAd(Model model, @PathVariable Long id) {
        //find the ad
        Optional<Ad> optionalAd = adDao.findById(id);
        if (optionalAd.isEmpty()) {
            return "redirect:/error";
        }

        //make sure the ad belongs to the current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getId() != optionalAd.get().getUser().getId()) {
            return "redirect:/error";
        } else {
            model.addAttribute("allStates", allStates);
            model.addAttribute("categories", categoryDao.findAll());
            model.addAttribute("ad", optionalAd.get());
            model.addAttribute("adCategoryIds", optionalAd.get().getCategoryIds());
            model.addAttribute("pageTitle", "Edit Ad");
            return "ads/editAd";
        }


    }

    @Transactional
    @PostMapping("/edit-listing")
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
        List<String> categories = objectMapper.readValue(categoriesJson, new TypeReference<List<String>>() {
        });
        List<String> photos = objectMapper.readValue(photosJson, new TypeReference<List<String>>() {
        });
        List<String> photosRemove = objectMapper.readValue(photosRemoveJson, new TypeReference<List<String>>() {
        });

        Optional<Ad> currentAd = adDao.findById(id);
        if (currentAd.isEmpty()) {
            return "redirect:/error";
        }

        Ad copyOfAd = currentAd.get();

        copyOfAd.setTitle(title);
        copyOfAd.setDescription(description);
        copyOfAd.setPrice(Double.valueOf(price));
        copyOfAd.setAdCondition(Ad.getCorrespondingCondition(condition));
        copyOfAd.setCity(city);
        copyOfAd.setState(state);
        copyOfAd.setAdStatus(Ad.getCorrespondingStatus(adStatus));
        copyOfAd.setId(id);

        adDao.save(copyOfAd);

        //find if any categories were removed or added
        List<String> missingCats = findMissingElements(currentAd.get().getCategoryIds(), categories);
        List<String> newCats = findNewElements(currentAd.get().getCategoryIds(), categories);

        //remove categories from database
        for (int i = 0; i < missingCats.size(); i++) {
            Optional<AdCategories> optionalAdCategories = adsCategoriesDao.findByAdIdAndCategoryId(copyOfAd.getId(), Long.valueOf(missingCats.get(i)));
            if (optionalAdCategories.isEmpty()) {
                return "redirect:/error";
            }

            AdCategories copyOfOptional = optionalAdCategories.get();
            copyOfOptional.setAd(null);
            adsCategoriesDao.save(copyOfOptional);
        }

        //add categories to database
        for (int i = 0; i < newCats.size(); i++) {
            Optional<Category> optionalCategory = categoryDao.findById(Long.valueOf(newCats.get(i)));
            if (optionalCategory.isEmpty()) {
                return "redirect:/error";
            }
            AdCategories adCategories = new AdCategories(copyOfAd, optionalCategory.get());
            System.out.println(adCategories.getCategory().getCategory());
            adsCategoriesDao.save(adCategories);
        }

        //find any photos that they removed and remove them from the database
        for (int i = 0; i < photosRemove.size(); i++) {
            Optional<AdImages> optionalAdImages = adsImagesDao.findById(Long.valueOf(photosRemove.get(i)));
            if (optionalAdImages.isEmpty()) {
                System.out.println("error");
                return "redirect:/error";
            }
            AdImages copyOfOptional = optionalAdImages.get();
            copyOfOptional.setAd(null);
            adsImagesDao.save(copyOfOptional);
        }

        //add any new images to the database
        for (int i = 0; i < photos.size(); i++) {
            AdImages adImages = new AdImages(photos.get(i), copyOfAd);
            adsImagesDao.save(adImages);
        }
        return "redirect:/ads";
    }


    @GetMapping("/deleteAd/{id}")
    @ResponseBody
    public boolean deleteAd(Model model, @PathVariable Long id) {
        //find ad
        Optional<Ad> optionalAd = adDao.findById(id);
        if (optionalAd.isEmpty()) {
            return false;
        }

        //check that user owns ad
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getId() != optionalAd.get().getUser().getId()) {
            return false;
        } else {
            //remove the ad
            optionalAd.get().setArchived(true);
            optionalAd.get().setUser(null);
            adDao.save(optionalAd.get());
            return true;
        }
    }


}
