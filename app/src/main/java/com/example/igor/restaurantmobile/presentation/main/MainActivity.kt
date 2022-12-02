package com.example.igor.restaurantmobile.presentation.main

import android.app.Activity
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.data.database.repository.RepositoryNotification
import com.example.igor.restaurantmobile.databinding.ActivityMainBinding
import com.example.igor.restaurantmobile.presentation.main.viewmodel.MainViewModel
import com.example.igor.restaurantmobile.utils.ContextManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private var requestPingJob: Job? = Job()
    val binding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ContextManager.injectContext(this)

//        val navController = findNavController()
//        val navGraph = navController?.navInflater?.inflate(R.navigation.nav_graph)
//
//        var extras = Bundle()
//
//        val notifyId = intent.getIntExtra("notifyId", -9)
//
//        if(notifyId != -9){
//            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            manager.cancel(notifyId)
//
//            intent.extras?.let {
//                extras = it
//            }
//        }
//
//        navGraph?.setStartDestination(R.id.myBills)
//        navGraph?.let {
//            navController.setGraph(it, extras)
//        }

        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                viewModel.ping()
                delay(5000)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.pingFlow.distinctUntilChanged().collectLatest {
                if (it) {
                    binding.imageConnectedState.setColorFilter(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.colorPrimary
                        )
                    )
                } else {
                    binding.imageConnectedState.setColorFilter(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.red
                        )
                    )
                }
            }
        }

    }

    override fun onNewIntent(intentReceived: Intent?) {
        super.onNewIntent(intentReceived)
        intent = intentReceived
        val billId = intent.getStringExtra("billId")
        val tagNotify = intent.getStringExtra("tag")
        Log.e("TAG", "onNewIntent: $title, $billId, $tagNotify")

        tagNotify?.let {
            val notifyRepository = RepositoryNotification(this)
            lifecycleScope.launch(Dispatchers.IO) {
                notifyRepository.updateNotification(it, System.currentTimeMillis().toString())
            }
        }
        billId?.let {
            Log.e("TAG", "emit bill id from new intent: $billId")
            lifecycleScope.launch(Dispatchers.IO) {
                MyBillsFragment.billFlow.emit(it)
            }
        }

        intent.removeExtra("billId")
        intent.removeExtra("tag")
    }

    override fun onResume() {
        super.onResume()
//        hideSystemUI()

        Log.e("TAG", "onCreate: MainActivity")
        val billId = intent.getStringExtra("billId")
        val tag = intent.getStringExtra("tag")
        billId?.let {
            Log.e("TAG", "bill id from intent: $billId")
            lifecycleScope.launch(Dispatchers.IO) {
                delay(1000)
                MyBillsFragment.billFlow.emit(it)
            }
        }
        tag?.let {
            val notifyRepository = RepositoryNotification(this)
            lifecycleScope.launch(Dispatchers.IO) {
                notifyRepository.updateNotification(it, System.currentTimeMillis().toString())
            }
        }
    }


    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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