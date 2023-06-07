package md.edi.mobilewaiter.presentation.add_client

import android.Manifest
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import md.edi.mobilewaiter.databinding.ActivityAddClientBinding
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import md.edi.mobilewaiter.presentation.main.viewmodel.MainViewModel
import md.edi.mobilewaiter.utils.ErrorHandler
import md.edi.mobilewaiter.utils.enums.EnumRemoteErrors
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import md.edi.mobilewaiter.R
import okhttp3.internal.and
import okio.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


@AndroidEntryPoint
class AddClientActivity : AppCompatActivity() {

    //NFC variables
    var nfcAdapter: NfcAdapter? = null

    var pendingIntent: PendingIntent? = null
    lateinit var writeTagFilters: Array<IntentFilter>

    val progressDialog by lazy { ProgressDialog(this) }

    val binding by lazy {
        ActivityAddClientBinding.inflate(LayoutInflater.from(this))
    }
    private val viewModel by viewModels<MainViewModel>()

    val options = ScanOptions()

    var cardCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val billId = intent.getStringExtra("billId")

        if (billId != null) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this)
            if (nfcAdapter == null) {
                Toast.makeText(this, getString(R.string.acest_dispozitiv_nu_suporta_nfc), Toast.LENGTH_LONG).show()
                binding.textView30.text = getString(R.string.acest_dispozitiv_nu_suporta_nfc)
            } else {
                nfcAdapter?.let {
                    if (!it.isEnabled) {
                        dialogShow(
                            getString(R.string.nfc_dezactivat),
                            getString(R.string.pentru_a_putea_folosi_cardul_nfc_a_clientului_este_nevoie_sa_porniti_nfc)
                        )
                    } else {
                        readFromIntent(intent)
                    }
                }
            }

            val policy: StrictMode.ThreadPolicy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
            tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
            writeTagFilters = arrayOf(tagDetected)
        }

        binding.buttonApply.isEnabled = false

        binding.etCardCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.isNotBlank()) {
                    cardCode = charSequence.toString()
                    binding.buttonApply.isEnabled = true
                } else {
                    binding.buttonApply.isEnabled = false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding.buttonApply.setOnClickListener {
            progressDialog.setMessage(getString(R.string.va_rugam_asteptati))
            progressDialog.setCancelable(false)
            progressDialog.show()

            billId?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.applyCard(it, cardCode)
                }
            }
        }

        binding.imageButtonOpenCamera.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                options.setPrompt(getString(R.string.scaneaza_codul_clientului))
                options.setCameraId(0) // Use a specific camera of the device
                options.setBeepEnabled(false)
                options.setBarcodeImageEnabled(true)
                barcodeLauncher.launch(options)
            } else {
                val permission = Manifest.permission.CAMERA
                ActivityCompat.requestPermissions(
                    this, arrayOf(permission), 90
                )
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.applyCardResult.collectLatest {
                progressDialog.dismiss()
                when (it.Result) {
                    0 -> {
                        Toast.makeText(
                            this@AddClientActivity,
                            getString(R.string.clientul_a_fost_adaugat),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    -9 -> {
                        dialogShowAddCard(getString(R.string.eroare_adaugare_clientului), it.ResultMessage)
                    }
                    else -> {
                        dialogShowAddCard(
                            getString(R.string.eroare_adaugare_clientului),
                            ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result))
                                    + "\n" + if (it.ResultMessage.isNullOrBlank()) "" else it.ResultMessage
                        )
                    }
                }
            }
        }

        initToolbar()
    }

    // Register the launcher and result handler
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this@AddClientActivity, getString(R.string.anulat), Toast.LENGTH_LONG).show()
        } else {
            binding.etCardCode.setText(result.contents)
            binding.buttonApply.performClick()
        }
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar

        toolbar.setTitle(getString(R.string.adauga_client))
        toolbar.setSubTitle(getString(R.string.introduceti_codul_sau_scanati_cardul))
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 90) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                options.setPrompt(getString(R.string.scan_a_barcode))
                options.setCameraId(0) // Use a specific camera of the device
                options.setOrientationLocked(false)
                options.setBeepEnabled(false)
                options.setBarcodeImageEnabled(true)
                barcodeLauncher.launch(options)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null)
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun dialogShow(title: String, description: String) {
        this.let {
            DialogAction(it, title, description, getString(R.string.porneste), getString(R.string.renun), {
                it.dismiss()
                val intentSettings = Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intentSettings);
            }, {
                it.dismiss()
            }).show()
        }
    }

    private fun dialogShowAddCard(title: String, description: String?) {
        this.let {
            DialogAction(it, title, description, getString(R.string.ok), getString(R.string.renun), {
                it.dismiss()
                cardCode = ""
                binding.etCardCode.setText("")
            }, {
                it.dismiss()
                cardCode = ""
                binding.etCardCode.setText("")
            }).show()
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readFromIntent(intent)
    }

    private fun readFromIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val tagFromIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }
            if (tagFromIntent != null) {
                val mUltra = MifareUltralight.get(tagFromIntent)
                if (mUltra != null) {
                    try {
                        mUltra.connect()
                        val sb = StringBuilder()
                        val pages = mUltra.readPages(0)
                        for (page in pages) {
                            val b: Int = page and 0xff
                            if (b < 0x10) sb.append("")
                            sb.append(b)
                        }
                        cardCode = getMD5HashCardCode(sb.toString())
                        binding.textResultScan.text = cardCode
                        binding.buttonApply.isEnabled = true
                        binding.buttonApply.performClick()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            mUltra.close()
                            Log.e("NFC", "MifareUltralight disconected")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                var auth = false
                val mfc = MifareClassic.get(tagFromIntent)
                if (mfc != null) {
                    try {
                        var metaInfo = ""
                        //Enable I/O operations to the tag from this TagTechnology object.
                        mfc.connect()
                        val sb = StringBuilder()
                        //
                        //Authenticate a sector with key A.
                        auth = mfc.authenticateSectorWithKeyA(
                            0,
                            MifareClassic.KEY_DEFAULT
                        )
                        if (auth) {
                            val data = mfc.readBlock(0)
                            for (page in data) {
                                val b: Int = page and 0xff
                                if (b < 0x10) sb.append("")
                                sb.append(b)
                            }
                        } else {
                            metaInfo += "Sector " + 0 + ": Verified failure\n";
                            Log.e("Error NFC", metaInfo)
                        }
                        cardCode = getMD5HashCardCode(sb.toString())
                        binding.textResultScan.text = cardCode
                        binding.buttonApply.isEnabled = true
                        binding.buttonApply.performClick()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        try {
                            mfc.close()
                            Log.e("NFC", "MifareClassic disconected")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else {
            Log.e("Error NFC", "Unknown intent $intent")
        }
    }

    private fun getMD5HashCardCode(message: String): String {
        var resultHash = ""
        try {
            val m = MessageDigest.getInstance("MD5")
            m.reset()
            m.update(message.toByteArray())
            val digest: ByteArray = m.digest()
            val bigInt = BigInteger(1, digest)
            resultHash = bigInt.toString(16)
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (resultHash.length < 32) {
                resultHash = "0$resultHash"
            }
            return resultHash
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return resultHash
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }


}