<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTripById } from '@/services/tripService'
import { getWagonsByTrainId } from '@/services/wagonService'
import { useReservationStore } from '@/stores/reservation'

const reservationStore = useReservationStore()
const route = useRoute()
const router = useRouter()

const trip = ref(null)
const wagons = ref([])
const selectedWagonId = ref(null)
const selectedSeats = ref([])
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
}

function isOccupied(seat) {
  return Boolean(seat.occupancyGender)
}

function isSelected(seat) {
  return selectedSeats.value.some((selected) => selected.id === seat.id)
}

function toggleSeat(seat) {
  if (isOccupied(seat)) {
    return
  }

  if (isSelected(seat)) {
    selectedSeats.value = selectedSeats.value.filter(
      (selected) => selected.id !== seat.id,
    )
    return
  }

  selectedSeats.value.push({
    ...seat,
    wagonId: selectedWagon.value.id,
    wagonNumber: selectedWagon.value.wagonNumber,
  })
}

function seatClass(seat) {
  return {
    selected: isSelected(seat),
    'occupied-female': seat.occupancyGender === 'FEMALE',
    'occupied-male': seat.occupancyGender === 'MALE',
  }
}

function occupancyText(seat) {
  if (seat.occupancyGender === 'FEMALE') return 'K'
  if (seat.occupancyGender === 'MALE') return 'E'
  return ''
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

function continueToPassengers() {
  if (selectedSeats.value.length === 0) {
    return
  }

  reservationStore.setSelection(trip.value, selectedSeats.value)

  router.push({
    name: 'passenger-details',
    params: { tripId: trip.value.id },
  })
}
</script>

<template>
  <main class="seat-page">
    <button class="back-button" type="button" @click="router.back()">
      ← Seferlere dön
    </button>

    <p v-if="loading" class="message">Koltuklar yükleniyor...</p>
    <p v-else-if="errorMessage" class="error">{{ errorMessage }}</p>

    <template v-else-if="trip">
      <section class="trip-summary">
        <div>
          <span class="train-code">{{ trip.trainCode }}</span>
          <h1>
            {{ trip.departureStationName }}
            <span class="arrow">→</span>
            {{ trip.arrivalStationName }}
          </h1>
        </div>

        <div class="trip-times">
          <div>
            <small>Hareket</small>
            <strong>{{ formatTime(trip.departureTime) }}</strong>
          </div>

          <span>→</span>

          <div>
            <small>Varış</small>
            <strong>{{ formatTime(trip.arrivalTime) }}</strong>
          </div>
        </div>
      </section>

      <section v-if="wagons.length" class="selection-card">
        <div class="section-heading">
          <div>
            <span class="step">1. ADIM</span>
            <h2>Vagon ve koltuk seç</h2>
          </div>

          <div class="selection-count">
            {{ selectedSeats.length }} koltuk seçildi
          </div>
        </div>

        <div class="wagon-tabs">
          <button
            v-for="wagon in wagons"
            :key="wagon.id"
            type="button"
            :class="{ active: wagon.id === selectedWagonId }"
            @click="selectWagon(wagon.id)"
          >
            <strong>Vagon {{ wagon.wagonNumber }}</strong>
            <small>
              {{ wagon.wagonType === 'ECONOMY' ? 'Ekonomi' : 'Business' }}
            </small>
          </button>
        </div>

        <div class="legend">
          <span><i class="available"></i> Boş</span>
          <span><i class="selected-color"></i> Seçiminiz</span>
          <span><i class="female"></i> Kadın yolcu</span>
          <span><i class="male"></i> Erkek yolcu</span>
        </div>

        <div class="train-scroll">
          <div class="horizontal-train">
            <div class="train-nose">
              <span>ÖN</span>
            </div>

            <div class="wagon-body">
              <div class="wagon-info">
                <strong>Vagon {{ selectedWagon?.wagonNumber }}</strong>
                <span>
                  {{
                    selectedWagon?.wagonType === 'ECONOMY'
                      ? 'Ekonomi'
                      : 'Business'
                  }}
                </span>
              </div>

              <div class="seat-columns">
                <div
                  v-for="row in seatRows"
                  :key="row.rowNumber"
                  class="seat-column"
                >
                  <span class="row-number">{{ row.rowNumber }}</span>

                  <template
                    v-for="(seat, index) in row.seats"
                    :key="seat.id"
                  >
                    <button
                      type="button"
                      class="seat"
                      :class="seatClass(seat)"
                      :disabled="isOccupied(seat)"
                      :aria-label="`${seat.seatNumber} numaralı koltuk`"
                      :aria-pressed="isSelected(seat)"
                      @click="toggleSeat(seat)"
                    >
                      <strong>{{ seat.seatNumber }}</strong>

                      <small v-if="isOccupied(seat)">
                        {{ occupancyText(seat) }}
                      </small>

                      <small v-else-if="seat.window">Cam</small>
                    </button>

                    <span
                      v-if="hasAisleAfter(index)"
                      class="aisle"
                      aria-hidden="true"
                    ></span>
                  </template>
                </div>
              </div>

              <div class="wagon-exit">
                <span>ÇIKIŞ</span>
              </div>
            </div>
          </div>
        </div>

        <footer class="selection-footer">
          <div class="selected-summary">
            <span>Seçilen koltuklar</span>

            <div v-if="selectedSeats.length" class="selected-list">
              <button
                v-for="seat in selectedSeats"
                :key="seat.id"
                type="button"
                @click="toggleSeat(seat)"
              >
                V{{ seat.wagonNumber }} / {{ seat.seatNumber }}
                <span>×</span>
              </button>
            </div>

            <strong v-else>Henüz koltuk seçilmedi</strong>
          </div>

          <button
            class="continue-button"
            type="button"
            :disabled="selectedSeats.length === 0"
            @click="continueToPassengers"
          >
            {{ selectedSeats.length }} yolcu için devam et
          </button>
        </footer>
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
  padding: 36px 24px 80px;
  background: linear-gradient(145deg, #eff6ff, #f8fafc);
}

.back-button {
  display: block;
  max-width: 1180px;
  min-height: auto;
  margin: 0 auto 18px;
  padding: 0;
  background: transparent;
  color: #0369a1;
}

.trip-summary,
.selection-card,
.empty {
  max-width: 1180px;
  margin: auto;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  background: white;
  box-shadow: 0 12px 35px rgb(15 23 42 / 7%);
}

.trip-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 30px;
}

