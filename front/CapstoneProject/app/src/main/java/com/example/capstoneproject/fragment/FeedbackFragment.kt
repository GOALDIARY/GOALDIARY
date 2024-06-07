package com.example.capstoneproject.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneproject.API.GoalFinalFeedBackResponse
import com.example.capstoneproject.R
import com.example.capstoneproject.API.RetrofitClient
import com.example.capstoneproject.API.UserProfileResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

var sampleFinalfeedBack =
    """
{
    "emotionList": [
    {
        "repEmotion": "중립",
        "probability": 90,
        "emotion": "중립"
    },
    {
        "repEmotion": "슬픔",
        "probability": 90,
        "emotion": "슬픔"
    },
    {
        "repEmotion": "슬픔",
        "probability": 10,
        "emotion": "슬픔"
    }
    ],
    "feedback": "목표를 향해 노력하고 실패하는 것은 전혀 부끄러운 일이 아닙니다. 오히려 그 경험을 통해 무엇이 잘못되었는지 배우고 더 나은 방향으로 나아갈 수 있는 기회가 될 수 있습니다. 패스트푸드를 먹지 않는 것과 저녁 후 산책하기 등 몇 가지 성공한 점이 있습니다. 이러한 성공한 부분을 인정하고 칭찬해주세요. 매일 아침 운동을 하기 위해 노력했으나 실패했다면, 어떤 부분이 잘못되었는지 돌아보고 개선해나가는 것이 중요합니다. 또한, 끼니를 거르지 않는 것도 중요한데 하루 세 끼 다 먹는 습관을 들이는 것이 건강에 좋을 뿐만 아니라 목표 달성에도 도움이 될 것입니다. 부정적인 감정이 들었을 때는 먼저 마음의 상태를 가볍게 해주는 것이 중요합니다. 마음을 편히 해주는 활동을 찾아보고 스트레스를 해소할 수 있는 방법을 찾아보세요. 또한, 자신에 대한 부정적인 생각을 긍정적으로 바꿀 수 있도록 노력해보세요. 실패는 성공으로 가는 길에 있는 단계일 뿐이며, 그 경험을 통해 더 강해질 수 있습니다. 계속해서 목표를 향해 나아가는 것이 중요합니다. 함께 응원하겠습니다. 힘내세요!"
}
"""

class FeedbackFragment : Fragment() {

    private lateinit var nicknameTextView: TextView
    private lateinit var timelineRecyclerView: RecyclerView
    private var selectedGoalIndex: Int = -1
    var finalfeedback: GoalFinalFeedBackResponse? = null

    data class EmotionData(val date: String, val emotion: String)

    inner class TimelineAdapter(private val emotionDataList: List<EmotionData>) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
            val emotionImageView: ImageView = itemView.findViewById(R.id.emotionImageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val emotionData = emotionDataList[position]
            holder.dateTextView.text = emotionData.date

            val emotionImageRes = when (emotionData.emotion.lowercase()) {
                "happy" -> R.drawable.happy
                "angry" -> R.drawable.angry
                "sad" -> R.drawable.sad
                "fear" -> R.drawable.fear
                "disgust" -> R.drawable.disgust
                "neutral" -> R.drawable.neutral
                else -> R.drawable.neutral
            }
            holder.emotionImageView.setImageResource(emotionImageRes)
        }

        override fun getItemCount(): Int {
            return emotionDataList.size
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feedback, container, false)
        nicknameTextView = view.findViewById(R.id.user_nickname)
        timelineRecyclerView = view.findViewById(R.id.timelineRecyclerView)
        selectedGoalIndex = requireArguments().getInt("selectedGoalIndex")
        val feedbackFinalTextView: TextView = view.findViewById(R.id.feedbackFinalText)

        // ============================ *** ** ** **** ===============================
        val gson = Gson() // 인공지능 연결시 해당 코드 주석
        try {
            finalfeedback = gson.fromJson(sampleFinalfeedBack, GoalFinalFeedBackResponse::class.java) // 인공지능 연결시 해당 코드 주석
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            // 오류 메시지를 사용자에게 표시하거나 로그에 기록
        }
        // ============================ *** ** ** **** ===============================


        // ============================ *** ** ** **** ===============================
//            finalfeedback = getFinalFeedBack() // 인공지능 연결시 해당 코드 주석 해제
        // ============================ *** ** ** **** ===============================
        feedbackFinalTextView.text = finalfeedback?.feedback

        println("최종 목표 피드백입니다. : ${finalfeedback}")

        loadUserProfile()

        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            findNavController().navigate(R.id.action_feedbackFragment_to_proceedingFragment)
        }

        setupTimeline()

        return view
    }

    private fun setupTimeline() {
        val emotionDataList = finalfeedback?.emotionList?.mapNotNull { emotion ->
            emotion?.let {
                EmotionData(it.emotion, it.repEmotion)
            }
        } ?: emptyList()

        timelineRecyclerView.layoutManager = GridLayoutManager(context, 7)
        timelineRecyclerView.adapter = TimelineAdapter(emotionDataList)
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

    private fun getFinalFeedBack(): GoalFinalFeedBackResponse? {
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())
        val call = apiService.getFinalFeedBack(goalId = selectedGoalIndex)

        var finalFeedback: GoalFinalFeedBackResponse? = null

        call.enqueue(object : Callback<GoalFinalFeedBackResponse> {
            override fun onResponse(
                call: Call<GoalFinalFeedBackResponse>,
                response: Response<GoalFinalFeedBackResponse>
            ) {
                if (response.isSuccessful) {
                    finalFeedback = response.body()
                } else {
                    // 에러 처리
                }
            }
            override fun onFailure(call: Call<GoalFinalFeedBackResponse>, t: Throwable) {
                // 네트워크 실패 처리
            }
        })
        return finalFeedback
    }
}
