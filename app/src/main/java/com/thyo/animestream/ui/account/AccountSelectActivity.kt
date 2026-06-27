package com.thyo.animestream.ui.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.thyo.animestream.CommonActivity
import com.thyo.animestream.CommonActivity.loadThemes
import com.thyo.animestream.CommonActivity.showToast
import com.thyo.animestream.MainActivity
import com.thyo.animestream.R
import com.thyo.animestream.databinding.ActivityAccountSelectBinding
import com.thyo.animestream.mvvm.observe
import com.thyo.animestream.ui.AutofitRecyclerView
import com.thyo.animestream.ui.account.AccountAdapter.Companion.VIEW_TYPE_EDIT_ACCOUNT
import com.thyo.animestream.ui.account.AccountAdapter.Companion.VIEW_TYPE_SELECT_ACCOUNT
import com.thyo.animestream.ui.settings.Globals.EMULATOR
import com.thyo.animestream.ui.settings.Globals.PHONE
import com.thyo.animestream.ui.settings.Globals.TV
import com.thyo.animestream.ui.settings.Globals.isLayout
import com.thyo.animestream.utils.BiometricAuthenticator
import com.thyo.animestream.utils.BiometricAuthenticator.BiometricCallback
import com.thyo.animestream.utils.BiometricAuthenticator.biometricPrompt
import com.thyo.animestream.utils.BiometricAuthenticator.deviceHasPasswordPinLock
import com.thyo.animestream.utils.BiometricAuthenticator.isAuthEnabled
import com.thyo.animestream.utils.BiometricAuthenticator.promptInfo
import com.thyo.animestream.utils.BiometricAuthenticator.startBiometricAuthentication
import com.thyo.animestream.utils.DataStoreHelper.accounts
import com.thyo.animestream.utils.DataStoreHelper.selectedKeyIndex
import com.thyo.animestream.utils.DataStoreHelper.setAccount
import com.thyo.animestream.utils.UIHelper.colorFromAttribute
import com.thyo.animestream.utils.UIHelper.openActivity

class AccountSelectActivity : FragmentActivity(), BiometricCallback {

    val accountViewModel: AccountViewModel by viewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadThemes(this)

        @Suppress("DEPRECATION")
        window.navigationBarColor = colorFromAttribute(R.attr.primaryBlackBackground)

        // Are we editing and coming from MainActivity?
        val isEditingFromMainActivity = intent.getBooleanExtra(
            "isEditingFromMainActivity",
            false
        )

        val settingsManager = PreferenceManager.getDefaultSharedPreferences(this)
        val skipStartup = settingsManager.getBoolean(getString(R.string.skip_startup_account_select_key), false
        ) || accounts.count() <= 1

        fun askBiometricAuth() {

            if (isLayout(PHONE) && isAuthEnabled(this)) {
                if (deviceHasPasswordPinLock(this)) {
                    startBiometricAuthentication(
                        this,
                        R.string.biometric_authentication_title,
                        false
                    )

                    promptInfo?.let { prompt ->
                        biometricPrompt?.authenticate(prompt)
                    }
                }
            }
        }

        observe(accountViewModel.isAllowedLogin) { isAllowedLogin ->
            if (isAllowedLogin) {
                // We are allowed to continue to MainActivity
                navigateToMainActivity()
            }
        }

        // Don't show account selection if there is only
        // one account that exists
        if (!isEditingFromMainActivity && skipStartup) {
            val currentAccount = accounts.firstOrNull { it.keyIndex == selectedKeyIndex }
            if (currentAccount?.lockPin != null) {
                CommonActivity.init(this)
                accountViewModel.handleAccountSelect(currentAccount, this, true)
            } else {
                if (accounts.count() > 1) {
                    showToast(this, getString(
                        R.string.logged_account,
                        currentAccount?.name
                    ))
                }

                navigateToMainActivity()
            }

            return
        }

        CommonActivity.init(this)

        val binding = ActivityAccountSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView: AutofitRecyclerView = binding.accountRecyclerView

        observe(accountViewModel.accounts) { liveAccounts ->
            val adapter = AccountAdapter(
                liveAccounts,
                // Handle the selected account
                accountSelectCallback = {
                    accountViewModel.handleAccountSelect(it, this)
                },
                accountCreateCallback = { accountViewModel.handleAccountUpdate(it, this) },
                accountEditCallback = {
                    accountViewModel.handleAccountUpdate(it, this)

                    // We came from MainActivity, return there
                    // and switch to the edited account
                    if (isEditingFromMainActivity) {
                        setAccount(it)
                        navigateToMainActivity()
                    }
                },
                accountDeleteCallback = { accountViewModel.handleAccountDelete(it,this) }
            )

            recyclerView.adapter = adapter

            if (isLayout(TV or EMULATOR)) {
                binding.editAccountButton.setBackgroundResource(
                    R.drawable.player_button_tv_attr_no_bg
                )
            }

            observe(accountViewModel.selectedKeyIndex) { selectedKeyIndex ->
                // Scroll to current account (which is focused by default)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                layoutManager.scrollToPositionWithOffset(selectedKeyIndex, 0)
            }

            observe(accountViewModel.isEditing) { isEditing ->
                if (isEditing) {
                    binding.editAccountButton.setImageResource(R.drawable.ic_baseline_close_24)
                    binding.title.setText(R.string.manage_accounts)
                    adapter.viewType = VIEW_TYPE_EDIT_ACCOUNT
                } else {
                    binding.editAccountButton.setImageResource(R.drawable.ic_baseline_edit_24)
                    binding.title.setText(R.string.select_an_account)
                    adapter.viewType = VIEW_TYPE_SELECT_ACCOUNT
                }

                adapter.notifyDataSetChanged()
            }

            if (isEditingFromMainActivity) {
                accountViewModel.setIsEditing(true)
            }

            binding.editAccountButton.setOnClickListener {
                // We came from MainActivity, return there
                // and resume its state
                if (isEditingFromMainActivity) {
                    navigateToMainActivity()
                    return@setOnClickListener
                }

                accountViewModel.toggleIsEditing()
            }

            if (isLayout(TV or EMULATOR)) {
                recyclerView.spanCount = if (liveAccounts.count() + 1 <= 6) {
                    liveAccounts.count() + 1
                } else 6
            }
        }

        askBiometricAuth()
    }

    private fun navigateToMainActivity() {
        openActivity(MainActivity::class.java)
        finish() // Finish the account selection activity
    }

    override fun onAuthenticationSuccess() {
       Log.i(BiometricAuthenticator.TAG,"Authentication successful in AccountSelectActivity")
    }

    override fun onAuthenticationError() {
        finish()
    }
}