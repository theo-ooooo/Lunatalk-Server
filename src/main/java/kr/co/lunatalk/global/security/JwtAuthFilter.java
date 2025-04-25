package kr.co.lunatalk.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.lunatalk.domain.auth.dto.TokenDto;
import kr.co.lunatalk.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		final String token = request.getHeader("Authorization");


		if(token != null && token.startsWith("Bearer ")) {
			String jwtToken = token.replaceFirst("Bearer ", "");
			TokenDto tokenDto = jwtUtil.parseAccessToken(jwtToken);
			//토큰 검증 완료후 SecurityContextHolder 내 인증 정보가 없는 경우만 저장
			if(tokenDto != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				setAuthenticationToContext(tokenDto);
			}
		}
		filterChain.doFilter(request, response);
	}

	private static void setAuthenticationToContext(TokenDto tokenDto) {
		UserDetails userDetails = new PrincipalDetails(tokenDto.memberId(), tokenDto.memberRole());
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
