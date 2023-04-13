package md.edi.mobilewaiter.presentation.main

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.common.decor.GridSpanDecoration
import md.edi.mobilewaiter.common.delegates.CompositeAdapter
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.controllers.App
import md.edi.mobilewaiter.controllers.BillsController
import md.edi.mobilewaiter.data.listeners.ActionOnBillListener
import md.edi.mobilewaiter.databinding.FragmentMyBillsBinding
import md.edi.mobilewaiter.presentation.bill_details.BillDetailsFragment
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import md.edi.mobilewaiter.presentation.main.items.ItemBillDelegate
import md.edi.mobilewaiter.presentation.main.viewmodel.MainViewModel
import md.edi.mobilewaiter.utils.ErrorHandler
import md.edi.mobilewaiter.utils.enums.EnumRemoteErrors

@AndroidEntryPoint
class MyBillsFragment : Fragment(), ActionOnBillListener {

    val mainViewModel by viewModels<MainViewModel>()
    private lateinit var layoutManager: GridLayoutManager
    private var spanCount = 2
    val progressDialog by lazy { ProgressDialog(requireContext()) }
    private var pairToCombine = false
    private var firstBillCombineNumber = 0

    private val TIME_INTERVAL = 2000
    private var mBackPressed: Long = 0

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(
                ItemBillDelegate({ bill ->
                    if (pairToCombine) {
                        mainViewModel.arrayCombine.add(bill.Uid)
                        if (mainViewModel.arrayCombine.size == 2) {
                            dialogConfirmCombineBillsShow("Atentie!", "Confirmati ca doriti sa comasati contul $firstBillCombineNumber in contul ${bill.Number} ?")
                        }
                    } else {
                        App.instance.navigateToAnimated(
                            findNavController(),
                            MyBillsFragmentDirections.actionMyBillsToBillsDetails(bill.Uid)
                        )
                    }

                }, { billLongClick ->
                    if (!pairToCombine) {
                        pairToCombine = true
                        firstBillCombineNumber = billLongClick.Number
                        mainViewModel.arrayCombine.add(billLongClick.Uid)
                        binding.toolbar.setShowCombineMenu(true)
                        binding.toolbar.setCombineMenuTitle("Combina contul ${billLongClick.Number} cu -> ")
                        binding.toolbar.setCloseCombineMenuClickListener {
                            pairToCombine = false
                            binding.toolbar.setShowCombineMenu(false)
                            mainViewModel.arrayCombine.clear()
                            lifecycleScope.launch(Dispatchers.Main) {
                                mainViewModel.initData(BillsController.billsBody, null)
                            }
                        }
                        lifecycleScope.launch(Dispatchers.Main) {
                            mainViewModel.initData(BillsController.billsBody, billLongClick.Uid)
                        }
                    }
                })
            )
            .build()
    }

    val binding by lazy {
        FragmentMyBillsBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lifecycleScope.launch(Dispatchers.Main) {
            mainViewModel.billListResponse.collectLatest {
                initList(it)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            mainViewModel.getBillListResult.collectLatest {
                progressDialog.dismiss()
                it.let {
                    when (it.Result) {
                        0 -> {
                            BillsController.setBillResponse(it)
                            mainViewModel.initData(BillsController.billsBody)
                        }
                        -9 -> {
                            dialogGetBillsShow("Eroare la obtinerea conturilor", it.ResultMessage.toString())
                        }
                        else -> {
                            dialogGetBillsShow(
                                "Eroare la obtinerea conturilor",
                                ErrorHandler().getErrorMessage(
                                    EnumRemoteErrors.getByValue(it.Result)
                                )
                            )
                        }
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            billFlow.distinctUntilChanged().collectLatest {
                Log.e("TAG", "distinctUntilChanged: $it ")
                val billFind = BillsController.getBillById(it)
                if (billFind != null) {
                    App.instance.navigateToAnimated(
                        findNavController(),
                        MyBillsFragmentDirections.actionMyBillsToBillsDetails(billFind.Uid)
                    )
                } else {
                    Toast.makeText(requireContext(), "Contul nu a fost gasit!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        initToolbar()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                        requireActivity().finish()
                    }
                    else {
                        Toast.makeText(requireContext(), "Mai tastati odata pentru a iesi din aplicatie!", Toast.LENGTH_SHORT).show()
                    }
                    mBackPressed = System.currentTimeMillis()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    companion object {
        val billFlow = MutableSharedFlow<String>()
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar
        toolbar.setRightIcon(R.drawable.ic_notification_bell) {
            App.instance.navigateToAnimated(
                findNavController(),
                MyBillsFragmentDirections.actionMyBillsToFragmentNotification()
            )
        }

        toolbar.setSecondRightIcon(R.drawable.ic_plus) {
            App.instance.navigateToAnimated(
                findNavController(),
                MyBillsFragmentDirections.actionMyBillsToTableList("")
            )
        }

        toolbar.setTitle("Conturile mele")
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftIcon(R.drawable.ic_settings) {
            App.instance.navigateToAnimated(
                findNavController(),
                MyBillsFragmentDirections.actionMyBillsToFragmentSettings()
            )
        }
    }

    private fun notifyAdapter(delegateAdapterItems: List<DelegateAdapterItem>) {
        compositeAdapter.submitList(delegateAdapterItems)
        lifecycleScope.launch(Dispatchers.Main) {
            delay(250)
            binding.recycler.smoothScrollToPosition(0)
        }
    }

    private fun initList(items: List<DelegateAdapterItem>) {
        binding.recycler.adapter = compositeAdapter

        layoutManager = GridLayoutManager(context, 3)
        binding.recycler.layoutManager = layoutManager

        binding.recycler.smoothScrollToPosition(compositeAdapter.itemCount)

        val deco = GridSpanDecoration(
            binding.root.context.resources.getDimensionPixelSize(
                com.intuit.sdp.R.dimen._4sdp
            ),
            true
        )

        while (binding.recycler.itemDecorationCount > 0) {
            binding.recycler.removeItemDecorationAt(0)
        }

        binding.recycler.addItemDecoration(deco)

        compositeAdapter.submitList(items)
    }

    override fun onCloseBill(dialogs: BillDetailsFragment) {
        lifecycleScope.launch(Dispatchers.IO) {
            mainViewModel.getMyBills()
            mainViewModel.billListResponse.collectLatest {
                notifyAdapter(it)
            }
        }
    }

    private fun dialogCombineBillsShow(title: String?, description: String?) {
        DialogAction(requireActivity(), title, description, "OK", "Renunta", {
            it.dismiss()
        }, {
            it.dismiss()
        }).show()
    }

    private fun dialogConfirmCombineBillsShow(title: String?, description: String?) {
        DialogAction(requireActivity(), title, description, "Da", "Renunta", {
            it.dismiss()
            lifecycleScope.launch(Dispatchers.Main) {
                progressDialog.setMessage("Va rugam asteptati...")
                progressDialog.show()
                mainViewModel.combineBill()
            }
            lifecycleScope.launch(Dispatchers.Main) {
                mainViewModel.combineBillsResult.collectLatest {
                    pairToCombine = false
                    binding.toolbar.setShowCombineMenu(false)
                    mainViewModel.arrayCombine.clear()
                    it.let {
                        when (it.Result) {
                            0 -> {
                                mainViewModel.getMyBills()
                            }
                            -9 -> {
                                progressDialog.dismiss()
                                dialogCombineBillsShow("Eroare la comasarea conturilor", it.ResultMessage.toString())
                            }
                            else -> {
                                progressDialog.dismiss()
                                dialogCombineBillsShow(
                                    "Eroare la comasarea conturilor",
                                    ErrorHandler().getErrorMessage(
                                        EnumRemoteErrors.getByValue(it.Result)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }, {
            it.dismiss()
            mainViewModel.arrayCombine.remove(mainViewModel.arrayCombine.last())
        }).show()
    }

    private fun dialogGetBillsShow(title: String?, description: String?) {
        DialogAction(requireActivity(), title, description, "Reincearca", "Renunta", {
            it.dismiss()
            lifecycleScope.launch(Dispatchers.Main) {
                progressDialog.setMessage("Va rugam asteptati...")
                progressDialog.show()
                mainViewModel.getMyBills()
            }
        }, {
            requireActivity().finish()
        }).show()
    }

    override fun onResume() {
        super.onResume()
        activity?.let{
            val cout =  childFragmentManager.backStackEntryCount
            if(cout > 1){
                childFragmentManager.popBackStackImmediate()
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            mainViewModel.getMyBills()
        }
    }
}