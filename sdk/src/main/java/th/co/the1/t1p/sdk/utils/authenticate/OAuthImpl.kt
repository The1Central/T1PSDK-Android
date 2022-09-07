package th.co.the1.t1p.sdk.utils.authenticate

import th.co.the1.t1p.sdk.domain.EnvironmentInfo
import th.co.the1.t1p.sdk.domain.InitialPage
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

class OAuthImpl : OAuth {

    override fun generateState(withLength: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..withLength)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val codeVerifier = ByteArray(SIZE_CODE_VERIFIER)
        secureRandom.nextBytes(codeVerifier)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier)
    }

    override fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray(charset("US-ASCII"))
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        val digest = messageDigest.digest()
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }

    override fun generateRequestUrl(
        environmentInfo: EnvironmentInfo,
        initialPage: InitialPage
    ): String {
        return BASE_URL_REQUEST_WEB_VIEW
            .replace("{{BASE_URL}}", environmentInfo.environment.baseUrl)
            .replace("{{CLIENT_ID}}", environmentInfo.clientId)
            .replace("{{state}}", environmentInfo.state)
            .replace("{{code_challenge}}", environmentInfo.codeChallenge)
            .replace("{{REDIRECT_URI}}", environmentInfo.redirectURI)
            .replace("{{page}}", initialPage.value)
    }

    companion object {
        private const val SIZE_CODE_VERIFIER = 64
        private const val BASE_URL_REQUEST_WEB_VIEW =
            "{{BASE_URL}}/oauth/authorize?&client_id={{CLIENT_ID}}" +
                    "&response_type=code" +
                    "&state={{state}}" +
                    "&code_challenge={{code_challenge}}" +
                    "&code_challenge_method=S256" +
                    "&&redirect_uri={{REDIRECT_URI}}" +
                    "&scope=openid" +
                    "&page={{page}}"
    }
}
