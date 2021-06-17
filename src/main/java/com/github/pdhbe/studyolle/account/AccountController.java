package com.github.pdhbe.studyolle.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
    @GetMapping("sign-up")
    public String signUpForm(Model model){
        model.addAttribute("signUpFormDto",new SignUpFormDto());
        return "account/sign-up";
    }
}
