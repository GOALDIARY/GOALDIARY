package com.example.capstoneproject.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.UserProfileResponse
import com.example.capstoneproject.API.SubGoalRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubGoalRegisterFragment : Fragment() {

    private lateinit var subGoalContainer: LinearLayout
    private lateinit var nicknameTextView: TextView
    private lateinit var inputGoal: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sub_goal_register, container, false)

        subGoalContainer = view.findViewById(R.id.subGoalContainer)
        nicknameTextView = view.findViewById(R.id.user_nickname)
        inputGoal = view.findViewById(R.id.InputGoal)
        val goalId = arguments?.getLong("goalId")

        loadUserProfile()

        view.findViewById<Button>(R.id.addSubGoalButton).setOnClickListener {
            addSubGoal()
        }

        view.findViewById<Button>(R.id.RegisterBtn).setOnClickListener {
            val goalText = inputGoal.text.toString()
            val subGoals = getSubGoals()

            if (goalText.isNotEmpty()) {
                subGoals.add(0, SubGoalRequest(goalText, "2023-01-01 00:00", "2023-12-31 00:00")) // 기본 날짜로 추가
            }

            if (subGoals.isNotEmpty()) {
                subGoals.forEach { subGoal ->
                    saveSubGoal(subGoal)
                }
                findNavController().navigate(R.id.action_subGoalRegisterFragment_to_listFragment)
            } else {
                Toast.makeText(context, "목표와 세부 목표를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadUserProfile() {
        val sharedPreferences = requireActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())

        apiService.profile().enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    nicknameTextView.text = userProfile?.name ?: "Nickname"
                } else {
                    nicknameTextView.text = "Nickname"
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                nicknameTextView.text = "Nickname"
            }
        })
    }

    private fun addSubGoal() {
        val inflater = LayoutInflater.from(context)
        val subGoalView = inflater.inflate(R.layout.sub_goal_item, subGoalContainer, false)
        subGoalContainer.addView(subGoalView)
    }

    private fun getSubGoals(): MutableList<SubGoalRequest> {
        val subGoals = mutableListOf<SubGoalRequest>()
        for (i in 0 until subGoalContainer.childCount) {
            val subGoalView = subGoalContainer.getChildAt(i)
            val subGoalTitle = subGoalView.findViewById<EditText>(R.id.InputSubGoal).text.toString()
            val subGoalStartDate = "${subGoalView.findViewById<EditText>(R.id.SubStartYear).text}-${subGoalView.findViewById<EditText>(R.id.SubStartMonth).text}-${subGoalView.findViewById<EditText>(R.id.SubStartDay).text} 00:00"
            val subGoalEndDate = "${subGoalView.findViewById<EditText>(R.id.SubEndYear).text}-${subGoalView.findViewById<EditText>(R.id.SubEndMonth).text}-${subGoalView.findViewById<EditText>(R.id.SubEndDay).text} 00:00"
            subGoals.add(SubGoalRequest(subGoalTitle, subGoalStartDate, subGoalEndDate))
        }
        return subGoals
    }

    private fun saveSubGoal(subGoal: SubGoalRequest) {
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())
        val goalId = arguments?.getLong("goalId")
        if (goalId != null) {
            apiService.createSubGoal(goalId, subGoal).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "세부 목표가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "세부 목표 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
