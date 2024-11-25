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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agora_beta_20.R
import com.example.agora_beta_20.databinding.FragmentCalendarBinding
import com.example.agora_beta_20.ui.SharedViewModel
import com.example.agora_beta_20.ui.ToDoLists.TodoAdapter
import com.example.agora_beta_20.ui.ToDoLists.TodoItem
import com.example.agora_beta_20.ui.reminder.AddTodoDialogFragment
import com.example.agora_beta_20.ui.reminder.EditTodoDialogFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter
import java.util.*


class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var todoAdapter: TodoAdapter

    private var selectedDateMillis: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setupRecyclerView()
        setupCalendarView()

        // Fab 버튼으로 새로운 일정 추가
        binding.fabAddTask.setOnClickListener {
            showAddTodoDialog()
        }

        // ViewModel 데이터 관찰
        sharedViewModel.tasks.observe(viewLifecycleOwner) {
            updateTaskListForDate(selectedDateMillis)
        }

        // 서버에서 현재 연도의 일정 데이터 가져오기
        val email = "user@example.com"
        val currentYear = Calendar.getInstance().get(Calendar.YEAR) // 현재 연도 계산
        sharedViewModel.fetchSchedules(email, currentYear)

        // 오늘 날짜 설정 및 초기 데이터 로드
        val today = CalendarDay.today()
        selectedDateMillis = getDateInMillis(today.year, today.month, today.day)
        binding.calendarView.setSelectedDate(today)
        updateTaskListForDate(selectedDateMillis)

        return binding.root
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(
            onItemCheckedChange = { todoItem, isChecked ->
                // 체크박스 상태 변경 시 서버 요청
                sharedViewModel.updateTaskCompletionStatus(todoItem.id.toLong(), isChecked)
                Toast.makeText(requireContext(), "${todoItem.title} 완료 상태: $isChecked", Toast.LENGTH_SHORT).show()
            },
            onOptionsClick = { todoItem ->
                // 점 3개 버튼 클릭 시 수정 다이얼로그 호출
                showEditDialog(todoItem)
            }
        )
        binding.recyclerTodo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
    }

    // EditTodoDialogFragment 띄우기
    private fun showEditDialog(todoItem: TodoItem) {
        val dialog = EditTodoDialogFragment.newInstance(todoItem)
        dialog.show(parentFragmentManager, "EditTodoDialog")
    }

    private fun setupCalendarView() {
        binding.calendarView.apply {
            setWeekDayFormatter(CustomWeekDayFormatter())
            setArrowColor(Color.WHITE)
            selectionColor = ContextCompat.getColor(requireContext(), R.color.orange)
            addDecorator(OtherMonthDayDecorator())

            setOnDateChangedListener { _, date, selected ->
                if (selected) {
                    selectedDateMillis = getDateInMillis(date.year, date.month, date.day)
                    updateTaskListForDate(selectedDateMillis)
                }
            }
        }
    }
    // 요일 표시 글자 색상
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
            }
            return spannableString
        }
    }
// 다른 달 날짜를 감지하여 투명도를 설정하는 데코레이터
    class OtherMonthDayDecorator : com.prolificinteractive.materialcalendarview.DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val currentDate = CalendarDay.today()
            return day.month != currentDate.month || day.year != currentDate.year
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.parseColor("#80FFFFFF"))) // 50% 투명도 적용
        }
    }

    private fun updateTaskListForDate(dateMillis: Long) {
        val tasksForDate = sharedViewModel.tasks.value?.filter {
            isSameDay(it.date, dateMillis)
        } ?: emptyList()
        todoAdapter.submitList(tasksForDate)
    }

    private fun showAddTodoDialog() {
        val dialog = AddTodoDialogFragment.newInstance()
        dialog.setTargetFragment(this, 0)
        dialog.show(parentFragmentManager, "AddTodoDialogFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDateInMillis(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

//    한달씩 차이나는 현상 해결을 위해 -1 도입
private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

    // 로그로 각 값을 출력
    Log.d("isSameDay", "Timestamp1: $timestamp1 -> Year: ${cal1.get(Calendar.YEAR)}, Month: ${cal1.get(Calendar.MONTH) + 1}, Day: ${cal1.get(Calendar.DAY_OF_MONTH)}")
    Log.d("isSameDay", "Timestamp2: $timestamp2 -> Year: ${cal2.get(Calendar.YEAR)}, Month: ${cal2.get(Calendar.MONTH) + 1}, Day: ${cal2.get(Calendar.DAY_OF_MONTH)}")

    val isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    val isSameMonth = cal1.get(Calendar.MONTH) == (cal2.get(Calendar.MONTH) + 1) // 수정된 부분
    val isSameDay = cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)

    Log.d("isSameDay", "isSameYear: $isSameYear, isSameMonth: $isSameMonth, isSameDay: $isSameDay")

    return isSameYear && isSameMonth && isSameDay
}

    private fun convertServerMonthToCalendarMonth(serverMonth: Int): Int {
        return serverMonth - 1 // 서버는 1-base, Calendar는 0-base
    }
    private fun convertCalendarMonthToServerMonth(calendarMonth: Int): Int {
        return calendarMonth + 1 // Calendar는 0-base, 서버는 1-base
    }
}


