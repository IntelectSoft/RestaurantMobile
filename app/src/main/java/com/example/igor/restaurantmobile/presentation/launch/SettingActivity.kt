package com.example.igor.restaurantmobile.presentation.launch

import android.app.ProgressDialog
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.databinding.ActivitySettingBinding
import com.example.igor.restaurantmobile.presentation.base.DialogFragmentSettings
import com.example.igor.restaurantmobile.presentation.launch.viewmodel.LaunchViewModel

class SettingActivity: AppCompatActivity(), DialogFragmentSettings.OnStateLicense {

    lateinit var binding: ActivitySettingBinding
    private val networkViewModel: LaunchViewModel by viewModels()
    lateinit var progressDialog: ProgressDialog
    var progressCompleteSettings = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        //set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)

//        val licenseIsActive = CustomLocalStorage.readString(Constants.LICENSE_KEY) != ""
//        val pingExist = CustomLocalStorage.readBoolean(Constants.PING_KEY)

//        if(licenseIsActive){
//            binding.imageStateLicense.setImageResource(R.drawable.icon_done)
//            binding.textPreviewLicense.text = "Aplicația este activată"
//        }
//        else{
//            binding.imageStateLicense.setImageResource(R.drawable.icon_close)
//            binding.textPreviewLicense.text = "Aplicația nu este activată"
//        }
//
//        if(pingExist){
//            binding.imageStateConnection.setImageResource(R.drawable.icon_done)
//            binding.textPreviewConnectionAddress.text = "Conexiune stabilită pe: ${CustomLocalStorage.readString(SERVER_URL)}"
//        }
//        else{
//            binding.imageStateConnection.setImageResource(R.drawable.icon_close)
//            binding.textPreviewConnectionAddress.text = "Conexiune nu este stabilită"
//        }

        binding.saveSettingsContinue.isEnabled = false

        binding.constraintLayoutLicense.setOnClickListener {
            val dialogLicense = DialogFragmentSettings.newInstance(false)
            dialogLicense.isCancelable = false
            dialogLicense.show(supportFragmentManager, "dialogToast")
        }

        binding.constraintLayoutConnection.setOnClickListener {
            val dialogLicense = DialogFragmentSettings.newInstance(true)
            dialogLicense.isCancelable = false
            dialogLicense.show(supportFragmentManager, "dialogToast")
        }

        binding.saveSettingsContinue.setOnClickListener {
            progressDialog.setMessage("Încărcarea datelor...")
            progressDialog.show()
//            downloadAssortment()
        }

        binding.textBack.setOnClickListener{
            finish()
        }
    }

//    private fun downloadAssortment(){
//        networkViewModel.downloadAssortmentList(CustomLocalStorage.readString(Constants.DEVICE_ID))
//
//        networkViewModel.downloadAssortmentResponse.observe(this){
//            progressDialog.dismiss()
//            if(it != null){
//                if(it.Result == 0){
////                    RestaurantApplication().downloadedData = it
////                    startActivity(Intent(this, MainActivity::class.java))
////                    StartActivity.getStartActivity().finish()
////                    finish()
//                }
//                else{
//                    //show error received
//                    Snackbar.make(binding.saveSettingsContinue, ErrorHandler().getErrorMessage(it.Result), Snackbar.LENGTH_LONG).show()
//                }
//            }
//            else
//            //show error received
//                Snackbar.make(binding.saveSettingsContinue, "Răspuns de la server nu a fost primit, verificați corectitudinea datelor la casă", Snackbar.LENGTH_LONG).show()
//        }
//    }

    override fun onLicenseActive(isActive: Boolean) {
        if(isActive){
            if(progressCompleteSettings == 50 ){
                binding.saveSettingsContinue.isEnabled = true
            }
            else
                progressCompleteSettings = 50
//            binding.imageStateLicense.setImageResource(R.drawable.icon_done)
            binding.textPreviewLicense.text = "Aplicația este activată"
        }
        else{
//            binding.imageStateLicense.setImageResource(R.drawable.icon_close)
            binding.textPreviewLicense.text = "Aplicația nu este activată"
        }
    }

    override fun onConnectionStable(isConnected: Boolean) {
        if(isConnected){
            if(progressCompleteSettings == 50 ){
                binding.saveSettingsContinue.isEnabled = true
            }
            else
                progressCompleteSettings = 50

//            binding.imageStateConnection.setImageResource(R.drawable.icon_done)
//            binding.textPreviewConnectionAddress.text = "Conexiune stabilită pe: ${CustomLocalStorage.readString(SERVER_URL)}"
        }
//        else{
//            binding.imageStateConnection.setImageResource(R.drawable.icon_close)
//            binding.textPreviewConnectionAddress.text = "Conexiune nu este stabilită"
//        }
    }
}