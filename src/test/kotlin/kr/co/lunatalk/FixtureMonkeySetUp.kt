package kr.co.lunatalk

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
open class FixtureMonkeySetUp {

    protected lateinit var fixtureMonkey: FixtureMonkey

    @BeforeEach
    fun setUp() {
        fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .defaultNotNull(true)
            .build()
    }
}
