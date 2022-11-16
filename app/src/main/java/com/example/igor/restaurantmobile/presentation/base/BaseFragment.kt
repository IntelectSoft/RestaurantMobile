package com.example.igor.restaurantmobile.presentation.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.common.APIResponseStatus
import com.example.igor.restaurantmobile.common.RequestParams
import com.example.igor.restaurantmobile.common.mapApiErrorKeyToResourceString
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class BaseFragment<T : ViewBinding> : Fragment() {


    private lateinit var navController: NavController

    //local use
    private var _binding: T? = null


    @get:IdRes
    var loadingRootId: Int = 0

    var screenRequestParams = RequestParams()

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): T?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(false)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)

        return _binding?.root!!
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard()
        val rootView = view.findViewById<RelativeLayout>(loadingRootId)

        try {
            navController = findNavController()
        } catch (E: Exception) {

        }
    }


    fun setBackgroundScreen(view: View, color: Int) {
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }

    fun reloadScreen(id: Int) {
        findNavController().popBackStack()
        findNavController().navigate(id)
    }

    fun getNavController(): NavController {
        return navController
    }

    fun navigateToExtraInfo(destination: NavDirections, extraInfo: FragmentNavigator.Extras) =
        with(findNavController()) {
            currentDestination?.getAction(destination.actionId)
                ?.let { navigate(destination, extraInfo) }
        }

    fun navigateToMain(@IdRes destinationIdRes: Int) {
        getNavController().navigate(destinationIdRes)
    }

    fun navigateToAnimated(navDirections: NavDirections) {
        getNavController().navigate(navDirections, getNavOptionsRightToLeft(getNavBuilder()))
    }

    fun navigateToAnimatedLeft(navDirections: NavDirections) {
        getNavController().navigate(navDirections, getNavOptionsLeftToRight(getNavBuilder()))
    }

    fun navigateToAnimatedPopBackStack(
        navDirections: NavDirections,
        @IdRes destinationIdRes: Int,
        popInclusive: Boolean = false
    ) {
        hideKeyboard()
        val builder = getNavBuilder()
        builder.setPopUpTo(destinationIdRes, popInclusive)
        getNavController().navigate(
            navDirections,
            getNavOptionsRightToLeft(builder)
        )
    }

    fun navigateToAnimatedTop(navDirections: NavDirections) {
        hideKeyboard()
        getNavController().navigate(navDirections, getNavOptionsUp(getNavBuilder()))
    }

    fun clearBackStack(navDirections: NavDirections) {
        hideKeyboard()
        getNavController().navigate(navDirections);
    }

    fun navigatePopBackStack(): Boolean {
        hideKeyboard()
        return getNavController().popBackStack()
    }

    fun navigatePopBackStack(
        @IdRes destinationIdRes: Int,
        popInclusive: Boolean = false
    ): Boolean {
        hideKeyboard()
        return getNavController().popBackStack(destinationIdRes, popInclusive)
    }

    fun navigateUp() {
        hideKeyboard()
        getNavController().navigateUp()
    }

    open fun hideKeyboard() {
        activity?.let {
            val view = it.currentFocus
            if (view != null) {
                val imm =
                    it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

    }

    open fun showKeyboard() {
        activity?.let {
            val view = it.currentFocus
            if (view != null) {
                val imm =
                    it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun getNavBuilder(): NavOptions.Builder {
        return NavOptions.Builder()

    }

    private fun getNavOptionsRightToLeft(builder: NavOptions.Builder): NavOptions? {
        return builder
            .setEnterAnim(R.anim.h_fragment_enter)
            .setExitAnim(R.anim.h_fragment_pop_exit)
            .setPopEnterAnim(R.anim.h_fragment_pop_enter)
            .setPopExitAnim(R.anim.h_fragment_exit)
            .build()
    }

    private fun getNavOptionsLeftToRight(builder: NavOptions.Builder): NavOptions? {
        return builder
            .setEnterAnim(R.anim.h_fragment_enter_left)
            .setExitAnim(R.anim.h_fragment_pop_exit_left)
            .setPopEnterAnim(R.anim.h_fragment_pop_enter)
            .setPopExitAnim(R.anim.h_fragment_exit)
            .build()
    }

    private fun getNavOptionsUp(builder: NavOptions.Builder): NavOptions? {
        return builder
            .setEnterAnim(R.anim.fragment_slide_in_bottom)
            .setExitAnim(R.anim.v_fragment_pop_exit)
            .setPopEnterAnim(R.anim.v_fragment_pop_enter)
            .setPopExitAnim(R.anim.fragment_slide_out_bottom)
            .build()
    }

    private fun getNavOptionsTop(): NavOptions? {
        return NavOptions.Builder()
            .build()
    }

    fun getNavigationResult(key: String = "result") =
        try {
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(key)
        } catch (e: java.lang.Exception) {
            MutableLiveData<String>(key)
        }

    fun setNavigationResult(result: String?, key: String = "result") {
        try {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
        } catch (e: java.lang.Exception) {
        }
    }


    private fun processApiError(error: APIResponseStatus.Error) {
        when (error) {
            is APIResponseStatus.Error.NoInternetConnectionError -> {
                if (screenRequestParams.isShowNoConnection) {
                    showNoInternetConnectionError()
                }
            }
            is APIResponseStatus.Error.CertificateExpirationError -> {
                if (screenRequestParams.isShowUnauthorizedError) {
                    val errorMessage = error.mapApiErrorKeyToResourceString(
                        gson = Gson(),
                        requireActivity()
                    ) ?: ""
                    showError(errorMessage)
                }
            }
            is APIResponseStatus.Error.HttpError.RequestHttpUnauthorizedError -> {
                if (screenRequestParams.isShowUnauthorizedError) {
                    val errorMessage = error.mapApiErrorKeyToResourceString(
                        gson = Gson(),
                        requireActivity()
                    ) ?: ""
                    showError(errorMessage)
                }
            }
            is APIResponseStatus.Error.HttpError.RequestHttpNotFoundError -> {
                if (screenRequestParams.isShowHttpError) {
                    showPageWasNotFoundError()
                }
            }

            is APIResponseStatus.Error.HttpError.RequestHttpError -> {
                hideKeyboard()
                if (screenRequestParams.isShowHttpError) {
                    showHttpError(error)
                }
            }

            is APIResponseStatus.Error.RequestGeneralServerError -> {
                //if(screenRequestParams.isShowHttpError){
                val errorMessage = error.mapApiErrorKeyToResourceString(
                    gson = Gson(),
                    requireActivity()
                ) ?: ""
                showError(errorMessage)
                //}
            }
        }
    }

    protected open fun showHttpError(error: APIResponseStatus.Error) {
        val errorMessage = error.mapApiErrorKeyToResourceString(
            gson = Gson(),
            requireActivity()
        ) ?: ""
        showError(errorMessage)
    }

    protected open fun showNoInternetConnectionError() {
        context?.let {
            DialogAction(
                it,
                title = "No internet connection",
                titleColor = Color.parseColor("#D82F2F"),
                description = "error_no_internet_connection",
                okButtonLabel = "OK"
            ).show()
        }
    }

    protected open fun showPageWasNotFoundError() {
        context?.let {
            DialogAction(
                it,
                title = "Error",
                titleColor = Color.parseColor("#D82F2F"),
                description = "Page not found",
                okButtonLabel = "OK"
            ).show()
        }
    }

    protected open fun showError(message: String) {
        context?.let {
            DialogAction(
                it,
                title = "Error",
                titleColor = Color.parseColor("#D82F2F"),
                description = message,
                okButtonLabel = "Ok"
            ).show()
        }
    }


    fun setFragmentResultListener(key: String = "result") =
        try {
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(key)
        } catch (e: java.lang.Exception) {
            MutableLiveData(key)
        }

    fun setFragmentResult(key: String = "result", bundle: Bundle) {
        try {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(key, bundle)
        } catch (e: java.lang.Exception) {
        }
    }
}