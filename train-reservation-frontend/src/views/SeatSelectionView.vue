<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTripById } from '@/services/tripService'
import { getWagonsByTrainId } from '@/services/wagonService'

const route = useRoute()
const router = useRouter()

const trip = ref(null)
const wagons = ref([])
const selectedWagonId = ref(null)
const selectedSeat = ref(null)
const loading = ref(true)
const errorMessage = ref('')

const selectedWagon = computed(() =>
  wagons.value.find((wagon) => wagon.id === selectedWagonId.value),
)

const seatRows = computed(() => {
  const rows = new Map()

  for (const seat of selectedWagon.value?.seats ?? []) {
    const rowNumber = seat.seatNumber.replace(/[A-Za-z]+$/, '')

    if (!rows.has(rowNumber)) {
      rows.set(rowNumber, [])
    }

    rows.get(rowNumber).push(seat)
  }

  return Array.from(rows, ([rowNumber, seats]) => ({
    rowNumber,
    seats,
  }))
})

onMounted(async () => {
  try {
    trip.value = await getTripById(route.params.tripId)
    wagons.value = await getWagonsByTrainId(trip.value.trainId)

    if (wagons.value.length > 0) {
      selectedWagonId.value = wagons.value[0].id
    }
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loading.value = false
  }
})

function selectWagon(wagonId) {
  selectedWagonId.value = wagonId
  selectedSeat.value = null
}

function selectSeat(seat) {
  selectedSeat.value = seat
}

function hasAisleAfter(index) {
  if (selectedWagon.value?.wagonType === 'ECONOMY') {
    return index === 1
  }

  return index === 0
}

function formatTime(value) {
  return new Intl.DateTimeFormat('tr-TR', {
    hour: '2-digit',
    minute: '2-digit',
    timeZone: 'Europe/Istanbul',
  }).format(new Date(value))
}
</script>

<template>
  <main class="seat-page">
    <button class="back-button" type="button" @click="router.back()">
      ← Seferlere dön
    </button>

    <p v-if="loading">Koltuklar yükleniyor...</p>
    <p v-else-if="errorMessage" class="error">{{ errorMessage }}</p>

    <template v-else-if="trip">
      <section class="trip-summary">
        <div>
          <span>{{ trip.trainCode }}</span>
          <h1>
            {{ trip.departureStationName }}
            →
            {{ trip.arrivalStationName }}
          </h1>
        </div>

        <div class="trip-times">
          <strong>{{ formatTime(trip.departureTime) }}</strong>
          <span>—</span>
          <strong>{{ formatTime(trip.arrivalTime) }}</strong>
        </div>
      </section>

      <section v-if="wagons.length" class="selection-card">
        <h2>Vagon ve koltuk seç</h2>

        <div class="wagon-tabs">
          <button
            v-for="wagon in wagons"
            :key="wagon.id"
            type="button"
            :class="{ active: wagon.id === selectedWagonId }"
            @click="selectWagon(wagon.id)"
          >
            Vagon {{ wagon.wagonNumber }}
            <small>
              {{ wagon.wagonType === 'ECONOMY' ? 'Ekonomi' : 'Business' }}
            </small>
          </button>
        </div>

        <div class="legend">
          <span><i class="available"></i> Boş</span>
          <span><i class="selected"></i> Seçilen</span>
        </div>

        <div class="train-body">
          <div class="front">Trenin önü</div>

          <div
            v-for="row in seatRows"
            :key="row.rowNumber"
            class="seat-row"
          >
            <span class="row-number">{{ row.rowNumber }}</span>

            <template
              v-for="(seat, index) in row.seats"
              :key="seat.id"
            >
              <button
                type="button"
                class="seat"
                :class="{ selected: selectedSeat?.id === seat.id }"
                @click="selectSeat(seat)"
              >
                {{ seat.seatNumber }}
                <small v-if="seat.window">Pencere</small>
              </button>

              <span
                v-if="hasAisleAfter(index)"
                class="aisle"
                aria-hidden="true"
              ></span>
            </template>
          </div>
        </div>

        <div class="selection-footer">
          <div>
            <span>Seçilen koltuk</span>
            <strong>{{ selectedSeat?.seatNumber ?? 'Henüz seçilmedi' }}</strong>
          </div>

          <button type="button" :disabled="!selectedSeat">
            Devam Et
          </button>
        </div>
      </section>

      <p v-else class="empty">
        Bu trene henüz vagon eklenmemiş.
      </p>
    </template>
  </main>
</template>

<style scoped>
.seat-page {
  min-height: calc(100vh - 72px);
  padding: 40px 24px 80px;
  background: #f1f5f9;
}

.back-button {
  display: block;
  max-width: 1100px;
  min-height: auto;
  margin: 0 auto 20px;
  padding: 0;
  background: transparent;
  color: #0369a1;
}

.trip-summary,
.selection-card,
.empty {
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px;
  border-radius: 18px;
  background: white;
}

.trip-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.trip-summary span {
  color: #0369a1;
  font-weight: 700;
}

.trip-summary h1 {
  margin: 6px 0 0;
  font-size: 28px;
}

.trip-times {
  display: flex;
  gap: 12px;
  color: #0f172a;
  font-size: 20px;
}

.selection-card {
  margin-top: 24px;
}

.wagon-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 20px 0;
}

.wagon-tabs button {
  border: 1px solid #cbd5e1;
  padding: 8px 18px;
  background: white;
  color: #334155;
}

.wagon-tabs button.active {
  border-color: #0369a1;
  background: #0369a1;
  color: white;
}

.wagon-tabs small {
  display: block;
}

.legend {
  display: flex;
  gap: 22px;
  margin-bottom: 20px;
}

.legend span {
  display: flex;
  align-items: center;
  gap: 7px;
}

.legend i {
  width: 18px;
  height: 18px;
  border-radius: 5px;
}

.available {
  border: 1px solid #94a3b8;
  background: white;
}

.legend .selected {
  background: #0284c7;
}

.train-body {
  max-width: 620px;
  margin: auto;
  padding: 24px;
  border: 2px solid #cbd5e1;
  border-radius: 80px 80px 24px 24px;
}

.front {
  margin-bottom: 24px;
  color: #64748b;
  text-align: center;
}

.seat-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 12px;
}

.row-number {
  width: 24px;
  color: #64748b;
}

.seat {
  width: 82px;
  min-height: 58px;
  border: 1px solid #94a3b8;
  padding: 6px;
  background: white;
  color: #0f172a;
}

.seat small {
  display: block;
  margin-top: 3px;
  color: #64748b;
  font-size: 9px;
}

.seat.selected {
  border-color: #0284c7;
  background: #0284c7;
  color: white;
}

.seat.selected small {
  color: white;
}

.aisle {
  width: 35px;
}

.selection-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.selection-footer div {
  display: flex;
  flex-direction: column;
}

.selection-footer span {
  color: #64748b;
}

.selection-footer strong {
  margin-top: 4px;
  color: #0f172a;
  font-size: 20px;
}

.error {
  color: #dc2626;
  text-align: center;
}

@media (max-width: 650px) {
  .trip-summary,
  .selection-footer {
    align-items: flex-start;
    flex-direction: column;
    gap: 18px;
  }

  .seat {
    width: 55px;
    font-size: 12px;
  }

  .aisle {
    width: 12px;
  }
}
</style>
