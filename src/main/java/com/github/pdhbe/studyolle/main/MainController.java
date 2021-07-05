package com.github.pdhbe.studyolle.main;

import com.github.pdhbe.studyolle.account.CurrentUser;
import com.github.pdhbe.studyolle.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class MainController {
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if(Objects.nonNull(account)){
            model.addAttribute(account);
        }
        return "index";
    }
}
