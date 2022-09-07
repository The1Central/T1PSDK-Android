package th.co.the1.t1p.sdk.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EnvironmentInfo(
    val environment: T1PEnvironment,
    val language: String,
    val clientId: String,
    val state: String,
    val codeVerifier: String,
    val codeChallenge: String,
    val redirectURI: String,
) : Parcelable
