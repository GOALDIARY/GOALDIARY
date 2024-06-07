package com.example.capstoneproject.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.UserProfileResponse
import com.example.capstoneproject.API.GoalRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    private lateinit var nicknameTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        nicknameTextView = view.findViewById(R.id.user_nickname)

        loadUserProfile()

        view.findViewById<Button>(R.id.RegisterBtn).setOnClickListener {
            val inputGoal = view.findViewById<EditText>(R.id.InputGoal)
            val goalText = inputGoal.text.toString()
            val startDate = "${view.findViewById<EditText>(R.id.StartYear).text}-${view.findViewById<EditText>(R.id.StartMonth).text}-${view.findViewById<EditText>(R.id.StartDay).text} 00:00"
            val endDate = "${view.findViewById<EditText>(R.id.EndYear).text}-${view.findViewById<EditText>(R.id.EndMonth).text}-${view.findViewById<EditText>(R.id.EndDay).text} 00:00"

            // 디버깅을 위해 startDate와 endDate 값을 로그에 출력
            Log.d("RegisterFragment", "StartDate: $startDate, EndDate: $endDate")

            if (goalText.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                saveGoal(goalText, startDate, endDate)
            } else {
                Toast.makeText(context, "모든 입력 값을 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        }

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
                    nicknameTextView.text = "Nickname"
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                // 네트워크 오류 처리
                nicknameTextView.text = "Nickname"
            }
        })
    }
    private fun saveGoal(goal: String, startDate: String, endDate: String) {
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())

        val goalRequest = GoalRequest(goal, startDate, endDate)
        apiService.createGoal(goalRequest).enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    // 세부 목표 입력 페이지로 이동
                    val goalId = response.body() // 최종 목표 ID 가져오기
                    val bundle = Bundle().apply {
                        putString("title", goal)
                        putString("startDate", startDate)
                        putString("endDate", endDate)
                        goalId?.let { putLong("goalId", it) } // 최종 목표 ID를 번들에 추가
                    }
                    println("최종 목표 id : ${goalId}");
                    findNavController().navigate(R.id.action_registerFragment_to_subGoalRegisterFragment, bundle)
                } else {
                    Toast.makeText(context, "목표 저장 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
