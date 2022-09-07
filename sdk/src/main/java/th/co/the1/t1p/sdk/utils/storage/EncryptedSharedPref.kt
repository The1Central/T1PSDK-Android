package th.co.the1.t1p.sdk.utils.storage

import th.co.the1.t1p.sdk.domain.AuthToken

interface EncryptedSharedPref {
    fun storeAuthToken(authToken: AuthToken?)
    fun getAccessToken(): String?
    fun revokeToken()
    fun getRefreshToken(): String?
    fun getExpireIn(): String?
}
