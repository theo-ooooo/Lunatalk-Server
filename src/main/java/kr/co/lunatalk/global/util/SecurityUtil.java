package kr.co.lunatalk.global.util;

import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.security.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
	public Long getCurrentMemberId() {
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Object principal = authentication.getPrincipal();
		if (principal instanceof PrincipalDetails) {
			return Long.parseLong(((PrincipalDetails) principal).getUsername());
		}
		throw new CustomException(ErrorCode.UNAUTHORIZED);
	}

	public String getCurrentMemberRole() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
	}
}
