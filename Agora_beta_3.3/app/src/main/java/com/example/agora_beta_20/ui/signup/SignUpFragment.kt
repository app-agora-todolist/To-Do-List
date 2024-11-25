package com.example.agora_beta_20.ui.signup

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.agora_beta_20.R
import com.example.agora_beta_20.databinding.FragmentSignUpBinding
import com.example.agora_beta_20.ui.viewmodel.AuthViewModel

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.buttonSignUp.setOnClickListener {
            performSignUp()
        }

        observeViewModel()
        return binding.root
    }

    private fun performSignUp() {
        val username = binding.editTextUsername.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        val email = binding.editTextConfirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(username)) {
            binding.editTextUsername.error = "아이디를 입력해주세요."
            return
        }

        if (TextUtils.isEmpty(password)) {
            binding.editTextPassword.error = "비밀번호를 입력해주세요."
            return
        }

        if (TextUtils.isEmpty(email)) {
            binding.editTextConfirmPassword.error = "이메일을 입력해주세요."
            return
        }

        authViewModel.signUp(username, password, email)
    }

    private fun observeViewModel() {
        authViewModel.loginResult.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            // 회원가입 성공 시 로그인 화면으로 이동
            if (message == "User signed successfully") {
                // Replace with appropriate navigation action
                parentFragmentManager.popBackStack() // 뒤로 이동
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}