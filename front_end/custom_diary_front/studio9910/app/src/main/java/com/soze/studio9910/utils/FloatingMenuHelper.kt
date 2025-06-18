package com.soze.studio9910.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.soze.studio9910.R
import com.soze.studio9910.TodaysFeelingCompleteActivity
import com.soze.studio9910.TodaysFeelingWriteActivity
import java.text.SimpleDateFormat
import java.util.*

class FloatingMenuHelper(private val context: Context, private val rootView: ViewGroup) {
    
    private var floatingMenuView: View? = null
    private var isMenuVisible = false
    
    fun setupFloatingMenu() {
        if (floatingMenuView == null) {
            floatingMenuView = LayoutInflater.from(context).inflate(R.layout.floating_menu, rootView, false)
            rootView.addView(floatingMenuView)
            setupMenuClickListeners()
        }
    }
    
    private fun setupMenuClickListeners() {
        floatingMenuView?.let { menuView ->
            val menuContainer = menuView.findViewById<ConstraintLayout>(R.id.floatingMenuContainer)
            val menuOverlay = menuView.findViewById<View>(R.id.menuOverlay)
            val menuTodaysFeeling = menuView.findViewById<LinearLayout>(R.id.menuTodaysFeeling)
            val menuDailyMytoon = menuView.findViewById<LinearLayout>(R.id.menuDailyMytoon)
            val menuWeeklyMytoon = menuView.findViewById<LinearLayout>(R.id.menuWeeklyMytoon)
            
            // 오버레이 클릭 시 메뉴 닫기
            menuOverlay.setOnClickListener {
                hideMenu()
            }
            
            menuContainer.setOnClickListener {
                hideMenu()
            }
            
            // Today's Feeling 클릭
            menuTodaysFeeling.setOnClickListener {
                hideMenu()
                handleTodaysFeelingClick()
            }
            
            // Daily Mytoon 클릭 (준비 중)
            menuDailyMytoon.setOnClickListener {
                hideMenu()
                Toast.makeText(context, "Daily Mytoon 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show()
            }
            
            // Weekly Mytoon 클릭 (준비 중)
            menuWeeklyMytoon.setOnClickListener {
                hideMenu()
                Toast.makeText(context, "Weekly Mytoon 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    fun showMenu() {
        if (isMenuVisible) return
        
        floatingMenuView?.let { menuView ->
            val menuItems = menuView.findViewById<LinearLayout>(R.id.menuItems)
            
            menuView.visibility = View.VISIBLE
            isMenuVisible = true
            
            // 페이드 인 애니메이션
            val fadeIn = ObjectAnimator.ofFloat(menuView, "alpha", 0f, 1f)
            fadeIn.duration = 200
            
            // 메뉴 아이템들 슬라이드 업 애니메이션
            menuItems.translationY = 100f
            val slideUp = ObjectAnimator.ofFloat(menuItems, "translationY", 100f, 0f)
            slideUp.duration = 250
            
            fadeIn.start()
            slideUp.start()
        }
    }
    
    fun hideMenu() {
        if (!isMenuVisible) return
        
        floatingMenuView?.let { menuView ->
            val menuItems = menuView.findViewById<LinearLayout>(R.id.menuItems)
            
            // 페이드 아웃 애니메이션
            val fadeOut = ObjectAnimator.ofFloat(menuView, "alpha", 1f, 0f)
            fadeOut.duration = 200
            
            // 메뉴 아이템들 슬라이드 다운 애니메이션
            val slideDown = ObjectAnimator.ofFloat(menuItems, "translationY", 0f, 100f)
            slideDown.duration = 200
            
            fadeOut.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    menuView.visibility = View.GONE
                    isMenuVisible = false
                }
            })
            
            fadeOut.start()
            slideDown.start()
        }
    }
    
    private fun handleTodaysFeelingClick() {
        val intent = if (hasTodayDiary()) {
            // 이미 작성한 경우 - 완료 페이지로
            Intent(context, TodaysFeelingCompleteActivity::class.java)
        } else {
            // 작성하지 않은 경우 - 작성 페이지로
            Intent(context, TodaysFeelingWriteActivity::class.java)
        }
        context.startActivity(intent)
    }
    
    // 오늘 다이어리 작성 여부 확인
    private fun hasTodayDiary(): Boolean {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val prefs = context.getSharedPreferences("diary", Context.MODE_PRIVATE)
        return prefs.getBoolean(today, false)
    }
    
    fun isMenuVisible(): Boolean = isMenuVisible
} 