.train-code,
.step {
  color: #0369a1;
  font-size: 13px;
  font-weight: 800;
}

.trip-summary h1 {
  margin: 6px 0 0;
  color: #0f172a;
  font-size: 27px;
}

.arrow {
  margin: 0 8px;
  color: #0284c7;
}

.trip-times {
  display: flex;
  align-items: center;
  gap: 18px;
}

.trip-times div {
  display: flex;
  flex-direction: column;
}

.trip-times small {
  color: #64748b;
}

.trip-times strong {
  color: #0f172a;
  font-size: 21px;
}

.selection-card {
  margin-top: 22px;
  padding: 28px;
}

.section-heading,
.selection-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-heading h2 {
  margin: 5px 0 0;
  color: #0f172a;
}

.selection-count {
  border-radius: 999px;
  padding: 9px 15px;
  background: #e0f2fe;
  color: #0369a1;
  font-weight: 700;
}

.wagon-tabs {
  display: flex;
  gap: 10px;
  margin: 24px 0 18px;
  overflow-x: auto;
}

.wagon-tabs button {
  min-width: 130px;
  border: 1px solid #cbd5e1;
  padding: 9px 18px;
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
  margin-top: 3px;
}

.legend {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
  color: #475569;
  font-size: 14px;
}

.legend span {
  display: flex;
  align-items: center;
  gap: 7px;
}

.legend i {
  width: 18px;
  height: 18px;
  border: 1px solid #94a3b8;
  border-radius: 5px;
}

.available {
  background: white;
}

.selected-color {
  border-color: #0d9488 !important;
  background: #0d9488;
}

.female {
  border-color: #ec4899 !important;
  background: #f9a8d4;
}

.male {
  border-color: #2563eb !important;
  background: #3b82f6;
}

.train-scroll {
  padding-bottom: 10px;
  overflow-x: auto;
}

.horizontal-train {
  display: flex;
  align-items: stretch;
  width: max-content;
  min-width: 100%;
}

.train-nose {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  border: 3px solid #94a3b8;
  border-right: 0;
  border-radius: 80px 0 0 80px;
  background: #e2e8f0;
  color: #475569;
  font-weight: 800;
}

.wagon-body {
  display: flex;
  align-items: center;
  min-height: 390px;
  border: 3px solid #94a3b8;
  border-radius: 0 24px 24px 0;
  background: #f8fafc;
}

.wagon-info,
.wagon-exit {
  padding: 20px;
  color: #475569;
  text-align: center;
}

.wagon-info span {
  display: block;
  font-size: 12px;
}

.seat-columns {
  display: flex;
  gap: 12px;
  padding: 24px;
  border-right: 1px dashed #cbd5e1;
  border-left: 1px dashed #cbd5e1;
}

.seat-column {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 7px;
}

.row-number {
  color: #64748b;
  font-size: 12px;
}

.seat {
  width: 62px;
  min-height: 57px;
  border: 1px solid #94a3b8;
  padding: 5px;
  background: white;
  color: #0f172a;
}

.seat small {
  display: block;
  margin-top: 2px;
  color: #64748b;
  font-size: 9px;
}

.seat:hover:not(:disabled) {
  border-color: #0284c7;
  transform: translateY(-2px);
}

.seat.selected {
  border-color: #0d9488;
  background: #0d9488;
  color: white;
}

.seat.selected small {
  color: white;
}

.seat.occupied-female {
  border-color: #ec4899;
  background: #f9a8d4;
  color: #831843;
}

.seat.occupied-male {
  border-color: #2563eb;
  background: #3b82f6;
  color: white;
}

.aisle {
  width: 100%;
  height: 25px;
  border-top: 1px dashed #cbd5e1;
  border-bottom: 1px dashed #cbd5e1;
}

.selection-footer {
  gap: 24px;
  margin-top: 25px;
  padding-top: 22px;
  border-top: 1px solid #e2e8f0;
}

.selected-summary > span {
  display: block;
  margin-bottom: 8px;
  color: #64748b;
}

.selected-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.selected-list button {
  min-height: auto;
  border: 1px solid #5eead4;
  padding: 7px 10px;
  background: #ccfbf1;
  color: #0f766e;
}

.selected-list button span {
  margin-left: 7px;
}

.continue-button {
  padding: 0 24px;
  background: #0369a1;
}

.error,
.message {
  text-align: center;
}

.error {
  color: #dc2626;
}

@media (max-width: 700px) {
  .trip-summary,
  .section-heading,
  .selection-footer {
    align-items: flex-start;
    flex-direction: column;
    gap: 18px;
  }
}
</style>
