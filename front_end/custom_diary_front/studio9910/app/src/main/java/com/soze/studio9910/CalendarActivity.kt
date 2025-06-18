package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var tvCurrentMonth: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var selectedDateTitle: TextView
    private lateinit var selectedDateTime: TextView
    private lateinit var selectedDateContent: TextView
    private lateinit var secondDiaryTitle: TextView
    private lateinit var secondDiaryTime: TextView
    private lateinit var secondDiaryContent: TextView

    private lateinit var calendarAdapter: CalendarAdapter
    private var currentCalendar = Calendar.getInstance()
    private var selectedDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        initViews()
        setupCalendar()
        setupClickListeners()
        setupBottomNavigation()
        updateCalendarDisplay()
        
        // 기본으로 15일 선택
        selectDate(15)
    }

    private fun initViews() {
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth)
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        selectedDateTitle = findViewById(R.id.selectedDateTitle)
        selectedDateTime = findViewById(R.id.selectedDateTime)
        selectedDateContent = findViewById(R.id.selectedDateContent)
        secondDiaryTitle = findViewById(R.id.secondDiaryTitle)
        secondDiaryTime = findViewById(R.id.secondDiaryTime)
        secondDiaryContent = findViewById(R.id.secondDiaryContent)
    }

    private fun setupCalendar() {
        calendarAdapter = CalendarAdapter { day ->
            selectDate(day)
        }
        
        calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(this@CalendarActivity, 7)
            adapter = calendarAdapter
        }
    }

    private fun setupClickListeners() {
        // 이전 달 버튼
        findViewById<ImageView>(R.id.btnPrevMonth).setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateCalendarDisplay()
        }

        // 다음 달 버튼
        findViewById<ImageView>(R.id.btnNextMonth).setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateCalendarDisplay()
        }

        // 새로고침 버튼
        findViewById<ImageView>(R.id.btnRefresh).setOnClickListener {
            updateCalendarDisplay()
        }

        // 검색 버튼
        findViewById<ImageView>(R.id.btnSearch).setOnClickListener {
            // TODO: 검색 기능 구현
        }
    }

    private fun updateCalendarDisplay() {
        // 년월 표시 업데이트
        val monthFormat = SimpleDateFormat("yyyy년 M월", Locale.KOREAN)
        tvCurrentMonth.text = monthFormat.format(currentCalendar.time)

        // 캘린더 데이터 생성
        val calendarDays = generateCalendarDays()
        calendarAdapter.updateDays(calendarDays)
    }

    private fun generateCalendarDays(): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        
        // 현재 월의 첫 번째 날
        val firstDayOfMonth = Calendar.getInstance().apply {
            time = currentCalendar.time
            set(Calendar.DAY_OF_MONTH, 1)
        }
        
        // 첫 번째 날의 요일 (일요일 = 1)
        val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1
        
        // 이전 달의 마지막 날들 추가
        val prevMonth = Calendar.getInstance().apply {
            time = firstDayOfMonth.time
            add(Calendar.MONTH, -1)
        }
        val prevMonthLastDay = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        for (i in firstDayOfWeek - 1 downTo 0) {
            days.add(CalendarDay(
                day = prevMonthLastDay - i,
                isCurrentMonth = false,
                hasDiary = false
            ))
        }
        
        // 현재 달의 날들 추가
        val currentMonthLastDay = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..currentMonthLastDay) {
            days.add(CalendarDay(
                day = day,
                isCurrentMonth = true,
                hasDiary = getDiaryStatus(day),
                isSelected = selectedDate?.get(Calendar.DAY_OF_MONTH) == day &&
                           selectedDate?.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                           selectedDate?.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
            ))
        }
        
        // 다음 달의 첫 번째 날들 추가 (42개 셀 채우기)
        val remainingCells = 42 - days.size
        for (day in 1..remainingCells) {
            days.add(CalendarDay(
                day = day,
                isCurrentMonth = false,
                hasDiary = false
            ))
        }
        
        return days
    }

    private fun getDiaryStatus(day: Int): Boolean {
        // 예시 데이터 - 실제로는 SharedPreferences나 DB에서 확인
        val diaryDays = listOf(2, 5, 9, 12, 15, 16, 20, 21, 23, 26, 30)
        return diaryDays.contains(day)
    }

    private fun selectDate(day: Int) {
        selectedDate = Calendar.getInstance().apply {
            time = currentCalendar.time
            set(Calendar.DAY_OF_MONTH, day)
        }
        
        updateCalendarDisplay()
        updateSelectedDateInfo(day)
    }

    private fun updateSelectedDateInfo(day: Int) {
        val monthFormat = SimpleDateFormat("M월", Locale.KOREAN)
        val currentMonth = monthFormat.format(currentCalendar.time)
        
        // 예시 데이터 - 실제로는 선택된 날짜의 일기 데이터를 불러와야 함
        when (day) {
            15 -> {
                selectedDateTitle.text = "${currentMonth}${day}일 | 여수 여행 일지"
                selectedDateTime.text = "19:45"
                selectedDateContent.text = "아침부터 일찍 일어나서 바로 바다 보..."
                
                secondDiaryTitle.text = "${currentMonth}${day}일 | 여수 여행 소감"
                secondDiaryTime.text = "21:45"
                secondDiaryContent.text = "내일 돌아가는 차 타면서 한 달간의 출장..."
                
                secondDiaryTitle.visibility = View.VISIBLE
                secondDiaryTime.visibility = View.VISIBLE
                secondDiaryContent.visibility = View.VISIBLE
            }
            else -> {
                selectedDateTitle.text = "${currentMonth}${day}일 | 일기"
                selectedDateTime.text = "20:00"
                selectedDateContent.text = "오늘 하루도 수고했어요..."
                
                secondDiaryTitle.visibility = View.GONE
                secondDiaryTime.visibility = View.GONE
                secondDiaryContent.visibility = View.GONE
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.navigation_calendar // 현재 페이지 활성화
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_feed -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_calendar -> {
                    // 이미 캘린더 화면이므로 아무 작업 안함
                    true
                }
                R.id.navigation_add -> {
                    // + 버튼 클릭 시 일기 작성 페이지로
                    if (hasTodayDiary()) {
                        // 이미 작성한 경우 - 완료 페이지로
                        val intent = Intent(this, TodaysFeelingCompleteActivity::class.java)
                        startActivity(intent)
                    } else {
                        // 작성하지 않은 경우 - 작성 페이지로
                        val intent = Intent(this, TodaysFeelingWriteActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }
                R.id.navigation_storage -> {
                    startActivity(Intent(this, StorageActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    // 오늘 다이어리 작성 여부 확인
    private fun hasTodayDiary(): Boolean {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)
        return prefs.getBoolean(today, false)
    }
} 