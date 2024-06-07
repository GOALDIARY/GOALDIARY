package com.example.capstoneproject.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.API.GoalResponse
import com.example.capstoneproject.API.SubGoalResponse
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.UserProfileResponse
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.awaitResponse

class ProceedingFragment : Fragment() {

    private var progressIndicator: CircularProgressIndicator? = null
    private var progressText: TextView? = null
    private var goalText: TextView? = null
    private var subGoalContainer: LinearLayout? = null
    private var selectedGoalIndex: Int = -1
    private lateinit var nicknameTextView: TextView

    private var subGoals: List<SubGoalResponse>? = null // 세부 목표 데이터를 저장할 멤버 변수

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_proceeding, container, false)
        selectedGoalIndex = requireArguments().getInt("selectedGoalIndex")
        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {
        progressIndicator = view.findViewById(R.id.circularProgress)
        progressText = view.findViewById(R.id.progressText)
        goalText = view.findViewById(R.id.goalText)
        subGoalContainer = view.findViewById(R.id.subGoalContainer)
        nicknameTextView = view.findViewById(R.id.user_nickname)

        loadUserProfile()

        val goalId: Int = selectedGoalIndex

        if (goalId != null) {
            loadGoal(goalId)
            loadSubGoals(goalId)
        }

        // 뒤로가기
        view.findViewById<Button>(R.id.btnBack)?.setOnClickListener {
            findNavController().navigate(R.id.action_proceedingFragment_to_listFragment)
        }
        // 세부 목표 리스트 페이지 이동
        view.findViewById<Button>(R.id.btnSubGoalList)?.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedGoalIndex", goalId)
                subGoals?.let {
                    val gson = Gson()
                    val subGoalsJson = gson.toJson(it)
                    putString("subGoals", subGoalsJson)
                }
            }
            findNavController().navigate(R.id.action_proceedingFragment_to_subGoalListFragment, bundle)
        }
        // 피드백 페이지 이동
        view.findViewById<Button>(R.id.btnFeedback)?.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedGoalIndex", goalId)
            }

            findNavController().navigate(R.id.action_proceedingFragment_to_feedbackFragment, bundle)
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

    private fun loadGoal(goalId: Int) {
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())
        apiService.getGoal(goalId).enqueue(object : Callback<GoalResponse> {
            override fun onResponse(call: Call<GoalResponse>, response: Response<GoalResponse>) {
                if (response.isSuccessful) {
                    val goalResponse = response.body()
                    goalResponse?.let {
                        goalText?.text = it.title
                    }
                } else {
                    Toast.makeText(context, "목표 로드 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GoalResponse>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
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
                        subGoals?.let {
                            val successSubGoals = it.filter { subGoal -> subGoal.status == "SUCCESS" }
                            loadSubGoals(successSubGoals.map { subGoal -> subGoal.title })
                            updateProgress(calculateProgress(it))
                        }
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
    }    private fun loadSubGoals(subGoals: List<String>) {
        subGoalContainer?.removeAllViews()
        subGoals.forEach { subGoalText ->
            val subGoalButton = Button(requireContext()).apply {
                text = subGoalText
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.MATCH_PARENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(32, 16, 32, 16)
                }
                background = resources.getDrawable(R.drawable.main_button_shape, null)
                setPadding(60, 32, 32, 32)
                setTextColor(resources.getColor(R.color.black, null))
                textSize = 15f
                setTypeface(null, Typeface.BOLD)
            }
            subGoalContainer?.addView(subGoalButton)
        }
    }

    private fun calculateProgress(subGoals: List<SubGoalResponse>): Int {
        val completedSubGoals = subGoals.count { it.status == "SUCCESS" || it.status == "FAIL" }
        return if (subGoals.isEmpty()) 0 else (completedSubGoals * 100) / subGoals.size
    }

    fun updateProgress(newProgress: Int) {
        progressIndicator?.setProgressCompat(newProgress, true)
        progressText?.text = "$newProgress%"
    }
}
