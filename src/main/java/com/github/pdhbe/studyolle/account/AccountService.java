package com.github.pdhbe.studyolle.account;

import com.github.pdhbe.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public void submitSignUp(SignUpFormDto signUpFormDto){
        Account savedAccount = saveNewAccount(signUpFormDto);
        savedAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(savedAccount);
    }

    private void sendSignUpConfirmEmail(Account savedAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedAccount.getEmail());
        mailMessage.setSubject("스터디 올레, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + savedAccount.getEmailCheckToken() +
                "&email=" + savedAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    private Account saveNewAccount(SignUpFormDto signUpFormDto) {
        Account account = Account.builder()
                .nickname(signUpFormDto.getNickname())
                .email(signUpFormDto.getEmail())
                .password(signUpFormDto.getPassword()) // 추후에 encoding 작업 추가
                .AlarmByWebStudyCreated(true)
                .AlarmByWebStudyUpdated(true)
                .AlarmByWebStudyEnrollmentResult(true)
                .build();

        Account savedAccount = accountRepository.save(account);
        return savedAccount;
    }
}
