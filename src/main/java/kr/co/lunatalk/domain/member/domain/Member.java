package kr.co.lunatalk.domain.member.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    private LocalDateTime lastLoginAt;

    @Builder
    public Member(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

    public static Member of(String loginId, String password) {
        return Member.builder().loginId(loginId).password(password).build();
    }

}
