package kr.co.lunatalk.domain.member.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = {
	@UniqueConstraint(name = "uk_provider_provider_id", columnNames = {"provider", "provider_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = true)
    private String password;

    @Column(nullable = true)
    private String provider;

    @Column(name = "provider_id", nullable = true)
    private String providerId;

    @Embedded
    private Profile profile = Profile.of("","");

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

	@Column(nullable = true)
	private String phone;
	@Column(nullable = true)
	private String email;

    private LocalDateTime lastLoginAt;

    @Builder(access = AccessLevel.PRIVATE)
    public Member(String username, String password, Profile profile, MemberStatus status, MemberRole role, LocalDateTime lastLoginAt, String phone, String email, String provider, String providerId) {
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.status = status;
        this.role = role;
        this.lastLoginAt = lastLoginAt;
		this.phone = phone != null && phone.isBlank() ? null : phone;
		this.email = email;
		this.provider = provider;
		this.providerId = providerId;
    }

    public static Member createMember(String username, String password, Profile profile, String phone, String email) {
		return Member.builder()
			.username(username)
			.password(password)
			.profile(profile)
			.role(MemberRole.USER)
			.status(MemberStatus.NORMAL)
			.email(email)
			.phone(phone)
			.lastLoginAt(LocalDateTime.now())
			.provider(null)
			.providerId(null)
			.build();
    }

	public static Member createSocialMember(String username, Profile profile, String email, String provider, String providerId) {
		return Member.builder()
			.username(username)
			.password(null)
			.profile(profile)
			.role(MemberRole.USER)
			.status(MemberStatus.NORMAL)
			.email(email)
			.phone(null)
			.lastLoginAt(LocalDateTime.now())
			.provider(provider)
			.providerId(providerId)
			.build();
	}

    public void updateProfile(Profile profile) {
        this.profile = profile;
    }

    public void withdrawal() {
        this.status = MemberStatus.DELETE;
    }

    public void updateNickname(String nickname) {
        this.profile = Profile.of(nickname,this.profile.getProfileImageUrl());
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
