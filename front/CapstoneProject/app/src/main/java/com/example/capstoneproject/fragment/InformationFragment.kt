//package com.example.capstoneproject.fragment
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.navigation.fragment.findNavController
//import com.example.capstoneproject.R
//
//class InformationFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_information, container, false)
//
//        // EditText 참조
//        val inputSports = view.findViewById<EditText>(R.id.InputSports)
//        val inputMusic = view.findViewById<EditText>(R.id.InputMusic)
//        val inputFood = view.findViewById<EditText>(R.id.InputFood)
//        val inputHobby = view.findViewById<EditText>(R.id.InputHobby)
//
//        // 로그인 페이지로 돌아가는 버튼
//        view.findViewById<Button>(R.id.SigninBtn).setOnClickListener {
//            findNavController().navigate(R.id.action_informationFragment_to_loginFragment)
//        }
//
//        // 입력 필드를 모두 채운 후 로그인 페이지로 이동
//        view.findViewById<Button>(R.id.SignUp).setOnClickListener {
//            if (inputSports.text.isNotEmpty() && inputMusic.text.isNotEmpty() &&
//                inputFood.text.isNotEmpty() && inputHobby.text.isNotEmpty()) {
//                findNavController().navigate(R.id.action_informationFragment_to_loginFragment)
//            } else {
//                Toast.makeText(context, "모든 입력 값을 채워주세요.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        return view
//    }
//
//}