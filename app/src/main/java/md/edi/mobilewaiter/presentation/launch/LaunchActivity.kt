package md.edi.mobilewaiter.presentation.launch

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import md.edi.mobilewaiter.BuildConfig
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.data.datastore.SettingsRepository
import md.edi.mobilewaiter.data.listeners.OnLicenseListener
import md.edi.mobilewaiter.data.remote.models.license.response.ApplicationInfo
import md.edi.mobilewaiter.databinding.ActivityStartedBinding
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import md.edi.mobilewaiter.presentation.launch.viewmodel.LaunchViewModel
import md.edi.mobilewaiter.presentation.main.MainActivity
import md.edi.mobilewaiter.utils.DeviceInfo
import md.edi.mobilewaiter.utils.ErrorHandler
import md.edi.mobilewaiter.utils.Urls
import md.edi.mobilewaiter.utils.enums.EnumLicenseErrors
import md.edi.mobilewaiter.utils.enums.EnumRemoteErrors
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.presentation.settings.language.ActivityLanguage
import md.edi.mobilewaiter.utils.capitaliseWord
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("HardwareIds")
@AndroidEntryPoint
class LaunchActivity : AppCompatActivity(), OnLicenseListener {

    private val launchViewModel by viewModels<LaunchViewModel>()
    val progressDialog by lazy { ProgressDialog(this) }

    private val binding by lazy {
        ActivityStartedBinding.inflate(LayoutInflater.from(this))
    }

    private val versionApp by lazy {
        val pInfo = this.packageManager.getPackageInfo(packageName, 0)
        pInfo.versionName
    }

