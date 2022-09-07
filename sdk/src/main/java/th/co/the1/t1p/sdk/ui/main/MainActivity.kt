package th.co.the1.t1p.sdk.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import th.co.the1.t1p.sdk.R
import th.co.the1.t1p.sdk.databinding.ActivityMainBinding
import th.co.the1.t1p.sdk.domain.EnvironmentInfo
import th.co.the1.t1p.sdk.domain.InitialPage
import th.co.the1.t1p.sdk.domain.Resource
import th.co.the1.t1p.sdk.repository.MainRepositoryImpl
import th.co.the1.t1p.sdk.ui.dialog.AlertDialogFragment

class MainActivity : LocalizationActivity(),
    Toolbar.OnMenuItemClickListener,
    AlertDialogFragment.NoticeDialogListener {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var viewModel: MainViewModel

    private lateinit var page: InitialPage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupPage()
        setupRepository()
        setupView()
        setupWebView()
    }

    private fun setupPage() {
        intent?.getStringExtra(PAGE_EXTRA)?.let {
            page = InitialPage.valueOf(it)
        }
    }

    private fun setupRepository() {
        val repository = MainRepositoryImpl()
        intent?.getParcelableExtra<EnvironmentInfo>(ENVIRONMENT_INFO_EXTRA)?.let { networkEnv ->
            val viewModelProviderFactory =
                MainViewModelProviderFactory(repository, networkEnv, application)
            viewModel = ViewModelProvider(this, viewModelProviderFactory)[MainViewModel::class.java]
            setLanguage(networkEnv.language)
        }
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener(this)

        viewModel.finished.observe(this) {
            when (it) {
                is Resource.Success -> {
                    val intent = Intent()
                    intent.putExtra(page.value, it.data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                is Resource.Error -> {
                    val intent = Intent()
                    setResult(Activity.RESULT_CANCELED, intent)
                    finish()
                }
                else -> Unit
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        with(binding.webViewThe1Sdk) {
            setBackgroundColor(ContextCompat.getColor(context, R.color.black_the1))

            intent.getStringExtra(WEB_VIEW_URL_REQUEST_EXTRA)?.let {
                loadUrl(it)
            }

            settings.javaScriptEnabled = true
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            clearCache(true)
            clearFormData()
            clearHistory()
            clearSslPreferences()

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val code = request?.url?.getQueryParameter(CODE) ?: ""
                    val state = request?.url?.getQueryParameter(STATE) ?: ""
                    viewModel.exchangeCode(code, state)
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuClose -> {
                showAlertDialog()
                return true
            }
        }
        return false
    }

    private fun showAlertDialog() {
        val newFragment = AlertDialogFragment()
        newFragment.show(supportFragmentManager, AlertDialogFragment::class.java.simpleName)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        dialog.dismissAllowingStateLoss()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismissAllowingStateLoss()
        finish()
    }

    companion object {
        private const val WEB_VIEW_URL_REQUEST_EXTRA = "WEB_VIEW_URL_REQUEST_EXTRA"
        private const val ENVIRONMENT_INFO_EXTRA = "ENVIRONMENT_INFO_EXTRA"
        private const val PAGE_EXTRA = "PAGE_EXTRA"
        private const val CODE = "code"
        private const val STATE = "state"

        fun launch(
            activity: Activity,
            webViewUrlRequest: String,
            environmentInfo: EnvironmentInfo,
            page: InitialPage,
            launcherForResult: ActivityResultLauncher<Intent>
        ) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(WEB_VIEW_URL_REQUEST_EXTRA, webViewUrlRequest)
            intent.putExtra(ENVIRONMENT_INFO_EXTRA, environmentInfo)
            intent.putExtra(PAGE_EXTRA, page.name)
            launcherForResult.launch(intent)
        }
    }
}
