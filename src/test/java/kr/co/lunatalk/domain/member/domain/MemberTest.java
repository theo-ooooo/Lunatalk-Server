package kr.co.lunatalk.domain.member.domain;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@Transactional
class MemberTest {

	FixtureMonkey fixtureMonkey;

	@BeforeEach
	void setUp() {
		fixtureMonkey = FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE).build();
	}

	@Test
	void createMember_성공() {
		//given
		Profile profile = fixtureMonkey.giveMeOne(Profile.class);

		//when
		Member member = Member.createMember("user", "password", profile);

		//then
		assertNotNull(member);
		assertEquals(profile, member.getProfile());
		assertEquals(MemberStatus.NORMAL, member.getStatus());
		assertEquals(MemberRole.USER, member.getRole());
	}

	@Test
	void updateLastLoginAt_성공() {
		// given
		Member member = fixtureMonkey.giveMeOne(Member.class);

		//when
		member.updateLastLoginAt();

		//then
		assertNotNull(member.getLastLoginAt());
	}

	@Test
	void updateProfile() {
		// given
		Member member = fixtureMonkey.giveMeOne(Member.class);
		Profile newProfile = fixtureMonkey.giveMeOne(Profile.class);
		// when
		member.updateProfile(newProfile);
		// then
		assertEquals(newProfile, member.getProfile());
	}


}
