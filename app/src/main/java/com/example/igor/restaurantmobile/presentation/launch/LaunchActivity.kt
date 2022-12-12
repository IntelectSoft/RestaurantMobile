package com.example.igor.restaurantmobile.presentation.launch

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationManager
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
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.igor.restaurantmobile.BuildConfig
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.controllers.App
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.data.database.repository.RepositoryNotification
import com.example.igor.restaurantmobile.data.datastore.SettingsRepository
import com.example.igor.restaurantmobile.data.listeners.OnLicenseListener
import com.example.igor.restaurantmobile.data.remote.models.license.response.ApplicationInfo
import com.example.igor.restaurantmobile.databinding.ActivityStartedBinding
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.example.igor.restaurantmobile.presentation.dialog.DialogActionInputText
import com.example.igor.restaurantmobile.presentation.launch.viewmodel.LaunchViewModel
import com.example.igor.restaurantmobile.presentation.main.MainActivity
import com.example.igor.restaurantmobile.presentation.table.TableFragmentDirections
import com.example.igor.restaurantmobile.utils.ContextManager
import com.example.igor.restaurantmobile.utils.DeviceInfo
import com.example.igor.restaurantmobile.utils.ErrorHandler
import com.example.igor.restaurantmobile.utils.Urls
import com.example.igor.restaurantmobile.utils.enums.EnumLicenseErrors
import com.example.igor.restaurantmobile.utils.enums.EnumRemoteErrors
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

        askNotificationPermission()

        lifecycleScope.launchWhenCreated {
            val deviceId = Settings.Secure.getString(
                this@LaunchActivity.contentResolver,
                Settings.Secure.ANDROID_ID
            )
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
                    progressDialog.setMessage("Verificare starea terminalului...")
                    progressDialog.show()
                    launchViewModel.getURI()
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            launchViewModel.terminalRegister.collectLatest {
                it.let {
                    when (it.Result) {
                        0 -> {
                            DeviceInfo.deviceNumber = it.DeviceNumber
                            progressDialog.setMessage("Sincronizare...")
                            launchViewModel.syncAssortment()
                        }
                        -9 -> {
                            progressDialog.dismiss()
                            dialogShowRegisterDevice("Eroare", it.ResultMessage.toString())
                        }
                        else -> {
                            progressDialog.dismiss()
                            dialogShowRegisterDevice("Eroare", ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result)))
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
                                "Dispozitivul:  ${DeviceInfo.deviceNumber}",
                                it.errorMessage,
                                true
                            )
                        }
                        else -> {
                            dialogShow(
                                "Dispozitivul:  ${DeviceInfo.deviceNumber}",
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
                            launchViewModel.registerTerminal()
                        }
                        -9 -> {
                            dialogShow(
                                "Atentie",
                                it.ErrorMessage,
                                true
                            )
                        }
                        else -> {
                            dialogShow(
                                "Atentie",
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

        progressDialog.setMessage("Activare aplicatiei...")
        progressDialog.show()
        launchViewModel.registerApplication(code)

        lifecycleScope.launch(Dispatchers.Main) {
            launchViewModel.registerApplication.collectLatest {
                progressDialog.dismiss()
                if (it.ErrorCode == 0) {
                    dialogs.dismiss()
                    it.AppData?.let { applicationInfo ->
                        saveLicenseInfo(applicationInfo)
                    }
                    progressDialog.setMessage("Verificare starea terminalului...")
                    progressDialog.show()
                    launchViewModel.registerTerminal()

                } else if (it.ErrorCode == -9) {
                    dialogShowActivateApp(
                        "Eroare",
                        it.ErrorMessage
                    )
                } else {
                    it.ErrorCode?.let { code ->
                        dialogShowActivateApp(
                            "Eroare",
                            ErrorHandler().getErrorLicenseMessage(EnumLicenseErrors.getByValue(code))
                        )
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
        }
        applicationInfo.ServerDateTime?.let {
            val srvDate = it.replace("/Date(", "").replace("+0200)/", "").toLong()
            settingsRepository.setSrvDate(srvDate)
            settingsRepository.setAppDate(Date().time)
        }
        applicationInfo.Company?.let {
            settingsRepository.setCompany(it)
        }
    }

    private fun dialogShow(title: String?, description: String?, reloadSyncAssortment: Boolean) {
        DialogAction(this, title, description, "Reincearca", "Renunta", {
            it.dismiss()
            if (reloadSyncAssortment) {
                progressDialog.setMessage("Sincronizare...")
                progressDialog.show()
                launchViewModel.syncAssortment()
            } else {
                progressDialog.setMessage("Va rugam asteptati...")
                progressDialog.show()
                launchViewModel.registerTerminal()
            }

        }, {
            finish()
        }).show()
    }
    private fun dialogShowActivateApp(title: String?, description: String?) {
        DialogAction(this, title, description, "OK", "Renunta", {
            it.dismiss()
        }, {
            finish()
        }).show()
    }

    private fun dialogShowRegisterDevice(title: String?, description: String?) {
        DialogAction(this, title, description, "OK", "Renunta", {
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