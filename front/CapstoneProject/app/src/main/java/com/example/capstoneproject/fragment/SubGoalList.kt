package com.example.capstoneproject.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.API.JournalFeedBackResponse
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.SubGoalResponse
import com.example.capstoneproject.API.UserProfileResponse
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import androidx.appcompat.app.AlertDialog

class SubGoalListFragment : Fragment() {

    private var subGoalContainer: LinearLayout? = null
    private var selectedGoalIndex: Int = -1
    private lateinit var nicknameTextView: TextView
    private var subGoals: List<SubGoalResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sub_goal_list, container, false)

        selectedGoalIndex = requireArguments().getInt("selectedGoalIndex", -1)

        arguments?.let { bundle ->
            selectedGoalIndex = bundle.getInt("selectedGoalIndex")
            val subGoalsJson = bundle.getString("subGoals")

            subGoalsJson?.let {
                val gson = Gson()
                val type = object : TypeToken<List<SubGoalResponse>>() {}.type
                subGoals = gson.fromJson(it, type)
            }
        }

        setupUI(view)

        return view
    }

    private fun setupUI(view: View) {
        subGoalContainer = view.findViewById(R.id.subGoalContainer)
        nicknameTextView = view.findViewById(R.id.user_nickname)

        loadUserProfile()
        subGoals?.let {
            displaySubGoals(it)
        }

        view.findViewById<MaterialButton>(R.id.Back).setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedGoalIndex", selectedGoalIndex)
            }
            findNavController().navigate(R.id.action_subGoalListFragment_to_proceedingFragment, bundle)
        }

        view.findViewById<MaterialButton>(R.id.btnFinal).setOnClickListener {
            showResultDialog()
        }
    }

    private fun showResultDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose Result")
        builder.setMessage("Select SUCCESS or FAILURE")

        builder.setPositiveButton("SUCCESS") { dialog, which ->
            Toast.makeText(requireContext(), "SUCCESS", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("FAILURE") { dialog, which ->
            Toast.makeText(requireContext(), "FAILURE", Toast.LENGTH_SHORT).show()
        }

        val dialog = builder.create()
        dialog.show()

        // Positive 버튼 색상 변경
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(R.color.mint, null))

        // Negative 버튼 색상 변경
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(resources.getColor(R.color.failure_red, null))
    }


    private fun loadUserProfile() {
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

    private fun displaySubGoals(subGoals: List<SubGoalResponse>) {
        val sharedPreferences = requireActivity().getSharedPreferences("button_colors", Context.MODE_PRIVATE)
        subGoalContainer?.removeAllViews()
        subGoals.forEachIndexed { index, subGoal ->
            val subGoalButton = MaterialButton(requireContext()).apply {
                text = subGoal.title
                id = View.generateViewId() // 고유 ID 생성
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.MATCH_PARENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(32, 16, 32, 16)
                }
                setPadding(60, 32, 32, 32)
                textSize = 15f
                setTypeface(null, Typeface.BOLD)

                // 초기 테두리와 텍스트 색상 설정
                setBackgroundColor(resources.getColor(android.R.color.white, null))
                val color = when (subGoal.status) {
                    "SUCCESS" -> "mint"
                    "FAIL" -> "red"
                    else -> "grey"
                }
                setStrokeAndTextColor(color)

                // 버튼 클릭 시 SubGoalDetailFragment로 이동
                setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("subGoalTitle", subGoal.title)
                        putInt("selectedGoalIndex", selectedGoalIndex)
                        putInt("subGoalId", subGoal.id)
                        putInt("buttonId", id) // 버튼 ID 추가
                    }
                    findNavController().navigate(R.id.action_subGoalListFragment_to_subGoalDetailFragment, bundle)

                    // 버튼 클릭 시 색상 저장
                    with(sharedPreferences.edit()) {
                        putString("button_$id", color) // 클릭 시 색상 저장
                        apply()
                    }
                }
            }
            subGoalContainer?.addView(subGoalButton)
        }
    }

    private fun MaterialButton.setStrokeAndTextColor(colorName: String?) {
        when (colorName) {
            "mint" -> {
                strokeColor = resources.getColorStateList(R.color.mint, null)
                setTextColor(resources.getColor(R.color.mint, null))
            }
            "red" -> {
                strokeColor = resources.getColorStateList(R.color.failure_red, null)
                setTextColor(resources.getColor(R.color.failure_red, null))
            }
            else -> {
                strokeColor = resources.getColorStateList(R.color.grey, null)
                setTextColor(resources.getColor(R.color.grey, null))
            }
        }
        strokeWidth = 2
        cornerRadius = resources.getDimensionPixelSize(R.dimen.corner_radius)
    }
}
