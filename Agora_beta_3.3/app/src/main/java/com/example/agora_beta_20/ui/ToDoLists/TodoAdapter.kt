package com.example.agora_beta_20.ui.ToDoLists

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agora_beta_20.databinding.ItemTodoBinding
import java.text.SimpleDateFormat
import java.util.*

data class TodoItem(
    val id: Int,
    var title: String,
    var date: Long,
    var reminder: ReminderOption,
    var category: String,
    var isCompleted: Boolean = false
)

enum class ReminderOption(val displayName: String) {
    TODAY("오늘"),
    DAY_BEFORE("하루 전"),
    DAY_TWO_BEFORE("이틀 전"),
    WEEK_BEFORE("일주일 전")
}

class TodoAdapter(
    private val onItemCheckedChange: (TodoItem, Boolean) -> Unit, // 체크박스 상태 변경 콜백
    private val onOptionsClick: (TodoItem) -> Unit // 점 3개 버튼 클릭 콜백
) : ListAdapter<TodoItem, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    inner class TodoViewHolder(private val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todoItem: TodoItem) {
            binding.todoTitle.text = todoItem.title
            binding.todoCategory.text = if (todoItem.category.isNotEmpty()) todoItem.category else "카테고리 없음"
            binding.todoReminder.text = todoItem.reminder.displayName
            binding.todoDate.text = formatDate(todoItem.date)
            binding.todoCheckbox.isChecked = todoItem.isCompleted

            // 체크박스 리스너
            binding.todoCheckbox.setOnCheckedChangeListener(null) // 기존 리스너 초기화
            binding.todoCheckbox.isChecked = todoItem.isCompleted
            binding.todoCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onItemCheckedChange(todoItem, isChecked) // 상태 변경 콜백 호출
            }

            // 점 3개 버튼 리스너
            binding.todoOptionsButton.setOnClickListener {
                onOptionsClick(todoItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todoItem = getItem(position)
        holder.bind(todoItem)
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

class TodoDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
    }
}