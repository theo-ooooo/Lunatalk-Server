package kr.co.lunatalk.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse;
import kr.co.lunatalk.domain.member.service.MemberService;
import kr.co.lunatalk.domain.order.dto.response.OrderFindResponse;
import kr.co.lunatalk.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "회원", description = "회원 관련 API")
public class MemberController {
	private final MemberService memberService;
	private final OrderService orderService;

	@GetMapping("/me")
	@Operation(summary = "나의 회원 조회", description = "현재 로그인된 회원을 조회합니다.")
	public MemberInfoResponse myInformation() {
		return memberService.myInformation();
	}


	@GetMapping()
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "전체 회원 조회", description = "전체 회원을 조회합니다.")
	public Page<MemberInfoResponse> getMembers(@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return memberService.getMembers(pageable);
	}

	@GetMapping("/{id}/orders")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "회원 주문 조회", description = "회원의 주믄을 조회합니다.")
	public Page<OrderFindResponse> getMemberOrders(@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable Long id) {
		return orderService.findOrdersByMemberId(id, pageable);
	}


	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "회원 정보 조회", description = "회원의 정보을 조회합니다.")
	public MemberInfoResponse getMemberInformation(@PathVariable Long id) {
		return memberService.getMemberInformation(id);
	}



}
