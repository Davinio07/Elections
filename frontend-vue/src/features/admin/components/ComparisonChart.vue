<script setup lang="ts">
import { computed } from 'vue';
import { Bar } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js';
import type { PropType } from 'vue';
import type { NationalResult } from '../service/partyService';
import { getPartyColor } from '../service/partyService';

// Register Chart.js components
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

// Define component props
const props = defineProps({
  parties: {
    type: Array as PropType<NationalResult[]>,
    required: true,
  },
  metric: {
    type: String as PropType<'seats' | 'votes'>,
    required: true,
  },
  title: {
    type: String,
    required: true,
  }
});

// Create chart-compatible data
const chartData = computed(() => {
  const labels = props.parties.map(p => p.partyName);
  const data = props.parties.map(p => (props.metric === 'seats' ? p.seats : p.votes));
  const backgroundColors = props.parties.map(p => getPartyColor(p.partyName));

  return {
    labels,
    datasets: [
      {
        label: props.metric.charAt(0).toUpperCase() + props.metric.slice(1), // 'Votes' or 'Seats'
        data,
        backgroundColor: backgroundColors,
        borderRadius: 4,
        maxBarThickness: 100,
      },
    ],
  };
});

// Configure chart options
const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false, // Hide legend, colors are in the bars
    },
    title: {
      display: true,
      text: props.title,
      font: {
        size: 16,
      },
      padding: {
        bottom: 20,
      }
    },
    tooltip: {
      callbacks: {
        // Format tooltip to use commas for thousands
        label: function (context: any) {
          let label = context.dataset.label || '';
          if (label) {
            label += ': ';
          }
          if (context.parsed.y !== null) {
            label += new Intl.NumberFormat('nl-NL').format(context.parsed.y);
          }
          return label;
        },
      },
    },
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        // Format Y-axis to use commas
        callback: function(value: any) {
          return new Intl.NumberFormat('nl-NL').format(value);
        }
      }
    },
  },
}));
</script>

<template>
  <div class="chart-container">
    <Bar :data="chartData" :options="chartOptions" />
  </div>
</template>

<style scoped>
.chart-container {
  position: relative;
  height: 300px; /* Give the chart a defined height */
  width: 100%;
  padding: 1rem;
  background: #fdfdfd;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
}
</style>
