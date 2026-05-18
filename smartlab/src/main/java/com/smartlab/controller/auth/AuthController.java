package com.smartlab.controller.auth;

import com.smartlab.dto.RegisterDTO;
import com.smartlab.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute(
                "registerDTO",
                new RegisterDTO()
        );
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO dto, BindingResult result){

        if(result.hasErrors()){
            return "auth/register";
        }

        authService.register(dto);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "auth/login";
    }
}