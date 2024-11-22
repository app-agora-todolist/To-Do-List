package com.example.agora_beta_20.ui.ToDoLists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agora_beta_20.databinding.ItemTodoBinding
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(
    private val onItemCheckedChange: (TodoItem, Boolean) -> Unit
) : ListAdapter<TodoItem, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    inner class TodoViewHolder(private val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todoItem: TodoItem) {
            binding.todoTitle.text = todoItem.title
            binding.todoCategory.text = if (todoItem.category.isNotEmpty()) todoItem.category else "카테고리 없음"
            binding.todoReminder.text = todoItem.reminder.displayName
            binding.todoDate.text = formatDate(todoItem.date)
            binding.todoCheckbox.isChecked = todoItem.isCompleted

            // 체크박스 상태 변경 리스너 설정
            binding.todoCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onItemCheckedChange(todoItem, isChecked)
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
        // 고유 ID로 아이템 비교
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        // 모든 속성 비교
        return oldItem == newItem
    }
}