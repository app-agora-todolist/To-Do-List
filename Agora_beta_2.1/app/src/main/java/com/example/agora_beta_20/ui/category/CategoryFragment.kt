package com.example.agora_beta_20.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agora_beta_20.R
import com.example.agora_beta_20.databinding.FragmentCategoryBinding
import com.example.agora_beta_20.ui.ToDoLists.ReminderOption
import com.example.agora_beta_20.ui.ToDoLists.TodoAdapter
import com.example.agora_beta_20.ui.ToDoLists.TodoItem
import java.util.*

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val tasks = mutableListOf<TodoItem>() // 전체 To-Do 리스트
    private lateinit var todoAdapter: TodoAdapter

    // 버튼 리스트
    private lateinit var categoryButtons: List<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
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

        setupCategoryButtons()
        setupTodoList()
        return root
    }

    private fun setupCategoryButtons() {
        categoryButtons = listOf(
            binding.categoryWork,
            binding.categoryPersonal,
            binding.categoryImportant,
            binding.categoryBirthday
        )

        // 버튼 클릭 이벤트
        categoryButtons.forEach { button ->
            button.setOnClickListener { selectCategory(button) }
        }
    }

    private fun selectCategory(selectedButton: View) {
        // 카테고리 버튼 검읂색으로 초기화
        categoryButtons.forEach { button ->
            if (button is androidx.cardview.widget.CardView) {
                button.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.dark_gray)
                )
            }
        }

        // 선택된 버튼만 주황색으로 설정
        if (selectedButton is androidx.cardview.widget.CardView) {
            selectedButton.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.orange)
            )
        }

        // 선택된 카테고리 가져오기
        val selectedCategory = when (selectedButton.id) {
            binding.categoryWork.id -> "Work"
            binding.categoryPersonal.id -> "Personal"
            binding.categoryImportant.id -> "Important"
            binding.categoryBirthday.id -> "Birthday"
            else -> null
        }

        // 선택된 카테고리에 맞게 필터링
        selectedCategory?.let { filterTodoList(it) }
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

    private fun filterTodoList(category: String) {
        val filteredTasks = tasks.filter { it.category == category }
        todoAdapter.submitList(filteredTasks)
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