package kr.co.lunatalk.global.common.constants


enum class EnvironmentConstants(val value: String) {
    PROD(Constants.PROD),
    DEV(Constants.DEV),
    LOCAL(Constants.LOCAL);

    object Constants {
        const val PROD = "prod"
        const val DEV = "dev"
        const val LOCAL = "local"
        val PROD_DEV = listOf(PROD, DEV)

    }
}
