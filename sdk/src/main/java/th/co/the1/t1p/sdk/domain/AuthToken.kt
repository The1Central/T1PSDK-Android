package th.co.the1.t1p.sdk.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthToken(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("expires_in") val expiresIn: String? = null,
    @SerializedName("id_token") val idToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("scope") val scope: String? = null,
    @SerializedName("token_type") val tokenType: String? = null,
    @SerializedName("error") val error: String? = null
) : Parcelable
