package md.edi.mobilewaiter.presentation.settings

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.data.datastore.SettingsRepository
import md.edi.mobilewaiter.databinding.FragmentSettingsBinding
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import md.edi.mobilewaiter.presentation.launch.viewmodel.LaunchViewModel
import md.edi.mobilewaiter.utils.DeviceInfo
import md.edi.mobilewaiter.utils.ErrorHandler
import md.edi.mobilewaiter.utils.enums.EnumRemoteErrors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.presentation.settings.language.ActivityLanguage
import md.edi.mobilewaiter.utils.capitaliseWord
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
            progressDialog.setMessage(getString(R.string.sincronizare_loading))
            progressDialog.show()
            launchViewModel.syncAssortment()
        }
        val sph = runBlocking { SettingsRepository(requireContext()).getLanguage().first() }
        binding.textLanguage.text = Locale(sph.language).displayLanguage.capitaliseWord()

        binding.textLanguage.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityLanguage::class.java))
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
                                getString(R.string.dispozitivul)+ "${DeviceInfo.deviceNumber}",
                                it.errorMessage,
                            )
                        }
                        else -> {
                            dialogShow(
                                getString(R.string.dispozitivul)+ "${DeviceInfo.deviceNumber}",
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

        toolbar.setTitle(getString(R.string.setari))
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            findNavController().popBackStack()
        }
    }

    private fun dialogShow(title: String, description: String?) {
        DialogAction(requireActivity(), title, description, getString(R.string.reincearca), getString(R.string.renun), {
            it.dismiss()
            progressDialog.setMessage(getString(R.string.sincronizare_loading))
            progressDialog.show()
            launchViewModel.syncAssortment()

        }, {
            it.dismiss()
        }).show()
    }
}