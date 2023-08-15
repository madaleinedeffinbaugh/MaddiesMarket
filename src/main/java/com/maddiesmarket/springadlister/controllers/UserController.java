package com.maddiesmarket.springadlister.controllers;

import com.maddiesmarket.springadlister.models.User;
import com.maddiesmarket.springadlister.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UserController {
    private final UserRepository userDao;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/sign-up")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Register");
        return "users/sign-up";
    }

    @PostMapping("/sign-up")
    public String saveUser(@ModelAttribute User user, Model model) {
        //check if the username or email is taken
        boolean usernameTaken = false;
        boolean emailTaken = false;

        if (userDao.findByUsername(user.getUsername()) != null) {
            System.out.println("username taken ");
            usernameTaken = true;
        }

        if (userDao.findByEmail(user.getEmail()) != null) {
            System.out.println("email taken ");
            emailTaken = true;
        }

        if (usernameTaken || emailTaken) {
            model.addAttribute("usernameTaken", usernameTaken);
            model.addAttribute("emailTaken", emailTaken);
            model.addAttribute("user", user);
            return "users/sign-up";
        } else {
            //save the new user
            String hash = passwordEncoder.encode(user.getPassword());
            user.setPassword(hash);
            userDao.save(user);
            return "redirect:/login";
        }
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        //make sure the user is logged in before trying to access the profile page
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                Optional<User> optionalUser = userDao.findById(user.getId());
                if (optionalUser.isEmpty()) {
                    return "redirect:/login";
                } else {
                    //get current user info
                    model.addAttribute("user", optionalUser.get());
                }
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
        model.addAttribute("pageTitle", "Your Ads");
        return "users/profile";
    }

    @GetMapping("/editProfile")
    public String editProfile(Model model, @RequestParam(name = "usernameTaken", required = false) boolean usernameTaken,
                              @RequestParam(name = "emailTaken", required = false) boolean emailTaken,
                              @RequestParam(name = "incorrectPassword", required = false) boolean incorrectPassword,
                              @RequestParam(name = "profileUpdated", required = false) boolean profileUpdated,
                              @RequestParam(name = "passwordUpdated", required = false) boolean passwordUpdated) {
        //make sure the user is logged in before accessing the edit profile page
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            return "redirect:/login";
        }

        Optional<User> optionalUser = userDao.findById(user.getId());
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("usernameTaken", usernameTaken);
        model.addAttribute("emailTaken", emailTaken);
        model.addAttribute("incorrectPassword", incorrectPassword);
        model.addAttribute("profileUpdated", profileUpdated);
        model.addAttribute("passwordUpdated", passwordUpdated);
        model.addAttribute("currentUser", optionalUser.get());
        model.addAttribute("pageTitle", "Edit Profile");

        return "/users/editProfile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser.getId() == 0) {
            return "redirect:/login";
        }

        Optional<User> userObject = userDao.findById(loggedInUser.getId());
        if (userObject.isEmpty()) {
            return "redirect:/login";
        }

        boolean usernameTaken = false;
        boolean emailTaken = false;

        if (!userObject.get().getUsername().equals(user.getUsername()) && (userDao.findByUsername(user.getUsername()) != null)) {
            System.out.println("username taken ");
            usernameTaken = true;
        }

        if (!userObject.get().getEmail().equals(user.getEmail()) && userDao.findByEmail(user.getEmail()) != null) {
            System.out.println("email taken ");
            emailTaken = true;
        }

        if (usernameTaken || emailTaken) {
            redirectAttributes.addAttribute("usernameTaken", usernameTaken);
            redirectAttributes.addAttribute("emailTaken", emailTaken);
            return "redirect:/editProfile";
        } else {
            loggedInUser.setFirst_name(user.getFirst_name());
            loggedInUser.setLast_name(user.getLast_name());
            loggedInUser.setUsername(user.getUsername());
            loggedInUser.setEmail(user.getEmail());

            userObject.get().setFirst_name(user.getFirst_name());
            userObject.get().setLast_name(user.getLast_name());
            userObject.get().setUsername(user.getUsername());
            userObject.get().setEmail(user.getEmail());

            userDao.save(userObject.get());
            redirectAttributes.addAttribute("profileUpdated", true);
            return "redirect:/editProfile";
        }

    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam String newPassword, @RequestParam String currentPassword, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userDao.findById(loggedInUser.getId());
        if (optionalUser.isEmpty()) {
            return "redirect:/error";
        }

        String currentValidPassword = optionalUser.get().getPassword();

        boolean passwordCorrect = passwordEncoder.matches(currentPassword, currentValidPassword);
        if (passwordCorrect) {
            String newPasswordHash = passwordEncoder.encode(newPassword);
            optionalUser.get().setPassword(newPasswordHash);
            userDao.save(optionalUser.get());
            redirectAttributes.addAttribute("passwordUpdated", true);
        } else {
            redirectAttributes.addAttribute("incorrectPassword", true);
            return "redirect:/editProfile";
        }
        return "redirect:/editProfile";
    }

    @GetMapping("/messages")
    public String getUserMessages(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                Optional<User> optionalUser = userDao.findById(user.getId());
                if (optionalUser.isEmpty()) {
                    return "redirect:/login";
                } else {
                    //get current user info
                    model.addAttribute("startUsername", optionalUser.get().getUsername());
                    model.addAttribute("startId", optionalUser.get().getId());
                    model.addAttribute("startEmail", optionalUser.get().getEmail());
                    //get other user
                    model.addAttribute("otherUsername", "No Conversation Selected");
                    model.addAttribute("otherId", 0);
                    model.addAttribute("otherEmail", null);
                    model.addAttribute("pageTitle", "Messages");

                }
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }

        return "users/messages";
    }

    @GetMapping("/messages/{otherUserId}")
    public String getMessages(@PathVariable Long otherUserId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                Optional<User> optionalUser = userDao.findById(user.getId());
                if (optionalUser.isEmpty()) {
                    return "redirect:/login";
                } else {
                    //get current user info
                    model.addAttribute("startUsername", optionalUser.get().getUsername());
                    model.addAttribute("startId", optionalUser.get().getId());
                    model.addAttribute("startEmail", optionalUser.get().getEmail());
                    //get other user
                    Optional<User> optionalOtherUser = userDao.findById(otherUserId);
                    if (optionalOtherUser.isEmpty()) {
                        return "redirect:/error";
                    }
                    model.addAttribute("otherUsername", optionalOtherUser.get().getUsername());
                    model.addAttribute("otherId", optionalOtherUser.get().getId());
                    model.addAttribute("otherEmail", optionalOtherUser.get().getEmail());
                    model.addAttribute("pageTitle", "Messages");
                }
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
        return "users/messages";
    }

}
