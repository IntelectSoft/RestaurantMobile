package com.example.igor.restaurantmobile.presentation.launch

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.databinding.ActivityStartedBinding
import com.example.igor.restaurantmobile.presentation.launch.viewmodel.LaunchViewModel
import com.example.igor.restaurantmobile.presentation.main.MainActivity
import com.example.igor.restaurantmobile.data.datastore.SettingsRepository
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.example.igor.restaurantmobile.utils.ContextManager
import com.example.igor.restaurantmobile.utils.ErrorHandler
import com.example.igor.restaurantmobile.utils.enums.EnumRemoteErrors
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@SuppressLint("HardwareIds")
@AndroidEntryPoint
class LaunchActivity : AppCompatActivity() {
    private val launchViewModel by viewModels<LaunchViewModel>()
    var deviceNumber = 0
    val progressDialog by lazy { ProgressDialog(this) }

    private val binding by lazy {
        ActivityStartedBinding.inflate(LayoutInflater.from(this))
    }

    private val license by lazy {
        SettingsRepository(this).getLicense()
    }

    private val versionApp by lazy {
        val pInfo = this.packageManager.getPackageInfo(packageName, 0)
        pInfo.versionName
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /*FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            } // Get new FCM registration token
            val token = task.result
            Log.e("TAG", "Fetching FCM registration token $token")

           // CustomLocalStorage.writeData(LocalStorage.FIREBASE_TOKEN, token?.toByteArray())
        })
        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
*/

        binding.txtVersionMobile.text = "v. $versionApp"

        lifecycleScope.launchWhenCreated {
            SettingsRepository(this@LaunchActivity).setDeviceId(
//                Secure.getString(
//                    this@LaunchActivity.contentResolver,
//                    Secure.ANDROID_ID
//                )
                "c0d0e2e8-9ad4-4d75-acf8-a9528918aa49"
            )
        }

        binding.btnStart.setOnClickListener {
            progressDialog.setMessage("Verificare starea terminalului...")
            progressDialog.show()
            launchViewModel.registerTerminal()

//            if(licenseIsActive && pingExist){


//            }
//            else{
//                startActivity(Intent(this, SettingActivity::class.java))
//            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            launchViewModel.terminalRegister.collectLatest {
                it.let {
                    when (it.Result) {
                        0 -> {
                            deviceNumber = it.DeviceNumber
                            progressDialog.setMessage("Sincronizare...")
                            launchViewModel.syncAssortment()
                        }
                        -1 -> {
                            progressDialog.dismiss()
                            dialogShow("Eroare", it.ResultMessage.toString(), false)
                        }
                        else -> {
                            progressDialog.dismiss()
                            Snackbar.make(
                                binding.btnStart,
                                ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result)),
                                Snackbar.LENGTH_LONG
                            ).show()
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
                            AssortmentController.assortmentBody = it
                            startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
                            finish()
                        }
                        EnumRemoteErrors.ShiftIsNotValid.code -> {
                            progressDialog.dismiss()

                        }
                        EnumRemoteErrors.DeviceNotRegistered.code -> {
                            dialogShow(
                                "Dispozitivul:  $deviceNumber",
                                "Dispozitivul nu este inregistrat la casa, verificati la casa",
                                true
                            )
                        }
                        else -> {
                            Snackbar.make(
                                binding.btnStart,
                                ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result)),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG", "onDestroy: StartActivity")
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun dialogShow(title: String, description: String, reloadSyncAssortment: Boolean) {
        DialogAction(this, title, description, "Reincearca", "Renunta", {
            it.dismiss()
            if(reloadSyncAssortment){
                progressDialog.setMessage("Sincronizare...")
                progressDialog.show()
                launchViewModel.syncAssortment()
            }
            else{
                progressDialog.setMessage("Verificare starea terminalului...")
                progressDialog.show()
                launchViewModel.registerTerminal()
            }

        }, {
            finish()
        }).show()
    }
}