package th.co.the1.t1p.sdk.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import th.co.the1.t1p.sdk.domain.AuthToken
import th.co.the1.t1p.sdk.domain.EnvironmentInfo
import th.co.the1.t1p.sdk.domain.GrantType
import th.co.the1.t1p.sdk.domain.Resource
import th.co.the1.t1p.sdk.repository.MainRepository
import th.co.the1.t1p.sdk.utils.storage.EncryptedSharedPreferencesImpl

class MainViewModel(
    application: Application,
    private val environmentInfo: EnvironmentInfo,
    private val mainRepository: MainRepository
) : AndroidViewModel(application) {

    val finished: MutableLiveData<Resource<AuthToken>> = MutableLiveData()

    fun exchangeCode(code: String, state: String) = viewModelScope.launch {
        if (code.isNotEmpty() && state.isNotEmpty() && environmentInfo.state == state) {
            executeExchangeCode(code = code)
        }
    }

    private suspend fun executeExchangeCode(code: String) {
        finished.postValue(Resource.Loading())
        try {
            val response = mainRepository.exchangeCode(
                grantType = GrantType.AUTHORIZATION_CODE.value,
                clientId = environmentInfo.clientId,
                code = code,
                codeVerifier = environmentInfo.codeVerifier,
                redirectUri = environmentInfo.redirectURI
            )
            finished.postValue(handleRequestToken(response))
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            finished.postValue(Resource.Error("Throwable Error"))
        }
    }

    private fun handleRequestToken(response: Response<AuthToken>): Resource<AuthToken> {
        val authToken = response.body()
        if (response.isSuccessful && authToken != null) {
            storeAuthToken(authToken)
            return Resource.Success(authToken)
        }
        return Resource.Error(response.message())
    }

    private fun storeAuthToken(authToken: AuthToken) {
        val sharedPref = EncryptedSharedPreferencesImpl(application = getApplication())
        sharedPref.storeAuthToken(authToken)
    }
}
