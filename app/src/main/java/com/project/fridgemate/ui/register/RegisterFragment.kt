package com.project.fridgemate.ui.register

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentRegisterBinding

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

        setupUI()
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

    private fun setupListeners() {
        binding.btnSignUp.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(context, getString(R.string.error_passwords_dont_match), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Implement registration logic
            Toast.makeText(context, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
