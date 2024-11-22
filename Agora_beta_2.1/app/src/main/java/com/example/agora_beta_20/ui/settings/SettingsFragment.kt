package com.example.agora_beta_20.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.agora_beta_20.R
import com.example.agora_beta_20.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // !! 설정은 다시 하기 !!


        // Account Setting 버튼 클릭 리스너 설정
        binding.cardAccountSetting.setOnClickListener {
            // Account Setting 페이지로 이동하는 로직을 추가하세요
            // 예시: Toast 메시지 표시
            Toast.makeText(requireContext(), "Account Setting Clicked", Toast.LENGTH_SHORT).show()

            // 실제로는 AccountSettingsActivity로 이동하거나, 다른 프래그먼트로 네비게이션
            /*
            val intent = Intent(requireContext(), AccountSettingsActivity::class.java)
            startActivity(intent)
            */
        }

//        // Light/Dark Mode 스위치 초기 상태 설정
//        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
//        binding.switchTheme.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES
//
//        // Light/Dark Mode 스위치 리스너 설정
//        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                // 다크 모드로 전환
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                Toast.makeText(requireContext(), "Dark Mode Enabled", Toast.LENGTH_SHORT).show()
//            } else {
//                // 라이트 모드로 전환
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                Toast.makeText(requireContext(), "Light Mode Enabled", Toast.LENGTH_SHORT).show()
//            }
//            // 액티비티 재시작하여 테마 적용
//            activity?.recreate()
//        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}