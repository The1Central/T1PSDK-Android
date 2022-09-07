package th.co.the1.t1p.sdk.domain

enum class GrantType(val value: String) {
    AUTHORIZATION_CODE(value = "authorization_code"),
    REFRESH_TOKEN(value = "refresh_token")
}
