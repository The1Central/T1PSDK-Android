package th.co.the1.t1p.sdk.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import th.co.the1.t1p.sdk.domain.AuthToken
import th.co.the1.t1p.sdk.domain.TokenStatus

interface T1PSdkApi {
    @Headers("Content-type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("token")
    suspend fun token(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("code") code: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("redirect_uri") redirectUri: String
    ): Response<AuthToken>

    @Headers("Content-type:application/json")
    @GET("/api/sessions/user/signout")
    fun logout(@Header("Authorization") accessToken: String): Call<Unit>

    @Headers("Content-type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("token/introspection ")
    fun tokenIntrospection(
        @Field("client_id") clientId: String,
        @Field("token") accessToken: String
    ): Call<TokenStatus>

    @Headers("Content-type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("token")
    fun refreshToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String
    ): Call<AuthToken>
}