    private val settingsRepository by lazy { SettingsRepository(this) }
    val onLicenseListener by lazy { this }
    val sdf by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sph = runBlocking { SettingsRepository(this@LaunchActivity).getLanguage().first() }
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(sph.language)
        binding.textAppLanguage.text = Locale(sph.language).displayLanguage.capitaliseWord()
        AppCompatDelegate.setApplicationLocales(appLocale)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            Log.e("TAG", "Fetching FCM registration token $token")
            DeviceInfo.firebaseId = token
//            lifecycleScope.launch(Dispatchers.IO) {
//                settingsRepository.setFirebaseToken(token)
//            }
        })
        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }

        val billId = intent.getStringExtra("billId")
        val tagNotify = intent.getStringExtra("tag")
        Log.e("TAG", "onCreate launch:  $billId, $tagNotify")

        binding.txtVersionMobile.text = "v. $versionApp"

        binding.textAppLanguage.setOnClickListener {
            startActivity(Intent(this, ActivityLanguage::class.java))
        }

        askNotificationPermission()

        lifecycleScope.launchWhenCreated {
            val deviceId = Settings.Secure.getString(
                this@LaunchActivity.contentResolver,
                Settings.Secure.ANDROID_ID
            )
//            val deviceId = "18e0e45ad460007d"
            settingsRepository.setDeviceId(
                deviceId
            )
            DeviceInfo.deviceId = deviceId
            DeviceInfo.appVersion = getAppVersion(this@LaunchActivity)
            DeviceInfo.deviceModel = Build.MODEL
            DeviceInfo.deviceName =
                Settings.Global.getString(this@LaunchActivity.contentResolver, "device_name")
            DeviceInfo.osVersion = Build.VERSION.RELEASE
        }

        lifecycleScope.launchWhenResumed {
            DeviceInfo.licenseId = settingsRepository.getLicenseId().first()
            if (DeviceInfo.licenseId.isBlank()) {
                val dialogAction = ActivationAppFragment()
                dialogAction.setListener(onLicenseListener)
                dialogAction.show(supportFragmentManager, ActivationAppFragment::class.simpleName)
            }
        }

        binding.btnStart.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                DeviceInfo.licenseCode = settingsRepository.getLicenseCode().first()
                if (DeviceInfo.licenseCode.isNotBlank()) {
                    progressDialog.setMessage(getString(R.string.verificare_starea_terminalului))
                    progressDialog.show()
                    launchViewModel.getURI()
                }
                else{
                    val dialogAction = ActivationAppFragment()
                    dialogAction.setListener(onLicenseListener)
                    dialogAction.show(supportFragmentManager, ActivationAppFragment::class.simpleName)
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            launchViewModel.terminalRegister.collectLatest {
                it.let {
                    when (it.Result) {
                        0 -> {
                            DeviceInfo.deviceNumber = it.DeviceNumber
                            progressDialog.setMessage(getString(R.string.sincronizare_loading))
                            launchViewModel.syncAssortment()
                        }
                        -9 -> {
                            progressDialog.dismiss()
                            dialogShowRegisterDevice(getString(R.string.eroare), it.ResultMessage.toString())
                        }
                        else -> {
                            progressDialog.dismiss()
                            dialogShowRegisterDevice(getString(R.string.eroare), ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result)))
                        }
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            launchViewModel.syncAssortment.collectLatest {
                progressDialog.dismiss()
                it.let {
                    when (it.Result) {
                        0 -> {
                            DeviceInfo.lastSync = sdf.format(Date())
                            AssortmentController.setAssortmentResponse(it)
                            val mainActivity = Intent(this@LaunchActivity, MainActivity::class.java)
                            mainActivity.putExtra("billId", billId)
                            mainActivity.putExtra("tag", tagNotify)
                            startActivity(mainActivity)
                            finish()
                        }
                        -9 -> {
                            dialogShow(
                                getString(R.string.dispozitivul) + DeviceInfo.deviceNumber,
                                it.errorMessage,
                                true
                            )
                        }
                        else -> {
                            dialogShow(
                                getString(R.string.dispozitivul) + DeviceInfo.deviceNumber,
                                ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result)),
                                true
                            )
                        }
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            launchViewModel.getUri.collectLatest {
                it.let {
                    when (it.ErrorCode) {
                        0 -> {
                            it.AppData?.let{ appInfo ->
                                if(!appInfo.URI.isNullOrEmpty()){
                                    var uri = appInfo.URI!!
                                    if (uri.last().toString() != "/") {
                                        uri += "/"
                                    }
                                    settingsRepository.setURI(uri)
                                    Urls.baseUrl = uri
                                    Urls.init()
                                    Log.e("TAG", "saveLicenseInfo: ${Urls.baseUrl} + uri: $uri")
                                    launchViewModel.registerTerminal()
                                }
                                else{
                                    dialogShowActivateApp(
                                        getString(R.string.eroare),
                                        getString(R.string.nu_este_setat_adresa_de_conectare_a_aplicatiei)
                                    )
                                }
                            }
                        }
                        -9 -> {
                            dialogShow(
                                getString(R.string.atentie),
                                it.ErrorMessage,
                                true
                            )
                        }
                        else -> {
                            dialogShow(
                                getString(R.string.atentie),
                                ErrorHandler().getErrorLicenseMessage(
                                    EnumLicenseErrors.getByValue(
                                        it.ErrorCode!!
                                    )
                                ),
                                true
                            )
                        }
                    }
                }
            }
        }

    }

    override fun onLicenseActivate(dialogs: Dialog, code: String) {

        progressDialog.setMessage(getString(R.string.activare_aplicatiei))
        progressDialog.show()
        launchViewModel.registerApplication(code)

        lifecycleScope.launch(Dispatchers.Main) {
            launchViewModel.registerApplication.collectLatest {
                progressDialog.dismiss()
                when (it.ErrorCode) {
                    0 -> {
                        dialogs.dismiss()
                        it.AppData?.let { applicationInfo ->
                            saveLicenseInfo(applicationInfo)

                            if(!applicationInfo.URI.isNullOrEmpty()){
                                progressDialog.setMessage(getString(R.string.verificare_starea_terminalului))
                                progressDialog.show()
                                launchViewModel.registerTerminal()
                            }
                            else{
                                dialogShowActivateApp(
                                    getString(R.string.eroare_la_activarea_aplicatiei),
                                    getString(R.string.nu_este_setat_adresa_de_conectare_a_aplicatiei)
                                )
                            }
                        }
                    }
                    -9 -> {
                        it.ErrorMessage?.let {msg ->
                            if(msg.length > 1000){
                                dialogShowActivateApp(
                                    getString(R.string.eroare_la_activarea_aplicatiei),
                                    getString(R.string.incercati_mai_tirziu_sau_luati_legatura_cu_provider_ul_aplicatiei)
                                )
                            }
                            else{
                                dialogShowActivateApp(
                                    getString(R.string.eroare),
                                    msg
                                )
                            }
                        }
                    }
                    else -> {
                        it.ErrorCode?.let { code ->
                            dialogShowActivateApp(
                                getString(R.string.eroare),
                                ErrorHandler().getErrorLicenseMessage(EnumLicenseErrors.getByValue(code))
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCancelActivate(dialogs: Dialog) {
        dialogs.dismiss()
        finish()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->

    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        hideSystemUI()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private suspend fun saveLicenseInfo(applicationInfo: ApplicationInfo) {
        applicationInfo.LicenseCode?.let { code ->
            DeviceInfo.licenseCode = code
            settingsRepository.setLicenseCode(code)
        }
        applicationInfo.LicenseID?.let {
            DeviceInfo.licenseId = it
            settingsRepository.setLicenseId(it)
        }
        applicationInfo.URI?.let {
            var uri = it
            if (uri.last().toString() != "/") {
                uri += "/"
            }
            settingsRepository.setURI(it)
            Urls.baseUrl = uri
            Urls.init()
        }
        applicationInfo.ServerDateTime?.let {
            val srvDate = it.replace("/Date(", "").replace("+0200)/", "").replace("+0300)/", "").toLong()
            settingsRepository.setSrvDate(srvDate)
            settingsRepository.setAppDate(Date().time)
        }
        applicationInfo.Company?.let {
            settingsRepository.setCompany(it)
        }
    }

    private fun dialogShow(title: String?, description: String?, reloadSyncAssortment: Boolean) {
        DialogAction(this, title, description, getString(R.string.reincearca), getString(R.string.renun), {
            it.dismiss()
            if (reloadSyncAssortment) {
                progressDialog.setMessage(getString(R.string.sincronizare_loading))
                progressDialog.show()
                launchViewModel.syncAssortment()
            } else {
                progressDialog.setMessage(getString(R.string.va_rugam_asteptati))
                progressDialog.show()
                launchViewModel.registerTerminal()
            }

        }, {
            finish()
        }).show()
    }
    private fun dialogShowActivateApp(title: String?, description: String?) {
        DialogAction(this, title, description, getString(R.string.ok), getString(R.string.renun), {
            it.dismiss()
        }, {
            finish()
        }).show()
    }

    private fun dialogShowRegisterDevice(title: String?, description: String?) {
        DialogAction(this, title, description, getString(R.string.ok), getString(R.string.renun), {
            it.dismiss()
        }, {
            it.dismiss()
        }).show()
    }

    private fun getAppVersion(context: Context): String {
        var result = ""
        try {
            result =
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            result = result.replace("[a-zA-Z] |-".toRegex(), "")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return result
    }
}