package kr.co.lunatalk.domain.auth.repository

import kr.co.lunatalk.TestRedisConfig
import kr.co.lunatalk.domain.auth.domain.RefreshToken
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Import(TestRedisConfig::class)
@DataRedisTest
class RefreshRepositoryTest {

    @Autowired
    lateinit var refreshRepository: RefreshRepository

    @AfterEach
    fun tearDown() {
        refreshRepository.deleteAll()
    }

    @Test
    fun `리프레쉬 토큰을 저장한다`() {
        // given
        val refreshToken = RefreshToken(id = 1L, refreshToken = "testRefreshToken", ttl = 1000L)
        // when
        refreshRepository.save(refreshToken)
        // then
        val findById = refreshRepository.findById(1L)
        assertThat(findById.isPresent).isTrue()
    }

    @Test
    fun `리프레쉬 토큰을 삭제한다`() {
        // given
        val refreshToken = RefreshToken(id = 2L, refreshToken = "testRefreshToken", ttl = 1000L)
        refreshRepository.save(refreshToken)
        // when
        refreshRepository.deleteById(2L)
        // then
        assertThat(refreshRepository.findById(2L).isPresent).isFalse()
    }

    @Test
    fun `리프레쉬 토큰을 조회한다`() {
        // given
        val refreshToken = RefreshToken(id = 3L, refreshToken = "testRefreshToken", ttl = 1000L)
        refreshRepository.save(refreshToken)
        // when
        val findById = refreshRepository.findById(3L)
        // then
        assertThat(findById.isPresent).isTrue()
        assertThat(findById.get().id).isEqualTo(3L)
        assertThat(findById.get().refreshToken).isEqualTo("testRefreshToken")
    }
}
