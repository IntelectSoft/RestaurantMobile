package com.example.igor.restaurantmobile.presentation.settings

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.data.datastore.SettingsRepository
import com.example.igor.restaurantmobile.databinding.FragmentSettingsBinding
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.example.igor.restaurantmobile.presentation.launch.viewmodel.LaunchViewModel
import com.example.igor.restaurantmobile.utils.DeviceInfo
import com.example.igor.restaurantmobile.utils.ErrorHandler
import com.example.igor.restaurantmobile.utils.enums.EnumRemoteErrors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val launchViewModel by viewModels<LaunchViewModel>()
    private val settingsRepository by lazy { SettingsRepository(requireContext()) }
    val progressDialog by lazy { ProgressDialog(context) }

    val binding by lazy {
        FragmentSettingsBinding.inflate(LayoutInflater.from(context))
    }
    val sdf by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.textSDeviceModel.text = DeviceInfo.deviceModel +  " / " +  DeviceInfo.deviceName
        binding.textSDeviceId.text = DeviceInfo.deviceId
        binding.textSDeviceNumber.text = DeviceInfo.deviceNumber.toString()

        lifecycleScope.launch(Dispatchers.Main){
            combine(
                settingsRepository.getCompany(),
                settingsRepository.getLicenseCode(),
                settingsRepository.getURI()
            ) { company, licCode, uri ->
                binding.textSCompanyName.text = company
                binding.textSLicCode.text = licCode
                binding.textSUri.text = uri
            }.collect()
        }

        binding.textSLastSync.text = DeviceInfo.lastSync

        binding.btnSync.setOnClickListener {
            progressDialog.setMessage("Sincronizare...")
            progressDialog.show()
            launchViewModel.syncAssortment()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            launchViewModel.syncAssortment.collectLatest {
                progressDialog.dismiss()
                it.let {
                    when (it.Result) {
                        0 -> {
                            AssortmentController.setAssortmentResponse(it)
                            DeviceInfo.lastSync = sdf.format(Date())
                            binding.textSLastSync.text = DeviceInfo.lastSync
                            progressDialog.dismiss()
                        }
                        -9 -> {
                            dialogShow(
                                "Dispozitivul:  ${DeviceInfo.deviceNumber}",
                                it.errorMessage,
                            )
                        }
                        else -> {
                            dialogShow(
                                "Dispozitivul:  ${DeviceInfo.deviceNumber}",
                                ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result)),
                            )
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar

        toolbar.setTitle("Setari")
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            findNavController().popBackStack()
        }
    }

    private fun dialogShow(title: String, description: String?) {
        DialogAction(requireActivity(), title, description, "Reincearca", "Renunta", {
            it.dismiss()
            progressDialog.setMessage("Sincronizare...")
            progressDialog.show()
            launchViewModel.syncAssortment()

        }, {
            it.dismiss()
        }).show()
    }
}