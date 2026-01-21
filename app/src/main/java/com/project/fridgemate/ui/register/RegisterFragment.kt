package com.project.fridgemate.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentRegisterBinding
import com.project.fridgemate.ui.auth.AuthFragment

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSignUp.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            var hasError = false
            binding.tilFullName.error = null
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            binding.tilConfirmPassword.error = null

            if (fullName.isEmpty()) {
                binding.tilFullName.error = getString(R.string.error_fill_all_fields)
                hasError = true
            }
            if (email.isEmpty()) {
                binding.tilEmail.error = getString(R.string.error_fill_all_fields)
                hasError = true
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = getString(R.string.error_fill_all_fields)
                hasError = true
            }
            if (confirmPassword.isEmpty()) {
                binding.tilConfirmPassword.error = getString(R.string.error_fill_all_fields)
                hasError = true
            }

            if (!hasError && password != confirmPassword) {
                binding.tilConfirmPassword.error = getString(R.string.error_passwords_dont_match)
                hasError = true
            }

            if (hasError) return@setOnClickListener

            // TODO: Implement registration logic
            (parentFragment as? AuthFragment)?.showLogin()
        }

        binding.tvLogin.setOnClickListener {
            (parentFragment as? AuthFragment)?.showLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
