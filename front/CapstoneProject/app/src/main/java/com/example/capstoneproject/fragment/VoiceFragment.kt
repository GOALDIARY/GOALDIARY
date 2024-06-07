package com.example.capstoneproject.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.capstoneproject.R
import com.example.capstoneproject.API.ApiService
import com.example.capstoneproject.API.JournalRequest
import com.example.capstoneproject.API.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class VoiceFragment : Fragment() {
    private var mRecognizer: SpeechRecognizer? = null
    private lateinit var btnStartVoice: Button
    private lateinit var tvVoiceInput: TextView
    private lateinit var waveformView: WaveformView
    private lateinit var backButton: ImageButton
    private var isListening = false
    private var subIndex: Int = -1

    private var selectedGoalIndex: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_voice, container, false)
        btnStartVoice = view.findViewById(R.id.btnStartVoice)
        tvVoiceInput = view.findViewById(R.id.tvVoiceInput)
        waveformView = view.findViewById(R.id.waveformView)
        backButton = view.findViewById(R.id.backButton)
        subIndex = arguments?.getInt("subGoalId") ?: -1

        selectedGoalIndex = arguments?.getInt("selectedGoalIndex") ?: -1


        setupSpeechRecognizer()
        setupBackButton()
        return view
    }

    private fun setupSpeechRecognizer() {
        btnStartVoice.setOnClickListener {
            if (isListening) {
                showSaveDialog()
            } else {
                startListening()
            }
        }
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedGoalIndex", selectedGoalIndex)
            }

            findNavController().navigate(R.id.action_voiceFragment_to_subGoalDetailFragment, bundle)
        }
    }

    private fun startListening() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        } else {
            initializeRecognizer()
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context?.packageName)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                // Complete silence length increased to 15 seconds
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 30000)
                // Possibly complete silence length increased to 15 seconds
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 30000)
                // Minimum length of the speech increased to 5 seconds
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 25000)
            }
            mRecognizer?.startListening(intent)
            isListening = true
            btnStartVoice.setBackgroundResource(R.drawable.rounded_button_pressed) // 빨간색으로 변경
        }
    }


    private fun initializeRecognizer() {
        if (mRecognizer == null) {
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(getRecognitionListener())
            }
        }
    }

    private fun stopListening() {
        mRecognizer?.stopListening()
        mRecognizer?.cancel()
        mRecognizer?.destroy()
        mRecognizer = null
        isListening = false
        btnStartVoice.setBackgroundResource(R.drawable.rounded_button) // 초록색으로 변경
    }

    private fun showSaveDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setMessage("저장하시겠습니까?")
            setPositiveButton("확인") { _, _ ->
                saveVoiceRecord(tvVoiceInput.text.toString())
                backButton.performClick()
            }
            setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            Toast.makeText(context, "음성 인식 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("VoiceFragment", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d("VoiceFragment", "Speech started")
            }

            override fun onRmsChanged(dB: Float) {
                Log.d("VoiceFragment", "onRmsChanged: $dB")
                waveformView.updateAmplitude(dB / 10)
                waveformView.visibility = View.VISIBLE
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("VoiceFragment", "Buffer received")
            }

            override fun onEndOfSpeech() {
                Log.d("VoiceFragment", "Speech ended")
            }

            override fun onError(error: Int) {
                Log.e("VoiceFragment", "Recognition error: $error")
                waveformView.visibility = View.GONE
                handleError(error)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d("VoiceFragment", "onResults: $matches")
                val recognizedText = matches?.joinToString(separator = ", ") ?: "No speech recognized"
                tvVoiceInput.text = recognizedText
                waveformView.visibility = View.GONE
                stopListening()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d("VoiceFragment", "onPartialResults: $partial")
                tvVoiceInput.text = partial?.joinToString(separator = ", ") ?: "Listening..."
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("VoiceFragment", "onEvent: $eventType")
            }
        }
    }

    private fun saveVoiceRecord(text: String) {
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

    private fun handleError(error: Int) {
        Log.e("VoiceFragment", "Recognition error: $error")
        if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            startListening()
        } else {
            stopListening()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopListening()
    }
}
