package com.thyo.animestream

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.Session
import com.google.android.gms.cast.framework.SessionManager
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.snackbar.Snackbar
import com.google.common.collect.Comparators.min
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.thyo.animestream.APIHolder.allProviders
import com.thyo.animestream.APIHolder.apis
import com.thyo.animestream.APIHolder.initAll
import com.thyo.animestream.AcraApplication.Companion.getKey
import com.thyo.animestream.AcraApplication.Companion.removeKey
import com.thyo.animestream.AcraApplication.Companion.setKey
import com.thyo.animestream.CommonActivity.loadThemes
import com.thyo.animestream.CommonActivity.onColorSelectedEvent
import com.thyo.animestream.CommonActivity.onDialogDismissedEvent
import com.thyo.animestream.CommonActivity.onUserLeaveHint
import com.thyo.animestream.CommonActivity.screenHeight
import com.thyo.animestream.CommonActivity.setActivityInstance
import com.thyo.animestream.CommonActivity.showToast
import com.thyo.animestream.CommonActivity.updateLocale
import com.thyo.animestream.CommonActivity.updateTheme
import com.thyo.animestream.actions.temp.fcast.FcastManager
import com.thyo.animestream.databinding.ActivityMainBinding
import com.thyo.animestream.databinding.ActivityMainTvBinding
import com.thyo.animestream.databinding.BottomResultviewPreviewBinding
import com.thyo.animestream.mvvm.Resource
import com.thyo.animestream.mvvm.logError
import com.thyo.animestream.mvvm.safe
import com.thyo.animestream.mvvm.observe
import com.thyo.animestream.mvvm.observeNullable
import com.thyo.animestream.network.initClient
import com.thyo.animestream.plugins.PluginManager
import com.thyo.animestream.plugins.PluginManager.___DO_NOT_CALL_FROM_A_PLUGIN_loadAllOnlinePlugins
import com.thyo.animestream.plugins.PluginManager.loadSinglePlugin
import com.thyo.animestream.receivers.VideoDownloadRestartReceiver
import com.thyo.animestream.services.SubscriptionWorkManager
import com.thyo.animestream.syncproviders.AccountManager
import com.thyo.animestream.syncproviders.AccountManager.Companion.APP_STRING
import com.thyo.animestream.syncproviders.AccountManager.Companion.APP_STRING_PLAYER
import com.thyo.animestream.syncproviders.AccountManager.Companion.APP_STRING_REPO
import com.thyo.animestream.syncproviders.AccountManager.Companion.APP_STRING_RESUME_WATCHING
import com.thyo.animestream.syncproviders.AccountManager.Companion.APP_STRING_SEARCH
import com.thyo.animestream.syncproviders.AccountManager.Companion.APP_STRING_SHARE
import com.thyo.animestream.syncproviders.AccountManager.Companion.localListApi
import com.thyo.animestream.syncproviders.SyncAPI
import com.thyo.animestream.ui.APIRepository
import com.thyo.animestream.ui.SyncWatchType
import com.thyo.animestream.ui.WatchType
import com.thyo.animestream.ui.account.AccountHelper.showAccountSelectLinear
import com.thyo.animestream.ui.download.DOWNLOAD_NAVIGATE_TO
import com.thyo.animestream.ui.home.HomeViewModel
import com.thyo.animestream.ui.library.LibraryViewModel
import com.thyo.animestream.ui.player.BasicLink
import com.thyo.animestream.ui.player.GeneratorPlayer
import com.thyo.animestream.ui.player.LinkGenerator
import com.thyo.animestream.ui.result.LinearListLayout
import com.thyo.animestream.ui.result.ResultViewModel2
import com.thyo.animestream.ui.result.START_ACTION_RESUME_LATEST
import com.thyo.animestream.ui.result.SyncViewModel
import com.thyo.animestream.ui.search.SearchFragment
import com.thyo.animestream.ui.search.SearchResultBuilder
import com.thyo.animestream.ui.settings.Globals.EMULATOR
import com.thyo.animestream.ui.settings.Globals.PHONE
import com.thyo.animestream.ui.settings.Globals.TV
import com.thyo.animestream.ui.settings.Globals.isLayout
import com.thyo.animestream.ui.settings.Globals.updateTv
import com.thyo.animestream.ui.settings.SettingsGeneral
import com.thyo.animestream.ui.setup.HAS_DONE_SETUP_KEY
import com.thyo.animestream.ui.setup.SetupFragmentExtensions
import com.thyo.animestream.utils.ApkInstaller
import com.thyo.animestream.utils.AppContextUtils.getApiDubstatusSettings
import com.thyo.animestream.utils.AppContextUtils.html
import com.thyo.animestream.utils.AppContextUtils.isCastApiAvailable
import com.thyo.animestream.utils.AppContextUtils.isLtr
import com.thyo.animestream.utils.AppContextUtils.isNetworkAvailable
import com.thyo.animestream.utils.AppContextUtils.isRtl
import com.thyo.animestream.utils.AppContextUtils.loadCache
import com.thyo.animestream.utils.AppContextUtils.loadRepository
import com.thyo.animestream.utils.AppContextUtils.loadResult
import com.thyo.animestream.utils.AppContextUtils.loadSearchResult
import com.thyo.animestream.utils.AppContextUtils.setDefaultFocus
import com.thyo.animestream.utils.AppContextUtils.updateHasTrailers
import com.thyo.animestream.utils.BackPressedCallbackHelper.attachBackPressedCallback
import com.thyo.animestream.utils.BackPressedCallbackHelper.detachBackPressedCallback
import com.thyo.animestream.utils.BackupUtils.backup
import com.thyo.animestream.utils.BackupUtils.setUpBackup
import com.thyo.animestream.utils.BiometricAuthenticator.BiometricCallback
import com.thyo.animestream.utils.BiometricAuthenticator.biometricPrompt
import com.thyo.animestream.utils.BiometricAuthenticator.deviceHasPasswordPinLock
import com.thyo.animestream.utils.BiometricAuthenticator.isAuthEnabled
import com.thyo.animestream.utils.BiometricAuthenticator.promptInfo
import com.thyo.animestream.utils.BiometricAuthenticator.startBiometricAuthentication
import com.thyo.animestream.utils.Coroutines.ioSafe
import com.thyo.animestream.utils.Coroutines.main
import com.thyo.animestream.utils.DataStore.getKey
import com.thyo.animestream.utils.DataStore.setKey
import com.thyo.animestream.utils.DataStoreHelper
import com.thyo.animestream.utils.DataStoreHelper.accounts
import com.thyo.animestream.utils.DataStoreHelper.migrateResumeWatching
import com.thyo.animestream.utils.Event
import com.thyo.animestream.utils.ImageLoader.loadImage
import com.thyo.animestream.utils.InAppUpdater.Companion.runAutoUpdate
import com.thyo.animestream.utils.SingleSelectionHelper.showBottomDialog
import com.thyo.animestream.utils.SnackbarHelper.showSnackbar
import com.thyo.animestream.utils.UIHelper.changeStatusBarState
import com.thyo.animestream.utils.UIHelper.checkWrite
import com.thyo.animestream.utils.UIHelper.colorFromAttribute
import com.thyo.animestream.utils.UIHelper.dismissSafe
import com.thyo.animestream.utils.UIHelper.getResourceColor
import com.thyo.animestream.utils.UIHelper.hideKeyboard
import com.thyo.animestream.utils.UIHelper.navigate
import com.thyo.animestream.utils.UIHelper.requestRW
import com.thyo.animestream.utils.UIHelper.toPx
import com.thyo.animestream.utils.USER_PROVIDER_API
import com.thyo.animestream.utils.USER_SELECTED_HOMEPAGE_API
import com.thyo.animestream.utils.setText
import com.thyo.animestream.utils.setTextHtml
import com.thyo.animestream.utils.txt
import com.lagradost.safefile.SafeFile
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.lang.ref.WeakReference
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.Charset
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.system.exitProcess
import androidx.core.net.toUri

