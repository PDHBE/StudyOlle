package com.github.pdhbe.studyolle.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpFormDto.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        SignUpFormDto signUpFormDto = (SignUpFormDto) object;

        if(accountRepository.existsByEmail(signUpFormDto.getEmail())){
            errors.rejectValue("email","invalid.email",new Object[]{signUpFormDto.getEmail()},"이미 사용중인 이메일 입니다.");
        }

        if(accountRepository.existsByNickname(signUpFormDto.getNickname())){
            errors.rejectValue("nickname","invalid.nickname",new Object[]{signUpFormDto.getNickname()},"이미 사용중인 닉네임 입니다.");
        }
    }
}
