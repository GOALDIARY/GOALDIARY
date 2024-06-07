package com.example.capstoneproject.fragment

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
import com.example.capstoneproject.API.SignupRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        val inputName = view.findViewById<EditText>(R.id.InputN)
        val inputId = view.findViewById<EditText>(R.id.InputId)
        val inputPassword = view.findViewById<EditText>(R.id.InputPw)
        val inputPositiveKeyword = view.findViewById<EditText>(R.id.InputPositiveKeyword)

        val loginView = view.findViewById<Button>(R.id.SigninBtn)
        loginView.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        val infoView = view.findViewById<Button>(R.id.NextBtn)
        infoView.setOnClickListener {
            if (inputName.text.isNotEmpty() && inputId.text.isNotEmpty() && inputPassword.text.isNotEmpty() && inputPositiveKeyword.text.isNotEmpty()) {
                val name = inputName.text.toString()
                val id = inputId.text.toString()
                val password = inputPassword.text.toString()
                val positiveKeyword = inputPositiveKeyword.text.toString()

                // positiveKeyword를 List<String> 타입으로 변환
                val positiveKeywordsSet = listOf(positiveKeyword)

                val request = SignupRequest(name, id, password, positiveKeywordsSet)

                RetrofitClient.apiService.signup(request).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                        } else {
                            Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
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
