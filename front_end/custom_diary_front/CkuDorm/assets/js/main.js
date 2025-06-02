// Import components
import { initCharts } from './components/charts.js';
import { initTabs } from './components/tabs.js';

// Initialize all components when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initCharts();
    initTabs();
    
    // Initialize search functionality
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            // Add search functionality here
            console.log('Searching for:', e.target.value);
        });
    }

    // Initialize notification functionality
    const notificationButton = document.querySelector('.notification-button');
    if (notificationButton) {
        notificationButton.addEventListener('click', function() {
            // Add notification functionality here
            console.log('Notifications clicked');
        });
    }

    // 알림 오버레이 열기/닫기
    const notificationPopover = document.getElementById('notificationPopover');
    if (notificationButton && notificationPopover) {
        notificationButton.addEventListener('click', function(e) {
            e.stopPropagation();
            notificationPopover.classList.toggle('hidden');
            // 오버레이가 열릴 때 위치 조정
            const rect = notificationButton.getBoundingClientRect();
            notificationPopover.style.top = rect.bottom + window.scrollY + 8 + 'px';
            notificationPopover.style.right = (window.innerWidth - rect.right - 8) + 'px';
        });
    }
    // 바깥 클릭 시 오버레이 닫기
    document.addEventListener('click', function(e) {
        if (notificationPopover && !notificationPopover.classList.contains('hidden')) {
            notificationPopover.classList.add('hidden');
        }
    });
    // 오버레이 내부 클릭 시 닫히지 않도록
    if (notificationPopover) {
        notificationPopover.addEventListener('click', function(e) {
            e.stopPropagation();
        });
    }

    // 최근 요청 승인/거절 버튼 동작
    document.querySelectorAll('.approve-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const td = btn.parentElement;
            td.innerHTML = '<span class="status-badge approved">승인됨</span>';
        });
    });
    document.querySelectorAll('.reject-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const td = btn.parentElement;
            td.innerHTML = '<span class="status-badge urgent" style="background-color: #FF6F61; color: white;">거절됨</span>';
        });
    });
}); 