package th.co.the1.t1p.sdk.utils.authenticate

import th.co.the1.t1p.sdk.domain.EnvironmentInfo
import th.co.the1.t1p.sdk.domain.InitialPage

interface OAuth {
    fun generateCodeVerifier(): String
    fun generateCodeChallenge(codeVerifier: String): String
    fun generateState(withLength: Int = 10): String
    fun generateRequestUrl(environmentInfo: EnvironmentInfo, initialPage: InitialPage): String
}
