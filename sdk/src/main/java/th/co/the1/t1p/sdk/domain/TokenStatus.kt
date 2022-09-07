package th.co.the1.t1p.sdk.domain

import com.google.gson.annotations.SerializedName

data class TokenStatus(
    @SerializedName("active") val active: Boolean? = null
)
