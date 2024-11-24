package com.example.agora_beta_20.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora_beta_20.data.api.ScheduleRequest
import com.example.agora_beta_20.data.api.ScheduleResponse
import com.example.agora_beta_20.data.repository.ScheduleRepository
import com.example.agora_beta_20.ui.ToDoLists.ReminderOption
import com.example.agora_beta_20.ui.ToDoLists.TodoItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SharedViewModel : ViewModel() {

    private val repository = ScheduleRepository()

    // LiveData for tasks
    private val _tasks = MutableLiveData<List<TodoItem>>()
    val tasks: LiveData<List<TodoItem>> get() = _tasks

    /**
     * 일정 가져오기
     * Fetch schedules for a specific year and update LiveData
     */
    fun fetchSchedules(email: String, year: Int) {
        viewModelScope.launch {
            try {
                val schedules = repository.getSchedules(email, year)
                _tasks.value = schedules.map { mapScheduleResponseToTodoItem(it) }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Failed to fetch schedules: ${e.message}")
            }
        }
    }

    /**
     * 일정 추가
     */
    fun addTask(scheduleRequest: ScheduleRequest) {
        viewModelScope.launch {
            try {
                val newTaskResponse = repository.addSchedule(scheduleRequest)
                val newTask = mapScheduleResponseToTodoItem(newTaskResponse)
                _tasks.value = _tasks.value?.plus(newTask) ?: listOf(newTask)
                Log.d("SharedViewModel", "Task added: $newTask")
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error adding task: ${e.message}")
            }
        }
    }

    /**
     * 일정 수정
     */
    fun updateTask(id: Long, updatedRequest: ScheduleRequest) {
        viewModelScope.launch {
            try {
                val updatedTaskResponse = repository.updateSchedule(id, updatedRequest)
                val updatedTask = mapScheduleResponseToTodoItem(updatedTaskResponse)
                _tasks.value = _tasks.value?.map { if (it.id == id.toInt()) updatedTask else it }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error updating task: ${e.message}")
            }
        }
    }

    /**
     * 일정 삭제
     */
    fun deleteTask(id: Long, email: String) {
        viewModelScope.launch {
            try {
                val isDeleted = repository.deleteSchedule(id, email)
                if (isDeleted) {
                    _tasks.value = _tasks.value?.filter { it.id != id.toInt() }
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error deleting task: ${e.message}")
            }
        }
    }

    /**
     * 특정 카테고리에 맞는 할 일 반환
     */
    fun getTasksForCategory(category: String): List<TodoItem> {
        return _tasks.value?.filter { it.category == category } ?: emptyList()
    }

    /**
     * 리마인더 옵션에 따라 필터링된 할 일 반환
     */
    fun getTasksForReminder(reminderOption: ReminderOption): List<TodoItem> {
        return _tasks.value?.filter { it.reminder == reminderOption } ?: emptyList()
    }

    /**
     * 샘플 데이터 추가 (테스트 용도)
     */
    fun addSampleData() {
        val sampleData = listOf(
            TodoItem(
                id = 1,
                title = "Sample Task 1",
                date = System.currentTimeMillis() + 86400000L,
                reminder = ReminderOption.DAY_BEFORE,
                category = "Work",
                isCompleted = false
            ),
            TodoItem(
                id = 2,
                title = "Sample Task 2",
                date = System.currentTimeMillis() + (86400000L * 2),
                reminder = ReminderOption.WEEK_BEFORE,
                category = "Personal",
                isCompleted = false
            ),
            TodoItem(
                id = 3,
                title = "Sample Task 3",
                date = System.currentTimeMillis() + (86400000L * 3),
                reminder = ReminderOption.TODAY,
                category = "Important",
                isCompleted = false
            ),
            TodoItem(
                id = 4,
                title = "Sample Task 4",
                date = System.currentTimeMillis() + (86400000L * 4),
                reminder = ReminderOption.DAY_BEFORE,
                category = "Birthday",
                isCompleted = false
            )
        )

        _tasks.value = _tasks.value?.plus(sampleData) ?: sampleData
    }

    /**
     * ScheduleResponse -> TodoItem 변환
     */
    private fun mapScheduleResponseToTodoItem(scheduleResponse: ScheduleResponse): TodoItem {
        return TodoItem(
            id = scheduleResponse.id.toInt(),
            title = scheduleResponse.title,
            date = parseDate(scheduleResponse.schedule),
            reminder = ReminderOption.TODAY, // 기본값 설정
            category = scheduleResponse.category,
            isCompleted = scheduleResponse.completionStatus
        )
    }

    /**
     * 날짜 문자열을 Long으로 변환
     */
    private fun parseDate(dateString: String): Long {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.parse(dateString)?.time ?: 0L
    }

    init {
        addSampleData() // 초기화 시 샘플 데이터 추가
    }
}
//    fun fetchAllTasks(email: String) {
//        viewModelScope.launch {
//            try {
//                val schedules = repository.getSchedules(email, year)
//                _tasks.value = schedules.map { mapScheduleResponseToTodoItem(it) }
//                Log.d("SharedViewModel", "Fetched tasks: ${_tasks.value}")
//            } catch (e: Exception) {
//                Log.e("SharedViewModel", "Error fetching tasks", e)
//            }
//        }
//    }

//    private fun filterTodoList(category: String) {
//        val filteredTasks = sharedViewModel.getTasksForCategory(category)
//        todoAdapter.submitList(filteredTasks)
//    }



//package com.example.agora_beta_20.ui
//
//import android.app.Application
//import android.content.Context
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.example.agora_beta_20.ui.ToDoLists.ReminderOption
//import com.example.agora_beta_20.ui.ToDoLists.TodoItem
//import org.xmlpull.v1.XmlPullParser
//import org.xmlpull.v1.XmlPullParserFactory
//import org.xmlpull.v1.XmlSerializer
//import java.io.File
//import java.io.FileOutputStream
//import java.io.StringWriter
//import java.util.*
//
//class SharedViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val _tasks = MutableLiveData<MutableList<TodoItem>>(mutableListOf())
//    val tasks: LiveData<MutableList<TodoItem>> get() = _tasks
//
//    private val context = application
//    private val fileName = "TodoList_saved.xml"
//
//    init {
//        // XML에서 데이터 로드
//        loadTasksFromFile()
//
//        // 샘플 데이터 추가
//        if (_tasks.value.isNullOrEmpty()) {
//            _tasks.value = mutableListOf(
//                TodoItem(
//                    id = 1,
//                    title = "Sample Task 1",
//                    date = getDateInMillis(2024, 11, 23),
//                    reminder = ReminderOption.DAY_BEFORE,
//                    category = "Work"
//                ),
//                TodoItem(
//                    id = 2,
//                    title = "Sample Task 2",
//                    date = getDateInMillis(2024, 11, 24),
//                    reminder = ReminderOption.WEEK_BEFORE,
//                    category = "Personal"
//                ),
//                TodoItem(
//                    id = 3,
//                    title = "Sample Task 3",
//                    date = getDateInMillis(2024, 11, 25),
//                    reminder = ReminderOption.DAY_TWO_BEFORE,
//                    category = "Birthday"
//                )
//            )
//            saveTasksToFile() // 샘플 데이터를 파일에 저장
//        }
//    }
//
//    // 할 일 추가 메서드
//    fun addTask(task: TodoItem) {
//        _tasks.value?.apply {
//            add(task)
//            _tasks.value = this // 리스트 업데이트
//            saveTasksToFile() // 파일에 저장
//        }
//    }
//
//    // 특정 날짜에 맞는 할 일 반환
//    fun getTasksForDate(dateMillis: Long): List<TodoItem> {
//        return _tasks.value?.filter { isSameDay(it.date, dateMillis) } ?: emptyList()
//    }
//
//    // 특정 카테고리에 맞는 할 일 반환
//    fun getTasksForCategory(category: String): List<TodoItem> {
//        return _tasks.value?.filter { it.category == category } ?: emptyList()
//    }
//
//    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
//        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
//        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
//        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
//                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
//    }
//
//    private fun getDateInMillis(year: Int, month: Int, day: Int): Long {
//        val calendar = Calendar.getInstance()
//        calendar.set(year, month - 1, day, 0, 0, 0)
//        calendar.set(Calendar.MILLISECOND, 0)
//        return calendar.timeInMillis
//    }
//
//    // 데이터를 XML 파일에 저장
//    private fun saveTasksToFile() {
//        val tasks = _tasks.value ?: return
//        try {
//            val file = File(context.filesDir, fileName)
//            val fos: FileOutputStream = FileOutputStream(file)
//            val serializer: XmlSerializer = android.util.Xml.newSerializer()
//            val writer = StringWriter()
//
//            serializer.setOutput(writer)
//            serializer.startDocument("UTF-8", true)
//            serializer.startTag("", "tasks")
//
//            for (task in tasks) {
//                serializer.startTag("", "task")
//                serializer.attribute("", "id", task.id.toString())
//
//                serializer.startTag("", "title")
//                serializer.text(task.title)
//                serializer.endTag("", "title")
//
//                serializer.startTag("", "date")
//                serializer.text(task.date.toString())
//                serializer.endTag("", "date")
//
//                serializer.startTag("", "reminder")
//                serializer.text(task.reminder.name) // Enum의 name 저장
//                serializer.endTag("", "reminder")
//
//                serializer.startTag("", "category")
//                serializer.text(task.category)
//                serializer.endTag("", "category")
//
//                serializer.startTag("", "isCompleted")
//                serializer.text(task.isCompleted.toString())
//                serializer.endTag("", "isCompleted")
//
//                serializer.endTag("", "task")
//            }
//
//            serializer.endTag("", "tasks")
//            serializer.endDocument()
//
//            fos.write(writer.toString().toByteArray())
//            fos.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    // XML 파일에서 데이터를 로드
//    private fun loadTasksFromFile() {
//        try {
//            val file = File(context.filesDir, fileName)
//            if (!file.exists()) return // 파일이 없으면 로드하지 않음
//
//            val fis = file.inputStream()
//            val factory = XmlPullParserFactory.newInstance()
//            val parser = factory.newPullParser()
//            parser.setInput(fis, "UTF-8")
//
//            var eventType = parser.eventType
//            var currentTask: TodoItem? = null
//            var currentTag: String? = null
//            val loadedTasks = mutableListOf<TodoItem>()
//
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                when (eventType) {
//                    XmlPullParser.START_TAG -> {
//                        currentTag = parser.name
//                        if (currentTag == "task") {
//                            currentTask = TodoItem(
//                                id = parser.getAttributeValue(null, "id").toInt(),
//                                title = "",
//                                date = 0L,
//                                reminder = ReminderOption.TODAY, // 기본값 설정
//                                category = ""
//                            )
//                        }
//                    }
//                    XmlPullParser.TEXT -> {
//                        currentTask?.let { task ->
//                            when (currentTag) {
//                                "title" -> task.title = parser.text
//                                "date" -> task.date = parser.text.toLong()
//                                "reminder" -> task.reminder = ReminderOption.valueOf(parser.text) // Enum 변환
//                                "category" -> task.category = parser.text
//                                "isCompleted" -> task.isCompleted = parser.text.toBoolean()
//                            }
//                        }
//                    }
//                    XmlPullParser.END_TAG -> {
//                        if (parser.name == "task") {
//                            currentTask?.let { loadedTasks.add(it) }
//                            currentTask = null
//                        }
//                        currentTag = null
//                    }
//                }
//                eventType = parser.next()
//            }
//
//            _tasks.value = loadedTasks
//            fis.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}