package com.project.fridgemate.ui.login

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentLoginBinding

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

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        // Apply gradient to app title
        binding.appName.post {
            val paint = binding.appName.paint
            val width = paint.measureText(binding.appName.text.toString())
            val textShader: Shader = LinearGradient(
                0f, 0f, width, binding.appName.textSize,
                intArrayOf(
                    android.graphics.Color.parseColor("#00BC7D"),
                    android.graphics.Color.parseColor("#00B8DB")
                ), null, Shader.TileMode.CLAMP
            )
            binding.appName.paint.shader = textShader
            binding.appName.invalidate()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
        }

        viewModel.loginStatus.observe(viewLifecycleOwner) { status ->
            status?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearStatus()
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Navigate to Dashboard
            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
        }

        binding.tvForgotPassword.setOnClickListener {
            // TODO: Navigate to Forgot Password
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
