package com.example.agora_beta_20.ui.login

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.agora_beta_20.R
import com.example.agora_beta_20.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 클릭 리스너 설정
        binding.buttonLogin.setOnClickListener {
            performLogin()
        }
        binding.buttonSignUp.setOnClickListener {
            navigateToSignUp()
        }

        return root
    }

    private fun performLogin() {
        val username = binding.editTextUsername.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        // 입력 유효성 검사
        if (TextUtils.isEmpty(username)) {
            binding.editTextUsername.error = "아이디를 입력해주세요."
            return
        }
        if (TextUtils.isEmpty(password)) {
            binding.editTextPassword.error = "비밀번호를 입력해주세요."
            return
        }


        // 여기부터 로그인 로직 구현하기
        // 지금은 영어로 아무거나 입력하면 넘어감


        Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_loginFragment_to_navigation_calender)
    }

    private fun navigateToSignUp() {
        // 회원가입 화면으로 이동
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}