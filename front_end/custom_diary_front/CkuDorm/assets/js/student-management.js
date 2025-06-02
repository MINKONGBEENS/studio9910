document.addEventListener("DOMContentLoaded", function () {
  // 건물 점유율 차트 초기화
  const buildingOccupancyChart = echarts.init(
    document.getElementById("buildingOccupancyChart")
  );
  const buildingOccupancyOption = {
    animation: false,
    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(255, 255, 255, 0.8)",
      borderColor: "#e5e7eb",
      textStyle: {
        color: "#1f2937",
      },
    },
    legend: {
      data: ["전체 수용인원", "현재 점유율"],
      bottom: "0",
      left: "center",
      textStyle: {
        color: "#1f2937",
      },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "10%",
      top: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: ["A동", "B동", "C동", "D동"],
      axisLine: {
        lineStyle: {
          color: "#e5e7eb",
        },
      },
      axisTick: {
        alignWithLabel: true,
      },
    },
    yAxis: {
      type: "value",
      axisLine: {
        show: false,
      },
      splitLine: {
        lineStyle: {
          color: "#f3f4f6",
        },
      },
    },
    series: [
      {
        name: "전체 수용인원",
        type: "bar",
        barWidth: "30%",
        data: [350, 300, 250, 200],
        itemStyle: {
          color: "rgba(141, 211, 199, 1)",
          borderRadius: 4,
        },
      },
      {
        name: "현재 점유율",
        type: "bar",
        barWidth: "30%",
        data: [320, 280, 230, 180],
        itemStyle: {
          color: "rgba(87, 181, 231, 1)",
          borderRadius: 4,
        },
      },
    ],
  };
  buildingOccupancyChart.setOption(buildingOccupancyOption);

  // 학생 상태 분포 차트 초기화
  const statusDistributionChart = echarts.init(
    document.getElementById("statusDistributionChart")
  );
  const statusDistributionOption = {
    animation: false,
    tooltip: {
      trigger: "item",
      backgroundColor: "rgba(255, 255, 255, 0.8)",
      borderColor: "#e5e7eb",
      textStyle: {
        color: "#1f2937",
      },
    },
    legend: {
      bottom: "0",
      left: "center",
      textStyle: {
        color: "#1f2937",
      },
    },
    series: [
      {
        name: "학생 상태",
        type: "pie",
        radius: ["40%", "70%"],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: false,
        },
        emphasis: {
          label: {
            show: true,
            fontSize: "14",
            fontWeight: "bold",
          },
        },
        labelLine: {
          show: false,
        },
        data: [
          {
            value: 1024,
            name: "재학중",
            itemStyle: { color: "rgba(87, 181, 231, 1)" },
          },
          {
            value: 156,
            name: "휴학중",
            itemStyle: { color: "rgba(251, 191, 114, 1)" },
          },
          {
            value: 68,
            name: "졸업",
            itemStyle: { color: "rgba(252, 141, 98, 1)" },
          },
        ],
      },
    ],
  };
  statusDistributionChart.setOption(statusDistributionOption);

  // 창 크기 조절 이벤트 처리
  window.addEventListener("resize", function () {
    buildingOccupancyChart.resize();
    statusDistributionChart.resize();
  });

  // 모달 기능
  const addStudentBtn = document.getElementById("addStudentBtn");
  const addStudentModal = document.getElementById("addStudentModal");
  const closeAddStudentModal = document.getElementById("closeAddStudentModal");
  const cancelAddStudent = document.getElementById("cancelAddStudent");
  const deleteConfirmationModal = document.getElementById("deleteConfirmationModal");
  const cancelDelete = document.getElementById("cancelDelete");

  // 삭제 버튼
  const deleteButtons = document.querySelectorAll('[title="삭제"]');

  // 전체 선택 체크박스 기능
  const selectAllCheckbox = document.getElementById("selectAllCheckbox");
  const studentCheckboxes = document.querySelectorAll(".student-checkbox");

  addStudentBtn.addEventListener("click", function () {
    addStudentModal.style.display = "block";
    document.body.style.overflow = "hidden";
  });

  closeAddStudentModal.addEventListener("click", function () {
    addStudentModal.style.display = "none";
    document.body.style.overflow = "auto";
  });

  cancelAddStudent.addEventListener("click", function () {
    addStudentModal.style.display = "none";
    document.body.style.overflow = "auto";
  });

  deleteButtons.forEach((button) => {
    button.addEventListener("click", function () {
      deleteConfirmationModal.style.display = "block";
      document.body.style.overflow = "hidden";
    });
  });

  cancelDelete.addEventListener("click", function () {
    deleteConfirmationModal.style.display = "none";
    document.body.style.overflow = "auto";
  });

  selectAllCheckbox.addEventListener("change", function () {
    studentCheckboxes.forEach((checkbox) => {
      checkbox.checked = selectAllCheckbox.checked;
    });
  });

  // 모달 외부 클릭 시 닫기
  window.addEventListener("click", function (event) {
    if (event.target === addStudentModal) {
      addStudentModal.style.display = "none";
      document.body.style.overflow = "auto";
    }
    if (event.target === deleteConfirmationModal) {
      deleteConfirmationModal.style.display = "none";
      document.body.style.overflow = "auto";
    }
  });
}); 