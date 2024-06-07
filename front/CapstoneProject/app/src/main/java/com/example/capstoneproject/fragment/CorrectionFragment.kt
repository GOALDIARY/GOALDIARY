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
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CorrectionFragment : Fragment() {

    private lateinit var subGoalContainer: LinearLayout
    private var goal: String? = null
    private var subGoals: ArrayList<String>? = null
    private var startYear: String? = null
    private var startMonth: String? = null
    private var startDay: String? = null
    private var endYear: String? = null
    private var endMonth: String? = null
    private var endDay: String? = null
    private var selectedGoalIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_correction, container, false)
        val inputGoalEditText = view.findViewById<EditText>(R.id.InputGoal)
        val startYearEditText = view.findViewById<EditText>(R.id.StartYear)
        val startMonthEditText = view.findViewById<EditText>(R.id.StartMonth)
        val startDayEditText = view.findViewById<EditText>(R.id.StartDay)
        val endYearEditText = view.findViewById<EditText>(R.id.EndYear)
        val endMonthEditText = view.findViewById<EditText>(R.id.EndMonth)
        val endDayEditText = view.findViewById<EditText>(R.id.EndDay)
        subGoalContainer = view.findViewById(R.id.subGoalContainer)

        // 전달된 목표와 서브 목표를 가져옴
        arguments?.let {
            selectedGoalIndex = it.getInt("selectedGoalIndex")
            goal = it.getString("goal")
            subGoals = it.getStringArrayList("subGoals")
            startYear = it.getString("startYear")
            startMonth = it.getString("startMonth")
            startDay = it.getString("startDay")
            endYear = it.getString("endYear")
            endMonth = it.getString("endMonth")
            endDay = it.getString("endDay")
        }

        inputGoalEditText.setText(goal)
        startYearEditText.setText(startYear ?: "")
        startMonthEditText.setText(startMonth ?: "")
        startDayEditText.setText(startDay ?: "")
        endYearEditText.setText(endYear ?: "")
        endMonthEditText.setText(endMonth ?: "")
        endDayEditText.setText(endDay ?: "")

        subGoals?.forEach { subGoal ->
            addSubGoal(subGoal)
        }

        view.findViewById<Button>(R.id.addSubGoalButton).setOnClickListener {
            addSubGoal()
        }

        view.findViewById<Button>(R.id.CorrectionBtn).setOnClickListener {
            val goalText = inputGoalEditText.text.toString()
            val subGoalTexts = getSubGoals()
            val startDate = formatDateString(startYearEditText, startMonthEditText, startDayEditText)
            val endDate = formatDateString(endYearEditText, endMonthEditText, endDayEditText)

            if (goalText.isNotEmpty()) {
                saveGoal(goalText, subGoalTexts, startDate, endDate)
                findNavController().navigate(R.id.action_correctionFragment_to_listFragment)
            } else {
                Toast.makeText(context, "목표와 날짜를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun addSubGoal(subGoal: String = "") {
        val inflater = LayoutInflater.from(context)
        val subGoalView = inflater.inflate(R.layout.sub_goal_item, subGoalContainer, false)
        val subGoalEditText = subGoalView.findViewById<EditText>(R.id.InputSubGoal)
        subGoalEditText.setText(subGoal)
        subGoalContainer.addView(subGoalView)
    }

    private fun getSubGoals(): List<String> {
        val subGoals = mutableListOf<String>()
        for (i in 0 until subGoalContainer.childCount) {
            val subGoalView = subGoalContainer.getChildAt(i)
            val subGoalEditText = subGoalView.findViewById<EditText>(R.id.InputSubGoal)
            subGoals.add(subGoalEditText.text.toString())
        }
        return subGoals
    }

    private fun formatDateString(year: EditText, month: EditText, day: EditText): String {
        val y = year.text.toString().padStart(4, '0')
        val m = month.text.toString().padStart(2, '0')
        val d = day.text.toString().padStart(2, '0')
        return "$y-$m-$d"
    }

    private fun saveGoal(goal: String, subGoals: List<String>, startDate: String, endDate: String) {
        val sharedPrefs = requireActivity().getSharedPreferences("Goals", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonAllGoals = sharedPrefs.getString("AllGoals", null)
        val type = object : TypeToken<MutableList<Map<String, Any>>>() {}.type
        val allGoals: MutableList<Map<String, Any>> = if (jsonAllGoals == null) {
            mutableListOf()
        } else {
            gson.fromJson(jsonAllGoals, type)
        }

        val updatedGoals = allGoals.toMutableList()
        updatedGoals[selectedGoalIndex] = mapOf(
            "goal" to goal,
            "subGoals" to subGoals,
            "startDate" to startDate,
            "endDate" to endDate,
            "timestamp" to System.currentTimeMillis() // 추가된 부분
        )

        val newJsonAllGoals = gson.toJson(updatedGoals)
        sharedPrefs.edit().putString("AllGoals", newJsonAllGoals).apply()
    }

}
