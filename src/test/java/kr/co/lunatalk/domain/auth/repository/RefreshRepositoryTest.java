//package kr.co.lunatalk.domain.auth.repository;
//
//import kr.co.lunatalk.TestRedisConfig;
//import kr.co.lunatalk.domain.auth.domain.RefreshToken;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@ActiveProfiles("test")
//@Import(TestRedisConfig.class)
//@DataRedisTest
//class RefreshRepositoryTest {
//
//	@Autowired RefreshRepository refreshRepository;
//
//	@AfterEach
//	void tearDown() {
//		refreshRepository.deleteAll();
//	}
//
//	@Test
//	void 리프레쉬_토큰을_저장한다(){
//		//given
//		RefreshToken refreshToken = RefreshToken.builder().id(1L).refreshToken("testRefreshToken").ttl(1000L).build();
//		//when
//		refreshRepository.save(refreshToken);
//		//then
//		Optional<RefreshToken> findById = refreshRepository.findById(1L);
//
//		assertThat(findById.isPresent()).isTrue();
//	}
//
//	@Test
//	void 리프레쉬_토큰을_삭제한다() {
//		//given
//		RefreshToken refreshToken = RefreshToken.builder().id(2L).refreshToken("testRefreshToken").ttl(1000L).build();
//		refreshRepository.save(refreshToken);
//		//when
//		refreshRepository.deleteById(2L);
//		//then
//		assertThat(refreshRepository.findById(2L).isPresent()).isFalse();
//	}
//
//	@Test
//	void 리프레쉬_토큰을_조회한다() {
//		//given
//		RefreshToken refreshToken = RefreshToken.builder().id(3L).refreshToken("testRefreshToken").ttl(1000L).build();
//		refreshRepository.save(refreshToken);
//
//		//when
//		Optional<RefreshToken> findById = refreshRepository.findById(3L);
//		//then
//		assertThat(findById.isPresent()).isTrue();
//		assertThat(3L).isEqualTo(findById.get().getId());
//		assertThat("testRefreshToken").isEqualTo(findById.get().getRefreshToken());
//	}
//}