class MainActivity : AppCompatActivity(), ColorPickerDialogListener, BiometricCallback {
    companion object {
        var activityResultLauncher: ActivityResultLauncher<Intent>? = null

        const val TAG = "MAINACT"
        const val ANIMATED_OUTLINE: Boolean = false
        var lastError: String? = null

        private const val FILE_DELETE_KEY = "FILES_TO_DELETE_KEY"
        const val API_NAME_EXTRA_KEY = "API_NAME_EXTRA_KEY"

        private var filesToDelete: Set<String>
            get() = getKey<Set<String>>(FILE_DELETE_KEY) ?: setOf()
            private set(value) = setKey(FILE_DELETE_KEY, value)

        fun deleteFileOnExit(file: File) {
            filesToDelete = filesToDelete + file.path
        }

        var nextSearchQuery: String? = null

        val afterPluginsLoadedEvent = Event<Boolean>()
        val mainPluginsLoadedEvent = Event<Boolean>() 
        val afterRepositoryLoadedEvent = Event<Boolean>()
        val bookmarksUpdatedEvent = Event<Boolean>()
        val reloadHomeEvent = Event<Boolean>()
        val reloadLibraryEvent = Event<Boolean>()
        val reloadAccountEvent = Event<Boolean>()

        @Suppress("DEPRECATION_ERROR")
        fun handleAppIntentUrl(
            activity: FragmentActivity?,
            str: String?,
            isWebview: Boolean,
            extraArgs: Bundle? = null
        ): Boolean =
            with(activity) {
                fun safeURI(uri: String) = safe { URI(uri) }

                if (str != null && this != null) {
                    if (str.startsWith("https://as.repo")) {
                        val realUrl = "https://" + str.substringAfter("?")
                        println("Repository url: $realUrl")
                        loadRepository(realUrl)
                        return true
                    } else if (str.contains(APP_STRING)) {
                        for (api in AccountManager.allApis) {
                            if (api.isValidRedirectUrl(str)) {
                                ioSafe {
                                    Log.i(TAG, "handleAppIntent $str")
                                    try {
                                        val isSuccessful = api.login(str)
                                        if (isSuccessful) {
                                            Log.i(TAG, "authenticated ${api.name}")
                                        } else {
                                            Log.i(TAG, "failed to authenticate ${api.name}")
                                        }
                                        showToast(
                                            if (isSuccessful) {
                                                txt(R.string.authenticated_user, api.name)
                                            } else {
                                                txt(R.string.authenticated_user_fail, api.name)
                                            }
                                        )
                                    } catch (t: Throwable) {
                                        logError(t)
                                        showToast(
                                            txt(R.string.authenticated_user_fail, api.name)
                                        )
                                    }
                                }
                                return true
                            }
                        }
                        if (str == "$APP_STRING:") {
                            ioSafe {
                                PluginManager.___DO_NOT_CALL_FROM_A_PLUGIN_hotReloadAllLocalPlugins(
                                    activity
                                )
                            }
                        }
                    } else if (safeURI(str)?.scheme == APP_STRING_REPO) {
                        val url = str.replaceFirst(APP_STRING_REPO, "https")
                        loadRepository(url)
                        return true
                    } else if (safeURI(str)?.scheme == APP_STRING_SEARCH) {
                        val query = str.substringAfter("$APP_STRING_SEARCH://")
                        nextSearchQuery =
                            try {
                                URLDecoder.decode(query, "UTF-8")
                            } catch (t: Throwable) {
                                logError(t)
                                query
                            }
                        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.selectedItemId =
                            R.id.navigation_search
                        activity?.findViewById<NavigationRailView>(R.id.nav_rail_view)?.selectedItemId =
                            R.id.navigation_search
                    } else if (safeURI(str)?.scheme == APP_STRING_PLAYER) {
                        val uri = Uri.parse(str)
                        val name = uri.getQueryParameter("name")
                        val url = URLDecoder.decode(uri.authority, "UTF-8")

                        navigate(
                            R.id.global_to_navigation_player,
                            GeneratorPlayer.newInstance(
                                LinkGenerator(
                                    listOf(BasicLink(url, name)),
                                    extract = true,
                                )
                            )
                        )
                    } else if (safeURI(str)?.scheme == APP_STRING_RESUME_WATCHING) {
                        val id =
                            str.substringAfter("$APP_STRING_RESUME_WATCHING://").toIntOrNull()
                                ?: return false
                        ioSafe {
                            val resumeWatchingCard =
                                HomeViewModel.getResumeWatching()?.firstOrNull { it.id == id }
                                    ?: return@ioSafe
                            activity.loadSearchResult(
                                resumeWatchingCard,
                                START_ACTION_RESUME_LATEST
                            )
                        }
                    } else if(str.startsWith(APP_STRING_SHARE)){
                        try{
                            val data = str.substringAfter("$APP_STRING_SHARE:")
                            val parts = data.split("?",limit=2)
                            loadResult(String(base64DecodeArray(parts[1]), Charsets.UTF_8),String(base64DecodeArray(parts[0]), Charsets.UTF_8),"")
                            return true
                        }catch (e: Exception) {
                            showToast("Invalid Uri",Toast.LENGTH_SHORT)
                            return false
                        }
                    }else if (!isWebview) {
                        if (str.startsWith(DOWNLOAD_NAVIGATE_TO)) {
                            this.navigate(R.id.navigation_downloads)
                            return true
                        } else {
                            val apiName = extraArgs?.getString(API_NAME_EXTRA_KEY)
                                ?.takeIf { it.isNotBlank() }
                            
                            if (apiName != null) {
                                loadResult(str, apiName, "")
                                return true
                            }

                            synchronized(apis) {
                                for (api in apis) {
                                    if (str.startsWith(api.mainUrl)) {
                                        loadResult(str, api.name, "")
                                        return true
                                    }
                                }
                            }
                        }
                    }
                }
                return false
            }
    }

    var lastPopup: SearchResponse? = null
    fun loadPopup(result: SearchResponse, load: Boolean = true) {
        lastPopup = result
        val syncName = syncViewModel.syncName(result.apiName)

        if (result is SyncAPI.LibraryItem && syncName != null) {
            isLocalList = false
            syncViewModel.setSync(syncName, result.syncId)
            syncViewModel.updateMetaAndUser()
        } else {
            isLocalList = true
            syncViewModel.clear()
        }

        if (load) {
            viewModel.load(
                this, result.url, result.apiName, false, if (getApiDubstatusSettings()
                        .contains(DubStatus.Dubbed)
                ) DubStatus.Dubbed else DubStatus.Subbed, null
            )
        } else {
            viewModel.loadSmall(result)
        }
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        onColorSelectedEvent.invoke(Pair(dialogId, color))
    }

    override fun onDialogDismissed(dialogId: Int) {
        onDialogDismissedEvent.invoke(dialogId)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateLocale() 
        updateTheme(this) 

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.currentDestination?.let { updateNavBar(it) }
    }

