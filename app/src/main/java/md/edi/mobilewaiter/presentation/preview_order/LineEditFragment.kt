package md.edi.mobilewaiter.presentation.preview_order

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.controllers.App
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.controllers.CreateBillController
import md.edi.mobilewaiter.data.remote.response.assortment.AssortmentItem
import md.edi.mobilewaiter.databinding.FragmentLineEditBinding
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import md.edi.mobilewaiter.utils.ContextManager
import java.util.regex.Matcher
import java.util.regex.Pattern


class LineEditFragment : Fragment() {

    private var selectedValue = 1.0
    private var existingComment = ""
    private val listOfComments = mutableListOf<String>()
    private lateinit var assortmentItem: AssortmentItem

    val binding by lazy {
        FragmentLineEditBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lineInternId = arguments?.getString("lineId") ?: ""
        val orderLine = CreateBillController.getInternLineById(lineInternId)
        orderLine?.let { line ->
            selectedValue = line.count

            AssortmentController.getAssortmentById(line.assortimentUid)?.let {
                assortmentItem = it
            }
            Log.e("TAG", "onViewCreated: assortent item: $assortmentItem")
            Log.e("TAG", "onViewCreated: order item: $orderLine")

            binding.textItemPrice.text = getString(R.string.mdl,"${assortmentItem.Price}")

            if (!assortmentItem.AllowNonIntegerSale) {
                binding.etCountOfItem.setText(selectedValue.toInt().toString())

            } else {
                binding.etCountOfItem.setText(selectedValue.toString())
            }

            binding.etCountOfItem.inputType =
                if (!assortmentItem.AllowNonIntegerSale) {
                    InputType.TYPE_CLASS_NUMBER

                } else {
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
                }

            line.comments.forEach {
                if (isGuid(it)) {
                    listOfComments.add(it)
                    val tryComment = AssortmentController.getCommentById(it)
                    val tryKit = AssortmentController.getAssortmentNameById(it)
                    if (tryComment != null) {
                        if (binding.textComments.text.isBlank()) {
                            binding.textComments.text = tryComment.Comment
                        } else {
                            binding.textComments.append(" | " + tryComment.Comment)
                        }
                    }
                    if (tryKit != "Not found") {
                        if (binding.textComplects.text.isBlank()) {
                            binding.textComplects.text = tryKit
                        } else {
                            binding.textComplects.append(" | $tryKit")
                        }
                    }
                } else {
                    existingComment += it
                    binding.textCustomComments.append(it)
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
                        binding.buttonAddCounts.isEnabled = selectedValue != 0.0
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
                    var curr = try {
                        binding.etCountOfItem.text.toString().toDouble()
                    } catch (e: Exception) {
                        (binding.etCountOfItem.text.toString().replace(",", ".") + 1).toDouble()
                    }
                    if (curr - 1 >= 0) curr -= 1.0
                    binding.etCountOfItem.setText(curr.toString())
                } else {
                    var curr: Int = Integer.valueOf(binding.etCountOfItem.text.toString())
                    if (curr - 1 >= 0) curr -= 1
                    binding.etCountOfItem.setText(curr.toString())
                }
            }

            binding.buttonAddCounts.setOnClickListener {
                if (binding.textCustomComments.text.toString() != existingComment) {
                    listOfComments.add(binding.textCustomComments.text.toString())
                    line.comments = listOfComments
                }
                if (selectedValue != line.count) {
                    CreateBillController.setCartCount(selectedValue - line.count)
                    line.count = selectedValue
                    line.sum = selectedValue * line.price
                    line.sumAfterDiscount = selectedValue * line.price
                }

                findNavController().popBackStack()
            }

            binding.textBack2.setOnClickListener {
                findNavController().popBackStack()
            }

            initToolbar(assortmentItem.Name)
        }
    }

    private fun initToolbar(name: String) {
        val toolbar = binding.toolbar

        toolbar.setTitle(name)
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            findNavController().popBackStack()
        }
    }

    private fun isGuid(str: String): Boolean {
        val regex = ("^[{(]?[0-9A-Fa-f]{8}[-]?(?:[0-9A-Fa-f]{4}[-]?){3}[0-9A-Fa-f]{12}[)}]?$")
        val p1: Pattern = Pattern.compile(regex)
        val m1: Matcher = p1.matcher(str)
        return m1.matches()
    }

}