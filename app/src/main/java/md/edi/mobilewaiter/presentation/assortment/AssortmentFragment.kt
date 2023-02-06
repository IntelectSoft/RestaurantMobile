package md.edi.mobilewaiter.presentation.assortment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.controllers.App
import md.edi.mobilewaiter.controllers.CreateBillController
import md.edi.mobilewaiter.databinding.FragmentAssortimentBinding
import md.edi.mobilewaiter.presentation.assortment.items.ItemAssortmentDelegate
import md.edi.mobilewaiter.presentation.assortment.viewmodels.AssortmentViewModel
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import md.edi.mobilewaiter.common.delegates.CompositeAdapter

@AndroidEntryPoint
class AssortmentFragment : Fragment() {

    val viewModel by viewModels<AssortmentViewModel>()
    private lateinit var layoutManager: LinearLayoutManager

    private var requestSearchJob: Job? = Job()
    private var searchTextQuery = ""
    private lateinit var searchView: SearchView

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemAssortmentDelegate { item ->
                if (item.IsFolder) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.lastParentFolder = item.Uid
                        viewModel.getChildAssortment(item.Uid, item.ParentUid)
                        viewModel.assortmentChildList.collectLatest {
                            notifyAdapter(it)
                        }
                    }
                } else {
                    val asortmentActivity = Intent(requireContext(), AssortmentInsertActivity::class.java)
                    asortmentActivity.putExtra("itemId", item.Uid)
                    startActivity(asortmentActivity)
                }
            })

            .build()
    }

    private fun notifyAdapter(delegateAdapterItems: List<DelegateAdapterItem>) {
        compositeAdapter.submitList(delegateAdapterItems)
        lifecycleScope.launch(Dispatchers.Main) {
            delay(250)
            binding.assortmentListView.smoothScrollToPosition(0)
        }
    }

    val binding by lazy {
        FragmentAssortimentBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e("TAG", "onCreateView: ")

        lifecycleScope.launchWhenResumed {
            CreateBillController.countCartFlow.collectLatest {
                Log.e("TAG", "onCreateView: badges count ${it} ")
                binding.toolbar.setCartCount(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            if(searchTextQuery.isNotBlank()  && viewModel.listUidNavigation.isNotEmpty()){
                viewModel.getChildAssortment(viewModel.lastParentFolder)
                viewModel.assortmentChildList.collectLatest {
                    Log.e("TAG", "assortmentChildList.collectLatest when resume otBlank()  && viewMod")
                    notifyAdapter(it)
                }
            }
            else if (searchTextQuery.isNotBlank()) {
                searchView.requestFocus()
                val imm: InputMethodManager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchView.findViewById(androidx.appcompat.R.id.search_src_text), InputMethodManager.RESULT_SHOWN)
                viewModel.searchAssortment(searchTextQuery)
                viewModel.assortmentChildList.collectLatest {
                    Log.e("TAG", "assortmentChildList.collectLatest when resume searchTextQuery.isNotBlank()")
                    notifyAdapter(it)
                }
            } else if (viewModel.listUidNavigation.isNotEmpty()) {
                val lastUi = viewModel.listUidNavigation.last()
                viewModel.getChildAssortment(lastUi)
                viewModel.assortmentChildList.collectLatest {
                    Log.e("TAG", "assortmentChildList.collectLatest when resume viewModel.listUidNavigation.isNotEmpty()")
                    notifyAdapter(it)
                }
            }
            else{
                viewModel.getDefaultAssortment()
                viewModel.assortmentList.collectLatest {
                    Log.e("TAG", "assortmentList.collectLatest when resumed")
                    initList(it)
                }
            }
        }

        initToolbar()

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackStack()
                }
            })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("TAG", "onViewCreated: ")
    }

    private fun initToolbar() {
        Log.e("TAG", "init toolbar")
        val toolbar = binding.toolbar

        toolbar.setTitle("Adauga produse")
        toolbar.showBottomLine(true)
        toolbar.showLeftBtn(true)

        toolbar.setRightIcon(R.drawable.ic_cart) {
            App.instance.navigateToAnimated(
                findNavController(),
                AssortmentFragmentDirections.actionAssortmentListToPreviewCart()
            )
        }

        toolbar.setSecondRightIcon(R.drawable.ic_search) {
            toolbar.showSearchText(true)
        }


        toolbar.setLeftClickListener {
            onBackStack()
        }

        searchView = toolbar.getSearchView()
        searchView.setOnCloseListener {
            if (searchView.query.toString().isBlank())
                toolbar.showSearchText(false)
            else
                searchView.setQuery("", false)

            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(searchText: String): Boolean {
                Log.e("TAG", "onQueryTextChange: $searchText")
                searchTextQuery = searchText
                if (searchText != "") {
                    if (searchText.length > 3) {
                        requestSearchJob?.cancel()
                        requestSearchJob = lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.searchAssortment(searchText)
                            viewModel.assortmentChildList.collectLatest {
                                Log.e("TAG", "assortmentChildList.collectLatest request job ")
                                notifyAdapter(it)
                            }
                            delay(1200)
                            requestSearchJob?.ensureActive()
                        }
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.listUidNavigation.clear()
                        viewModel.getDefaultAssortment()
                        viewModel.assortmentList.collectLatest {
                            Log.e("TAG", "assortmentList.collectLatest empty search string ")
                            notifyAdapter(it)
                        }
                    }
                }

                return true
            }
        })
    }

    private fun onBackStack() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (viewModel.getBackPage()) {
                if (CreateBillController.orderModel.Orders.isNotEmpty()) {
                    dialogShow(
                        "Atentie",
                        "Contul nu a fost salvat, sunteti sigur ca doriti sa inchideti pagina?"
                    )
                } else {
                    findNavController().popBackStack(R.id.myBills, false)
                }
            } else {
                viewModel.assortmentChildList.collectLatest {
                    Log.e("TAG", "assortmentChildList.collectLatest onback stack ")
                    notifyAdapter(it)
                }
            }
        }
    }

    private fun initList(items: List<DelegateAdapterItem>) {
        binding.assortmentListView.adapter = compositeAdapter

        layoutManager = LinearLayoutManager(context)
        binding.assortmentListView.layoutManager = layoutManager

        compositeAdapter.submitList(items)
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAG", "onQueryTextChange on resume: $searchTextQuery")
    }

    private fun dialogShow(title: String, description: String) {
        DialogAction(requireActivity(), title, description, "Da", "Nu", {
            it.dismiss()
            lifecycleScope.launch(Dispatchers.IO) {
                CreateBillController.clearAllData()
            }
            findNavController().popBackStack(R.id.myBills, false)
        }, {
            it.dismiss()
        }).show()
    }
}