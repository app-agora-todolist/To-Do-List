package com.example.agora_beta_20.ui.signup

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.agora_beta_20.R
import com.example.agora_beta_20.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 회원가입 버튼 클릭 리스너 설정
        binding.buttonSignUp.setOnClickListener {
            performSignUp()
        }

        return root
    }

    private fun performSignUp() {
        val username = binding.editTextUsername.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

        // 입력 유효성 검사
        if (TextUtils.isEmpty(username)) {
            binding.editTextUsername.error = "아이디를 입력해주세요."
            return
        }

        if (TextUtils.isEmpty(password)) {
            binding.editTextPassword.error = "비밀번호를 입력해주세요."
            return
        }

        if (password != confirmPassword) {
            binding.editTextConfirmPassword.error = "비밀번호가 일치하지 않습니다."
            return
        }


        // !! 로직 구현하기 !!
        // 지금은 그냥 넘어감
        Toast.makeText(requireContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}