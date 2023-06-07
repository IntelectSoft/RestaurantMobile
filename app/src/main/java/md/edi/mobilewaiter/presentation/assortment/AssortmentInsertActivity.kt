package md.edi.mobilewaiter.presentation.assortment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.controllers.CreateBillController
import md.edi.mobilewaiter.data.remote.response.assortment.AssortmentItem
import md.edi.mobilewaiter.data.remote.response.assortment.KitMemberItem
import md.edi.mobilewaiter.databinding.FragmentAssortmentDetailsBinding
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import java.util.regex.Matcher
import java.util.regex.Pattern

class AssortmentInsertActivity : AppCompatActivity() {
    var selectedValue = 1.0
    var indexKitMember = 0
    val listOfComments = mutableListOf<String>()
    val baseCommentsOfAssortment = mutableListOf<String>()
    val listOfSelectedComments = mutableListOf<String>()
    private lateinit var assortmentItem: AssortmentItem

    val binding by lazy {
        FragmentAssortmentDetailsBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        AssortmentController.getAssortmentById(intent.getStringExtra("itemId") ?: "")?.let {
            assortmentItem = it
        }
        Log.e("TAG", "onViewCreated: assortment item: $assortmentItem")

        val buttonItemName = if (assortmentItem.Name.length > 23) assortmentItem.Name.substring(
            0,
            22
        ) + "..." else assortmentItem.Name
        binding.buttonAddCounts.text = getString(R.string.adauga) + " ${binding.etCountOfItem.text} - $buttonItemName"
        binding.textItemPrice.text = getString(R.string.mdl,assortmentItem.Price.toString())

        binding.numberPicker.minValue = 0
        binding.numberPicker.maxValue = CreateBillController.numberCook + 10
        binding.numberPicker.value = CreateBillController.numberCook
        binding.numberPicker.wrapSelectorWheel = false

        binding.textItemAllowNonInteger.text =
            if (!assortmentItem.AllowNonIntegerSale) getString(R.string.nu) else getString(R.string.da)
        binding.etCountOfItem.inputType =
            if (!assortmentItem.AllowNonIntegerSale) InputType.TYPE_CLASS_NUMBER else InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.textContainsKitMembers.text =
            if (assortmentItem.KitMembers.isNullOrEmpty()) getString(R.string.nu) else getString(R.string.da)
        binding.textContainMandatoryComments.text =
            if (!assortmentItem.MandatoryComment) getString(R.string.nu) else getString(R.string.da)

        if (assortmentItem.Comments.isNullOrEmpty()) {
            binding.textTitleComments.isVisible = false
            binding.constraintLayoutComments.isVisible = false
        } else {
            assortmentItem.Comments.forEach {
                if (isGuid(it)) {
                    baseCommentsOfAssortment.add(it)
                } else {
                    binding.textCustomComments.append(it)
                }
            }

            if (baseCommentsOfAssortment.size > 0) {
                binding.textTitleComments.isVisible = true
                binding.constraintLayoutComments.isVisible = true

                binding.imageButtonAddComments.setOnClickListener {
                    showComments()
                }
            }

        }

        binding.etCountOfItem.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.isNotBlank()) {
                    selectedValue = charSequence.toString().toDouble()
                    Log.e("TAG", "onTextChanged: $selectedValue")
                    if (selectedValue == 0.0) {
                        binding.buttonAddCounts.isEnabled = false
                    } else {
                        binding.buttonAddCounts.text =
                            getString(R.string.adauga) + " ${binding.etCountOfItem.text} - $buttonItemName"
                        binding.buttonAddCounts.isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding.imageAddOne.setOnClickListener {
            binding.etCountOfItem.setText(
                if (assortmentItem.AllowNonIntegerSale) {
                    try {
                        (binding.etCountOfItem.text.toString().toDouble() + 1).toString()
                    } catch (e: Exception) {
                        (binding.etCountOfItem.text.toString().replace(",", ".") + 1).toString()
                    }
                } else {
                    (binding.etCountOfItem.text.toString().toInt() + 1).toString()
                }
            )
        }
        binding.imageRemoveOne.setOnClickListener {
            if (assortmentItem.AllowNonIntegerSale) {
                var curr: Double =
                    java.lang.Double.valueOf(java.lang.String.valueOf(binding.etCountOfItem.getText()))
                if (curr - 1 >= 0) curr -= 1.0
                binding.etCountOfItem.setText(curr.toString())
            } else {
                var curr: Int = Integer.valueOf(binding.etCountOfItem.text.toString())
                if (curr - 1 >= 0) curr -= 1
                binding.etCountOfItem.setText(curr.toString())
            }
        }

        binding.buttonAddCounts.setOnClickListener {
            if (assortmentItem.KitMembers.isNullOrEmpty()) {
                checkAndSave()
            } else {
                showKitMembers()
            }
        }

        binding.textBack2.setOnClickListener {
            finish()
        }

        initToolbar(assortmentItem.Name)
    }

    private fun initToolbar(name: String) {
        val toolbar = binding.toolbar

        if (name.length > 32){
            toolbar.setTitle(name.substring(0,32) + "...")
        }
        else{
            toolbar.setTitle(name)
        }

        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            finish()
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

    private fun showKitMembers() {
        val kitAssortmentList = mutableListOf<HashMap<String, Any>>()
        if (indexKitMember < assortmentItem.KitMembers.size) {
            val kitMember: KitMemberItem = assortmentItem.KitMembers[indexKitMember]

            val assortmentListKitMember: List<String> = kitMember.AssortimentList
            val kitStepNumber: Int = kitMember.StepNumber
            val kitMandatory: Boolean = kitMember.Mandatory

            for (i in assortmentListKitMember.indices) {
                val kitName = AssortmentController.getAssortmentById(assortmentListKitMember[i])
                val asortimentKitMebmerMap = HashMap<String, Any>()
                asortimentKitMebmerMap["Name"] = kitName?.Name ?: ""
                asortimentKitMebmerMap["Guid"] = assortmentListKitMember[i]
                kitAssortmentList.add(asortimentKitMebmerMap)
            }
            if (assortmentListKitMember.size == 1 && kitMandatory) {
                listOfComments.add(kitAssortmentList[0]["Guid"] as String)
                if (indexKitMember < assortmentItem.KitMembers.size) {
                    indexKitMember += 1
                    showKitMembers()
                } else {
                    checkAndSave()
                }
            } else {
                val adapterKitMebmers = SimpleAdapter(
                    this,
                    kitAssortmentList,
                    android.R.layout.simple_list_item_1,
                    arrayOf("Name"),
                    intArrayOf(
                        android.R.id.text1
                    )
                )
                val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                dialog.setTitle("Complectul: $kitStepNumber")
                dialog.setCancelable(false)
                dialog.setAdapter(adapterKitMebmers) { dialog, wich ->
                    listOfComments.add(kitAssortmentList.get(wich).get("Guid") as String)
                    if (indexKitMember < assortmentItem.KitMembers.size) {
                        indexKitMember += 1
                        showKitMembers()
                    } else {
                        checkAndSave()
                    }
                }
                if (!kitMandatory) {
                    dialog.setPositiveButton("Пропустить шаг") { dialogInterface, i ->
                        if (indexKitMember < assortmentItem.KitMembers.size) {
                            indexKitMember += 1
                            showKitMembers()
                        } else {
                            checkAndSave()
                        }
                    }
                }
                dialog.show()
            }
        } else {
            checkAndSave()
        }
    }

    private fun showComments() {
        val commentsAssortmentList = mutableListOf<HashMap<String, String>>()

        for (i in baseCommentsOfAssortment.indices) {
            val comment = AssortmentController.getCommentById(baseCommentsOfAssortment[i])

            val asortmentCommentMap: HashMap<String, String> = HashMap()
            comment?.let {
                asortmentCommentMap["Name"] = it.Comment
                asortmentCommentMap["Guid"] = it.Uid
            }
            commentsAssortmentList.add(asortmentCommentMap)
        }

        val adapterComments = SimpleAdapter(
            this,
            commentsAssortmentList,
            android.R.layout.simple_list_item_1,
            arrayOf("Name"),
            intArrayOf(
                android.R.id.text1
            )
        )
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.selectati_comentariu))
        dialog.setCancelable(false)
        dialog.setAdapter(adapterComments) { dialog, which ->
            val selectedGuid = commentsAssortmentList[which]["Guid"] as String
            if (listOfSelectedComments.contains(selectedGuid)) {
                dialogShow(
                    getString(R.string.atentie_produsul_deja_exista),
                    commentsAssortmentList[which]["Name"] + getString(R.string.deja_este_adaugat_sunteti_sigur_ca_doriti_sa_mai_adaugati_odata),
                    selectedGuid,
                    commentsAssortmentList[which]["Name"] as String
                )
            } else {
                listOfSelectedComments.add(selectedGuid)
                if (binding.textComments.text.isBlank()) {
                    binding.textComments.text = commentsAssortmentList[which]["Name"] as String
                } else {
                    binding.textComments.append(" | " + commentsAssortmentList[which]["Name"] as String)
                }
            }

        }
        dialog.setNegativeButton(getString(md.edi.mobilewaiter.R.string.renun)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        dialog.show()
    }


    private fun checkAndSave() {
        if (assortmentItem.MandatoryComment) {
            if(listOfSelectedComments.isEmpty()) {
                Toast.makeText(this, getString(R.string.nu_ati_ales_nici_un_comentariu), Toast.LENGTH_SHORT)
                    .show()
            }else{
                saveOrderLine()
            }
        }
        else {
            saveOrderLine()
        }
    }

    private fun saveOrderLine(){
        if (binding.textCustomComments.text.toString().isNotBlank())
            listOfComments.add(binding.textCustomComments.text.toString())

        listOfComments.addAll(listOfSelectedComments)

        CreateBillController.addAssortment(
            priceLineId = assortmentItem.PricelineUid,
            assortmentId = assortmentItem.Uid,
            number = binding.numberPicker.value,
            count = selectedValue,
            comments = listOfComments,
            price = assortmentItem.Price
        )
        CreateBillController.setCartCount(selectedValue)
        finish()
    }

    private fun dialogShow(title: String, description: String, guid: String, name: String) {
        this.let {
            DialogAction(it, title, description, getString(R.string.adauga), getString(md.edi.mobilewaiter.R.string.renun), {
                it.dismiss()
                listOfSelectedComments.add(guid)
                binding.textComments.append(" | $name")
            }, {
                it.dismiss()
            }).show()
        }
    }

    private fun isGuid(str: String): Boolean {
        val regex = ("^[{(]?[0-9A-Fa-f]{8}[-]?(?:[0-9A-Fa-f]{4}[-]?){3}[0-9A-Fa-f]{12}[)}]?$")
        val p1: Pattern = Pattern.compile(regex)
        val m1: Matcher = p1.matcher(str)
        return m1.matches()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}