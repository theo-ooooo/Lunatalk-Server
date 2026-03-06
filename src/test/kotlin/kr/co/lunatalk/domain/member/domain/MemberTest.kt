package kr.co.lunatalk.domain.member.domain

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberTest {

    private lateinit var fixtureMonkey: FixtureMonkey

    @BeforeEach
    fun setUp() {
        fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
            .build()
    }

    @Test
    fun `createMember 성공`() {
        // given
        val profile = fixtureMonkey.giveMeOne(Profile::class.java)

        // when
        val member = Member.createMember(
            "user", "password", profile,
            "01012341234", "kkwondev@gmail.com"
        )

        // then
        assertNotNull(member)
        assertEquals(profile, member.profile)
        assertEquals(MemberStatus.NORMAL, member.status)
        assertEquals(MemberRole.USER, member.role)
    }

    @Test
    fun `updateLastLoginAt 성공`() {
        // given
        val member = fixtureMonkey.giveMeOne(Member::class.java)

        // when
        member.updateLastLoginAt()

        // then
        assertNotNull(member.lastLoginAt)
    }

    @Test
    fun updateProfile() {
        // given
        val member = fixtureMonkey.giveMeOne(Member::class.java)
        val newProfile = fixtureMonkey.giveMeOne(Profile::class.java)

        // when
        member.updateProfile(newProfile)

        // then
        assertEquals(newProfile, member.profile)
    }
}
