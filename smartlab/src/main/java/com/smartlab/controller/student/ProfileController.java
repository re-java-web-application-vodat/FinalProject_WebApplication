package com.smartlab.controller.student;

import com.smartlab.entity.UserProfile;
import com.smartlab.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public String profilePage(Principal principal, Model model){

        UserProfile profile = profileService.getProfile(principal.getName());

        model.addAttribute("profile", profile);

            return "profile/index";
        }

    @PostMapping
    public String updateProfile(@ModelAttribute UserProfile profile, Principal principal){

        profileService.updateProfile(principal.getName(), profile);

            return "redirect:/profile";
    }
}
