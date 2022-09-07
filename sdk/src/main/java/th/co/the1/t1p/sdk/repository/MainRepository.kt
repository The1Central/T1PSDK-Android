package th.co.the1.t1p.sdk.repository

import retrofit2.Response
import th.co.the1.t1p.sdk.domain.AuthToken

interface MainRepository {
    suspend fun exchangeCode(
        grantType: String,
        clientId: String,
        code: String,
        codeVerifier: String,
        redirectUri: String
    ): Response<AuthToken>
}
