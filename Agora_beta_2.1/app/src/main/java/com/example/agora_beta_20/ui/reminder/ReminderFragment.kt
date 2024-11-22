package com.example.agora_beta_20.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agora_beta_20.R
import com.example.agora_beta_20.databinding.FragmentReminderBinding
import com.example.agora_beta_20.ui.ToDoLists.ReminderOption
import com.example.agora_beta_20.ui.ToDoLists.TodoAdapter
import com.example.agora_beta_20.ui.ToDoLists.TodoItem
import java.util.*

class ReminderFragment : Fragment(), AddTodoDialogFragment.AddTodoListener {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private val tasks = mutableListOf<TodoItem>()
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var reminderButtons: List<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 샘플 데이터 추가
        tasks.add(TodoItem(
            id = 1,
            title = "Finish project report",
            date = getDateInMillis(2024, 11, 22),
            reminder = ReminderOption.TODAY,
            category = "Work"
        ))
        tasks.add(TodoItem(
            id = 2,
            title = "Plan a birthday party",
            date = getDateInMillis(2024, 11, 21),
            reminder = ReminderOption.WEEK_BEFORE,
            category = "Birthday"
        ))
        tasks.add(TodoItem(
            id = 3,
            title = "Buy groceries",
            date = getDateInMillis(2024, 11, 23),
            reminder = ReminderOption.DAY_TWO_BEFORE,
            category = "Personal"
        ))
        tasks.add(TodoItem(
            id = 4,
            title = "Prepare meeting notes",
            date = getDateInMillis(2024, 11, 22),
            reminder = ReminderOption.WEEK_BEFORE,
            category = "Work"
        ))
        tasks.add(TodoItem(
            id = 5,
            title = "Call mom",
            date = getDateInMillis(2024, 11, 22),
            reminder = ReminderOption.DAY_BEFORE,
            category = "Personal"
        ))
        tasks.add(TodoItem(
            id = 6,
            title = "Pay electricity bill",
            date = getDateInMillis(2024, 11, 23),
            reminder = ReminderOption.WEEK_BEFORE,
            category = "Important"
        ))

        setupReminderButtons()
        setupTodoList()
        binding.fabAddTask.setOnClickListener {
            showAddTodoDialog()
        }

        return root
    }

    private fun setupReminderButtons() {


        // !! 여기 이름 다시 제대로 하기 !!
        reminderButtons = listOf(
            binding.reminderHourBefore,
            binding.reminderDayBefore,
            binding.reminderWeekBefore,
            binding.reminderMonthBefore
        )

        // 각 버튼에 클릭 이벤트 설정
        reminderButtons.forEach { button ->
            button.setOnClickListener { selectReminder(button) }
        }
    }

    private fun selectReminder(selectedButton: View) {
        // 기본값 검정색
        reminderButtons.forEach { button ->
            if (button is androidx.cardview.widget.CardView) {
                button.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.dark_gray)
                )
            }
        }

        // 선택 주황색으로 설정
        if (selectedButton is androidx.cardview.widget.CardView) {
            selectedButton.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.orange)
            )
        }

        // 선택된 리마인더 옵션 가져오기
        val selectedReminder = when (selectedButton.id) {
            binding.reminderHourBefore.id -> ReminderOption.TODAY
            binding.reminderDayBefore.id -> ReminderOption.DAY_BEFORE
            binding.reminderWeekBefore.id -> ReminderOption.DAY_TWO_BEFORE
            binding.reminderMonthBefore.id -> ReminderOption.WEEK_BEFORE
            else -> null
        }
        selectedReminder?.let { filterTodoList(it) }
    }

    private fun setupTodoList() {
        todoAdapter = TodoAdapter { todoItem, isChecked ->
            todoItem.isCompleted = isChecked
            Toast.makeText(requireContext(), "${todoItem.title} 완료 상태: $isChecked", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerTodo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
        todoAdapter.submitList(tasks.toList()) // 불변 리스트로 전달
    }

    private fun filterTodoList(reminderOption: ReminderOption) {
        val filteredTasks = tasks.filter { it.reminder == reminderOption }
        todoAdapter.submitList(filteredTasks)
    }

    private fun showAddTodoDialog() {
        val dialog = AddTodoDialogFragment.newInstance()
        dialog.setTargetFragment(this, 0)
        dialog.show(parentFragmentManager, "AddTodoDialogFragment")
    }

    override fun onTodoAdded(todoItem: TodoItem) {
        tasks.add(todoItem)
        todoAdapter.submitList(tasks.toList())
        Toast.makeText(requireContext(), "할 일이 추가되었습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDateInMillis(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun generateUniqueId(): Int {
        return (0..Int.MAX_VALUE).random()
    }
}