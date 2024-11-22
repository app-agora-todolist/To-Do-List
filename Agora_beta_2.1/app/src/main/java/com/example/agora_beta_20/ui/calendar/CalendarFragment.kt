package com.example.agora_beta_20.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    // 현재 표시되는 월과 년도
    private var currentMonth: Int = 0
    private var currentYear: Int = 0

    private lateinit var otherMonthDayDecorator: OtherMonthDayDecorator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
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

        setupRecyclerView()
        setupCalendarView()
        binding.fabAddTask.setOnClickListener {
            showAddTodoDialog()
        }
        return root
    }

    // 체크박스 상태 변경 처리
    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter { todoItem, isChecked ->
            todoItem.isCompleted = isChecked
        }
        binding.recyclerTodo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
        todoAdapter.submitList(tasks.toList())
    }

    private fun setupCalendarView() {
        // 현재 월과 년도 설정
        val today = CalendarDay.today()
        currentMonth = today.month
        currentYear = today.year

        binding.calendarView.setWeekDayFormatter(CustomWeekDayFormatter())
        binding.calendarView.setHeaderTextAppearance(R.style.HeaderTextAppearance)
        binding.calendarView.setDateTextAppearance(R.style.DateTextAppearance)
        binding.calendarView.setArrowColor(Color.WHITE)
        binding.calendarView.selectionColor = Color.parseColor("#FF5722")
        otherMonthDayDecorator = OtherMonthDayDecorator()
        binding.calendarView.addDecorator(otherMonthDayDecorator)
        binding.calendarView.setOnMonthChangedListener { widget, date ->
            currentMonth = date.month
            currentYear = date.year
            binding.calendarView.invalidateDecorators()
        }

        // !! 날짜 선택 (다시 구현할 것) !!
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            // 선택된 날짜에 대한 할 일을 필터링하여 표시
            val selectedDateMillis = getDateInMillis(date.year, date.month, date.day)
            val filteredTasks = tasks.filter { it.date == selectedDateMillis }
            todoAdapter.submitList(filteredTasks)
        }
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

    // 캘린더 요일 표시
    class CustomWeekDayFormatter : WeekDayFormatter {
        private val weekDays = arrayOf("S", "M", "T", "W", "T", "F", "S")

        override fun format(dayOfWeek: Int): CharSequence {
            val dayLabel = weekDays[dayOfWeek - 1]
            val spannableString = SpannableString(dayLabel)

            when (dayOfWeek) {
                Calendar.SUNDAY -> {
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        spannableString.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                Calendar.SATURDAY -> {
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.BLUE),
                        0,
                        spannableString.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                else -> {
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.WHITE),
                        0,
                        spannableString.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            return spannableString
        }
    }

    // 다른 달의 날짜에 투명도 적용
    inner class OtherMonthDayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.month != currentMonth || day.year != currentYear
        }
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.parseColor("#80FFFFFF")))
        }
    }

    // 날짜 == Y-M-D 타임스탬프
    private fun getDateInMillis(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}