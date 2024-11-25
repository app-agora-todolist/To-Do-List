package com.example.agora_beta_20.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora_beta_20.data.api.ApiClient
import com.example.agora_beta_20.data.api.AuthApiService
import com.example.agora_beta_20.data.model.LoginRequest
import com.example.agora_beta_20.data.model.SignUpRequest
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authApi = ApiClient.createService(AuthApiService::class.java)

    val loginResult = MutableLiveData<String>()
    val token = MutableLiveData<String>()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authApi.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    response.body()?.let {
                        loginResult.value = it.message
                        token.value = it.token
                        saveToken(it.token) // 토큰 저장
                    }
                } else {
                    loginResult.value = "로그인 실패: ${response.code()}"
                }
            } catch (e: Exception) {
                loginResult.value = "네트워크 에러: ${e.message}"
            }
        }
    }

    fun signUp(username: String, password: String, email: String) {
        viewModelScope.launch {
            try {
                val response = authApi.signUp(SignUpRequest(username, password, email))
                if (response.isSuccessful) {
                    loginResult.value = response.body()?.message ?: "회원가입 성공"
                } else {
                    loginResult.value = "회원가입 실패: ${response.code()}"
                }
            } catch (e: Exception) {
                loginResult.value = "네트워크 에러: ${e.message}"
            }
        }
    }

    private fun saveToken(token: String?) {
        // SharedPreferences 또는 안전한 저장소에 토큰 저장 (추후 구현)
    }
}