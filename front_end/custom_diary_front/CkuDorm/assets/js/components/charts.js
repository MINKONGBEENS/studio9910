export function initCharts() {
    // Initialize occupancy chart
    const occupancyChart = echarts.init(document.getElementById('occupancyChart'));
    
    const option = {
        animation: false,
        tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(255, 255, 255, 0.8)',
            borderColor: '#e5e7eb',
            textStyle: {
                color: '#1f2937'
            }
        },
        legend: {
            bottom: '0',
            left: 'center',
            textStyle: {
                color: '#1f2937'
            }
        },
        series: [{
            name: '점유율',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 8,
                borderColor: '#fff',
                borderWidth: 2
            },
            label: {
                show: false
            },
            emphasis: {
                label: {
                    show: true,
                    fontSize: '14',
                    fontWeight: 'bold'
                }
            },
            labelLine: {
                show: false
            },
            data: [
                { value: 735, name: 'A동', itemStyle: { color: 'rgba(87, 181, 231, 1)' } },
                { value: 580, name: 'B동', itemStyle: { color: 'rgba(141, 211, 199, 1)' } },
                { value: 484, name: 'C동', itemStyle: { color: 'rgba(251, 191, 114, 1)' } },
                { value: 300, name: 'D동', itemStyle: { color: 'rgba(252, 141, 98, 1)' } }
            ]
        }]
    };

    occupancyChart.setOption(option);

    // Handle window resize
    window.addEventListener('resize', function() {
        occupancyChart.resize();
    });
} 