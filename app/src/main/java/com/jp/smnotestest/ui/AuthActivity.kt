package com.jp.smnotestest.ui

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jp.smnotestest.R
import com.jp.smnotestest.databinding.ActivityAuthBinding
import com.jp.smnotestest.ui.viewmodels.AuthViewModel

class AuthActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuthBinding
    val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authNavHostFragment = supportFragmentManager.findFragmentById(R.id.authNavHostFragment) as NavHostFragment
        binding.authBottomNavigationView.setupWithNavController(authNavHostFragment.findNavController())
        authNavHostFragment.findNavController()
            .addOnDestinationChangedListener { c, destination, a ->
                when (destination.id) {
                    R.id.loginFragment, R.id.registerFragment -> {
                        binding.authBottomNavigationView.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.authBottomNavigationView.visibility = View.GONE
                    }
                }
            }
    }
}