    private fun updateNavBar(destination: NavDestination) {
        this.hideKeyboard()

        binding?.castMiniControllerHolder?.isVisible =
            !listOf(
                R.id.navigation_results_phone,
                R.id.navigation_results_tv,
                R.id.navigation_player
            ).contains(destination.id)

        val isNavVisible = listOf(
            R.id.navigation_home,
            R.id.navigation_search,
            R.id.navigation_library,
            R.id.navigation_downloads,
            R.id.navigation_settings,
            R.id.navigation_download_child,
            R.id.navigation_subtitles,
            R.id.navigation_chrome_subtitles,
            R.id.navigation_settings_player,
            R.id.navigation_settings_updates,
            R.id.navigation_settings_ui,
            R.id.navigation_settings_account,
            R.id.navigation_settings_providers,
            R.id.navigation_settings_general,
            R.id.navigation_settings_extensions,
            R.id.navigation_settings_plugins,
            R.id.navigation_test_providers,
        ).contains(destination.id)

        val dontPush = listOf(
            R.id.navigation_home,
            R.id.navigation_search,
            R.id.navigation_results_phone,
            R.id.navigation_results_tv,
            R.id.navigation_player,
            R.id.navigation_quick_search,
        ).contains(destination.id)

        binding?.navHostFragment?.apply {
            val params = layoutParams as ConstraintLayout.LayoutParams
            val push =
                if (!dontPush && isLayout(TV or EMULATOR)) resources.getDimensionPixelSize(R.dimen.navbar_width) else 0

            if (!this.isLtr()) {
                params.setMargins(
                    params.leftMargin,
                    params.topMargin,
                    push,
                    params.bottomMargin
                )
            } else {
                params.setMargins(
                    push,
                    params.topMargin,
                    params.rightMargin,
                    params.bottomMargin
                )
            }

            layoutParams = params
        }

        val landscape = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                true
            }

            Configuration.ORIENTATION_PORTRAIT -> {
                isLayout(TV or EMULATOR)
            }

