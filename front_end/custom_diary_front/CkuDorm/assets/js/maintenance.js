// 모달 관련 요소
const addMaintenanceBtn = document.getElementById('addMaintenanceBtn');
const addMaintenanceModal = document.getElementById('addMaintenanceModal');
const closeAddMaintenanceModal = document.getElementById('closeAddMaintenanceModal');
const cancelAddMaintenance = document.getElementById('cancelAddMaintenance');
const addMaintenanceForm = document.getElementById('addMaintenanceForm');

// 모달 열기
addMaintenanceBtn.addEventListener('click', () => {
  addMaintenanceModal.style.display = 'block';
});

// 모달 닫기
function closeModal() {
  addMaintenanceModal.style.display = 'none';
}

closeAddMaintenanceModal.addEventListener('click', closeModal);
cancelAddMaintenance.addEventListener('click', closeModal);

// 모달 외부 클릭 시 닫기
window.addEventListener('click', (e) => {
  if (e.target === addMaintenanceModal) {
    closeModal();
  }
});

// 폼 제출 처리
addMaintenanceForm.addEventListener('submit', (e) => {
  e.preventDefault();
  
  // 폼 데이터 수집
  const formData = {
    roomNumber: document.getElementById('roomNumber').value,
    issueType: document.getElementById('issueType').value,
    description: document.getElementById('description').value,
    priority: document.getElementById('priority').value
  };

  // TODO: API 호출하여 데이터 저장
  console.log('보수 신청 데이터:', formData);

  // 모달 닫기
  closeModal();
  
  // 폼 초기화
  addMaintenanceForm.reset();
});

// 검색 기능
const searchInput = document.querySelector('.search-box input');
searchInput.addEventListener('input', (e) => {
  const searchTerm = e.target.value.toLowerCase();
  const rows = document.querySelectorAll('.maintenance-table tbody tr');

  rows.forEach(row => {
    const text = row.textContent.toLowerCase();
    row.style.display = text.includes(searchTerm) ? '' : 'none';
  });
});

// 필터 기능
const filterSelect = document.querySelector('.filter-dropdown select');
filterSelect.addEventListener('change', (e) => {
  const filterValue = e.target.value;
  const rows = document.querySelectorAll('.maintenance-table tbody tr');

  rows.forEach(row => {
    if (filterValue === 'all') {
      row.style.display = '';
    } else {
      const status = row.querySelector('.status-badge').textContent.toLowerCase();
      row.style.display = status === filterValue ? '' : 'none';
    }
  });
});

// 페이지네이션
const pageButtons = document.querySelectorAll('.btn-page');
pageButtons.forEach(button => {
  button.addEventListener('click', () => {
    // 현재 활성화된 버튼 비활성화
    document.querySelector('.btn-page.active').classList.remove('active');
    // 클릭된 버튼 활성화
    button.classList.add('active');
    
    // TODO: 페이지 변경 로직 구현
    console.log('페이지 변경:', button.textContent);
  });
});

// 테이블 행 작업 버튼 이벤트
document.querySelectorAll('.actions .btn-icon').forEach(button => {
  button.addEventListener('click', (e) => {
    const action = e.target.closest('.btn-icon').title;
    const row = e.target.closest('tr');
    const maintenanceId = row.dataset.id; // TODO: 각 행에 data-id 속성 추가 필요

    switch (action) {
      case '상세보기':
        // TODO: 상세보기 모달 표시
        console.log('상세보기:', maintenanceId);
        break;
      case '수정':
        // TODO: 수정 모달 표시
        console.log('수정:', maintenanceId);
        break;
      case '삭제':
        if (confirm('정말 삭제하시겠습니까?')) {
          // TODO: 삭제 API 호출
          console.log('삭제:', maintenanceId);
        }
        break;
    }
  });
}); 