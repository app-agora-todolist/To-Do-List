package com.example.agora_beta_20.data.api

import retrofit2.http.*
import retrofit2.http.GET
import retrofit2.http.Query

data class ScheduleRequest(
    val email: String,
    val title: String,
    val category: String,
    val schedule: String,
    val reminderDate: String,
    val completionStatus: Boolean
)

// 추가용이므로 completionStatus는 제외
data class ScheduleRequestAdd(
    val email: String,
    val title: String,
    val category: String,
    val schedule: String,
    val reminderDate: String
)

data class ScheduleResponse(
    val email: String,
    val id: Long,
    val title: String,
    val category: String,
    val schedule: String,
    val reminderDate: String,
    val completionStatus: Boolean
)

interface ScheduleApiService {
    // 일정 조회
    @GET("/schedule")
    suspend fun getSchedules(
        @Query("email") email: String,
        @Query("year") year: Int
    ): List<ScheduleResponse>

    // 일정 추가
    @POST("/schedule")
    suspend fun addSchedule(@Body request: ScheduleRequestAdd): ScheduleResponse

    // 일정 수정
    @PUT("/schedule/{id}")
    suspend fun updateSchedule(
        @Path("id") id: Long,
        @Body schedule: ScheduleRequest
    ): ScheduleResponse

    // 일정 삭제
    @DELETE("/schedule/{id}")
    suspend fun deleteSchedule(
        @Path("id") id: Long,
        @Query("email") email: String
    ): Boolean

}



//// Retrofit 객체 생성
//object ApiClient {
//
//
////    private const val BASE_URL = "http://localhost:8080" // 서버 주소
//    private const val BASE_URL = "http://10.0.2.2:8080"
//
//
//    private val loggingInterceptor = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY // 요청/응답 전체 로그
//    }
//
//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .build()
//
//    val retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(okHttpClient)
//        .build()
//
//    val scheduleApi: ScheduleApiService = retrofit.create(ScheduleApiService::class.java)
//}