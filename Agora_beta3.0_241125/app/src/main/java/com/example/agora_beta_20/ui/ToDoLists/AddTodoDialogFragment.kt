package com.example.agora_beta_20.ui.reminder

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.agora_beta_20.data.api.ScheduleRequest
import com.example.agora_beta_20.databinding.DialogAddTodoBinding
import com.example.agora_beta_20.ui.SharedViewModel
import com.example.agora_beta_20.ui.ToDoLists.ReminderOption
import com.example.agora_beta_20.ui.ToDoLists.TodoItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class AddTodoDialogFragment : DialogFragment() {

    private var _binding: DialogAddTodoBinding? = null
    private val binding get() = _binding!!

    interface AddTodoListener {
        fun onTodoAdded(todoItem: TodoItem)
    }

    companion object {
        fun newInstance(): AddTodoDialogFragment {
            return AddTodoDialogFragment()
        }
    }

    private var selectedDateMillis: Long = Calendar.getInstance().timeInMillis

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddTodoBinding.inflate(LayoutInflater.from(context))

        // Spinner 어댑터 설정 (카테고리 선택)
        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Work", "Personal", "Important", "Birthday", "none")
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Spinner 어댑터 설정 (리마인더 옵션)
        binding.spinnerReminder.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ReminderOption.values().map { it.displayName }
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // 날짜 선택 버튼 클릭 리스너 설정
        binding.buttonSelectDate.setOnClickListener {
            showDatePicker()
        }

        // 초기 선택 날짜 표시
        updateSelectedDateText()

        return AlertDialog.Builder(requireContext())
            .setTitle("새 할 일 추가")
            .setView(binding.root)
            .setPositiveButton("추가") { _, _ ->
                addTodo()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateMillis

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
                selectedCalendar.set(Calendar.MILLISECOND, 0)
                selectedDateMillis = selectedCalendar.timeInMillis
                updateSelectedDateText()
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun updateSelectedDateText() {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.textViewSelectedDate.text = sdf.format(Date(selectedDateMillis))
    }

    private fun addTodo() {
        val title = binding.editTextTodoTitle.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem as String
        val reminderPosition = binding.spinnerReminder.selectedItemPosition
        val reminder = ReminderOption.values().getOrNull(reminderPosition) ?: ReminderOption.TODAY

        if (TextUtils.isEmpty(title)) {
            binding.editTextTodoTitle.error = "제목을 입력해주세요."
            return
        }

        // 카테고리가 "none"인 경우 빈 문자열로 설정
        val finalCategory = if (category == "none") "" else category

        // 리마인더 날짜 계산
        val reminderDateMillis = calculateReminderDate(selectedDateMillis, reminder)

        // 서버에 전달할 ScheduleRequest 생성
        val scheduleRequest = ScheduleRequest(
            email = "user@example.com", // 사용자 이메일 (적절히 수정 필요)
            title = title,
            category = finalCategory,
            schedule = formatDate(selectedDateMillis), // 선택한 날짜
            reminderDate = formatDate(reminderDateMillis) // 리마인더 날짜
        )

        // ViewModel을 사용해 서버와 통신
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.addTask(scheduleRequest)

        // 다이얼로그 닫기
        dismiss()
    }

//    formatDate
    private fun formatDate(dateMillis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(dateMillis))
    }

//    리마인더 요청 날짜 세팅 함수
private fun calculateReminderDate(baseDate: Long, reminderOption: ReminderOption): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = baseDate

    when (reminderOption) {
        ReminderOption.TODAY -> {
            // 해당 날짜로 설정 (변경 없음)
        }
        ReminderOption.DAY_BEFORE -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1) // 하루 전
        }
        ReminderOption.DAY_TWO_BEFORE -> {
            calendar.add(Calendar.DAY_OF_MONTH, -2) // 이틀 전
        }
        ReminderOption.WEEK_BEFORE -> {
            calendar.add(Calendar.WEEK_OF_YEAR, -1) // 일주일 전
        }
    }

    return calendar.timeInMillis
}

    private fun addTodoToXmlFile(todo: TodoItem) {
        try {
            // 내부 저장소의 XML 파일 열기/생성
            val fileName = "todolist_sample.xml"
            val file = File(requireContext().filesDir, fileName)
            if (!file.exists()) {
                file.createNewFile()
                file.writeText("<tasks></tasks>") // 초기 XML 구조 생성
            }

            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(file)

            // 새 TodoItem을 XML에 추가
            val root = document.documentElement
            val newTask = document.createElement("task")
            newTask.setAttribute("id", todo.id.toString())

            val titleElement = document.createElement("title")
            titleElement.textContent = todo.title
            newTask.appendChild(titleElement)

            val dateElement = document.createElement("date")
            dateElement.textContent = todo.date.toString()
            newTask.appendChild(dateElement)

            val reminderElement = document.createElement("reminder")
            reminderElement.textContent = todo.reminder.name
            newTask.appendChild(reminderElement)

            val categoryElement = document.createElement("category")
            categoryElement.textContent = todo.category
            newTask.appendChild(categoryElement)

            val isCompletedElement = document.createElement("isCompleted")
            isCompletedElement.textContent = todo.isCompleted.toString()
            newTask.appendChild(isCompletedElement)

            root.appendChild(newTask)

            // XML 파일 업데이트
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            val result = DOMSource(document)
            val output = StreamResult(file)
            transformer.transform(result, output)

            Toast.makeText(requireContext(), "새로운 일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "일정 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateUniqueId(): Int {
        return (0..Int.MAX_VALUE).random()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}