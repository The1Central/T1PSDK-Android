package th.co.the1.t1p.sdk

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import th.co.the1.t1p.sdk.api.RetrofitInstance
import th.co.the1.t1p.sdk.domain.*
import th.co.the1.t1p.sdk.ui.main.MainActivity
import th.co.the1.t1p.sdk.utils.T1PSdkFailedEnum
import th.co.the1.t1p.sdk.utils.authenticate.OAuthImpl
import th.co.the1.t1p.sdk.utils.storage.EncryptedSharedPref
import th.co.the1.t1p.sdk.utils.storage.EncryptedSharedPreferencesImpl

class T1PSDK(
    private val activity: Activity,
    environment: T1PEnvironment,
    language: T1PLanguage,
    clientId: String,
    redirectUrl: String
) : IT1PSdk {

    private var oAuthImpl: OAuthImpl = OAuthImpl()
    private var environmentInfo: EnvironmentInfo
    private var sharedPref: EncryptedSharedPref

    init {
        val codeVerifier = oAuthImpl.generateCodeVerifier()
        val codeChallenge = oAuthImpl.generateCodeChallenge(codeVerifier)
        val state = oAuthImpl.generateState()
        sharedPref = EncryptedSharedPreferencesImpl(application = activity.application)
        environmentInfo = EnvironmentInfo(
            environment = environment,
            language = language.name.lowercase(),
            clientId = clientId,
            state = state,
            codeVerifier = codeVerifier,
            codeChallenge = codeChallenge,
            redirectURI = redirectUrl
        )
        RetrofitInstance.t1PEnvironment = environment
    }

    override fun signIn(onActivityResult: ActivityResultLauncher<Intent>) {
        val webViewUrlRequest = oAuthImpl.generateRequestUrl(environmentInfo, InitialPage.SIGN_IN)
        MainActivity.launch(
            activity = activity,
            webViewUrlRequest = webViewUrlRequest,
            environmentInfo = environmentInfo,
            page = InitialPage.SIGN_IN,
            launcherForResult = onActivityResult
        )
    }

    override fun signUp(onActivityResult: ActivityResultLauncher<Intent>) {
        val webViewUrlRequest = oAuthImpl.generateRequestUrl(environmentInfo, InitialPage.SIGN_UP)
        MainActivity.launch(
            activity = activity,
            webViewUrlRequest = webViewUrlRequest,
            environmentInfo = environmentInfo,
            page = InitialPage.SIGN_UP,
            launcherForResult = onActivityResult
        )
    }

    override fun recovery(onActivityResult: ActivityResultLauncher<Intent>) {
        val webViewUrlRequest = oAuthImpl.generateRequestUrl(environmentInfo, InitialPage.RECOVERY)
        MainActivity.launch(
            activity = activity,
            webViewUrlRequest = webViewUrlRequest,
            environmentInfo = environmentInfo,
            page = InitialPage.RECOVERY,
            launcherForResult = onActivityResult
        )
    }

    override fun signOut(callback: T1PSdkCallback) {
        val accessToken = sharedPref.getAccessToken()
        if (accessToken != null) {
            val token = "Bearer $accessToken"
            val api = RetrofitInstance.getInstance().logout(token)
            api.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    sharedPref.revokeToken()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    sharedPref.revokeToken()
                }
            })
            callback.onSuccess()
        } else {
            callback.onFailure()
        }
    }

    override fun accessToken(callback: T1PSdkAccessTokenCallback) {
        val accessToken = sharedPref.getAccessToken()
        if (accessToken != null) {
            val api = RetrofitInstance.getInstance().tokenIntrospection(
                clientId = environmentInfo.clientId,
                accessToken = accessToken
            )
            api.enqueue(object : Callback<TokenStatus> {
                override fun onResponse(call: Call<TokenStatus>, response: Response<TokenStatus>) {
                    processAccessTokenResponse(response)
                }

                override fun onFailure(call: Call<TokenStatus>, t: Throwable) {
                    callback.onFailure(T1PSdkFailedEnum.ACCESS_TOKEN_FAILED)
                }

                private fun processAccessTokenResponse(response: Response<TokenStatus>) {
                    if (response.isSuccessful) {
                        val active = response.body()?.active ?: false
                        if (active) {
                            val authToken = AuthToken(
                                accessToken = accessToken,
                                refreshToken = sharedPref.getRefreshToken(),
                                expiresIn = sharedPref.getExpireIn()
                            )
                            callback.onSuccess(authToken)
                        } else {
                            refreshToken(callback, sharedPref.getRefreshToken())
                        }
                    } else {
                        refreshToken(callback, sharedPref.getRefreshToken())
                    }
                }
            })
        } else {
            callback.onFailure(T1PSdkFailedEnum.ACCESS_TOKEN_FAILED)
        }
    }

    private fun refreshToken(callback: T1PSdkAccessTokenCallback, refreshToken: String?) {
        if (refreshToken != null && refreshToken.isNotEmpty()) {
            val api = RetrofitInstance.getInstance()
                .refreshToken(
                    grantType = GrantType.REFRESH_TOKEN.value,
                    refreshToken = refreshToken,
                    clientId = environmentInfo.clientId
                )
            api.enqueue(object : Callback<AuthToken> {
                override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
                    processRefreshTokenResponse(response)
                }

                private fun processRefreshTokenResponse(response: Response<AuthToken>) {
                    if (response.isSuccessful) {
                        val authToken = response.body()
                        sharedPref.storeAuthToken(authToken)
                        callback.onSuccess(
                            AuthToken(
                                accessToken = authToken?.accessToken,
                                refreshToken = authToken?.refreshToken,
                                expiresIn = authToken?.expiresIn
                            )
                        )
                    } else {
                        sharedPref.revokeToken()
                        callback.onFailure(T1PSdkFailedEnum.REFRESH_TOKEN_FAILED)
                    }
                }

                override fun onFailure(call: Call<AuthToken>, throwable: Throwable) {
                    callback.onFailure(T1PSdkFailedEnum.REFRESH_TOKEN_FAILED)
                }
            })
        } else {
            sharedPref.revokeToken()
            callback.onFailure(T1PSdkFailedEnum.REFRESH_TOKEN_FAILED)
        }
    }
}

interface T1PSdkCallback {
    fun onSuccess()
    fun onFailure()
}

interface T1PSdkAccessTokenCallback {
    fun onSuccess(authToken: AuthToken)
    fun onFailure(t1pSdkFailed: T1PSdkFailedEnum)
}

private interface IT1PSdk {
    fun signIn(onActivityResult: ActivityResultLauncher<Intent>)
    fun signUp(onActivityResult: ActivityResultLauncher<Intent>)
    fun recovery(onActivityResult: ActivityResultLauncher<Intent>)
    fun signOut(callback: T1PSdkCallback)
    fun accessToken(callback: T1PSdkAccessTokenCallback)
}
