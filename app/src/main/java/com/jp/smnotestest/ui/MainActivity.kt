package com.jp.smnotestest.ui

import android.view.View
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jp.smnotestest.R
import com.jp.smnotestest.databinding.ActivityMainBinding
import com.jp.smnotestest.ui.viewmodels.AuthViewModel
import com.jp.smnotestest.ui.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val authViewModel: AuthViewModel by viewModels()
    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainBottomNavigationView.background = null

        val mainNavHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        binding.mainBottomNavigationView.setupWithNavController(mainNavHostFragment.findNavController())
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
                authViewModel.logout()
                authViewModel.currentUser.observe(this, Observer { currentUser ->
                    if (currentUser == null) {
                        Toast.makeText(this, "Logout Completed", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
