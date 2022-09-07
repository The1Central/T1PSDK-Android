package th.co.the1.t1p.demo

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import th.co.the1.t1p.demo.databinding.ActivityDemoBinding
import th.co.the1.t1p.demo.dialog.AlertDialogFragment
import th.co.the1.t1p.sdk.T1PSDK
import th.co.the1.t1p.sdk.T1PSdkAccessTokenCallback
import th.co.the1.t1p.sdk.T1PSdkCallback
import th.co.the1.t1p.sdk.domain.AuthToken
import th.co.the1.t1p.sdk.domain.InitialPage
import th.co.the1.t1p.sdk.domain.T1PLanguage
import th.co.the1.t1p.sdk.domain.T1PEnvironment
import th.co.the1.t1p.sdk.utils.T1PSdkFailedEnum

class DemoActivity : AppCompatActivity(),
    AlertDialogFragment.NoticeDialogListener {

    private val binding: ActivityDemoBinding by lazy { ActivityDemoBinding.inflate(layoutInflater) }

    private lateinit var t1PSDK: T1PSDK

    private var t1pEnvironment: T1PEnvironment = T1PEnvironment.SIT

    private var t1PLanguage: T1PLanguage = T1PLanguage.TH

    private lateinit var alertDialogFragment: AlertDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnSetup.setOnClickListener {
            setupClick()
        }

        featureSignIn()
        featureSignUp()
        featureRecovery()
        featureSignOut()
        featureGetAccessToken()
        binding.btnReload.setOnClickListener {
            reloadClick()
        }

        binding.radioGroupEnvironment.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioSit -> {
                    t1pEnvironment = T1PEnvironment.SIT
                }
                R.id.radioUat -> {
                    t1pEnvironment = T1PEnvironment.UAT
                }
                R.id.radioPvt -> {
                    t1pEnvironment = T1PEnvironment.PVT
                }
                R.id.radioProd -> {
                    t1pEnvironment = T1PEnvironment.PRODUCTION
                }
            }
        }

        binding.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioThai -> {
                    t1PLanguage = T1PLanguage.TH
                }
                R.id.radioEnglish -> {
                    t1PLanguage = T1PLanguage.EN
                }
            }
        }
    }

    private fun setupClick() {
        t1PSDK = T1PSDK(
            activity = this,
            environment = t1pEnvironment,
            redirectUrl = getRedirectUrl(),
            clientId = getClientId(),
            language = t1PLanguage
        )
        enableFeature()
        binding.btnReload.isEnabled = true
        binding.btnSetup.isEnabled = false
    }

    private fun reloadClick() {
        disableFeature()
        binding.btnReload.isEnabled = false
        binding.btnSetup.isEnabled = true
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        dialog.dismissAllowingStateLoss()
    }

    private fun showDialogWithMessage(message: String) {
        alertDialogFragment = AlertDialogFragment.newInstance(message)
        alertDialogFragment.show(
            supportFragmentManager,
            AlertDialogFragment::class.java.simpleName
        )
    }

    private val signInActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getParcelableExtra<AuthToken>(InitialPage.SIGN_IN.value)?.let {
                    showDialogWithMessage(it.toString())
                }
            }
        }

    private fun featureSignIn() {
        binding.btnSignIn.setOnClickListener {
            t1PSDK.signIn(onActivityResult = signInActivityResult)
        }
    }

    private val signUpActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getParcelableExtra<AuthToken>(InitialPage.SIGN_UP.value)?.let {
                    showDialogWithMessage(it.toString())
                }
            }
        }

    private fun featureSignUp() {
        binding.btnSignUp.setOnClickListener {
            t1PSDK.signUp(onActivityResult = signUpActivityResult)
        }
    }

    private val recoveryActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getParcelableExtra<AuthToken>(InitialPage.RECOVERY.value)?.let {
                    showDialogWithMessage(it.toString())
                }
            }
        }

    private fun featureRecovery() {
        binding.btnRecovery.setOnClickListener {
            t1PSDK.recovery(onActivityResult = recoveryActivityResult)
        }
    }

    private fun featureSignOut() {
        binding.btnSignOut.setOnClickListener {
            t1PSDK.signOut(object : T1PSdkCallback {
                override fun onSuccess() {
                    showDialogWithMessage("Sign-out")
                    reloadClick()
                }

                override fun onFailure() {
                    showDialogWithMessage("Please sign-in")
                }
            })
        }
    }

    private fun featureGetAccessToken() {
        binding.btnAccessToken.setOnClickListener {
            t1PSDK.accessToken(object : T1PSdkAccessTokenCallback {
                override fun onSuccess(authToken: AuthToken) {
                    showDialogWithMessage(message = "$authToken")
                }

                override fun onFailure(t1pSdkFailed: T1PSdkFailedEnum) {
                    showDialogWithMessage(message = "Please sign-in")
                }
            })
        }
    }

    private fun getRedirectUrl(): String {
        return binding.editTextRedirectUrl.text.toString()
    }

    private fun getClientId(): String {
        return binding.editTextClientId.text.toString()
    }

    private fun enableFeature() {
        with(binding) {
            btnSignIn.isEnabled = true
            btnSignUp.isEnabled = true
            btnRecovery.isEnabled = true
            btnSignOut.isEnabled = true
            btnAccessToken.isEnabled = true

            setting(enable = false)
        }
    }

    private fun disableFeature() {
        with(binding) {
            btnSignIn.isEnabled = false
            btnSignUp.isEnabled = false
            btnRecovery.isEnabled = false
            btnSignOut.isEnabled = false
            btnAccessToken.isEnabled = false

            setting(enable = true)
        }
    }

    private fun setting(enable: Boolean) {
        with(binding) {
            radioSit.isClickable = enable
            radioUat.isClickable = enable
            radioPvt.isClickable = enable
            radioProd.isClickable = enable

            radioEnglish.isClickable = enable
            radioThai.isClickable = enable

            editTextClientId.isEnabled = enable
            editTextRedirectUrl.isEnabled = enable
        }
    }
}
