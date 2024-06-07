package com.example.capstoneproject.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.R
import com.example.capstoneproject.API.ApiService
import com.example.capstoneproject.API.JournalRequest
import com.example.capstoneproject.API.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class WriteFragment : Fragment() {
    private var currentRecordId: String? = null
    private var subIndex: Int = -1

    private var selectedGoalIndex: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_write, container, false)
        subIndex = arguments?.getInt("subGoalId") ?: -1
        val backButton = view.findViewById<ImageButton>(R.id.backButton).apply {
            setOnClickListener { findNavController().navigateUp() }
        }

        val inputField = view.findViewById<EditText>(R.id.inputField)
        val btnCreate = view.findViewById<Button>(R.id.btnCreate)

        selectedGoalIndex = arguments?.getInt("selectedGoalIndex") ?: -1

        // 기존 데이터 로딩 (편집 모드)
        arguments?.getString("recordData")?.let {
            inputField.setText(it)
            currentRecordId = arguments?.getString("recordId")
        }

        btnCreate.setOnClickListener {
            val text = inputField.text.toString()
            if (text.isNotEmpty()) {
                if (currentRecordId == null) {
                    // 새 기록 저장
                    saveRecord(text)
                } else {
                    // 기존 기록 업데이트
                    updateRecord(currentRecordId!!, text)
                }
                findNavController().navigate(R.id.action_writeFragment_to_recordsFragment)
            }
        }
        return view
    }

    private fun saveRecord(text: String) {
        val sharedPrefs = requireActivity().getSharedPreferences("Records", Context.MODE_PRIVATE)
        val uniqueID = UUID.randomUUID().toString()
        sharedPrefs.edit().putString(uniqueID, text).apply()

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val journalRequest = JournalRequest(
            content = text,
            writingMode = "String",
            writeDate = currentDate,
            repEmotion = "",
            keywords = listOf(),
            emotions = listOf()
        )
        println("저널 테스트 =" + journalRequest)
        val apiService = RetrofitClient.createApiServiceWithAuth(requireContext())
        val call = apiService.saveJournal(subIndex, journalRequest)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Handle success
                } else {
                    // Handle failure
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun updateRecord(id: String, newText: String) {
        val sharedPrefs = requireActivity().getSharedPreferences("Records", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString(id, newText).apply()
    }
}