//class CalendarFragment : Fragment() {
//
//    private var _binding: FragmentCalendarBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var sharedViewModel: SharedViewModel
//    private lateinit var todoAdapter: TodoAdapter
//
//    private var selectedDateMillis: Long = 0L
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View? {
//        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
//
//        setupRecyclerView()
//        setupCalendarView()
//        binding.fabAddTask.setOnClickListener {
//            showAddTodoDialog()
//        }
//
//        sharedViewModel.tasks.observe(viewLifecycleOwner) {
//            updateTaskListForDate(selectedDateMillis)
//        }
//
//        // 오늘 날짜 설정 및 초기 데이터 로드
//        val today = CalendarDay.today()
//        selectedDateMillis = getDateInMillis(today.year, today.month, today.day)
//        Log.d("CalendarFragment", "Today: $today, Selected Date Millis: $selectedDateMillis")
//        binding.calendarView.setSelectedDate(today)
//        updateTaskListForDate(selectedDateMillis)
//
//        return root
//    }
//
//    private fun setupRecyclerView() {
//        todoAdapter = TodoAdapter { todoItem, isChecked ->
//            todoItem.isCompleted = isChecked
//            Toast.makeText(requireContext(), "${todoItem.title} 완료 상태: $isChecked", Toast.LENGTH_SHORT).show()
//        }
//        binding.recyclerTodo.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = todoAdapter
//        }
//    }
//
//// 캘린더 관련 설정
//private fun setupCalendarView() {
//    binding.calendarView.apply {
//        setWeekDayFormatter(CustomWeekDayFormatter()) // 요일 색상 설정
//        setArrowColor(Color.WHITE)
//        selectionColor = ContextCompat.getColor(requireContext(), R.color.orange)
//
//        addDecorator(OtherMonthDayDecorator()) // 다른 달 날짜 투명도 설정
//
//        setOnDateChangedListener { _, date, selected ->
//            if (selected) {
//                selectedDateMillis = getDateInMillis(date.year, date.month, date.day)
//                updateTaskListForDate(selectedDateMillis)
//            }
//        }
//    }
//}
//
//    // 다른 달 날짜를 감지하여 투명도를 설정하는 데코레이터
//    class OtherMonthDayDecorator : com.prolificinteractive.materialcalendarview.DayViewDecorator {
//        override fun shouldDecorate(day: CalendarDay): Boolean {
//            val currentDate = CalendarDay.today()
//            return day.month != currentDate.month || day.year != currentDate.year
//        }
//
//        override fun decorate(view: DayViewFacade) {
//            view.addSpan(ForegroundColorSpan(Color.parseColor("#80FFFFFF"))) // 50% 투명도 적용
//        }
//    }
//
//    // 요일 표시 글자 색상
//    class CustomWeekDayFormatter : WeekDayFormatter {
//        private val weekDays = arrayOf("S", "M", "T", "W", "T", "F", "S")
//
//        override fun format(dayOfWeek: Int): CharSequence {
//            val dayLabel = weekDays[dayOfWeek - 1]
//            val spannableString = SpannableString(dayLabel)
//            when (dayOfWeek) {
//                Calendar.SUNDAY -> spannableString.setSpan(
//                    ForegroundColorSpan(Color.RED),
//                    0,
//                    spannableString.length,
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//                Calendar.SATURDAY -> spannableString.setSpan(
//                    ForegroundColorSpan(Color.BLUE),
//                    0,
//                    spannableString.length,
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//            }
//            return spannableString
//        }
//    }
//
//    private fun updateTaskListForDate(dateMillis: Long) {
//        Log.d("CalendarFragment", "Updating Task List for Date Millis: $dateMillis")
//
//        // tasks 리스트에서 직접 날짜 비교하여 필터링
//        val tasksForDate = sharedViewModel.tasks.value?.filter {
//            isSameDay(it.date, dateMillis)
//        } ?: emptyList()
//
//        Log.d("CalendarFragment", "Tasks for Date: $tasksForDate")
//        todoAdapter.submitList(tasksForDate)
//    }
//
//    private fun showAddTodoDialog() {
//        val dialog = AddTodoDialogFragment.newInstance() // 다이얼로그 생성
//        dialog.setTargetFragment(this, 0)
//        dialog.show(parentFragmentManager, "AddTodoDialogFragment")
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun getDateInMillis(year: Int, month: Int, day: Int): Long {
//        val timeZone = TimeZone.getDefault() // 시스템 타임존 가져오기
//        val calendar = Calendar.getInstance(timeZone).apply {
//            set(year, month - 1, day, 0, 0, 0) // month - 1: Calendar는 0-based
//            set(Calendar.MILLISECOND, 0)
//        }
//        val millis = calendar.timeInMillis
//        Log.d("CalendarFragment", "Converting to Millis: Year=$year, Month=$month, Day=$day => Millis=$millis")
//        return millis
//    }
//
//    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
//        val timeZone = TimeZone.getDefault() // 시스템 타임존 가져오기
//
//        val cal1 = Calendar.getInstance(timeZone).apply { timeInMillis = timestamp1 }
//        val cal2 = Calendar.getInstance(timeZone).apply { timeInMillis = timestamp2 }
//
//        val isSameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//                cal1.get(Calendar.MONTH) == (cal2.get(Calendar.MONTH) + 1) &&
//                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
//
//        Log.d(
//            "CalendarFragment",
//            "Comparing Days: Timestamp1=${Date(timestamp1)}, Timestamp2=${Date(timestamp2)}, IsSameDay=$isSameDay"
//        )
//
//        return isSameDay
//    }
//}