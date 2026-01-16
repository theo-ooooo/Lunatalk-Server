package kr.co.lunatalk.domain.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {
	NORMAL("NORMAL"),
	DELETE("DELETE");

	private final String value;
}
