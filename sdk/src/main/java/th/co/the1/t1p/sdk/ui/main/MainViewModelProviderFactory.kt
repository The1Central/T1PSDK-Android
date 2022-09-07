package th.co.the1.t1p.sdk.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import th.co.the1.t1p.sdk.repository.MainRepository
import th.co.the1.t1p.sdk.domain.EnvironmentInfo

@Suppress("UNCHECKED_CAST")
class MainViewModelProviderFactory(
    private val repository: MainRepository,
    private val environmentInfo: EnvironmentInfo,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application, environmentInfo, repository) as T
    }
}
