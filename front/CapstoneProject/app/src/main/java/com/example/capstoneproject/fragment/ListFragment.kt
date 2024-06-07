package com.example.capstoneproject.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.GoalResponse
import com.example.capstoneproject.API.UserProfileResponse
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListFragment : Fragment() {

    private lateinit var goalsContainer: LinearLayout
    private lateinit var nicknameTextView: TextView
    private lateinit var btnSortByDate: MaterialButton
    private lateinit var btnSortByEmotion: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        goalsContainer = view.findViewById(R.id.goalsContainer)
        nicknameTextView = view.findViewById(R.id.user_nickname)

        btnSortByDate = view.findViewById(R.id.btnSortByDate)
        btnSortByEmotion = view.findViewById(R.id.btnSortByEmotion)

        // Back 버튼 설정
        val btnBack = view.findViewById<Button>(R.id.Back)
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_homeFragment)
        }

        // 정렬 버튼 설정
        btnSortByDate.setOnClickListener {
            sortGoalsByDate()
            updateButtonStyles(btnSortByDate, btnSortByEmotion)
        }

        btnSortByEmotion.setOnClickListener {
            sortGoalsByEmotion()
            updateButtonStyles(btnSortByEmotion, btnSortByDate)
        }

        // 사용자 닉네임 가져오기
        loadUserProfile()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadGoals()  // 프래그먼트가 다시 보일 때마다 목표를 새로 로드
    }

    private fun updateButtonStyles(activeButton: MaterialButton, inactiveButton: MaterialButton) {
        activeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mint))
        activeButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        activeButton.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.mint)

        inactiveButton.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        inactiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.mint))
        inactiveButton.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.mint)
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
                    Log.e("ListFragment", "Failed to fetch user profile: ${response.code()} ${response.message()}")
                    // 오류 처리
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Log.e("ListFragment", "API call failed: ${t.message}")
                // 네트워크 오류 처리
            }
        })
    }

    private fun loadGoals() {
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())

        apiService.getGoals().enqueue(object : Callback<List<GoalResponse>> {
            override fun onResponse(call: Call<List<GoalResponse>>, response: Response<List<GoalResponse>>) {
                if (response.isSuccessful) {
                    val goals = response.body() ?: emptyList()
                    displayGoals(goals)
                } else {
                    Toast.makeText(context, "목표를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<GoalResponse>>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayGoals(goals: List<GoalResponse>) {
        goalsContainer.removeAllViews()
        for ((index, goal) in goals.withIndex()) {
            val goalText = goal.title
            val goalId = goal.id
            val goalButton = createGoalButton(goalText, goalId)
            goalsContainer.addView(goalButton)
        }
    }

    private fun sortGoalsByDate() {
        // 여기에 날짜로 정렬하는 코드를 추가할 수 있습니다.
    }

    private fun sortGoalsByEmotion() {
        // 여기에 감정순으로 정렬하는 코드를 추가할 수 있습니다.
    }

    private fun createGoalButton(goalText: String, index: Int): View {
        val inflater = LayoutInflater.from(context)
        val goalView = inflater.inflate(R.layout.goal_item, goalsContainer, false)

        val btnSetGoals = goalView.findViewById<Button>(R.id.btnSetGoals)
        val deleteButton = goalView.findViewById<Button>(R.id.deleteButton)
        val editButton = goalView.findViewById<Button>(R.id.editButton)

        btnSetGoals.text = goalText

        btnSetGoals.setOnClickListener {
            saveSelectedGoalIndex(index)
//            println("목표 인덱스 : ${index}")
            val bundle = Bundle().apply {
                putInt("selectedGoalIndex", index)
            }
            findNavController().navigate(R.id.action_listFragment_to_proceedingFragment, bundle)
        }

        deleteButton.setOnClickListener {
            deleteGoal(index)
        }

        editButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedGoalIndex", index)
            }
            findNavController().navigate(R.id.action_listFragment_to_correctionFragment, bundle)
        }

        return goalView
    }

    private fun saveSelectedGoalIndex(index: Int) {
        val sharedPrefs = requireActivity().getSharedPreferences("Goals", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putInt("selectedGoalIndex", index)
        editor.apply()
    }

    private fun deleteGoal(index: Int) {
        // 여기에 목표 삭제를 위한 API 호출 코드를 추가할 수 있습니다.
    }
}
