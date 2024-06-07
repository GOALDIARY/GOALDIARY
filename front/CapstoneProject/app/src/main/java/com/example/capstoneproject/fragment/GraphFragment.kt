package com.example.capstoneproject.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.capstoneproject.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.API.JournalFeedBackResponse
import com.example.capstoneproject.API.RetrofitClient
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val sampleJson = """
{
    "emotionList": [
        {
            "repEmotion": "공포",
            "probability": 7.13,
            "emotion": "공포"
        },
        {
            "repEmotion": "분노",
            "probability": 4.45,
            "emotion": "분노"
        },
        {
            "repEmotion": "슬픔",
            "probability": 61.12,
            "emotion": "슬픔"
        },
        {
            "repEmotion": "중립",
            "probability": 20.08,
            "emotion": "중립"
        },
        {
            "repEmotion": "행복",
            "probability": 1.1,
            "emotion": "행복"
        },
        {
            "repEmotion": "혐오",
            "probability": 6.13,
            "emotion": "혐오"
        }
    ],
    "keywordList": [
        "가족",
        "친구",
        "피아노",
        "음악",
        "축구"
    ],
    "feedback": "아침에 늦잠을 자서 헬스장에 가지 못한 건 조금 실망스러운 일일 수 있지만, 이런 날도 가끔은 생기는 법입니다. 중요한 건 이런 일이 한 번 있는 것을 너무 자책하지 않는 것이 중요합니다. 이러한 상황에서는 자신을 용서하고, 다음 번에는 더 나은 계획을 세워보는 것도 좋은 방법일 수 있습니다. 또한, 오늘은 다른 방식으로 운동을 할 수 있는 기회로 생각해보는 것도 좋을 것입니다. 이러한 상황을 긍정적으로 바라보며 또 다른 운동이 취할 수 있는 기회로 생각해보세요. 또한, 두려 움과 자책에도 휩싸이지 않도록 주의하세요. 정말 중요한 것은 지금부터 다시 시작하는 것이니까요. 계속해서 당당하게 나아가세요!"
}
"""

class GraphFragment : Fragment() {
    private var feedback: JournalFeedBackResponse? = null
    private var selectedGoalIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_graph, container, false)
        val barChart: BarChart = view.findViewById(R.id.chart)
        val keywordContainer: LinearLayout = view.findViewById(R.id.keywordsContainer)
        val feedbackTextView: TextView = view.findViewById(R.id.feedbackId)
        val btnBack: Button = view.findViewById(R.id.btnBack)

        selectedGoalIndex = arguments?.getInt("selectedGoalIndex") ?: -1

        val gson = Gson()
        try {
            feedback = gson.fromJson(sampleJson, JournalFeedBackResponse::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            // 오류 메시지를 사용자에게 표시하거나 로그에 기록
        }

        println("Feedback: $feedback")

        feedback?.let {
            setupBarChart(barChart, it)
            displayKeywords(keywordContainer)
            feedbackTextView.text = it.feedback // 피드백 설정
        }

        setupBackButton(btnBack)

        return view
    }

    private fun setupBarChart(chart: BarChart, feedback: JournalFeedBackResponse) {
        val emotions = feedback.emotionList.map { it?.emotion ?: "Unknown" }.toTypedArray()
        val entries = ArrayList<BarEntry>().apply {
            feedback.emotionList.forEachIndexed { index, emotionItem ->
                emotionItem?.let {
                    add(BarEntry(index.toFloat(), it.probability.toFloat()))
                }
            }
        }

        val dataSet = BarDataSet(entries, "감정 분석").apply {
            val colors = ArrayList<Int>()
            val highestValueIndex = entries.maxByOrNull { it.y }?.x?.toInt() ?: 0
            entries.forEachIndexed { index, _ ->
                if (index == highestValueIndex) {
                    colors.add(Color.rgb(247, 228, 246))  // 연보라색
                } else {
                    colors.add(Color.rgb(221, 221, 221))  // 회색
                }
            }
            setColors(colors)
            valueTextSize = 16f
            valueTextColor = Color.WHITE
        }

        chart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(emotions)
                setDrawGridLines(false)
            }
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                granularity = 20f
                setDrawGridLines(false)
            }
            axisRight.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)
            animateY(500)
        }
    }

    private fun displayKeywords(container: LinearLayout) {
        val keywords = feedback?.keywordList ?: emptyList()
        keywords.forEach { keyword ->
            val textView = TextView(context).apply {
                text = "#$keyword"
                textSize = 18f
                background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_keyword_background)
                setTextColor(Color.BLACK)
                setPadding(10, 10, 10, 10)  // 좌우, 상하 패딩 설정

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    rightMargin = 16.dp(context)  // 키워드 간 오른쪽 마진
                    leftMargin = if (keyword == keywords.first()) 16.dp(context) else 0  // 첫 번째 키워드 왼쪽 마진 추가
                }
            }
            container.addView(textView)
        }
    }

    // dp를 픽셀로 변환하는 확장 함수
    fun Int.dp(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun setupBackButton(button: Button) {
        button.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedGoalIndex", selectedGoalIndex)
            }
            findNavController().navigate(R.id.action_graphFragment_to_recordsFragment, bundle)
        }
    }

    var jounalId: Int = 1
    private fun getMyFeedback(): JournalFeedBackResponse? {
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())
        val call = apiService.getFeedback(jounalId)

        var feedbackResponse: JournalFeedBackResponse? = null

        call.enqueue(object : Callback<JournalFeedBackResponse> {
            override fun onResponse(
                call: Call<JournalFeedBackResponse>,
                response: Response<JournalFeedBackResponse>
            ) {
                if (response.isSuccessful) {
                    feedbackResponse = response.body()
                } else {
                    // 에러 처리
                }
            }
            override fun onFailure(call: Call<JournalFeedBackResponse>, t: Throwable) {
                // 네트워크 실패 처리
            }
        })
        return feedbackResponse
    }
}
