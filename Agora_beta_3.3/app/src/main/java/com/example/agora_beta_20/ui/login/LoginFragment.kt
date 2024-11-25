package com.example.agora_beta_20.ui.login

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agora_beta_20.R
import com.example.agora_beta_20.databinding.FragmentLoginBinding
import com.example.agora_beta_20.data.model.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel




//        일단 임시로 넘어가게
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // 로그인 버튼 클릭 시 다음 페이지로 바로 이동
        binding.buttonLogin.setOnClickListener {
            navigateToNextPage()
        }

        binding.buttonSignUp.setOnClickListener {
            navigateToSignUp()
        }

        return binding.root
    }

    private fun navigateToNextPage() {
        // 서버 통신 없이 바로 다음 페이지로 이동
        findNavController().navigate(R.id.action_loginFragment_to_navigation_calender)
    }


////    실제 로그인 서버요청 코드
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentLoginBinding.inflate(inflater, container, false)
//        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
//
//        binding.buttonLogin.setOnClickListener {
//            performLogin()
//        }
//
//        binding.buttonSignUp.setOnClickListener {
//            navigateToSignUp()
//        }
//
//        observeViewModel()
//        return binding.root
//    }
//
//    private fun performLogin() {
//        val username = binding.editTextUsername.text.toString().trim()
//        val password = binding.editTextPassword.text.toString().trim()
//
//        if (TextUtils.isEmpty(username)) {
//            binding.editTextUsername.error = "아이디를 입력해주세요."
//            return
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            binding.editTextPassword.error = "비밀번호를 입력해주세요."
//            return
//        }
//
//        authViewModel.login(username, password)
//    }
//
//    private fun observeViewModel() {
//        authViewModel.loginResult.observe(viewLifecycleOwner) { message ->
//            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//
//            // 로그인 성공 시
//            if (message == "Login successful") {
//                authViewModel.token.observe(viewLifecycleOwner) { token ->
//                    saveToken(token) // 토큰 저장
//                    findNavController().navigate(R.id.action_loginFragment_to_navigation_calender)
//                }
//            }
//        }
//    }




//    토큰 관리 코드
    private fun saveToken(token: String?) {
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "토큰 저장 실패", Toast.LENGTH_SHORT).show()
            return
        }
        val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("auth_token", token)
            apply()
        }
        Toast.makeText(requireContext(), "토큰 저장 완료", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToSignUp() {
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}