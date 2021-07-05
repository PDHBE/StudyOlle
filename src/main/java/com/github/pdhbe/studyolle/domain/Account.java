package com.github.pdhbe.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {
    @Id @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String nickname;
    private String password;

    private boolean emailVerified;
    private String emailCheckToken;
    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    private String bio;
    private String url;
    private String occupation;
    private String location;
    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean AlarmByEmailStudyCreated;
    private boolean AlarmByWebStudyCreated;

    private boolean AlarmByEmailStudyEnrollmentResult;
    private boolean AlarmByWebStudyEnrollmentResult;

    private boolean AlarmByEmailStudyUpdated;
    private boolean AlarmByWebStudyUpdated;

    public void generateEmailCheckToken() {
        emailCheckToken = UUID.randomUUID().toString();
        emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}
