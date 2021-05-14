package com.dino.todo.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.dino.todo.R
import com.dino.todo.databinding.ActivitySplashBinding
import com.dino.todo.home.HomeActivity
import com.dino.todo.utility.Constants.ConstantVariables.SPLASH_TIME_OUT

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var mDelayHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        mDelayHandler = Handler()
        mDelayHandler.postDelayed(mRunnable, SPLASH_TIME_OUT)
    }

    //Open home screen after 1second
    private val mRunnable: Runnable = Runnable {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}