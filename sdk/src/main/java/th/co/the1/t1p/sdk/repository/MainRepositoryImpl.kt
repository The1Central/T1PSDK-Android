package th.co.the1.t1p.sdk.repository

import retrofit2.Response
import th.co.the1.t1p.sdk.api.RetrofitInstance
import th.co.the1.t1p.sdk.domain.AuthToken

class MainRepositoryImpl : MainRepository {
    override suspend fun exchangeCode(
        grantType: String,
        clientId: String,
        code: String,
        codeVerifier: String,
        redirectUri: String
    ): Response<AuthToken> {
        return RetrofitInstance.getInstance()
            .token(grantType, clientId, code, codeVerifier, redirectUri)
    }
}
