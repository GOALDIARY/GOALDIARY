package com.example.capstoneproject.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.SubGoalResponse
import com.example.capstoneproject.API.UserProfileResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class SubGoalDetailFragment : Fragment() {

    private var subTitle: String? = null
    private var selectedGoalIndex: Int = -1
    private var subIndex: Int = -1
    private lateinit var nicknameTextView: TextView
    private var buttonId: Int = -1

    private var subGoals: List<SubGoalResponse>? = null // 세부 목표 데이터를 저장할 멤버 변수

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sub_goal_detail, container, false)
        subTitle = arguments?.getString("subGoalTitle")
        selectedGoalIndex = arguments?.getInt("selectedGoalIndex") ?: -1
        subIndex = arguments?.getInt("subGoalId") ?: -1
        buttonId = arguments?.getInt("buttonId") ?: -1

        val goalId: Int = selectedGoalIndex

        if (goalId != null) {
            loadSubGoals(goalId)
        }

        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {
        val subGoalTextView = view.findViewById<TextView>(R.id.subGoalTextView)
        subGoalTextView.text = subTitle
        nicknameTextView = view.findViewById(R.id.user_nickname)

        loadUserProfile()
        val bundle = Bundle().apply {
            putInt("selectedGoalIndex", selectedGoalIndex)
            putInt("selectedSubGoalIndex", subIndex)
            putString("subGoalTitle", subTitle)
            putInt("subGoalId", subIndex)
            putInt("buttonId", buttonId) // 버튼 ID 추가
        }
        // 말하기
        view.findViewById<Button>(R.id.btnSpeak).setOnClickListener {
            findNavController().navigate(R.id.action_subGoalDetailFragment_to_voiceFragment, bundle)
        }

        // 쓰기
        view.findViewById<Button>(R.id.btnWrite).setOnClickListener {
            findNavController().navigate(R.id.action_subGoalDetailFragment_to_writeFragment, bundle)
        }

        view.findViewById<Button>(R.id.btnRecords).setOnClickListener {
            findNavController().navigate(R.id.action_subGoalDetailFragment_to_recordsFragment, bundle)
        }

        // 결과 버튼
        view.findViewById<Button>(R.id.btnResult).setOnClickListener {
            findNavController().navigate(R.id.action_subGoalDetailFragment_to_resultFragment, bundle)
        }

        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedGoalIndex", selectedGoalIndex)
                subGoals?.let {
                    val gson = Gson()
                    val subGoalsJson = gson.toJson(it)
                    putString("subGoals", subGoalsJson)
                }

            }
            findNavController().navigate(R.id.action_subGoalDetailFragment_to_subGoalListFragment, bundle)
        }
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
                    // 오류 처리
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                // 네트워크 오류 처리
            }
        })
    }


    private fun loadSubGoals(goalId: Int) {
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getSubGoals(goalId).awaitResponse()
                if (response.isSuccessful) { // isSuccessful 멤버 함수 사용
                    withContext(Dispatchers.Main) {
                        subGoals = response.body()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "세부 목표 로드 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
