import { ref, computed, onMounted, watch } from 'vue'
import { getPartiesFromDb } from '@/features/admin/service/NationalElectionResults_api'
// PARTY_COLORS import removed to use partyService for consistent coloring
import { getChartOptions } from '@/features/admin/components/NationalResultChart.ts'
import { getPartyColor } from '@/features/admin/service/partyService' // NEW IMPORT

export function useNationalResult() {
  // --- State ---
  const availableElections = ref(['TK2023', 'TK2021'])
  const selectedElection = ref(availableElections.value[0])
  const nationalResults = ref<{ partyName: string; validVotes: number }[]>([])
  const nationalSeats = ref<Record<string, number>>({})
  const loading = ref(false)
  const error = ref<string | null>(null)

  // --- Data Fetching ---
  async function fetchElectionData(electionId: string) {
    loading.value = true
    error.value = null
    nationalResults.value = []
    nationalSeats.value = {}

    try {
      const parties = await getPartiesFromDb(electionId)
      nationalResults.value = parties.map(p => ({ partyName: p.name, validVotes: p.totalVotes }))
      nationalSeats.value = Object.fromEntries(parties.map(p => [p.name, p.nationalSeats]))
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Onbekende fout bij ophalen van partijdata.'
    } finally {
      loading.value = false
    }
  }

  // --- Lifecycle & Watchers ---
  onMounted(() => {
    fetchElectionData(selectedElection.value)
  })

  watch(selectedElection, (newId) => {
    fetchElectionData(newId)
  })

  // --- Statistics Logic ---
  const totalSeats = computed(() => Object.values(nationalSeats.value).reduce((sum, seats) => sum + seats, 0))
  const majorityThreshold = computed(() => totalSeats.value === 0 ? 0 : Math.floor(totalSeats.value / 2) + 1)
  const partyCount = computed(() => Object.keys(nationalSeats.value).length)

  const largestPartyData = computed(() => {
    if (totalSeats.value === 0 || Object.keys(nationalSeats.value).length === 0) {
      return { name: 'Geen data', seats: 0 }
    }
    const [name, seats] = Object.entries(nationalSeats.value).reduce((max, curr) => curr[1] > max[1] ? curr : max)
    return { name, seats }
  })

  const largestPartyPercentage = computed(() => {
    if (totalSeats.value === 0) return '(0.00%)'
    return `(${((largestPartyData.value.seats / totalSeats.value) * 100).toFixed(2)}%)`
  })

  // --- Chart Data Logic ---
  const chartData = computed(() => {
    if (!nationalResults.value.length) return { labels: [], datasets: [] }

    const totalVotes = nationalResults.value.reduce((sum, r) => sum + r.validVotes, 0)
    const sorted = [...nationalResults.value].sort((a, b) => b.validVotes - a.validVotes)
    const labels = sorted.map(r => r.partyName)

    // FIX: Map background color using party name for consistency
    const backgroundColors = labels.map(partyName => getPartyColor(partyName));

    return {
      labels,
      datasets: [{
        label: 'Stempercentage',
        data: sorted.map(r => totalVotes > 0 ? parseFloat(((r.validVotes / totalVotes) * 100).toFixed(2)) : 0),
        backgroundColor: backgroundColors,
        borderColor: '#ffffff',
        borderWidth: 1,
        votesData: sorted.map(r => r.validVotes),
        seatsData: labels.map(party => nationalSeats.value[party] || 0)
      }]
    }
  })

  const chartOptions = computed(() => getChartOptions(selectedElection.value))

  // --- Return everything needed by the template ---
  return {
    availableElections,
    selectedElection,
    loading,
    error,
    totalSeats,
    majorityThreshold,
    partyCount,
    largestPartyData,
    largestPartyPercentage,
    chartData,
    chartOptions
  }
}
