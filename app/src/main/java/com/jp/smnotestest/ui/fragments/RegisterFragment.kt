package com.jp.smnotestest.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jp.smnotestest.R
import com.jp.smnotestest.databinding.FragmentLoginBinding
import com.jp.smnotestest.databinding.FragmentRegisterBinding
import com.jp.smnotestest.ui.MainActivity
import com.jp.smnotestest.ui.viewmodels.AuthViewModel
import com.jp.smnotestest.utils.ResultHelper

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            if (binding.etRegisterName.editText?.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "A name is needed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.etRegisterEmail.editText?.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "An email is needed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.etRegisterPassword.editText?.text.isNullOrBlank() ||
                    binding.etRegisterPassword.editText?.text?.length!! < 6) {
                Toast.makeText(requireContext(), "A password of 6 characters is needed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.etRegisterConfirmPassword.editText?.text.isNullOrBlank() ||
                    (binding.etRegisterConfirmPassword.editText?.text.toString() != binding.etRegisterPassword.editText?.text.toString())) {
                Toast.makeText(requireContext(), "Passwords don't match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            register(
                binding.etRegisterEmail.editText?.text.toString(),
                binding.etRegisterPassword.editText?.text.toString()
            )
        }
    }

    private fun register(email: String, password: String) {
        authViewModel.register(email, password)
        authViewModel.registrationStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (it) {
                    is ResultHelper.Success -> {
                        Toast.makeText(requireContext(), "Registration Completed.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                    is ResultHelper.Error -> {
                        Toast.makeText(requireContext(), "Registration Failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}