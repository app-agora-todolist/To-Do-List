package com.example.agora_beta_20.ui.category

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
import com.example.agora_beta_20.databinding.FragmentCategoryBinding
import com.example.agora_beta_20.ui.SharedViewModel
import com.example.agora_beta_20.ui.ToDoLists.TodoAdapter
import com.example.agora_beta_20.ui.ToDoLists.TodoItem
import com.example.agora_beta_20.ui.reminder.AddTodoDialogFragment
import com.example.agora_beta_20.ui.reminder.EditTodoDialogFragment

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setupCategoryButtons()
        setupRecyclerView()
        setupAddTaskButton() // 추가된 부분: + 버튼 설정

        // Observe tasks LiveData from SharedViewModel
        sharedViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            todoAdapter.submitList(tasks) // 초기화면에 전체 데이터 표시
        }

        return root
    }

    private fun setupCategoryButtons() {
        val categoryButtons = listOf(
            binding.categoryWork to "Work",
            binding.categoryPersonal to "Personal",
            binding.categoryImportant to "Important",
            binding.categoryBirthday to "Birthday"
        )

        categoryButtons.forEach { (button, category) ->
            button.setOnClickListener {
                filterTodoList(category)
                highlightSelectedButton(button)
            }
        }
    }

    private fun filterTodoList(category: String) {
        // 오늘 날짜를 가져오기
        val today = System.currentTimeMillis()

        // 오늘 이후의 일정 + 해당 카테고리의 일정만 필터링
        val filteredTasks = sharedViewModel.tasks.value?.filter { task ->
            task.category == category && task.date >= today
        } ?: emptyList()

        // RecyclerView 업데이트
        todoAdapter.submitList(filteredTasks)
    }

    private fun highlightSelectedButton(selectedButton: View) {
        val categoryButtons = listOf(
            binding.categoryWork,
            binding.categoryPersonal,
            binding.categoryImportant,
            binding.categoryBirthday
        )

        categoryButtons.forEach { button ->
            button.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (button == selectedButton) R.color.orange else R.color.dark_gray
                )
            )
        }
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(
            onItemCheckedChange = { todoItem, isChecked ->
                // 체크박스 상태 변경 시 서버 요청
                sharedViewModel.updateTaskCompletionStatus(todoItem.id.toLong(), isChecked)
            },
            onOptionsClick = { todoItem ->
                // 점 3개 버튼 클릭 시 EditTodoDialogFragment 열기
                val dialog = EditTodoDialogFragment.newInstance(todoItem)
                dialog.show(parentFragmentManager, "EditTodoDialog")
            }
        )
        binding.recyclerTodo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
    }

    private fun setupAddTaskButton() {
        // FloatingActionButton 클릭 리스너 설정
        binding.fabAddTask.setOnClickListener {
            showAddDialog()
        }
    }

    // AddTodoDialogFragment 띄우기
    private fun showAddDialog() {
        val dialog = AddTodoDialogFragment.newInstance()
        dialog.show(parentFragmentManager, "AddTodoDialog")
    }

    // EditTodoDialogFragment 띄우기
    private fun showEditDialog(todoItem: TodoItem) {
        val dialog = EditTodoDialogFragment.newInstance(todoItem)
        dialog.show(parentFragmentManager, "EditTodoDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}