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
  type ChartOptions
} from 'chart.js';
import { getPartyColor } from '../service/partyService';
import type { MunicipalityResultDto } from '../service/MunicipalityElectionResults_api';

// 1. Register the Chart.js components we need
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

// 2. Define the props this component expects
const props = defineProps<{
  results: MunicipalityResultDto[];
}>();

// 3. Prepare the data for the chart
const chartData = computed(() => {
  // We limit to top 15 parties to keep the chart readable
  const displayData = props.results.slice(0, 15);

  return {
    labels: displayData.map(r => r.partyName),
    datasets: [{
      label: 'Aantal Stemmen',
      data: displayData.map(r => r.validVotes),
      // Get the official color for each party
      backgroundColor: displayData.map(r => getPartyColor(r.partyName)),
      borderRadius: 4,
    }]
  };
});

// 4. Configure chart options
const chartOptions: ChartOptions<'bar'> = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false }, // We hide the legend because the labels are enough
    tooltip: {
      callbacks: {
        label: (context) => {
          const value = typeof context.parsed?.y === 'number' ? context.parsed.y : 0;
          return `Stemmen: ${value.toLocaleString('nl-NL')}`;
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      title: { display: true, text: 'Aantal Stemmen' }
    },
    x: {
      ticks: {
        autoSkip: false, // Show all party names
        maxRotation: 45,
        minRotation: 45
      }
    }
  }
};
</script>

<template>
  <div class="chart-wrapper">
    <Bar :data="chartData" :options="chartOptions" />
  </div>
</template>

<style scoped>
.chart-wrapper {
  position: relative;
  height: 400px; /* Fixed height for the chart */
  width: 100%;
  background-color: white;
  padding: 1rem;
  border-radius: 0.5rem;
  border: 1px solid #e5e7eb;
}
</style>
