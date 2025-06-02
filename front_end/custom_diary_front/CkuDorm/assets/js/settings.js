// 프로필 폼 제출
const profileForm = document.getElementById('profileForm');
profileForm.addEventListener('submit', (e) => {
  e.preventDefault();
  
  const formData = {
    name: document.getElementById('name').value,
    email: document.getElementById('email').value,
    phone: document.getElementById('phone').value
  };

  // TODO: API 호출하여 프로필 정보 업데이트
  console.log('프로필 업데이트:', formData);
  alert('프로필이 업데이트되었습니다.');
});

// 비밀번호 변경 폼 제출
const passwordForm = document.getElementById('passwordForm');
passwordForm.addEventListener('submit', (e) => {
  e.preventDefault();
  
  const currentPassword = document.getElementById('currentPassword').value;
  const newPassword = document.getElementById('newPassword').value;
  const confirmPassword = document.getElementById('confirmPassword').value;

  if (newPassword !== confirmPassword) {
    alert('새 비밀번호가 일치하지 않습니다.');
    return;
  }

  // TODO: API 호출하여 비밀번호 변경
  console.log('비밀번호 변경:', {
    currentPassword,
    newPassword
  });
  alert('비밀번호가 변경되었습니다.');
  passwordForm.reset();
});

// 알림 설정 폼 제출
const notificationForm = document.getElementById('notificationForm');
notificationForm.addEventListener('submit', (e) => {
  e.preventDefault();
  
  const formData = {
    emailNotifications: document.getElementById('emailNotifications').checked,
    smsNotifications: document.getElementById('smsNotifications').checked,
    maintenanceNotifications: document.getElementById('maintenanceNotifications').checked,
    sleepoverNotifications: document.getElementById('sleepoverNotifications').checked
  };

  // TODO: API 호출하여 알림 설정 업데이트
  console.log('알림 설정 업데이트:', formData);
  alert('알림 설정이 업데이트되었습니다.');
});

// 시스템 설정 폼 제출
const systemForm = document.getElementById('systemForm');
systemForm.addEventListener('submit', (e) => {
  e.preventDefault();
  
  const formData = {
    language: document.getElementById('language').value,
    timezone: document.getElementById('timezone').value,
    dateFormat: document.getElementById('dateFormat').value
  };

  // TODO: API 호출하여 시스템 설정 업데이트
  console.log('시스템 설정 업데이트:', formData);
  alert('시스템 설정이 업데이트되었습니다.');
});

// 입력 필드 유효성 검사
function validateInput(input) {
  const value = input.value.trim();
  
  switch (input.type) {
    case 'email':
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(value)) {
        input.setCustomValidity('유효한 이메일 주소를 입력해주세요.');
      } else {
        input.setCustomValidity('');
      }
      break;
      
    case 'tel':
      const phoneRegex = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/;
      if (!phoneRegex.test(value)) {
        input.setCustomValidity('유효한 전화번호를 입력해주세요.');
      } else {
        input.setCustomValidity('');
      }
      break;
      
    case 'password':
      if (value.length < 8) {
        input.setCustomValidity('비밀번호는 8자 이상이어야 합니다.');
      } else {
        input.setCustomValidity('');
      }
      break;
  }
}

// 입력 필드 이벤트 리스너 등록
document.querySelectorAll('input').forEach(input => {
  input.addEventListener('input', () => validateInput(input));
  input.addEventListener('blur', () => validateInput(input));
}); 