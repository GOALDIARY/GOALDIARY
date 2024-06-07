package com.example.capstoneproject.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.API.GoalFinalFeedBackResponse
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.UserProfileResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultFragment : Fragment() {

    private var selectedGoalIndex: Int = -1
    private var selectedSubGoalIndex: Int = -1
    private lateinit var subGoals: MutableList<String>
    private lateinit var nicknameTextView: TextView
    private var buttonId: Int = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        selectedGoalIndex = requireArguments().getInt("selectedGoalIndex")
        selectedSubGoalIndex = requireArguments().getInt("selectedSubGoalIndex")
        buttonId = requireArguments().getInt("buttonId")

        val view = inflater.inflate(R.layout.fragment_result, container, false)
        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {
        nicknameTextView = view.findViewById(R.id.user_nickname)
        loadUserProfile()
        view.findViewById<Button>(R.id.btnSuccess)?.setOnClickListener {
            subGoalSuccess()
            updateButtonColor(true)
            navigateBack()
        }
        view.findViewById<Button>(R.id.btnFailure)?.setOnClickListener {
            subGoalFail()
            updateButtonColor(false)
            navigateBack()
        }
        view.findViewById<Button>(R.id.btnBack)?.setOnClickListener {
            navigateBack()
        }
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




    private fun updateButtonColor(isSuccess: Boolean) {
        val sharedPreferences = requireActivity().getSharedPreferences("button_colors", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val color = if (isSuccess) "mint" else "red"
        editor.putString("button_$buttonId", color)
        editor.apply()
    }

    private fun navigateBack() {
        val bundle = Bundle().apply {
            putInt("selectedGoalIndex", selectedGoalIndex)
        }
        findNavController().navigate(R.id.action_resultFragment_to_subGoalDetailFragment, bundle)
    }

    private fun subGoalSuccess() {
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())
        val call = apiService.subGoalSuccess(goalId = selectedGoalIndex,
                                            subGoalId = selectedSubGoalIndex)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    println("ERROR!!!")
                } else {
                    // 에러 처리
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 네트워크 실패 처리
            }
        })
    }

    private fun subGoalFail(){
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())
        val call = apiService.subGoalFail(goalId = selectedGoalIndex,
                                            subGoalId = selectedSubGoalIndex)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    println("ERROR!!!")
                } else {
                    // 에러 처리
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 네트워크 실패 처리
            }
        })
    }


}
