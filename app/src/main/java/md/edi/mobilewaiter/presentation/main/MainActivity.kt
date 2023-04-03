package md.edi.mobilewaiter.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View.OnLongClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.controllers.App
import md.edi.mobilewaiter.data.database.repository.RepositoryNotification
import md.edi.mobilewaiter.data.datastore.SettingsRepository
import md.edi.mobilewaiter.databinding.ActivityMainBinding
import md.edi.mobilewaiter.presentation.main.viewmodel.MainViewModel
import md.edi.mobilewaiter.utils.ContextManager


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private var requestPingJob: Job? = Job()
    val binding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    private val settingsRepository by lazy { SettingsRepository(this) }

    var xDown = 0f
    var yDown = 0f
    var imageMove = false

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

        lifecycleScope.launchWhenCreated{
//            val x = settingsRepository.getPositionX().first()
            val y = settingsRepository.getPositionY().first()
            Log.e("TAG", "onCreate: x:  and y: $y" )
            if( y != 0f){ //x != 0f &&
                withContext(Dispatchers.Main){
//                    binding.imageButton.x = x
                    binding.imageButton.y = y
                }
            }
        }

        binding.imageButton.setOnClickListener {
            Log.e("TAG", "on click : $imageMove" )
            if(!imageMove)
                Toast.makeText(this, "fsdfsdfgsf", Toast.LENGTH_SHORT).show()
            else {
                imageMove = false
                binding.imageButton.setOnTouchListener(null)
            }
        }

        binding.imageButton.setOnLongClickListener {
            it.setOnTouchListener { viewItem, motionEvent ->
                when (motionEvent.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
//                        xDown = motionEvent.x
                        yDown = motionEvent.y
                    }
                    MotionEvent.ACTION_MOVE -> {
//                        val movedX = motionEvent.x
                        val movedY = motionEvent.y
//                        val distanceX = binding.imageButton.x + (movedX - xDown)
                        val distanceY = binding.imageButton.y +  (movedY - yDown)
//                        binding.imageButton.x = distanceX
                        binding.imageButton.y = distanceY

                        lifecycleScope.launch(Dispatchers.IO) {
//                            settingsRepository.setPositionX(distanceX)
                            settingsRepository.setPositionY(distanceY)
                        }

                    }
                }

                false
            }
            imageMove = true
            val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                v.vibrate(80)
            }
            false
        }

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