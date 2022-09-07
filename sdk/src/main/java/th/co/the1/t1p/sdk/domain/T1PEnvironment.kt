package th.co.the1.t1p.sdk.domain

import th.co.the1.t1p.sdk.utils.Constants

enum class T1PEnvironment(val baseUrl: String) {
    SIT(baseUrl = Constants.baseUrlSit),
    UAT(baseUrl = Constants.baseUrlUat),
    PVT(baseUrl = Constants.baseUrlPvt),
    PRODUCTION(baseUrl = Constants.baseUrlProd)
}
