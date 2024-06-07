package com.example.capstoneproject.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.UserProfileResponse
import com.example.capstoneproject.API.JournalResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecordsFragment : Fragment() {

    private lateinit var nicknameTextView: TextView
    private var subIndex: Int = -1

    private var selectedGoalIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        subIndex = arguments?.getInt("subGoalId") ?: -1
        val view = inflater.inflate(R.layout.fragment_records, container, false)
        val recordsContainer = view.findViewById<LinearLayout>(R.id.recordsContainer)
        nicknameTextView = view.findViewById(R.id.user_nickname)

        selectedGoalIndex = arguments?.getInt("selectedGoalIndex") ?: -1

        loadUserProfile()
        loadRecords(recordsContainer)

        val btnBack = view.findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedGoalIndex", selectedGoalIndex)
            }
            findNavController().navigate(R.id.action_recordsFragment_to_subGoalDetailFragment, bundle)
        }
        return view
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

    private fun loadRecords(container: LinearLayout) {
        container.removeAllViews()  // 이전에 추가된 뷰를 모두 제거

        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())
        apiService.getjournals(subIndex).enqueue(object : Callback<List<JournalResponse>> {
            override fun onResponse(call: Call<List<JournalResponse>>, response: Response<List<JournalResponse>>) {
                if (response.isSuccessful) {
                    val journals = response.body()
                    journals?.let { list ->
                        context?.let { ctx ->
                            list.forEachIndexed { index, journal ->
                                val button = createRecordButton(ctx, journal.id.toString(), journal.content, index)
                                container.addView(button)  // 순서대로 추가
                                val divider = createDivider(ctx)
                                container.addView(divider)  // 구분선도 바로 뒤에 추가
                            }
                        }
                    }
                } else {
                    // 오류 처리
                }
            }

            override fun onFailure(call: Call<List<JournalResponse>>, t: Throwable) {
                // 네트워크 오류 처리
            }
        })
    }

    private fun createRecordButton(context: Context, id: String, recordText: String, index: Int): Button {
        return Button(context).apply {
            text = "${index + 1}. $recordText"
            textSize = 18f // 텍스트 크기를 조금 줄였습니다.
            gravity = Gravity.START
            background = ContextCompat.getDrawable(context, android.R.color.transparent)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 10.dp(context)  // 마진 추가
                leftMargin = 10.dp(context) // 좌측 마진 추가
                rightMargin = 10.dp(context) // 우측 마진 추가
            }
            setTextColor(ContextCompat.getColor(context, R.color.grey))
            maxLines = 1  // 최대 줄 수를 1로 제한
            ellipsize = TextUtils.TruncateAt.END  // 텍스트가 길 경우 끝에서 말줄임표 처리
            setOnClickListener {
                navigateToChart(id)
            }
        }
    }

    private fun navigateToChart(id: String) {
        val bundle = Bundle().apply {
            putInt("selectedGoalIndex", selectedGoalIndex)
            putString("recordId", id)
        }
        findNavController().navigate(R.id.action_recordsFragment_to_graphFragment, bundle)
    }

    private fun createDivider(context: Context): View {
        return View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.divider_height)
            )
            setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey))
        }
    }

    fun Int.dp(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
