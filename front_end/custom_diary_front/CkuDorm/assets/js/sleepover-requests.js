document.addEventListener("DOMContentLoaded", function () {
  // 외박 신청 추세 차트 초기화
  const requestsTrendChart = echarts.init(
    document.getElementById("requestsTrendChart")
  );
  const requestsTrendOption = {
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
      data: ["신청", "승인", "거절"],
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
      data: ["1월", "2월", "3월", "4월", "5월", "6월", "7월"],
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
        name: "신청",
        type: "line",
        smooth: true,
        data: [120, 132, 101, 134, 90, 180, 210],
        itemStyle: {
          color: "rgba(87, 181, 231, 1)",
        },
        areaStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: "rgba(87, 181, 231, 0.2)",
              },
              {
                offset: 1,
                color: "rgba(87, 181, 231, 0.01)",
              },
            ],
          },
        },
      },
      {
        name: "승인",
        type: "line",
        smooth: true,
        data: [90, 110, 85, 120, 70, 160, 185],
        itemStyle: {
          color: "rgba(141, 211, 199, 1)",
        },
        areaStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: "rgba(141, 211, 199, 0.2)",
              },
              {
                offset: 1,
                color: "rgba(141, 211, 199, 0.01)",
              },
            ],
          },
        },
      },
      {
        name: "거절",
        type: "line",
        smooth: true,
        data: [20, 12, 11, 14, 9, 18, 21],
        itemStyle: {
          color: "rgba(252, 141, 98, 1)",
        },
        areaStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: "rgba(252, 141, 98, 0.2)",
              },
              {
                offset: 1,
                color: "rgba(252, 141, 98, 0.01)",
              },
            ],
          },
        },
      },
    ],
  };
  requestsTrendChart.setOption(requestsTrendOption);

  // 상태 분포 차트 초기화
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
        name: "신청 상태",
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
            value: 24,
            name: "대기중",
            itemStyle: { color: "rgba(251, 191, 114, 1)" },
          },
          {
            value: 87,
            name: "승인",
            itemStyle: { color: "rgba(141, 211, 199, 1)" },
          },
          {
            value: 21,
            name: "거절",
            itemStyle: { color: "rgba(252, 141, 98, 1)" },
          },
        ],
      },
    ],
  };
  statusDistributionChart.setOption(statusDistributionOption);

  // 창 크기 조절 이벤트 처리
  window.addEventListener("resize", function () {
    requestsTrendChart.resize();
    statusDistributionChart.resize();
  });

  // 모달 기능
  const addRequestBtn = document.getElementById("addRequestBtn");
  const addRequestModal = document.getElementById("addRequestModal");
  const closeAddRequestModal = document.getElementById("closeAddRequestModal");
  const cancelAddRequest = document.getElementById("cancelAddRequest");
  const viewRequestModal = document.getElementById("viewRequestModal");
  const closeViewRequestModal = document.getElementById("closeViewRequestModal");
  const closeViewDetails = document.getElementById("closeViewDetails");
  const confirmationModal = document.getElementById("confirmationModal");
  const cancelConfirmation = document.getElementById("cancelConfirmation");

  // 상세보기 버튼
  const viewButtons = document.querySelectorAll('[title="상세보기"]');

  // 승인 버튼
  const approveButtons = document.querySelectorAll('[title="승인"]');

  // 전체 선택 체크박스 기능
  const selectAllCheckbox = document.getElementById("selectAllCheckbox");
  const requestCheckboxes = document.querySelectorAll(".request-checkbox");

  addRequestBtn.addEventListener("click", function () {
    addRequestModal.style.display = "block";
    document.body.style.overflow = "hidden";
  });

  closeAddRequestModal.addEventListener("click", function () {
    addRequestModal.style.display = "none";
    document.body.style.overflow = "auto";
  });

  cancelAddRequest.addEventListener("click", function () {
    addRequestModal.style.display = "none";
    document.body.style.overflow = "auto";
  });

  viewButtons.forEach((button) => {
    button.addEventListener("click", function () {
      viewRequestModal.style.display = "block";
      document.body.style.overflow = "hidden";
    });
  });

  closeViewRequestModal.addEventListener("click", function () {
    viewRequestModal.style.display = "none";
    document.body.style.overflow = "auto";
  });

  closeViewDetails.addEventListener("click", function () {
    viewRequestModal.style.display = "none";
    document.body.style.overflow = "auto";
  });

  approveButtons.forEach((button) => {
    button.addEventListener("click", function () {
      confirmationModal.style.display = "block";
      document.body.style.overflow = "hidden";
    });
  });

  cancelConfirmation.addEventListener("click", function () {
    confirmationModal.style.display = "none";
    document.body.style.overflow = "auto";
  });

  selectAllCheckbox.addEventListener("change", function () {
    requestCheckboxes.forEach((checkbox) => {
      checkbox.checked = selectAllCheckbox.checked;
    });
  });

  // 모달 외부 클릭 시 닫기
  window.addEventListener("click", function (event) {
    if (event.target === addRequestModal) {
      addRequestModal.style.display = "none";
      document.body.style.overflow = "auto";
    }
    if (event.target === viewRequestModal) {
      viewRequestModal.style.display = "none";
      document.body.style.overflow = "auto";
    }
    if (event.target === confirmationModal) {
      confirmationModal.style.display = "none";
      document.body.style.overflow = "auto";
    }
  });
}); 