<script setup>
import { computed, onMounted, ref } from 'vue'
import { getStations } from '@/services/stationService'
import { searchTrips } from '@/services/tripService'

const stations = ref([])
const departureStationId = ref('')
const arrivalStationId = ref('')
const travelDate = ref('')

const trips = ref([])
const loadingStations = ref(true)
const searching = ref(false)
const searchCompleted = ref(false)
const errorMessage = ref('')

const canSearch = computed(() => {
  return (
    departureStationId.value &&
    arrivalStationId.value &&
    travelDate.value &&
    departureStationId.value !== arrivalStationId.value
  )
})

onMounted(async () => {
  try {
    stations.value = await getStations()
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loadingStations.value = false
  }
})

async function handleSearch() {
  errorMessage.value = ''
  searchCompleted.value = false
  searching.value = true

  try {
    trips.value = await searchTrips(
      departureStationId.value,
      arrivalStationId.value,
      travelDate.value,
    )

    searchCompleted.value = true
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    searching.value = false
  }
}

function formatTime(value) {
  return new Intl.DateTimeFormat('tr-TR', {
    hour: '2-digit',
    minute: '2-digit',
    timeZone: 'Europe/Istanbul',
  }).format(new Date(value))
}

function formatPrice(value) {
  return new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
  }).format(value)
}
</script>

<template>
  <main class="page">
    <section class="hero">
      <p class="eyebrow">Güvenli ve hızlı yolculuk</p>
      <h1>Yolculuğunu planla</h1>
      <p class="description">
        Kalkış, varış ve seyahat tarihini seçerek uygun trenleri görüntüle.
      </p>

      <form class="search-card" @submit.prevent="handleSearch">
        <label>
          <span>Nereden?</span>

          <select v-model="departureStationId" :disabled="loadingStations">
            <option value="">Kalkış istasyonu seç</option>

            <option
              v-for="station in stations"
              :key="station.id"
              :value="station.id"
            >
              {{ station.cityName }} — {{ station.name }}
            </option>
          </select>
        </label>

        <label>
          <span>Nereye?</span>

          <select v-model="arrivalStationId" :disabled="loadingStations">
            <option value="">Varış istasyonu seç</option>

            <option
              v-for="station in stations"
              :key="station.id"
              :value="station.id"
            >
              {{ station.cityName }} — {{ station.name }}
            </option>
          </select>
        </label>

        <label>
          <span>Tarih</span>
          <input v-model="travelDate" type="date" />
        </label>

        <button type="submit" :disabled="!canSearch || searching">
          {{ searching ? 'Aranıyor...' : 'Sefer Ara' }}
        </button>
      </form>

      <p
        v-if="
          departureStationId &&
          departureStationId === arrivalStationId
        "
        class="warning"
      >
        Kalkış ve varış istasyonları farklı olmalıdır.
      </p>

      <p v-if="errorMessage" class="error">
        {{ errorMessage }}
      </p>
    </section>

    <section v-if="searchCompleted" class="results">
      <h2>Uygun seferler</h2>

      <div v-if="trips.length === 0" class="empty-state">
        Bu tarih için uygun sefer bulunamadı.
      </div>

      <article v-for="trip in trips" :key="trip.id" class="trip-card">
        <div>
          <span class="train-code">{{ trip.trainCode }}</span>
          <h3>
            {{ trip.departureStationName }}
            <span>→</span>
            {{ trip.arrivalStationName }}
          </h3>
        </div>

        <div class="time">
          <strong>{{ formatTime(trip.departureTime) }}</strong>
          <span>Hareket</span>
        </div>

        <div class="time">
          <strong>{{ formatTime(trip.arrivalTime) }}</strong>
          <span>Varış</span>
        </div>

        <div class="price">
          <strong>{{ formatPrice(trip.basePrice) }}</strong>
          <button type="button">Koltuk Seç</button>
        </div>
      </article>
    </section>
  </main>
</template>

<style scoped>
.page {
  min-height: calc(100vh - 72px);
  padding: 70px 24px;
  background: linear-gradient(145deg, #eff6ff, #f8fafc 50%, #ecfeff);
}

.hero,
.results {
  max-width: 1100px;
  margin: 0 auto;
}

.eyebrow {
  color: #0369a1;
  font-weight: 700;
}

h1 {
  margin: 8px 0;
  color: #0f172a;
  font-size: clamp(36px, 6vw, 64px);
}

.description {
  color: #475569;
  font-size: 18px;
}

.search-card {
  display: grid;
  grid-template-columns: 1fr 1fr 0.8fr auto;
  gap: 16px;
  margin-top: 36px;
  padding: 24px;
  border-radius: 18px;
  background: white;
  box-shadow: 0 18px 45px rgb(15 23 42 / 12%);
}

label {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label span {
  color: #334155;
  font-size: 14px;
  font-weight: 700;
}

select,
input,
button {
  min-height: 48px;
  border-radius: 10px;
  font: inherit;
}

select,
input {
  border: 1px solid #cbd5e1;
  padding: 0 12px;
  background: white;
  color: #0f172a;
}

button {
  align-self: end;
  border: 0;
  padding: 0 24px;
  background: #0369a1;
  color: white;
  cursor: pointer;
  font-weight: 700;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.warning,
.error {
  margin-top: 16px;
  color: #dc2626;
}

.results {
  margin-top: 48px;
}

.results h2 {
  margin-bottom: 18px;
  color: #0f172a;
}

.trip-card {
  display: grid;
  grid-template-columns: 2fr 0.7fr 0.7fr 1fr;
  align-items: center;
  gap: 24px;
  margin-bottom: 16px;
  padding: 22px;
  border-radius: 16px;
  background: white;
  box-shadow: 0 8px 24px rgb(15 23 42 / 8%);
}

.train-code {
  color: #0369a1;
  font-weight: 700;
}

.trip-card h3 {
  margin-top: 5px;
  color: #0f172a;
}

.time,
.price {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.time strong,
.price strong {
  color: #0f172a;
  font-size: 20px;
}

.time span {
  color: #64748b;
}

.price button {
  margin-top: 8px;
}

.empty-state {
  padding: 28px;
  border-radius: 14px;
  background: white;
  color: #475569;
}

@media (max-width: 850px) {
  .search-card,
  .trip-card {
    grid-template-columns: 1fr;
  }

  button {
    width: 100%;
  }
}
</style>
