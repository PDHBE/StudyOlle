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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("인증 메일 확인 - 이메일 오류")
    void checkEmailToken_invalidEmail() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("email", "nonExistsEmail@email.com")
                .param("token", "asdfasdf"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attribute("errorMsg", "Invalid Email"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("인증 메일 확인 - 토큰 오류")
    void checkEmailToken_invalidToken() throws Exception {
        String existEmail = "pdh@naver.com";
        Account savedAccount = accountRepository.save(Account.builder()
                .email(existEmail)
                .nickname("pdh")
                .password("12341234")
                .build());
        savedAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("email", savedAccount.getEmail())
                .param("token", "invalidToken"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attribute("errorMsg", "Invalid Token"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("인증 메일 확인 - 성공")
    void checkEmailToken_success() throws Exception {
        String existEmail = "pdh@naver.com";
        Account savedAccount = accountRepository.save(Account.builder()
                .email(existEmail)
                .nickname("pdh")
                .password("12341234")
                .build());
        savedAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("email", savedAccount.getEmail())
                .param("token", savedAccount.getEmailCheckToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeDoesNotExist("errorMsg"))
                .andExpect(model().attribute("numOfUsers",accountRepository.count()))
                .andExpect(model().attribute("nickname",savedAccount.getNickname()))
                .andExpect(authenticated().withAuthenticationName(savedAccount.getNickname()));
    }

    @Test
    @DisplayName("회원가입 페이지 테스트")
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpFormDto"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원가입 - 유효하지 않은 입력값")
    void signUpSubmit_invalidInput() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "pdh")
                .param("email", "invalidEmail...")
                .param("password", "12345")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signUpSubmit_success() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "pdh")
                .param("email", "ehgusdl67@naver.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withAuthenticationName("pdh"));

        assertTrue(accountRepository.existsByEmail("ehgusdl67@naver.com"));

        Account savedAccount = accountRepository.findByEmail("ehgusdl67@naver.com");
        assertNotEquals("12345678", savedAccount.getPassword());

        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}