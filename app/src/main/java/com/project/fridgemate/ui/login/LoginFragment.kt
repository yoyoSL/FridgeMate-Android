package com.project.fridgemate.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.fridgemate.ui.auth.AuthFragmentDirections
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentLoginBinding
import com.project.fridgemate.ui.auth.AuthFragment
import com.project.fridgemate.utils.AuthResult

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    companion object {
        private const val ARG_EMAIL = "email"

        fun newInstance(email: String? = null): LoginFragment {
            return LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_EMAIL, email)
                }
            }
        }
    }

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
        arguments?.getString(ARG_EMAIL)?.takeIf { it.isNotBlank() }?.let { email ->
            binding.etEmail.setText(email)
        }
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.validationResult.observe(viewLifecycleOwner) { result ->
            binding.tilEmail.error = result.emailError
            binding.tilPassword.error = result.passwordError
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Loading -> setLoadingState(true)
                is AuthResult.Success -> {
                    setLoadingState(false)
                    val action = AuthFragmentDirections.actionAuthFragmentToDashboardFragment()
                    findNavController().navigate(action)
                }
                is AuthResult.Error -> {
                    setLoadingState(false)
                    binding.tilPassword.error = result.message
                    viewModel.resetLoginResult()
                }
                is AuthResult.Idle -> setLoadingState(false)
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) getString(R.string.logging_in) else getString(R.string.log_in)
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            binding.tilEmail.error = null
            binding.tilPassword.error = null

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.tvForgotPassword.setOnClickListener {
            (parentFragment as? AuthFragment)?.showForgotPassword()
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
