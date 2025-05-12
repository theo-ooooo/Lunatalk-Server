package kr.co.lunatalk.domain.member.service;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private SecurityUtil securityUtil;

	private Member testMember;

	@BeforeEach
	void setUp() {
		testMember = Member.createMember(
			"testuser",
			"1234",
			Profile.of("테스트닉", "img"),
			"01012341234",
			"test@email.com"
		);
	}

	@Test
	void 회원_리스트_조회() {
		// given
		Page<Member> memberPage = new PageImpl<>(List.of(testMember));
		Pageable pageable = PageRequest.of(0, 10);

		when(memberRepository.findMembers(pageable)).thenReturn(memberPage);

		// when
		Page<MemberInfoResponse> result = memberService.getMembers(pageable);

		// then
		assertEquals(1, result.getContent().size());
		assertEquals(testMember.getEmail(), result.getContent().get(0).email());
	}

	@Test
	void 특정_회원_정보_조회() {
		// given
		when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));

		// when
		MemberInfoResponse response = memberService.getMemberInformation(testMember.getId());

		// then
		assertEquals(testMember.getProfile().getNickname(), response.nickname());
		assertEquals(testMember.getEmail(), response.email());
	}

	@Test
	void 특정_회원_정보_조회_실패_예외() {
		// given
		Long invalidId = 999L;
		when(memberRepository.findById(invalidId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> memberService.getMemberInformation(invalidId));
		assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void 현재_회원_정보_조회() {
		// given
		when(securityUtil.getCurrentMemberId()).thenReturn(testMember.getId());
		when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));

		// when
		MemberInfoResponse response = memberService.myInformation();

		// then
		assertEquals(testMember.getEmail(), response.email());
		assertEquals(testMember.getProfile().getNickname(), response.nickname());
	}

	@Test
	void MemberInfoResponse_정적_팩토리_메서드_검증() {
		// when
		MemberInfoResponse response = MemberInfoResponse.from(testMember);

		// then
		assertEquals(testMember.getId(), response.memberId());
		assertEquals(testMember.getEmail(), response.email());
		assertEquals(testMember.getProfile().getNickname(), response.nickname());
	}
}
