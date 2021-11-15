package com.jp.smnotestest.ui

import android.view.View
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.jp.smnotestest.R
import com.jp.smnotestest.databinding.ActivityMainBinding
import com.jp.smnotestest.ui.viewmodels.AuthViewModel
import com.jp.smnotestest.ui.viewmodels.MainViewModel
import com.jp.smnotestest.utils.ResultHelper

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val authViewModel: AuthViewModel by viewModels()
    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainNavHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        mainNavHostFragment.findNavController().addOnDestinationChangedListener { c, destination, a ->
            when (destination.id) {
                R.id.createNoteFragment -> {
                    binding.bottomBarContainer.visibility = View.GONE
                }
                else -> {
                    binding.bottomBarContainer.visibility = View.VISIBLE
                }
            }
        }

        /*binding.mainBottomNavigationView.background = null
        binding.mainBottomNavigationView.setupWithNavController(mainNavHostFragment.findNavController())*/

        binding.mainFabNewNote.setOnClickListener {
            mainNavHostFragment.findNavController().navigate(R.id.createNoteFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        authViewModel.signOut()
        authViewModel.signOutStatus.observe(this, { result ->
            result?.let {
                when (it) {
                    is ResultHelper.Success -> {
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is ResultHelper.Error -> {
                        Log.d("auth", "SignOut not Successful")
                    }
                }
            }
        })
    }
}
