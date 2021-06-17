package com.github.pdhbe.studyolle.account;

import lombok.Data;

@Data
public class SignUpFormDto {
    private String nickname;
    private String email;
    private String password;
}
