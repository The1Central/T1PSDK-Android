package th.co.the1.t1p.sdk.utils.storage

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import th.co.the1.t1p.sdk.domain.AuthToken

class EncryptedSharedPreferencesImpl(application: Application) : EncryptedSharedPref {

    private lateinit var encryptedSharedPreferences: SharedPreferences

    init {
        try {
            val masterKey = MasterKey.Builder(application, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                application,
                SHARED_PREFERENCES_KEY,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    override fun storeAuthToken(authToken: AuthToken?) {
        if (authToken == null) return
        encryptedSharedPreferences.edit()
            .putString(ACCESS_TOKEN_KEY, authToken.accessToken)
            .putString(REFRESH_TOKEN_KEY, authToken.refreshToken)
            .putString(EXPIRE_IN_KEY, EXPIRE_IN_KEY)
            .apply()
    }

    override fun getAccessToken(): String? {
        return runCatching {
            encryptedSharedPreferences.getString(ACCESS_TOKEN_KEY, null)
        }.getOrNull()
    }

    override fun revokeToken() {
        encryptedSharedPreferences.edit()
            .remove(ACCESS_TOKEN_KEY)
            .remove(REFRESH_TOKEN_KEY)
            .remove(EXPIRE_IN_KEY)
            .apply()
    }

    override fun getRefreshToken(): String? {
        return runCatching {
            encryptedSharedPreferences.getString(REFRESH_TOKEN_KEY, null)
        }.getOrNull()
    }

    override fun getExpireIn(): String? {
        return runCatching {
            encryptedSharedPreferences.getString(EXPIRE_IN_KEY, null)
        }.getOrNull()
    }

    companion object {
        private const val SHARED_PREFERENCES_KEY = "t1p_sdk_prefs"
        private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
        private const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
        private const val EXPIRE_IN_KEY = "EXPIRE_IN_KEY"
    }
}
