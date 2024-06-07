package com.example.capstoneproject.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var nicknameTextView: TextView

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        nicknameTextView = view.findViewById(R.id.user_nickname)

        // '목표 설정하기' 버튼 설정
        val btnSetGoals = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnSetGoals)
        btnSetGoals.setOnClickListener {
            // RegisterFragment로 네비게이션
            findNavController().navigate(R.id.action_homeFragment_to_registerFragment)
        }

        // '목표 리스트 보기' 버튼 설정
        val btnViewGoals = view.findViewById<android.widget.Button>(R.id.btnViewGoals)
        btnViewGoals.setOnClickListener {
            // ListFragment로 네비게이션
            findNavController().navigate(R.id.action_homeFragment_to_listFragment)
        }

        // 사용자 프로필 가져오기
        loadUserProfile()

        return view
    }

    private fun loadUserProfile() {
        val sharedPreferences = requireActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null) ?: return

        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())

        apiService.profile().enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    nicknameTextView.text = userProfile?.name ?: "Nickname"
                } else {
                    // 오류 처리
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                // 네트워크 오류 처리
            }
        })
    }
}
