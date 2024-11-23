package com.example.agora_beta_20.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agora_beta_20.R
import com.example.agora_beta_20.databinding.FragmentCalendarBinding
import com.example.agora_beta_20.ui.ToDoLists.ReminderOption
import com.example.agora_beta_20.ui.ToDoLists.TodoAdapter
import com.example.agora_beta_20.ui.ToDoLists.TodoItem
import com.example.agora_beta_20.ui.reminder.AddTodoDialogFragment
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter
import java.util.*

class CalendarFragment : Fragment(), AddTodoDialogFragment.AddTodoListener {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var todoAdapter: TodoAdapter
    private val tasks = mutableListOf<TodoItem>()

    private lateinit var otherMonthDayDecorator: OtherMonthDayDecorator

    // 현재 선택된 날짜의 타임스탬프
    private var selectedDateMillis: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 샘플 데이터 추가
        tasks.addAll(
            listOf(
                TodoItem(
                    id = 1,
                    title = "Task_1",
                    date = getDateInMillis(2024, 11, 21),
                    reminder = ReminderOption.TODAY,
                    category = "Work"
                ),
                TodoItem(
                    id = 2,
                    title = "Task_2",
                    date = getDateInMillis(2024, 11, 22),
                    reminder = ReminderOption.WEEK_BEFORE,
                    category = "Birthday"
                ),
                TodoItem(
                    id = 3,
                    title = "Task_3",
                    date = getDateInMillis(2024, 11, 23),
                    reminder = ReminderOption.DAY_TWO_BEFORE,
                    category = "Personal"
                ),
                TodoItem(
                    id = 4,
                    title = "Task_4",
                    date = getDateInMillis(2024, 11, 22),
                    reminder = ReminderOption.WEEK_BEFORE,
                    category = "Work"
                ),
                TodoItem(
                    id = 5,
                    title = "Task_5",
                    date = getDateInMillis(2024, 11, 22),
                    reminder = ReminderOption.DAY_BEFORE,
                    category = "Personal"
                ),
                TodoItem(
                    id = 6,
                    title = "Task_6",
                    date = getDateInMillis(2024, 11, 21),
                    reminder = ReminderOption.WEEK_BEFORE,
                    category = "Important"
                )
            )
        )

        // 로그를 통해 초기 데이터 확인
        tasks.forEach { task ->
            Log.d("CalendarFragment", "Task: ${task.title}, Date: ${Date(task.date)}")
        }

        setupRecyclerView()
        setupCalendarView()
        binding.fabAddTask.setOnClickListener {
            showAddTodoDialog()
        }

        // 초기 로드 시 오늘 날짜의 할 일 표시
        val today = CalendarDay.today()
        selectedDateMillis = getDateInMillis(today.year, today.month, today.day)
        binding.calendarView.setSelectedDate(today)
        updateTaskListForDate(selectedDateMillis)

        return root
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter { todoItem, isChecked ->
            todoItem.isCompleted = isChecked
            // 필요한 경우 상태 변경을 ViewModel이나 데이터베이스에 반영
            Toast.makeText(requireContext(), "${todoItem.title} 완료 상태: $isChecked", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerTodo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
        // 초기 리스트 설정
        todoAdapter.submitList(tasks.toList())
    }

    private fun setupCalendarView() {
        binding.calendarView.apply {
            setWeekDayFormatter(CustomWeekDayFormatter())
            setHeaderTextAppearance(R.style.HeaderTextAppearance)
            setDateTextAppearance(R.style.DateTextAppearance)
            setArrowColor(Color.WHITE)
            selectionColor = ContextCompat.getColor(requireContext(), R.color.orange)

            otherMonthDayDecorator = OtherMonthDayDecorator()
            addDecorator(otherMonthDayDecorator)

            setOnDateChangedListener { widget, date, selected ->
                if (selected) {
                    Log.d("CalendarFragment", "Date selected: Year=${date.year}, Month=${date.month}, Day=${date.day}")
                    selectedDateMillis = getDateInMillis(date.year, date.month, date.day)
                    Log.d("CalendarFragment", "Selected Date: ${Date(selectedDateMillis)}")
                    updateTaskListForDate(selectedDateMillis)
                }
            }

            setOnMonthChangedListener { widget, date ->
                invalidateDecorators()
            }
        }
    }

    private fun updateTaskListForDate(dateMillis: Long) {
        Log.d("CalendarFragment", "Selected Date Millis: $dateMillis")
        val filteredTasks = tasks.filter {
            val result = isSameDay(it.date, dateMillis)
            Log.d("CalendarFragment", "Task: ${it.title}, Task Date: ${Date(it.date)}, Is Same Day: $result")
            result
        }
        Log.d("CalendarFragment", "Filtered Tasks: $filteredTasks")
        todoAdapter.submitList(filteredTasks)
        if (filteredTasks.isEmpty()) {
            Toast.makeText(requireContext(), "선택한 날짜에 할 일이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddTodoDialog() {
        val dialog = AddTodoDialogFragment.newInstance()
        dialog.setTargetFragment(this, 0)
        dialog.show(parentFragmentManager, "AddTodoDialogFragment")
    }

    override fun onTodoAdded(todoItem: TodoItem) {
        tasks.add(todoItem)
        Log.d("CalendarFragment", "New Todo Added: ${todoItem.title}, Date: ${Date(todoItem.date)}")
        // 현재 선택된 날짜에 맞게 리스트를 업데이트
        if (isSameDay(todoItem.date, selectedDateMillis)) {
            updateTaskListForDate(selectedDateMillis)
        }
        Toast.makeText(requireContext(), "할 일이 추가되었습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDateInMillis(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        // Calendar.MONTH는 0-based이므로 month - 1을 합니다.
        calendar.set(year, month - 1, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val millis = calendar.timeInMillis
        Log.d("CalendarFragment", "Setting date: Year=$year, Month=${month - 1}, Day=$day => Millis=$millis")
        return millis
    }

    // Calendar 객체를 사용한 날짜 비교
    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
        Log.d("CalendarFragment", "Comparing dates: ${cal1.get(Calendar.YEAR)}-${cal1.get(Calendar.MONTH) + 1}-${cal1.get(Calendar.DAY_OF_MONTH)} vs ${cal2.get(Calendar.YEAR)}-${cal2.get(Calendar.MONTH) + 1}-${cal2.get(Calendar.DAY_OF_MONTH)}")
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == (cal2.get(Calendar.MONTH) + 1) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    class CustomWeekDayFormatter : WeekDayFormatter {
        private val weekDays = arrayOf("S", "M", "T", "W", "T", "F", "S")

        override fun format(dayOfWeek: Int): CharSequence {
            val dayLabel = weekDays[dayOfWeek - 1]
            val spannableString = SpannableString(dayLabel)
            when (dayOfWeek) {
                Calendar.SUNDAY -> spannableString.setSpan(
                    ForegroundColorSpan(Color.RED),
                    0,
                    spannableString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                Calendar.SATURDAY -> spannableString.setSpan(
                    ForegroundColorSpan(Color.BLUE),
                    0,
                    spannableString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                else -> spannableString.setSpan(
                    ForegroundColorSpan(Color.WHITE),
                    0,
                    spannableString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return spannableString
        }
    }

    inner class OtherMonthDayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.month != binding.calendarView.currentDate.month || day.year != binding.calendarView.currentDate.year
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.parseColor("#80FFFFFF")))
        }
    }
}