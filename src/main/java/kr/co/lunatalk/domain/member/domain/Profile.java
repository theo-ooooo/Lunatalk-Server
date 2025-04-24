package kr.co.lunatalk.domain.member.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {
	private String nickname;

	private String profileImageUrl;

	@Builder(access = AccessLevel.PROTECTED)
	public Profile(String nickname, String profileImageUrl) {
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
	}

	public static Profile of(String nickname, String profileImageUrl) {
		return Profile.builder().nickname(nickname).profileImageUrl(profileImageUrl).build();
	}
}
