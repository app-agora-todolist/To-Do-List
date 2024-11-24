package com.example.agora_beta_20.data.repository

import com.example.agora_beta_20.data.api.ApiClient
import com.example.agora_beta_20.data.api.ScheduleRequest
import com.example.agora_beta_20.data.api.ScheduleResponse

class ScheduleRepository {

    private val scheduleApi = ApiClient.scheduleApi

    // 일정 조회
    suspend fun getSchedules(email: String, year: Int): List<ScheduleResponse> {
        return scheduleApi.getSchedules(email = email, year = year)
    }

    // 일정 추가
    suspend fun addSchedule(schedule: ScheduleRequest): ScheduleResponse {
        return scheduleApi.addSchedule(schedule)
    }

    // 일정 수정
    suspend fun updateSchedule(id: Long, schedule: ScheduleRequest): ScheduleResponse {
        return scheduleApi.updateSchedule(id, schedule)
    }

    // 일정 삭제
    suspend fun deleteSchedule(id: Long, email: String): Boolean {
        return scheduleApi.deleteSchedule(id, email)
    }
}