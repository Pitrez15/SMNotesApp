package com.jp.smnotestest.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jp.smnotestest.R
import com.jp.smnotestest.databinding.FragmentSplashScreenBinding
import com.jp.smnotestest.ui.MainActivity
import com.jp.smnotestest.ui.viewmodels.AuthViewModel
import com.jp.smnotestest.utils.ResultHelper

class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verifyLoggedIn()
    }

    private fun verifyLoggedIn() {
        authViewModel.verifyLoggedIn()
        authViewModel.loggedInStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (it) {
                    is ResultHelper.Success -> {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                    is ResultHelper.Error -> {
                        findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
                        Log.d("auth", "User not logged.")
                    }
                }
            }
        })
    }
}