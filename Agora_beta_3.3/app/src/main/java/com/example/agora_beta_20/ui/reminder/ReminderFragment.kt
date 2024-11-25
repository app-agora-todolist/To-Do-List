package com.example.agora_beta_20.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agora_beta_20.R
import com.example.agora_beta_20.databinding.FragmentReminderBinding
import com.example.agora_beta_20.ui.SharedViewModel
import com.example.agora_beta_20.ui.ToDoLists.ReminderOption
import com.example.agora_beta_20.ui.ToDoLists.TodoAdapter
import com.example.agora_beta_20.ui.ToDoLists.TodoItem

class ReminderFragment : Fragment() {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var todoAdapter: TodoAdapter

    private lateinit var reminderButtons: List<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setupReminderButtons()
        setupTodoList()

        // 일정 추가 버튼 클릭 이벤트 설정
        binding.fabAddTask.setOnClickListener {
            showAddTodoDialog() // 다이얼로그 표시
        }

        sharedViewModel.tasks.observe(viewLifecycleOwner) {
            // Observe any updates
        }

        return root
    }

    private fun setupReminderButtons() {
        reminderButtons = listOf(
            binding.reminderHourBefore,
            binding.reminderDayBefore,
            binding.reminderWeekBefore,
            binding.reminderMonthBefore
        )

        reminderButtons.forEach { button ->
            button.setOnClickListener { selectReminder(button) }
        }
    }

    private fun selectReminder(selectedButton: View) {
        reminderButtons.forEach { button ->
            if (button is androidx.cardview.widget.CardView) {
                button.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.dark_gray)
                )
            }
        }

        if (selectedButton is androidx.cardview.widget.CardView) {
            selectedButton.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.orange)
            )
        }

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
        todoAdapter = TodoAdapter(
            onItemCheckedChange = { todoItem, isChecked ->
                // 체크박스 상태 변경 시 서버 요청
                sharedViewModel.updateTaskCompletionStatus(todoItem.id.toLong(), isChecked)
                Toast.makeText(requireContext(), "${todoItem.title} 완료 상태: $isChecked", Toast.LENGTH_SHORT).show()
            },
            onOptionsClick = { todoItem ->
                // 점 3개 버튼 클릭 시 동작
                showEditDialog(todoItem)
            }
        )
        binding.recyclerTodo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
        todoAdapter.submitList(sharedViewModel.tasks.value)
    }

    // EditTodoDialogFragment 띄우기
    private fun showEditDialog(todoItem: TodoItem) {
        val dialog = EditTodoDialogFragment.newInstance(todoItem)
        dialog.show(parentFragmentManager, "EditTodoDialog")
    }

    private fun filterTodoList(reminderOption: ReminderOption) {
        val filteredTasks = sharedViewModel.tasks.value?.filter { it.reminder == reminderOption } ?: emptyList()
        todoAdapter.submitList(filteredTasks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAddTodoDialog() {
        val dialog = AddTodoDialogFragment.newInstance() // 다이얼로그 생성
        dialog.setTargetFragment(this, 0)
        dialog.show(parentFragmentManager, "AddTodoDialogFragment")
    }
}