package com.example.agora_beta_20.ui.reminder

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
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
import java.text.SimpleDateFormat
import java.util.*

class EditTodoDialogFragment(private val todoItem: TodoItem) : DialogFragment() {

    private var _binding: DialogAddTodoBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private var selectedDateMillis: Long = todoItem.date

    companion object {
        fun newInstance(todoItem: TodoItem): EditTodoDialogFragment {
            return EditTodoDialogFragment(todoItem)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddTodoBinding.inflate(LayoutInflater.from(context))
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

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

        // 기존 일정 데이터를 UI에 채우기
        populateDialog(todoItem)

        // 날짜 선택 버튼 클릭 리스너 설정
        binding.buttonSelectDate.setOnClickListener {
            showDatePicker()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("일정 수정")
            .setView(binding.root)
            .setPositiveButton("수정") { _, _ ->
                editTodo()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("삭제") { _, _ ->
                deleteTodo()
            }
            .create()
    }

    private fun populateDialog(todoItem: TodoItem) {
        binding.editTextTodoTitle.setText(todoItem.title)
        binding.spinnerCategory.setSelection(
            (binding.spinnerCategory.adapter as ArrayAdapter<String>).getPosition(todoItem.category)
        )
        binding.spinnerReminder.setSelection(todoItem.reminder.ordinal)
        selectedDateMillis = todoItem.date
        updateSelectedDateText()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateMillis

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, day, 0, 0, 0)
                selectedDateMillis = selectedCalendar.timeInMillis
                updateSelectedDateText()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateSelectedDateText() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.textViewSelectedDate.text = sdf.format(Date(selectedDateMillis))
    }

    private fun editTodo() {
        val title = binding.editTextTodoTitle.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem as String
        val reminderPosition = binding.spinnerReminder.selectedItemPosition
        val reminder = ReminderOption.values().getOrNull(reminderPosition) ?: ReminderOption.TODAY

        if (TextUtils.isEmpty(title)) {
            binding.editTextTodoTitle.error = "제목을 입력해주세요."
            return
        }

        // ScheduleRequest 생성 시 기존 completionStatus 값 사용
        val scheduleRequest = ScheduleRequest(
            email = "user@example.com", // 적절히 수정 필요
            title = title,
            category = if (category == "none") "" else category,
            schedule = formatDate(selectedDateMillis),
            reminderDate = formatDate(calculateReminderDate(selectedDateMillis, reminder)),
            completionStatus = todoItem.isCompleted // 기존 값 유지
        )
        sharedViewModel.updateTask(todoItem.id.toLong(), scheduleRequest)

        Toast.makeText(requireContext(), "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    private fun deleteTodo() {
        sharedViewModel.deleteTask(todoItem.id.toLong(), "user@example.com")
        Toast.makeText(requireContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    private fun formatDate(dateMillis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(dateMillis))
    }

    private fun calculateReminderDate(baseDate: Long, reminderOption: ReminderOption): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = baseDate

        when (reminderOption) {
            ReminderOption.TODAY -> {
                // 해당 날짜로 설정
            }
            ReminderOption.DAY_BEFORE -> calendar.add(Calendar.DAY_OF_MONTH, -1)
            ReminderOption.DAY_TWO_BEFORE -> calendar.add(Calendar.DAY_OF_MONTH, -2)
            ReminderOption.WEEK_BEFORE -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
        }

        return calendar.timeInMillis
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}