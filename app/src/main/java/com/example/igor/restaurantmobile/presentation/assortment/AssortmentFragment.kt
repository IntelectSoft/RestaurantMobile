package com.example.igor.restaurantmobile.presentation.assortment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.controllers.App
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.databinding.FragmentAssortimentBinding
import com.example.igor.restaurantmobile.presentation.assortment.counts.CountAssortmentDialogFragment
import com.example.igor.restaurantmobile.presentation.assortment.items.ItemAssortmentDelegate
import com.example.igor.restaurantmobile.presentation.assortment.viewmodels.AssortmentViewModel
import com.example.igor.restaurantmobile.presentation.main.BillActionDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.distinctUntilChanged
import ro.lensa.common.decor.GridSpanDecoration
import ro.lensa.common.recycler_view.delegates.CompositeAdapter

@AndroidEntryPoint
class AssortmentFragment : Fragment() {

    val viewModel by viewModels<AssortmentViewModel>()
    private lateinit var layoutManager: LinearLayoutManager

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemAssortmentDelegate { item ->
                if(item.IsFolder){
                    lifecycleScope.launch{
                        viewModel.getChildAssortment(item.Uid)
                        viewModel.assortmentChildList.collectLatest {
                            notifyAdapter(it)
                        }
                    }
                }
                else{
                    CountAssortmentDialogFragment.newInstance(id = item.Uid, priceId = item.PricelineUid, name = item.Name).show(parentFragmentManager, "")
                }
            })

            .build()
    }

    private fun notifyAdapter(delegateAdapterItems: List<DelegateAdapterItem>) {
        compositeAdapter.submitList(delegateAdapterItems)
        lifecycleScope.launch(Dispatchers.Main){
            delay(250)
            binding.assortmentList.smoothScrollToPosition(0)
        }

    }

    val binding by lazy {
        FragmentAssortimentBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lifecycleScope.launch{
            viewModel.getDefaultAssortment()
            viewModel.assortmentList.collectLatest {
                initList(it)
            }
        }

        lifecycleScope.launch {
            CreateBillController.countCartFlow.collectLatest {
                Log.e("TAG", "onCreateView: badges count ${it} " )
                binding.toolbar.setCartCount(it)
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

        toolbar.setTitle("Adauga produse")
        toolbar.showBottomLine(true)

        toolbar.setRightIcon(R.drawable.ic_cart){
            App.instance.navigateToAnimated(findNavController(), AssortmentFragmentDirections.actionAssortmentListToPreviewCart())
        }

        toolbar.setSecondRightIcon(R.drawable.ic_search){

        }

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            findNavController().popBackStack(R.id.myBills, false)
        }
    }

    private fun initList(items: List<DelegateAdapterItem>) {
        binding.assortmentList.adapter = compositeAdapter

        layoutManager = LinearLayoutManager(context)
        binding.assortmentList.layoutManager = layoutManager

        compositeAdapter.submitList(items)
    }

}