package kr.co.lunatalk.global.util

import kr.co.lunatalk.global.common.constants.EnvironmentConstants.Constants.DEV
import kr.co.lunatalk.global.common.constants.EnvironmentConstants.Constants.LOCAL
import kr.co.lunatalk.global.common.constants.EnvironmentConstants.Constants.PROD
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SpringEnvironmentUtil(
    private val environment: Environment
) {

    fun getCurrentProfile(): String =
        getActiveProfiles()
            .filter { it == PROD || it == DEV }
            .firstOrNull()
            ?: LOCAL

    fun isProdProfile(): Boolean =
        getActiveProfiles().any { it == PROD }

    fun isDevProfile(): Boolean =
        getActiveProfiles().any { it == DEV }

    fun isLocalProfile(): Boolean =
        getActiveProfiles().any { it == LOCAL }

    private fun getActiveProfiles(): List<String> =
        environment.activeProfiles.toList()
}
