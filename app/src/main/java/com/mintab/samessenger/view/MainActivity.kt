package com.mintab.samessenger.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // nav controller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView_main_activity) as NavHostFragment
        navController = navHostFragment.navController
    }
}