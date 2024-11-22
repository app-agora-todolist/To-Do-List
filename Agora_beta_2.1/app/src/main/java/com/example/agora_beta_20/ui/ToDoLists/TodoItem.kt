package com.example.agora_beta_20.ui.ToDoLists

data class TodoItem(
    val id: Int,    // 고유 ID
    val title: String,  // 할 일 제목
    val date: Long, // 날짜
    val reminder: ReminderOption,   // 리마인더 옵션
    val category: String,   // 카테고리 이름
    var isCompleted: Boolean = false    // 완료 여부
)

enum class ReminderOption(val displayName: String) {
    TODAY("당일"),
    DAY_BEFORE("하루 전"),
    DAY_TWO_BEFORE("이틀 전"),
    WEEK_BEFORE("일주일 전")
}

