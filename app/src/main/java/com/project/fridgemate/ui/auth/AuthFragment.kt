package com.project.fridgemate.ui.auth

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentAuthBinding
import com.project.fridgemate.ui.login.LoginFragment

class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.auth_container, LoginFragment())
                .commit()
        }
    }

    private fun setupUI() {
        // Apply gradient to app title
        binding.appName.post {
            val paint = binding.appName.paint
            val width = paint.measureText(binding.appName.text.toString())
            val startColor = ContextCompat.getColor(requireContext(), R.color.title_gradient_start)
            val endColor = ContextCompat.getColor(requireContext(), R.color.title_gradient_end)
            
            val textShader: Shader = LinearGradient(
                0f, 0f, width, binding.appName.textSize,
                intArrayOf(startColor, endColor), null, Shader.TileMode.CLAMP
            )
            binding.appName.paint.shader = textShader
            binding.appName.invalidate()
        }
    }

    fun showRegister() {
        childFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.auth_container, com.project.fridgemate.ui.register.RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    fun showLogin() {
        childFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
