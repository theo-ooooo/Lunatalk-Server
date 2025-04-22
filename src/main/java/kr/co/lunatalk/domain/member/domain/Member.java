package kr.co.lunatalk.domain.member.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import lombok.*;


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

	//TODO: 멤버 기능 만들때 필드 추가 예정

    private LocalDateTime lastLoginAt;

    @Builder
    public Member(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

    public static Member of(String loginId, String password) {
        return Member.builder().loginId(loginId).password(password).build();
    }

	public void updateLastLoginAt() {
		this.lastLoginAt = LocalDateTime.now();
	}

}