            else -> {
                false
            }
        }

        binding?.apply {
            navRailView.isVisible = isNavVisible && landscape
            navView.isVisible = isNavVisible && !landscape

            when (destination.id) {
                in listOf(R.id.navigation_downloads, R.id.navigation_download_child) -> {
                    navRailView.menu.findItem(R.id.navigation_downloads).isChecked = true
                    navView.menu.findItem(R.id.navigation_downloads).isChecked = true
                }

                in listOf(
                    R.id.navigation_settings,
                    R.id.navigation_subtitles,
                    R.id.navigation_chrome_subtitles,
                    R.id.navigation_settings_player,
                    R.id.navigation_settings_updates,
                    R.id.navigation_settings_ui,
                    R.id.navigation_settings_account,
                    R.id.navigation_settings_providers,
                    R.id.navigation_settings_general,
                    R.id.navigation_settings_extensions,
                    R.id.navigation_settings_plugins,
                    R.id.navigation_test_providers
                ) -> {
                    navRailView.menu.findItem(R.id.navigation_settings).isChecked = true
                    navView.menu.findItem(R.id.navigation_settings).isChecked = true
                }
            }
        }
    }

    var mSessionManager: SessionManager? = null
    private val mSessionManagerListener: SessionManagerListener<Session> by lazy { SessionManagerListenerImpl() }

    private inner class SessionManagerListenerImpl : SessionManagerListener<Session> {
        override fun onSessionStarting(session: Session) {}
        override fun onSessionStarted(session: Session, sessionId: String) { invalidateOptionsMenu() }
        override fun onSessionStartFailed(session: Session, i: Int) {}
        override fun onSessionEnding(session: Session) {}
        override fun onSessionResumed(session: Session, wasSuspended: Boolean) { invalidateOptionsMenu() }
        override fun onSessionResumeFailed(session: Session, i: Int) {}
        override fun onSessionSuspended(session: Session, i: Int) {}
        override fun onSessionEnded(session: Session, error: Int) {}
        override fun onSessionResuming(session: Session, s: String) {}
    }

    override fun onResume() {
        super.onResume()
        afterPluginsLoadedEvent += ::onAllPluginsLoaded
        setActivityInstance(this)
        try {
            if (isCastApiAvailable()) {
                mSessionManager?.addSessionManagerListener(mSessionManagerListener)
            }
        } catch (e: Exception) {
            logError(e)
        }
    }

    override fun onPause() {
        super.onPause()

        if (ApkInstaller.delayedInstaller?.startInstallation() == true) {
            Toast.makeText(this, R.string.update_started, Toast.LENGTH_LONG).show()
        }
        try {
            if (isCastApiAvailable()) {
                mSessionManager?.removeSessionManagerListener(mSessionManagerListener)
            }
        } catch (e: Exception) {
            logError(e)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean =
        CommonActivity.dispatchKeyEvent(this, event) ?: super.dispatchKeyEvent(event)

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean =
        CommonActivity.onKeyDown(this, keyCode, event) ?: super.onKeyDown(keyCode, event)


    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        onUserLeaveHint(this)
    }

    @SuppressLint("ApplySharedPref") // commit since the op needs to be synchronous
    private fun showConfirmExitDialog(settingsManager: SharedPreferences) {
        val confirmBeforeExit = settingsManager.getInt(getString(R.string.confirm_exit_key), -1)

        if (confirmBeforeExit == 1 || (confirmBeforeExit == -1 && isLayout(PHONE))) {
            if (isLayout(TV)) exitProcess(0) else finish()
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.confirm_exit_dialog, null)
        val dontShowAgainCheck: CheckBox = dialogView.findViewById(R.id.checkboxDontShowAgain)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
            .setTitle(R.string.confirm_exit_dialog)
            .setNegativeButton(R.string.no) { _, _ -> /*NO-OP*/ }
            .setPositiveButton(R.string.yes) { _, _ ->
                if (dontShowAgainCheck.isChecked) {
                    settingsManager.edit().putInt(getString(R.string.confirm_exit_key), 1).commit()
                }
                if (isLayout(TV)) exitProcess(0) else finish()
            }

        builder.show().setDefaultFocus()
    }

    override fun onDestroy() {
        filesToDelete.forEach { path ->
            val result = File(path).deleteRecursively()
            if (result) {
                Log.d(TAG, "Deleted temporary file: $path")
            } else {
                Log.d(TAG, "Failed to delete temporary file: $path")
            }
        }
        filesToDelete = setOf()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restart_service"
        broadcastIntent.setClass(this, VideoDownloadRestartReceiver::class.java)
        this.sendBroadcast(broadcastIntent)
        afterPluginsLoadedEvent -= ::onAllPluginsLoaded
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        handleAppIntent(intent)
        super.onNewIntent(intent)
    }

    private fun handleAppIntent(intent: Intent?) {
        if (intent == null) return
        val str = intent.dataString
        loadCache()
        handleAppIntentUrl(this, str, false, intent.extras)
    }

    private fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
        hierarchy.any { it.id == destId }

    private var lastNavTime = 0L
    private fun onNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastNavTime < 400) return false
        lastNavTime = currentTime

        val destinationId = item.itemId

        if (navController.currentDestination?.id == destinationId) return false

        val targetView = when (destinationId) {
            R.id.navigation_search -> R.id.main_search
            R.id.navigation_library -> R.id.main_search
            R.id.navigation_downloads -> R.id.download_appbar
            else -> null
        }
        if (targetView != null && isLayout(TV or EMULATOR)) {
            val fromView = binding?.navRailView
            if (fromView != null) {
                fromView.nextFocusRightId = targetView

                for (focusView in arrayOf(
                    R.id.navigation_downloads,
                    R.id.navigation_home,
                    R.id.navigation_search,
                    R.id.navigation_library,
                    R.id.navigation_settings,
                )) {
                    fromView.findViewById<View?>(focusView)?.nextFocusRightId = targetView
                }
            }
        }

        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
            .setEnterAnim(R.anim.enter_anim)
            .setExitAnim(R.anim.exit_anim)
            .setPopEnterAnim(R.anim.pop_enter)
            .setPopExitAnim(R.anim.pop_exit)
        if (item.order and Menu.CATEGORY_SECONDARY == 0) {
            builder.setPopUpTo(
                navController.graph.findStartDestination().id,
                inclusive = false,
                saveState = true
            )
        }
        return try {
            navController.navigate(destinationId, null, builder.build())
            navController.currentDestination?.matchDestination(destinationId) == true
        } catch (e: IllegalArgumentException) {
            Log.e("NavigationError", "Failed to navigate: ${e.message}")
            false
        }
    }

    private val pluginsLock = Mutex()
    private fun onAllPluginsLoaded(success: Boolean = false) {
        ioSafe {
            pluginsLock.withLock {
                synchronized(allProviders) {
                    try {
                        getKey<Array<SettingsGeneral.CustomSite>>(USER_PROVIDER_API)?.let { list ->
                            list.forEach { custom ->
                                allProviders.firstOrNull { it.javaClass.simpleName == custom.parentJavaClass }
                                    ?.let {
                                        allProviders.add(
                                            it.javaClass.getDeclaredConstructor().newInstance()
                                                .apply {
                                                    name = custom.name
                                                    lang = custom.lang
                                                    mainUrl = custom.url.trimEnd('/')
                                                    canBeOverridden = false
                                                })
                                    }
                            }
                        }
                        apis =
                            allProviders.distinctBy { it.lang + it.name + it.mainUrl + it.javaClass.name }
                        APIHolder.apiMap = null
                    } catch (e: Exception) {
                        logError(e)
                    }
                }
            }
        }
    }

    lateinit var viewModel: ResultViewModel2
    lateinit var syncViewModel: SyncViewModel
    private var libraryViewModel: LibraryViewModel? = null

    var isLocalList: Boolean = false
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {

        viewModel = ViewModelProvider(this)[ResultViewModel2::class.java]
        syncViewModel = ViewModelProvider(this)[SyncViewModel::class.java]

        return super.onCreateView(name, context, attrs)
    }

    private fun hidePreviewPopupDialog() {
        bottomPreviewPopup.dismissSafe(this)
        bottomPreviewPopup = null
        bottomPreviewBinding = null
    }

    private var bottomPreviewPopup: Dialog? = null
    private var bottomPreviewBinding: BottomResultviewPreviewBinding? = null
    private fun showPreviewPopupDialog(): BottomResultviewPreviewBinding {
        val ret = (bottomPreviewBinding ?: run {

            val builder: Dialog
            val layout: Int

            if (isLayout(PHONE)) {
                builder =
                    BottomSheetDialog(this)
                layout = R.layout.bottom_resultview_preview
            } else {
                builder =
                    Dialog(this, R.style.DialogHalfFullscreen)
                layout = R.layout.bottom_resultview_preview_tv
                builder.window?.setGravity(Gravity.CENTER_VERTICAL or Gravity.END)
            }

            val root = layoutInflater.inflate(layout, null, false)
            val binding = BottomResultviewPreviewBinding.bind(root)

            bottomPreviewBinding = binding
            builder.setContentView(root)
            builder.setOnDismissListener {
                bottomPreviewPopup = null
                bottomPreviewBinding = null
                viewModel.clear()
            }
            builder.setCanceledOnTouchOutside(true)
            builder.show()
            bottomPreviewPopup = builder
            binding
        })

        return ret
    }

    var binding: ActivityMainBinding? = null

    object TvFocus {
        data class FocusTarget(
            val width: Int,
            val height: Int,
            val x: Float,
            val y: Float,
        ) {
            companion object {
                fun lerp(a: FocusTarget, b: FocusTarget, lerp: Float): FocusTarget {
                    val ilerp = 1 - lerp
                    return FocusTarget(
                        width = (a.width * ilerp + b.width * lerp).toInt(),
                        height = (a.height * ilerp + b.height * lerp).toInt(),
                        x = a.x * ilerp + b.x * lerp,
                        y = a.y * ilerp + b.y * lerp
                    )
                }
            }
        }

        var last: FocusTarget = FocusTarget(0, 0, 0.0f, 0.0f)
        var current: FocusTarget = FocusTarget(0, 0, 0.0f, 0.0f)

        var focusOutline: WeakReference<View> = WeakReference(null)
        var lastFocus: WeakReference<View> = WeakReference(null)
        private val layoutListener: View.OnLayoutChangeListener =
            View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                lastFocus.get()?.apply {
                    updateFocusView(
                        this, same = true
                    )
                    postDelayed({
                        updateFocusView(
                            lastFocus.get(), same = false
                        )
                    }, 300)
                }
            }
        private val attachListener: View.OnAttachStateChangeListener =
            object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    updateFocusView(v)
                }

                override fun onViewDetachedFromWindow(v: View) {
                    focusOutline.get()?.isVisible = false
                }
            }

        private fun setTargetPosition(target: FocusTarget) {
            focusOutline.get()?.apply {
                layoutParams = layoutParams?.apply {
                    width = target.width
                    height = target.height
                }

                translationX = target.x
                translationY = target.y
                bringToFront()
            }
        }

        private var animator: ValueAnimator? = null

        private const val NO_MOVE_LIST: Boolean = false
        private const val LEFTMOST_MOVE_LIST: Boolean = true

        private val reflectedScroll by lazy {
            try {
                RecyclerView::class.java.declaredMethods.firstOrNull {
                    it.name == "scrollStep"
                }?.also { it.isAccessible = true }
            } catch (t: Throwable) {
                null
            }
        }

        @MainThread
        fun updateFocusView(newFocus: View?, same: Boolean = false) {
            val focusOutline = focusOutline.get() ?: return
            val lastView = lastFocus.get()
            val exactlyTheSame = lastView == newFocus && newFocus != null
            if (!exactlyTheSame) {
                lastView?.removeOnLayoutChangeListener(layoutListener)
                lastView?.removeOnAttachStateChangeListener(attachListener)
                (lastView?.parent as? RecyclerView)?.apply {
                    removeOnLayoutChangeListener(layoutListener)
                }
            }

            val wasGone = focusOutline.isGone

            val visible =
                newFocus != null && newFocus.measuredHeight > 0 && newFocus.measuredWidth > 0 && newFocus.isShown && newFocus.tag != "tv_no_focus_tag"
            focusOutline.isVisible = visible

            if (newFocus != null) {
                lastFocus = WeakReference(newFocus)
                val parent = newFocus.parent
                var targetDx = 0
                if (parent is RecyclerView) {
                    val layoutManager = parent.layoutManager
                    if (layoutManager is LinearListLayout && layoutManager.orientation == LinearLayoutManager.HORIZONTAL) {
                        val dx =
                            LinearSnapHelper().calculateDistanceToFinalSnap(layoutManager, newFocus)
                                ?.get(0)

                        if (dx != null) {
                            val rdx = if (LEFTMOST_MOVE_LIST) {
                                val diff =
                                    ((layoutManager.width - layoutManager.paddingStart - newFocus.measuredWidth) / 2) - newFocus.marginStart
                                dx + if (parent.isRtl()) {
                                    -diff
                                } else {
                                    diff
                                }
                            } else {
                                if (dx > 0) dx else 0
                            }

                            if (!NO_MOVE_LIST) {
                                parent.smoothScrollBy(rdx, 0)
                            } else {
                                val smoothScroll = reflectedScroll
                                if (smoothScroll == null) {
                                    parent.smoothScrollBy(rdx, 0)
                                } else {
                                    try {
                                        val out = IntArray(2)
                                        smoothScroll.invoke(parent, rdx, 0, out)
                                        val scrolledX = out[0]
                                        if (abs(scrolledX) <= 0) { 
                                            smoothScroll.invoke(parent, -rdx, 0, out)
                                            parent.smoothScrollBy(scrolledX, 0)
                                            if (NO_MOVE_LIST) targetDx = scrolledX
                                        }
                                    } catch (t: Throwable) {
                                        parent.smoothScrollBy(rdx, 0)
                                    }
                                }
                            }
                        }
                    }
                }

                val out = IntArray(2)
                newFocus.getLocationInWindow(out)
                val (screenX, screenY) = out
                var (x, y) = screenX.toFloat() to screenY.toFloat()
                val (currentX, currentY) = focusOutline.translationX to focusOutline.translationY

                if (!newFocus.isLtr()) {
                    x = x - focusOutline.rootView.width + newFocus.measuredWidth
                }
                x -= targetDx

                if (screenX == 0 && screenY == 0) {
                    focusOutline.isVisible = false
                }
                if (!exactlyTheSame) {
                    (newFocus.parent as? RecyclerView)?.apply {
                        addOnLayoutChangeListener(layoutListener)
                    }
                    newFocus.addOnLayoutChangeListener(layoutListener)
                    newFocus.addOnAttachStateChangeListener(attachListener)
                }
                val start = FocusTarget(
                    x = currentX,
                    y = currentY,
                    width = focusOutline.measuredWidth,
                    height = focusOutline.measuredHeight
                )
                val end = FocusTarget(
                    x = x,
                    y = y,
                    width = newFocus.measuredWidth,
                    height = newFocus.measuredHeight
                )

                val deltaMinX = min(end.width / 2, 60.toPx)
                val deltaMinY = min(end.height / 2, 60.toPx)
                if (start.width == end.width && start.height == end.height && (start.x - end.x).absoluteValue < deltaMinX && (start.y - end.y).absoluteValue < deltaMinY) {
                    animator?.cancel()
                    last = start
                    current = end
                    setTargetPosition(end)
                    return
                }

                if (animator?.isRunning == true) {
                    current = end
                    return
                } else {
                    animator?.cancel()
                }

                last = start
                current = end

                if (wasGone) {
                    setTargetPosition(current)
                    return
                }

                animator = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
                    startDelay = 0
                    duration = 200
                    addUpdateListener { animation ->
                        val animatedValue = animation.animatedValue as Float
                        val target = FocusTarget.lerp(last, current, minOf(animatedValue, 1.0f))
                        setTargetPosition(target)
                    }
                    start()
                }

                if (!same) {
                    newFocus.postDelayed({
                        updateFocusView(lastFocus.get(), same = true)
                    }, 200)
                }
            }
        }
    }

    private fun centerView(view: View?) {
        if (view == null) return
        try {
            Log.v(TAG, "centerView: $view")
            val r = Rect(0, 0, 0, 0)
            view.getDrawingRect(r)
            val x = r.centerX()
            val y = r.centerY()
            val dx = r.width() / 2 
            val dy = screenHeight / 2
            val r2 = Rect(x - dx, y - dy, x + dx, y + dy)
            view.requestRectangleOnScreen(r2, false)
        } catch (_: Throwable) {
        }
    }

    @Suppress("DEPRECATION_ERROR")
    override fun onCreate(savedInstanceState: Bundle?) {
        app.initClient(this)
        val settingsManager = PreferenceManager.getDefaultSharedPreferences(this)

        val errorFile = filesDir.resolve("last_error")
        if (errorFile.exists() && errorFile.isFile) {
            lastError = errorFile.readText(Charset.defaultCharset())
            errorFile.delete()
        } else {
            lastError = null
        }

        val settingsForProvider = SettingsJson()
        settingsForProvider.enableAdult =
            settingsManager.getBoolean(getString(R.string.enable_nsfw_on_providers_key), false)

        MainAPI.settingsForProvider = settingsForProvider

        loadThemes(this)
        updateLocale()
        super.onCreate(savedInstanceState)
        try {
            if (isCastApiAvailable()) {
                CastContext.getSharedInstance(this) { it.run() }
                    .addOnSuccessListener { mSessionManager = it.sessionManager }
            }
        } catch (t: Throwable) {
            logError(t)
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        updateTv()

        safe {
            val appVer = BuildConfig.VERSION_NAME
            val lastAppAutoBackup: String = getKey("VERSION_NAME") ?: ""
            if (appVer != lastAppAutoBackup) {
                setKey("VERSION_NAME", BuildConfig.VERSION_NAME)
                safe {
                    backup(this)
                }
                safe {
                    PluginManager.deleteAllOatFiles(this)
                }
            }
        }

        binding = try {
            if (isLayout(TV or EMULATOR)) {
                val newLocalBinding = ActivityMainTvBinding.inflate(layoutInflater, null, false)
                setContentView(newLocalBinding.root)

                if (isLayout(TV) && ANIMATED_OUTLINE) {
                    TvFocus.focusOutline = WeakReference(newLocalBinding.focusOutline)
                    newLocalBinding.root.viewTreeObserver.addOnScrollChangedListener {
                        TvFocus.updateFocusView(TvFocus.lastFocus.get(), same = true)
                    }
                    newLocalBinding.root.viewTreeObserver.addOnGlobalFocusChangeListener { _, newFocus ->
                        TvFocus.updateFocusView(newFocus)
                    }
                } else {
                    newLocalBinding.focusOutline.isVisible = false
                }

                if (isLayout(TV)) {
                    val exceptionButtons = listOf(
                        R.id.home_preview_play_btt,
                        R.id.home_preview_info_btt,
                        R.id.home_preview_hidden_next_focus,
                        R.id.home_preview_hidden_prev_focus,
                        R.id.result_play_movie_button,
                        R.id.result_play_series_button,
                        R.id.result_resume_series_button,
                        R.id.result_play_trailer_button,
                        R.id.result_bookmark_Button,
                        R.id.result_favorite_Button,
                        R.id.result_subscribe_Button,
                        R.id.result_search_Button,
                        R.id.result_episodes_show_button,
                    )

                    newLocalBinding.root.viewTreeObserver.addOnGlobalFocusChangeListener { _, newFocus ->
                        if (exceptionButtons.contains(newFocus?.id)) return@addOnGlobalFocusChangeListener
                        centerView(newFocus)
                    }
                }

                ActivityMainBinding.bind(newLocalBinding.root) 
            } else {
                val newLocalBinding = ActivityMainBinding.inflate(layoutInflater, null, false)
                setContentView(newLocalBinding.root)
                newLocalBinding
            }
        } catch (t: Throwable) {
            showToast(txt(R.string.unable_to_inflate, t.message ?: ""), Toast.LENGTH_LONG)
            null
        }

        val padding = settingsManager.getInt(getString(R.string.overscan_key), 0).toPx
        binding?.homeRoot?.setPadding(padding, padding, padding, padding)

        changeStatusBarState(isLayout(EMULATOR))

        val noAccounts = settingsManager.getBoolean(
            getString(R.string.skip_startup_account_select_key),
            false
        ) || accounts.count() <= 1

        if (isLayout(PHONE) && isAuthEnabled(this) && noAccounts) {
            if (deviceHasPasswordPinLock(this)) {
                startBiometricAuthentication(this, R.string.biometric_authentication_title, false)

                promptInfo?.let { prompt ->
                    biometricPrompt?.authenticate(prompt)
                }

                binding?.navHostFragment?.isInvisible = true
            }
        }

        if (this.getKey<Boolean>(getString(R.string.jsdelivr_proxy_key)) == null && isNetworkAvailable()) {
            main {
                if (checkGithubConnectivity()) {
                    this.setKey(getString(R.string.jsdelivr_proxy_key), false)
                } else {
                    this.setKey(getString(R.string.jsdelivr_proxy_key), true)
                    showSnackbar(
                        this@MainActivity,
                        R.string.jsdelivr_enabled,
                        Snackbar.LENGTH_LONG,
                        R.string.revert
                    ) { setKey(getString(R.string.jsdelivr_proxy_key), false) }
                }
            }
        }

        ioSafe { SafeFile.check(this@MainActivity) }
        if (PluginManager.checkSafeModeFile()) {
            safe {
                showToast(R.string.safe_mode_file, Toast.LENGTH_LONG)
            }
        } else if (lastError == null) {

            var splashLayout: android.widget.FrameLayout? = null

            main {
                val root = findViewById<android.view.ViewGroup>(android.R.id.content)
                splashLayout = android.widget.FrameLayout(this@MainActivity).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(-1, -1)
                    setBackgroundColor(android.graphics.Color.parseColor("#000000")) 
                    elevation = 999f
                    z = 999f
                    isClickable = true 
                    isFocusable = true
                }

                val videoView = android.widget.VideoView(this@MainActivity).apply {
                    layoutParams = android.widget.FrameLayout.LayoutParams(-1, -1).apply {
                        gravity = android.view.Gravity.CENTER
                    }
                    
                    val videoId = resources.getIdentifier("splash_video", "raw", packageName)
                    if (videoId != 0) {
                        setVideoURI(android.net.Uri.parse("android.resource://$packageName/$videoId"))
                        setOnCompletionListener { 
                        }
                        setOnPreparedListener { mp ->
                            mp.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
                            start()
                        }
                    }
                }
                
                val loadingContainer = android.widget.LinearLayout(this@MainActivity).apply {
                    orientation = android.widget.LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER
                    layoutParams = android.widget.FrameLayout.LayoutParams(-2, -2).apply {
                        gravity = android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
                        bottomMargin = 150 
                    }
                }
                
                val spinner = android.widget.ProgressBar(this@MainActivity, null, android.R.attr.progressBarStyleSmall).apply {
                    indeterminateTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
                }
                
                val loadingText = android.widget.TextView(this@MainActivity).apply {
                    text = "  Memproses data server..."
                    setTextColor(android.graphics.Color.WHITE)
                    textSize = 14f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                
                loadingContainer.addView(spinner)
                loadingContainer.addView(loadingText)
                
                splashLayout?.addView(videoView)
                splashLayout?.addView(loadingContainer)
                root.addView(splashLayout)
            }

            ioSafe {
                val startTime = System.currentTimeMillis()

                try {
                    val myRepoUrl = "https://extension-thyoapp.up.railway.app/repo.json"
                    val currentRepos = com.thyo.animestream.AcraApplication.getKey<Array<com.thyo.animestream.ui.settings.extensions.RepositoryData>>(com.thyo.animestream.ui.settings.extensions.REPOSITORIES_KEY) ?: emptyArray()
                    if (currentRepos.none { it.url == myRepoUrl }) {
                        val newRepos = currentRepos + com.thyo.animestream.ui.settings.extensions.RepositoryData("Repo Anime Thyo", myRepoUrl)
                        com.thyo.animestream.AcraApplication.setKey(com.thyo.animestream.ui.settings.extensions.REPOSITORIES_KEY, newRepos)
                    }

                    var plugins: List<Pair<String, com.thyo.animestream.plugins.SitePlugin>>? = null
                    var attempt = 1
                    val localPlugins = com.thyo.animestream.plugins.PluginManager.getPluginsLocal()

                    while (plugins == null && attempt <= 4) {
                        try {
                            plugins = com.thyo.animestream.plugins.RepositoryManager.getRepoPlugins(myRepoUrl)
                        } catch (e: Exception) { }

                        if (plugins == null || plugins.isEmpty()) {
                            kotlinx.coroutines.delay(3000) 
                            attempt++
                        }
                    }

                    if (plugins != null && plugins.isNotEmpty()) {
                        val missingPlugins = plugins.filter { (_, onlinePlugin) ->
                            localPlugins.none { it.internalName == onlinePlugin.internalName }
                        }
                        
                        missingPlugins.forEach { (repoUrl, plugin) ->
                            try {
                                com.thyo.animestream.plugins.PluginManager.downloadPlugin(
                                    this@MainActivity, plugin.url, plugin.internalName, repoUrl, false 
                                )
                            } catch (e: Exception) { }
                        }
                    }
                } catch (e: Exception) {
                    logError(e)
                }

                val elapsedTime = System.currentTimeMillis() - startTime
                if (elapsedTime < 4500) {
                    kotlinx.coroutines.delay(4500 - elapsedTime)
                }

                DataStoreHelper.currentHomePage?.let { homeApi ->
                    mainPluginsLoadedEvent.invoke(loadSinglePlugin(this@MainActivity, homeApi))
                } ?: run {
                    mainPluginsLoadedEvent.invoke(false)
                }

                @Suppress("DEPRECATION_ERROR")
                com.thyo.animestream.plugins.PluginManager.___DO_NOT_CALL_FROM_A_PLUGIN_updateAllOnlinePluginsAndLoadThem(this@MainActivity)
                @Suppress("DEPRECATION_ERROR")
                com.thyo.animestream.plugins.PluginManager.___DO_NOT_CALL_FROM_A_PLUGIN_loadAllOnlinePlugins(this@MainActivity)
                @Suppress("DEPRECATION_ERROR")
                com.thyo.animestream.plugins.PluginManager.___DO_NOT_CALL_FROM_A_PLUGIN_loadAllLocalPlugins(this@MainActivity, false)

                main {
                    splashLayout?.let { layout ->
                        layout.animate().alpha(0f).setDuration(600).withEndAction {
                            (layout.parent as? android.view.ViewGroup)?.removeView(layout)
                        }.start()
                    }
                }
            }
        } else {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.safe_mode_title)
            builder.setMessage(R.string.safe_mode_description)
            builder.apply {
                setPositiveButton(R.string.safe_mode_crash_info) { _, _ ->
                    val tbBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                    tbBuilder.setTitle(R.string.safe_mode_title)
                    tbBuilder.setMessage(lastError)
                    tbBuilder.show()
                }

                setNegativeButton("Ok") { _, _ -> }
            }
            builder.show().setDefaultFocus()
        }

        fun setUserData(status: Resource<SyncAPI.AbstractSyncStatus>?) {
            if (isLocalList) return
            bottomPreviewBinding?.apply {
                when (status) {
                    is Resource.Success -> {
                        resultviewPreviewBookmark.isEnabled = true
                        resultviewPreviewBookmark.setText(status.value.status.stringRes)
                        resultviewPreviewBookmark.setIconResource(status.value.status.iconRes)
                    }

                    is Resource.Failure -> {
                        resultviewPreviewBookmark.isEnabled = false
                        resultviewPreviewBookmark.setIconResource(R.drawable.ic_baseline_bookmark_border_24)
                        resultviewPreviewBookmark.text = status.errorString
                    }

                    else -> {
                        resultviewPreviewBookmark.isEnabled = false
                        resultviewPreviewBookmark.setIconResource(R.drawable.ic_baseline_bookmark_border_24)
                        resultviewPreviewBookmark.setText(R.string.loading)
                    }
                }
            }
        }

        fun setWatchStatus(state: WatchType?) {
            if (!isLocalList || state == null) return

            bottomPreviewBinding?.resultviewPreviewBookmark?.apply {
                setIconResource(state.iconRes)
                setText(state.stringRes)
            }
        }

        fun setSubscribeStatus(state: Boolean?) {
            bottomPreviewBinding?.resultviewPreviewSubscribe?.apply {
                if (state != null) {
                    val drawable = if (state) {
                        R.drawable.ic_baseline_notifications_active_24
                    } else {
                        R.drawable.baseline_notifications_none_24
                    }
                    setImageResource(drawable)
                }
                isVisible = state != null

                setOnClickListener {
                    viewModel.toggleSubscriptionStatus(context) { newStatus: Boolean? ->
                        if (newStatus == null) return@toggleSubscriptionStatus

                        val message = if (newStatus) {
                            SubscriptionWorkManager.enqueuePeriodicWork(context)
                            R.string.subscription_new
                        } else {
                            R.string.subscription_deleted
                        }

                        val name = (viewModel.page.value as? Resource.Success)?.value?.title
                            ?: txt(R.string.no_data).asStringNull(context) ?: ""
                        showToast(txt(message, name), Toast.LENGTH_SHORT)
                    }
                }
            }
        }

        observe(viewModel.watchStatus, ::setWatchStatus)
        observe(syncViewModel.userData, ::setUserData)
        observeNullable(viewModel.subscribeStatus, ::setSubscribeStatus)

        observeNullable(viewModel.page) { resource ->
            if (resource == null) {
                hidePreviewPopupDialog()
                return@observeNullable
            }
            when (resource) {
                is Resource.Failure -> {
                    showToast(R.string.error)
                    viewModel.clear()
                    hidePreviewPopupDialog()
                }

                is Resource.Loading -> {
                    showPreviewPopupDialog().apply {
                        resultviewPreviewLoading.isVisible = true
                        resultviewPreviewResult.isVisible = false
                        resultviewPreviewLoadingShimmer.startShimmer()
                    }
                }

                is Resource.Success -> {
                    val d = resource.value
                    showPreviewPopupDialog().apply {
                        resultviewPreviewLoading.isVisible = false
                        resultviewPreviewResult.isVisible = true
                        resultviewPreviewLoadingShimmer.stopShimmer()

                        resultviewPreviewTitle.text = d.title

                        resultviewPreviewMetaType.setText(d.typeText)
                        resultviewPreviewMetaYear.setText(d.yearText)
                        resultviewPreviewMetaDuration.setText(d.durationText)
                        resultviewPreviewMetaRating.setText(d.ratingText)

                        resultviewPreviewDescription.setTextHtml(d.plotText)
                        if (isLayout(PHONE)) {
                            resultviewPreviewPoster.loadImage(
                                d.posterImage ?: d.posterBackgroundImage,
                                headers = d.posterHeaders
                            )
                        } else {
                            resultviewPreviewPoster.loadImage(
                                d.posterBackgroundImage ?: d.posterImage,
                                headers = d.posterHeaders
                            )
                        }

                        setUserData(syncViewModel.userData.value)
                        setWatchStatus(viewModel.watchStatus.value)
                        setSubscribeStatus(viewModel.subscribeStatus.value)

                        resultviewPreviewBookmark.setOnClickListener {
                            if (isLocalList) {
                                val value = viewModel.watchStatus.value ?: WatchType.NONE

                                this@MainActivity.showBottomDialog(
                                    WatchType.values().map { getString(it.stringRes) }.toList(),
                                    value.ordinal,
                                    this@MainActivity.getString(R.string.action_add_to_bookmarks),
                                    showApply = false,
                                    {}) {
                                    viewModel.updateWatchStatus(
                                        WatchType.values()[it],
                                        this@MainActivity
                                    )
                                }
                            } else {
                                val value =
                                    (syncViewModel.userData.value as? Resource.Success)?.value?.status
                                        ?: SyncWatchType.NONE

                                this@MainActivity.showBottomDialog(
                                    SyncWatchType.values().map { getString(it.stringRes) }.toList(),
                                    value.ordinal,
                                    this@MainActivity.getString(R.string.action_add_to_bookmarks),
                                    showApply = false,
                                    {}) {
                                    syncViewModel.setStatus(SyncWatchType.values()[it].internalId)
                                    syncViewModel.publishUserData()
                                }
                            }
                        }

                        observeNullable(viewModel.favoriteStatus) observeFavoriteStatus@{ isFavorite ->
                            resultviewPreviewFavorite.isVisible = isFavorite != null
                            if (isFavorite == null) return@observeFavoriteStatus

                            val drawable = if (isFavorite) {
                                R.drawable.ic_baseline_favorite_24
                            } else {
                                R.drawable.ic_baseline_favorite_border_24
                            }

                            resultviewPreviewFavorite.setImageResource(drawable)
                        }

                        resultviewPreviewFavorite.setOnClickListener {
                            viewModel.toggleFavoriteStatus(this@MainActivity) { newStatus: Boolean? ->
                                if (newStatus == null) return@toggleFavoriteStatus

                                val message = if (newStatus) {
                                    R.string.favorite_added
                                } else {
                                    R.string.favorite_removed
                                }

                                val name = (viewModel.page.value as? Resource.Success)?.value?.title
                                    ?: txt(R.string.no_data).asStringNull(this@MainActivity) ?: ""
                                showToast(txt(message, name), Toast.LENGTH_SHORT)
                            }
                        }

                        if (isLayout(PHONE)) 
                            resultviewPreviewDescription.setOnClickListener { view ->
                                view.context?.let { ctx ->
                                    val builder: AlertDialog.Builder =
                                        AlertDialog.Builder(ctx, R.style.AlertDialogCustom)
                                    builder.setMessage(d.plotText.asString(ctx).html())
                                        .setTitle(d.plotHeaderText.asString(ctx))
                                        .show()
                                }
                            }

                        resultviewPreviewMoreInfo.setOnClickListener {
                            viewModel.clear()
                            hidePreviewPopupDialog()
                            lastPopup?.let {
                                loadSearchResult(it)
                            }
                        }
                    }
                }
            }
        }

        ioSafe {
            this@MainActivity.runOnUiThread {
                libraryViewModel =
                    ViewModelProvider(this@MainActivity)[LibraryViewModel::class.java]
                libraryViewModel?.currentApiName?.observe(this@MainActivity) {
                    val syncAPI = libraryViewModel?.currentSyncApi
                    Log.i("SYNC_API", "${syncAPI?.name}, ${syncAPI?.idPrefix}")
                    val icon = if (syncAPI?.idPrefix == localListApi.idPrefix) {
                        R.drawable.library_icon_selector
                    } else {
                        syncAPI?.icon ?: R.drawable.library_icon_selector
                    }

                    binding?.apply {
                        navRailView.menu.findItem(R.id.navigation_library)?.setIcon(icon)
                        navView.menu.findItem(R.id.navigation_library)?.setIcon(icon)
                    }
                }
            }
        }

        SearchResultBuilder.updateCache(this)

        ioSafe {
            initAll()
            apis = synchronized(allProviders) {
                allProviders.distinctBy { it }
            }
        }

        setUpBackup()

        CommonActivity.init(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _: NavController, navDestination: NavDestination, bundle: Bundle? ->
            updateNavBar(navDestination)
            if (navDestination.matchDestination(R.id.navigation_search) && !nextSearchQuery.isNullOrBlank()) {
                bundle?.apply {
                    this.putString(SearchFragment.SEARCH_QUERY, nextSearchQuery)
                }
            }

            if (navDestination.matchDestination(R.id.navigation_home)) {
                attachBackPressedCallback("MainActivity") {
                    showConfirmExitDialog(settingsManager)
                    @Suppress("DEPRECATION")
                    window?.navigationBarColor =
                        colorFromAttribute(R.attr.primaryGrayBackground)
                    updateLocale()
                }
            } else detachBackPressedCallback("MainActivity")
        }

        val rippleColor = ColorStateList.valueOf(getResourceColor(R.attr.colorPrimary, 0.1f))

        binding?.navView?.apply {
            itemRippleColor = rippleColor
            itemActiveIndicatorColor = rippleColor
            setupWithNavController(navController)
            setOnItemSelectedListener { item ->
                onNavDestinationSelected(
                    item,
                    navController
                )
            }

        }

        binding?.navRailView?.apply {
            itemRippleColor = rippleColor
            itemActiveIndicatorColor = rippleColor
            setupWithNavController(navController)
            if (isLayout(TV or EMULATOR)) {
                background?.alpha = 200
            } else {
                background?.alpha = 255
            }

            setOnItemSelectedListener { item ->
                onNavDestinationSelected(
                    item,
                    navController
                )
            }

            fun noFocus(view: View) {
                view.tag = view.context.getString(R.string.tv_no_focus_tag)
                (view as? ViewGroup)?.let {
                    for (child in it.children) {
                        noFocus(child)
                    }
                }
            }

            val navProfileRoot = findViewById<LinearLayout>(R.id.nav_footer_root)

            if (isLayout(TV or EMULATOR)) {
                val navProfilePic = findViewById<ImageView>(R.id.nav_footer_profile_pic)
                val navProfileCard = findViewById<CardView>(R.id.nav_footer_profile_card)

                navProfileCard?.setOnClickListener {
                    showAccountSelectLinear()
                }

                val homeViewModel =
                    ViewModelProvider(this@MainActivity)[HomeViewModel::class.java]

                observe(homeViewModel.currentAccount) { currentAccount ->
                    if (currentAccount != null) {
                        navProfilePic?.loadImage(
                            currentAccount.image
                        )
                        navProfileRoot.isVisible = true
                    } else {
                        navProfileRoot.isGone = true
                    }
                }
            } else {
                navProfileRoot.isGone = true
            }
        }

        for (view in listOf(binding?.navView, binding?.navRailView)) {
            view?.findViewById<View?>(R.id.navigation_home)?.setOnLongClickListener {
                val recycler = binding?.root?.findViewById<RecyclerView?>(R.id.home_master_recycler)
                recycler?.smoothScrollToPosition(0)
                return@setOnLongClickListener recycler != null
            }

            view?.findViewById<View?>(R.id.navigation_library)?.setOnLongClickListener {
                val viewPager = binding?.root?.findViewById<ViewPager2?>(R.id.viewpager)
                    ?: return@setOnLongClickListener false
                try {
                    val children = (viewPager[0] as? RecyclerView)?.children
                        ?: return@setOnLongClickListener false
                    for (child in children) {
                        child.findViewById<RecyclerView?>(R.id.page_recyclerview)
                            ?.smoothScrollToPosition(0)
                    }
                } catch (_: IndexOutOfBoundsException) {
                } catch (t: Throwable) {
                    logError(t)
                }
                return@setOnLongClickListener true
            }

            view?.findViewById<View?>(R.id.navigation_search)?.setOnLongClickListener {
                for (recyclerId in arrayOf(
                    R.id.search_master_recycler,
                    R.id.search_autofit_results,
                    R.id.search_history_recycler
                )) {
                    val recycler = binding?.root?.findViewById<RecyclerView?>(recyclerId)
                        ?: return@setOnLongClickListener false
                    recycler.smoothScrollToPosition(0)
                }
                return@setOnLongClickListener true
            }

            view?.findViewById<View?>(R.id.navigation_downloads)?.setOnLongClickListener {
                val recycler: RecyclerView? = binding?.root?.findViewById(R.id.download_list)
                    ?: binding?.root?.findViewById(R.id.download_child_list)
                recycler?.smoothScrollToPosition(0)
                return@setOnLongClickListener recycler != null
            }
        }

        loadCache()
        updateHasTrailers()

        if (!checkWrite()) {
            requestRW()
            if (checkWrite()) return
        }

        if (BuildConfig.DEBUG) {
            var providersAndroidManifestString = "Current androidmanifest should be:\n"
            synchronized(allProviders) {
                for (api in allProviders) {
                    providersAndroidManifestString += "<data android:scheme=\"https\" android:host=\"${
                        api.mainUrl.removePrefix(
                            "https://"
                        )
                    }\" android:pathPrefix=\"/\"/>\n"
                }
            }
            println(providersAndroidManifestString)
        }

        handleAppIntent(intent)

        // MEMANGGIL FITUR UPDATE DYNAMIC ISLAND YANG ADA DI InAppUpdater.kt
        ioSafe {
             runAutoUpdate()
        }

        FcastManager().init(this, false)

        APIRepository.dubStatusActive = getApiDubstatusSettings()

        try {
            loadCache()
            File(filesDir, "exoplayer").deleteRecursively() 
            deleteFileOnExit(File(cacheDir, "exoplayer"))   
        } catch (e: Exception) {
            logError(e)
        }
        println("Loaded everything")

        ioSafe {
            migrateResumeWatching()
        }

        getKey<String>(USER_SELECTED_HOMEPAGE_API)?.let { homepage ->
            DataStoreHelper.currentHomePage = homepage
            removeKey(USER_SELECTED_HOMEPAGE_API)
        }

        try {
            setKey(HAS_DONE_SETUP_KEY, true)
        } catch (e: Exception) {
            logError(e)
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    @Suppress("DEPRECATION")
                    window?.navigationBarColor = colorFromAttribute(R.attr.primaryGrayBackground)
                    updateLocale()

                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        )
    }

    override fun onAuthenticationSuccess() {
        binding?.navHostFragment?.isInvisible = false
    }

    override fun onAuthenticationError() {
        finish()
    }

    suspend fun checkGithubConnectivity(): Boolean {
        return try {
            app.get(
                "https://thyo1.github.io/animestream-repo/keys.json",
                timeout = 5
            ).isSuccessful
        } catch (t: Throwable) {
            false
        }
    }
}
