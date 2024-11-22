package com.example.agora_beta_20.ui.reminder

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.agora_beta_20.databinding.DialogAddTodoBinding
import com.example.agora_beta_20.ui.ToDoLists.ReminderOption
import com.example.agora_beta_20.ui.ToDoLists.TodoItem
import java.util.*

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

        // TodoItem 생성
        val newTodo = TodoItem(
            id = generateUniqueId(),
            title = title,
            date = selectedDateMillis,
            reminder = reminder,
            category = finalCategory,
            isCompleted = false
        )

        // 리스너를 통해 추가된 TodoItem 전달
        (targetFragment as? AddTodoListener)?.onTodoAdded(newTodo)
    }

    // 고유 ID 생성 (간단한 예시, 실제 앱에서는 더 안전한 방법 사용 권장)
    private fun generateUniqueId(): Int {
        return (0..Int.MAX_VALUE).random()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}