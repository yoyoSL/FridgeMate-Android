package com.project.fridgemate.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentLoginBinding
import com.project.fridgemate.ui.auth.AuthFragment

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Parent AuthFragment should handle global progress if needed, 
            // but we can also disable our button
            binding.btnLogin.isEnabled = !isLoading
        }

        viewModel.loginStatus.observe(viewLifecycleOwner) { status ->
            status?.let {
                binding.tilEmail.error = it
                viewModel.clearStatus()
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            
            var hasError = false
            binding.tilEmail.error = null
            binding.tilPassword.error = null

            if (email.isBlank()) {
                binding.tilEmail.error = "Please enter email"
                hasError = true
            }
            if (password.isBlank()) {
                binding.tilPassword.error = "Please enter password"
                hasError = true
            }

            if (hasError) return@setOnClickListener
            
            // Navigate to Dashboard
            findNavController().navigate(R.id.action_authFragment_to_dashboardFragment)
        }

        binding.tvForgotPassword.setOnClickListener {
            // TODO: Navigate to Forgot Password
        }

        binding.tvSignUp.setOnClickListener {
            (parentFragment as? AuthFragment)?.showRegister()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
