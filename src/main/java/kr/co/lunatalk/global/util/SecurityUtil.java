package kr.co.lunatalk.global.util;

import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
	public Long getCurrentMemberId() {
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		try {
			return Long.parseLong(authentication.getName());
		}catch (Exception e) {
			throw new CustomException(ErrorCode.AUTH_SERVER_ERROR);
		}
	}

	public Long getCurrentMemberRole() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		try {
			return Long.parseLong(authentication.getAuthorities().stream().findFirst().get().getAuthority());
		}catch (Exception e) {
			throw new CustomException(ErrorCode.AUTH_SERVER_ERROR);
		}
	}
}
