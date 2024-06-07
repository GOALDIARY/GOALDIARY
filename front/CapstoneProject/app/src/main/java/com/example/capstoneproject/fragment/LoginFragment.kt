package com.example.capstoneproject.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.LoginRequest
import com.example.capstoneproject.API.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val usernameInput = view.findViewById<EditText>(R.id.IdText)
        val passwordInput = view.findViewById<EditText>(R.id.PwText)

        view.findViewById<Button>(R.id.SignupBtn).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        view.findViewById<Button>(R.id.SigninBtn).setOnClickListener {
            val loginId = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (loginId.isNotEmpty() && password.isNotEmpty()) {
                val request = LoginRequest(loginId, password)
                RetrofitClient.apiService.login(request).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()

                            // 토큰 저장
                            val token = response.headers()["access"] ?: ""

                            val sharedPreferences = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putString("auth_token", token)
                                apply()
                            }

                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        } else {
                            Toast.makeText(context, "잘못된 아이디 또는 비밀번호입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(context, "모든 입력 값을 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
