package kr.co.lunatalk.domain.auth.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash(value = "refreshToken")
public class RefreshToken {

	@Id
	private Long id;

	private String refreshToken;

	@TimeToLive
	private Long ttl;

	@Builder
	public RefreshToken(Long id, String refreshToken, Long ttl) {
		this.id = id;
		this.refreshToken = refreshToken;
		this.ttl = ttl;
	}

	public static RefreshToken of(Long memberId, String refreshToken, Long ttl) {
		return RefreshToken.builder().id(memberId).refreshToken(refreshToken).ttl(ttl).build();
	}
}
