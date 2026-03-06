package kr.co.lunatalk.domain.auth.service

import kr.co.lunatalk.domain.auth.dto.request.LoginRequest
import kr.co.lunatalk.domain.auth.dto.request.RefreshTokenRequest
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse
import kr.co.lunatalk.domain.auth.dto.response.TokenResponse
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.MemberRole
import kr.co.lunatalk.domain.member.domain.MemberStatus
import kr.co.lunatalk.domain.member.domain.Profile
import kr.co.lunatalk.domain.member.dto.request.CreateMemberRequest
import kr.co.lunatalk.domain.member.repository.MemberRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.security.JwtTokenProvider
import kr.co.lunatalk.global.util.MemberUtil
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberUtil: MemberUtil,
) {

    private val log = LoggerFactory.getLogger(AuthService::class.java)

    fun registerMember(request: CreateMemberRequest): AuthTokenResponse {
        val existsMember = memberRepository.findByUsername(request.username)
        if (existsMember.isPresent) {
            throw CustomException(ErrorCode.MEMBER_EXISTS)
        }

        val member = Member.createMember(
            request.username,
            encodePassword(request.password),
            Profile.of("", ""),
            request.phone,
            request.email,
        )
        memberRepository.save(member)

        val token = getTokenResponse(member)
        return AuthTokenResponse.from(token)
    }

    fun loginMember(request: LoginRequest): AuthTokenResponse {
        val member = findMemberByUsername(request.username)

        if (member.status == MemberStatus.DELETE) {
            throw CustomException(ErrorCode.MEMBER_NOT_FOUND)
        }

        return passwordMatchingAndTokenPair(request, member)
    }

    fun loginAdmin(request: LoginRequest): AuthTokenResponse {
        val member = findMemberByUsername(request.username)

        if (member.role != MemberRole.ADMIN) {
            throw CustomException(ErrorCode.AUTH_UNAUTHORIZED)
        }

        return passwordMatchingAndTokenPair(request, member)
    }

    private fun passwordMatchingAndTokenPair(request: LoginRequest, member: Member): AuthTokenResponse {
        val isMatching = matchingPassword(request.password, member.password ?: "")

        if (!isMatching) {
            throw CustomException(ErrorCode.AUTH_UNAUTHORIZED)
        }

        return AuthTokenResponse.from(getTokenResponse(member))
    }

    private fun findMemberByUsername(username: String): Member =
        memberRepository.findByUsername(username).orElseThrow {
            CustomException(ErrorCode.MEMBER_NOT_FOUND)
        }

    fun withdraw() {
        val currentMember = memberUtil.currentMember

        if (currentMember.status == MemberStatus.DELETE) {
            throw CustomException(ErrorCode.MEMBER_ALREADY_DELETED)
        }
        currentMember.withdrawal()
        memberRepository.deleteById(currentMember.id!!)
        jwtTokenProvider.deleteRefreshTokenFromRedis(currentMember.id!!)
    }

    private fun getTokenResponse(member: Member): TokenResponse =
        jwtTokenProvider.generateTokenPair(member.id!!, member.role)

    @Transactional(readOnly = true)
    fun reissueTokenPair(request: RefreshTokenRequest): AuthTokenResponse {
        val refreshTokenDto = jwtTokenProvider.retrieveRefreshToken(request.refreshToken)
            ?: throw CustomException(ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED)

        val member = memberRepository.findById(refreshTokenDto.memberId).orElseThrow {
            CustomException(ErrorCode.MEMBER_NOT_FOUND)
        }

        return AuthTokenResponse.from(getTokenResponse(member))
    }

    private fun encodePassword(password: String): String =
        passwordEncoder.encode(password)!!

    private fun matchingPassword(password: String, encodedPassword: String): Boolean =
        passwordEncoder.matches(password, encodedPassword)
}
