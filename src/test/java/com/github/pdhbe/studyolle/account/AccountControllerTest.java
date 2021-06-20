package com.github.pdhbe.studyolle.account;

import com.github.pdhbe.studyolle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("회원가입 페이지 테스트")
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpFormDto"));
    }

    @Test
    @DisplayName("")
    void signUpSubmit_invalidInput() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "pdh")
                .param("email", "invalidEmail...")
                .param("password", "12345")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @Test
    @DisplayName("")
    void signUpSubmit_validInput() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "pdh")
                .param("email", "ehgusdl67@naver.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertTrue(accountRepository.existsByEmail("ehgusdl67@naver.com"));

        Account savedAccount = accountRepository.findByEmail("ehgusdl67@naver.com");
        assertNotEquals("12345678",savedAccount.getPassword());

        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}