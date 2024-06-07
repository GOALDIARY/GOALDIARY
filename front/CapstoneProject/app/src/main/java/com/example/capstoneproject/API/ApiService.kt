package com.example.capstoneproject.API

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

//data class SignupRequest(val name: String, val loginId: String, val password: String)
data class SignupRequest(val name: String, val loginId: String, val password: String, var positiveKeywordSet : List<String>)
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)
data class UserProfileResponse(val name: String, val loginId: String)
data class GoalRequest(val title: String, val startDate: String, val endDate: String)
data class GoalResponse(val id: Int, val title: String, val startDate: String, val endDate: String, val status: String, val repEmotion: String?)
data class SubGoalRequest(val title: String, val startDate: String, val endDate: String)
data class SubGoalResponse(var id : Int, val title: String, val startDate: String, val endDate: String, val status:String, val repEmotion:String)
data class JournalRequest(val content: String, val writingMode: String, val repEmotion: String? = "", val writeDate: String, val keywords: List<String>? = listOf(), val emotions: List<String>? = listOf())
data class JournalResponse(val id: Int, val content: String)
data class JournalFeedBackResponse(
    var emotionList: List<EmotionItem?>,
    var keywordList: List<String>, // 여기서 수정
    var feedback: String
)
data class GoalFinalFeedBackResponse(var emotionList: List<EmotionItem?>, var feedback: String)

data class  EmotionItem(
    @SerializedName("repEmotion") val repEmotion: String,
    @SerializedName("probability") val probability: Float,
    @SerializedName("emotion") val emotion: String
)

interface ApiService {
    // 회원가입
    @POST("/users/join")
    fun signup(@Body request: SignupRequest): Call<Void>
    // 로그인
    @POST("/login")
    fun login(@Body request: LoginRequest): Call<Void>
    // 프로필
    @GET("/users/profile")
    fun profile(): Call<UserProfileResponse>
    // 목표 생성
    @POST("goals/create")
    fun createGoal(@Body request: GoalRequest): Call<Long>
    // 목표 조회
    @GET("/goals/current")
    fun getGoals(): Call<List<GoalResponse>>
    // 최종 목표 단건 조회
    @GET("/goals/{goalId}")
    fun getGoal(@Path("goalId") goalId: Int): Call<GoalResponse>
    // 서브목표 생성
    @POST("/goals/{goalId}/subGoals/create")
    fun createSubGoal(@Path("goalId") goalId: Long,
                      @Body request: SubGoalRequest): Call<Void>
    // 서브목표 전체 조회
    @GET("/goals/{goalId}/subGoals")
    fun getSubGoals(@Path("goalId") goalId: Int): Call<List<SubGoalResponse>>
    // 서부목표 성공
    @PUT("goals/{goalId}/subGoals/{subGoals}/complete?status=SUCCESS&emotion=happy")
    fun subGoalSuccess(@Path("goalId") goalId: Int,
                        @Path("subGoals") subGoalId: Int):
            Call<Void>
    // 서부목표 실패
    @PUT("goals/{goalId}/subGoals/{subGoals}/complete?status=FAIL&emotion=SAD")
    fun subGoalFail(@Path("goalId") goalId: Int,
                        @Path("subGoals") subGoalId: Int):
            Call<Void>
    // 일지 저장
    @POST("/subGoals/{subGoalId}/journal/save")
    fun saveJournal(@Path("subGoalId") subGoalId: Int, @Body journalRequest: JournalRequest): Call<Void>
    // 일지 단건 조회
    @GET("/subGoals/{subGoalId}/get/{id}")
    fun getjournal(@Path("subGoalId") subGoalId: Int,
                    @Path("id") id: Int): Call<JournalResponse>
    // 일지 전체 조회
    @GET("/subGoals/{subGoalId}/get")
    fun getjournals(@Path("subGoalId") subGoalId: Int,
                 ): Call<List<JournalResponse>>
    // 일반 피드백 조회
    @GET("journals/{journalId}/feedback")
    fun getFeedback(@Path("journalId") journalId: Int)
    : Call<JournalFeedBackResponse>
    // 최종목표 피드백
    @GET("/goals/{goalId}/feedback")
    fun getFinalFeedBack(@Path("goalId") goalId: Int)
            : Call<GoalFinalFeedBackResponse>
}

