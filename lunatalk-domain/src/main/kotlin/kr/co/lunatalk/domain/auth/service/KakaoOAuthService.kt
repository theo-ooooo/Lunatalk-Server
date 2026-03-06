package kr.co.lunatalk.domain.auth.service

import kr.co.lunatalk.domain.auth.config.KakaoOAuthProperties
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse
import kr.co.lunatalk.domain.auth.dto.response.KakaoTokenResponse
import kr.co.lunatalk.domain.auth.dto.response.KakaoUserInfoResponse
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.MemberStatus
import kr.co.lunatalk.domain.member.domain.Profile
import kr.co.lunatalk.domain.member.repository.MemberRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.security.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

@Service
@Transactional
class KakaoOAuthService(
    private val kakaoOAuthProperties: KakaoOAuthProperties,
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val restTemplate: RestTemplate,
) {

    private val log = LoggerFactory.getLogger(KakaoOAuthService::class.java)

    fun login(authorizationCode: String): AuthTokenResponse {
        val accessToken = getAccessToken(authorizationCode)
        val userInfo = getUserInfo(accessToken)
        val member = findOrCreateMember(userInfo)
        val tokenResponse = jwtTokenProvider.generateTokenPair(member.id!!, member.role)
        return AuthTokenResponse.from(tokenResponse)
    }

    private fun getAccessToken(authorizationCode: String): String {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val params = LinkedMultiValueMap<String, String>()
        params.add("grant_type", "authorization_code")
        params.add("client_id", kakaoOAuthProperties.clientId)
        params.add("client_secret", kakaoOAuthProperties.clientSecret)
        params.add("redirect_uri", kakaoOAuthProperties.redirectUri)
        params.add("code", authorizationCode)

        val request = HttpEntity(params, headers)

        try {
            val response = restTemplate.postForEntity(
                kakaoOAuthProperties.tokenUri,
                request,
                KakaoTokenResponse::class.java
            )

            if (response.statusCode == HttpStatus.OK && response.body != null) {
                return response.body!!.accessToken!!
            }

            throw CustomException(ErrorCode.OAUTH_TOKEN_REQUEST_FAILED)
        } catch (e: Exception) {
            log.error("카카오 액세스 토큰 발급 실패: {}", e.message)
            throw CustomException(ErrorCode.OAUTH_TOKEN_REQUEST_FAILED)
        }
    }

    private fun getUserInfo(accessToken: String): KakaoUserInfoResponse {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity<String>(headers)

        try {
            val response = restTemplate.exchange(
                kakaoOAuthProperties.userInfoUri,
                HttpMethod.GET,
                request,
                KakaoUserInfoResponse::class.java
            )

            if (response.statusCode == HttpStatus.OK && response.body != null) {
                return response.body!!
            }

            throw CustomException(ErrorCode.OAUTH_USER_INFO_REQUEST_FAILED)
        } catch (e: Exception) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.message)
            throw CustomException(ErrorCode.OAUTH_USER_INFO_REQUEST_FAILED)
        }
    }

    private fun findOrCreateMember(userInfo: KakaoUserInfoResponse): Member {
        val providerId = userInfo.id.toString()

        val existingMember = memberRepository.findByProviderAndProviderId(PROVIDER, providerId)

        if (existingMember.isPresent) {
            val member = existingMember.get()
            if (member.status == MemberStatus.DELETE) {
                throw CustomException(ErrorCode.MEMBER_NOT_FOUND)
            }
            member.updateLastLoginAt()
            return member
        }

        val email = userInfo.kakaoAccount?.email ?: "${userInfo.id}@kakao.com"
        val nickname = userInfo.kakaoAccount?.profile?.nickname ?: userInfo.id.toString()
        val profileImageUrl = userInfo.kakaoAccount?.profile?.profileImageUrl ?: ""

        val profile = Profile.of(nickname, profileImageUrl)
        val username = PROVIDER.lowercase() + "_" + userInfo.id

        val newMember = Member.createSocialMember(username, profile, email, PROVIDER, providerId)
        return memberRepository.save(newMember)
    }

    companion object {
        private const val PROVIDER = "KAKAO"
    }
}
