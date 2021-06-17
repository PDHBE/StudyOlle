package com.github.pdhbe.studyolle.account;

import com.github.pdhbe.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    @InitBinder(value = "signUpFormDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpFormDto", new SignUpFormDto());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpFormDto signUpFormDto, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        Account account = Account.builder()
                .nickname(signUpFormDto.getNickname())
                .email(signUpFormDto.getEmail())
                .password(signUpFormDto.getPassword()) // 추후에 encoding 작업 추가
                .AlarmByWebStudyCreated(true)
                .AlarmByWebStudyUpdated(true)
                .AlarmByWebStudyEnrollmentResult(true)
                .build();

        Account savedAccount = accountRepository.save(account);

        savedAccount.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedAccount.getEmail());
        mailMessage.setSubject("스터디 올레, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + savedAccount.getEmailCheckToken() +
                "&email=" + savedAccount.getEmail());
        javaMailSender.send(mailMessage);

        return "redirect:/";
    }
}
