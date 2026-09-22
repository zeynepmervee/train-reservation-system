<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useReservationStore } from '@/stores/reservation'
import { createReservation } from '@/services/reservationService'

const router = useRouter()
const reservationStore = useReservationStore()
const submitting = ref(false)
const errorMessage = ref('')
const reservationResult = ref(null)

const passengers = ref(
  reservationStore.selectedSeats.map((seat) => ({
    seatId: seat.id,
    seatNumber: seat.seatNumber,
    wagonNumber: seat.wagonNumber,
    firstName: '',
    lastName: '',
    birthDate: '',
    gender: '',
    passengerType: 'STANDARD',
  })),
)

const trip = computed(() => reservationStore.trip)

async function handleSubmit() {
  submitting.value = true
  errorMessage.value = ''

  const request = {
    tripId: trip.value.id,
    passengers: passengers.value.map((passenger) => ({
      seatId: passenger.seatId,
      firstName: passenger.firstName,
      lastName: passenger.lastName,
      birthDate: passenger.birthDate,
      gender: passenger.gender,
      passengerType: passenger.passengerType,
    })),
  }

  try {
    reservationResult.value = await createReservation(request)
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="passenger-page">
    <button class="back-button" type="button" @click="router.back()">
      ← Koltuk seçimine dön
    </button>

    <section v-if="trip && passengers.length" class="page-content">
      <header class="summary">
        <div>
          <span class="step">2. ADIM</span>
          <h1>Yolcu bilgileri</h1>
          <p>
            {{ trip.departureStationName }}
            →
            {{ trip.arrivalStationName }}
          </p>
        </div>

        <strong>{{ passengers.length }} yolcu</strong>
      </header>

      <form @submit.prevent="handleSubmit">
        <article
          v-for="(passenger, index) in passengers"
          :key="passenger.seatId"
          class="passenger-card"
        >
          <header>
            <div>
              <span>Yolcu {{ index + 1 }}</span>
              <h2>
                Vagon {{ passenger.wagonNumber }} /
                Koltuk {{ passenger.seatNumber }}
              </h2>
            </div>

            <span class="seat-badge">{{ passenger.seatNumber }}</span>
          </header>

          <div class="form-grid">
            <label>
              <span>Ad</span>
              <input
                v-model.trim="passenger.firstName"
                type="text"
                maxlength="100"
                required
                placeholder="Yolcunun adı"
              />
            </label>

            <label>
              <span>Soyad</span>
              <input
                v-model.trim="passenger.lastName"
                type="text"
                maxlength="100"
                required
                placeholder="Yolcunun soyadı"
              />
            </label>

            <label>
              <span>Doğum tarihi</span>
              <input
                v-model="passenger.birthDate"
                type="date"
                required
              />
            </label>

            <label>
              <span>Cinsiyet</span>
              <select v-model="passenger.gender" required>
                <option value="">Seçiniz</option>
                <option value="FEMALE">Kadın</option>
                <option value="MALE">Erkek</option>
              </select>
            </label>

            <label class="wide">
              <span>Yolcu tipi</span>
              <select v-model="passenger.passengerType" required>
                <option value="STANDARD">Standart</option>
                <option value="STUDENT">Öğrenci</option>
                <option value="SENIOR">65 yaş ve üzeri</option>
              </select>
            </label>
          </div>
        </article>

        <div class="form-footer">
          <div>
            <span>Toplam yolcu</span>
            <strong>{{ passengers.length }}</strong>
          </div>

          <button
            type="submit"
            :disabled="submitting || reservationResult"
          >
            {{ submitting ? 'Oluşturuluyor...' : 'Rezervasyonu oluştur' }}
          </button>
        </div>
      </form>

      <div v-if="errorMessage" class="error-notice">
        {{ errorMessage }}
      </div>

      <div v-if="reservationResult" class="success-notice">
        <div>
          <span>Rezervasyon numarası</span>
          <strong>#{{ reservationResult.id }}</strong>
        </div>

        <div>
          <span>Durum</span>
          <strong>{{ reservationResult.status }}</strong>
        </div>

        <div>
          <span>Toplam tutar</span>
          <strong>
            {{
              new Intl.NumberFormat('tr-TR', {
                style: 'currency',
                currency: 'TRY',
              }).format(reservationResult.totalPrice)
            }}
          </strong>
        </div>

        <p>
          Koltuklar 5 dakika süreyle sizin için tutulmaktadır.
        </p>
      </div>
    </section>

    <section v-else class="empty">
      <h1>Koltuk seçimi bulunamadı</h1>
      <p>Önce bir seferden en az bir koltuk seçmelisin.</p>
      <button type="button" @click="router.push('/')">
        Sefer aramaya dön
      </button>
    </section>
  </main>
</template>

<style scoped>
.passenger-page {
  min-height: calc(100vh - 72px);
  padding: 36px 24px 80px;
  background: linear-gradient(145deg, #f0fdfa, #f8fafc);
}

.back-button {
  display: block;
  max-width: 1000px;
  min-height: auto;
  margin: 0 auto 18px;
  padding: 0;
  background: transparent;
  color: #0f766e;
}

.page-content,
.empty {
  max-width: 1000px;
  margin: auto;
}

.summary,
.passenger-card,
.form-footer,
.empty {
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  background: white;
  box-shadow: 0 10px 30px rgb(15 23 42 / 6%);
}

.summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 26px;
}

.step {
  color: #0f766e;
  font-size: 13px;
  font-weight: 800;
}

.summary h1 {
  margin: 5px 0;
  color: #0f172a;
}

.summary p {
  margin: 0;
  color: #64748b;
}

.summary > strong {
  border-radius: 999px;
  padding: 10px 16px;
  background: #ccfbf1;
  color: #0f766e;
}

.passenger-card {
  margin-top: 18px;
  padding: 25px;
}

.passenger-card header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22px;
}

.passenger-card header span {
  color: #64748b;
}

.passenger-card h2 {
  margin: 4px 0 0;
  color: #0f172a;
  font-size: 20px;
}

.seat-badge {
  border-radius: 10px;
  padding: 10px 14px;
  background: #0d9488;
  color: white !important;
  font-weight: 800;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

label span {
  color: #334155;
  font-size: 14px;
  font-weight: 700;
}

input,
select {
  min-height: 48px;
  border: 1px solid #cbd5e1;
  border-radius: 10px;
  padding: 0 12px;
  background: white;
  color: #0f172a;
  font: inherit;
}

input:focus,
select:focus {
  border-color: #0d9488;
  outline: 3px solid #ccfbf1;
}

.wide {
  grid-column: 1 / -1;
}

.form-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 22px;
  padding: 22px 25px;
}

.form-footer div {
  display: flex;
  flex-direction: column;
  color: #64748b;
}

.form-footer strong {
  color: #0f172a;
  font-size: 22px;
}

.form-footer button,
.empty button {
  padding: 0 24px;
  background: #0d9488;
}

.notice {
  margin-top: 18px;
  border: 1px solid #5eead4;
  border-radius: 12px;
  padding: 16px;
  background: #ccfbf1;
  color: #115e59;
}

.empty {
  padding: 30px;
  text-align: center;
}

@media (max-width: 650px) {
  .summary,
  .passenger-card header,
  .form-footer {
    align-items: flex-start;
    flex-direction: column;
    gap: 16px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .wide {
    grid-column: auto;
  }
}
.error-notice,
.success-notice {
  margin-top: 18px;
  border-radius: 14px;
  padding: 18px;
}

.error-notice {
  border: 1px solid #fecaca;
  background: #fef2f2;
  color: #b91c1c;
}

.success-notice {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
  border: 1px solid #5eead4;
  background: #ccfbf1;
  color: #115e59;
}

.success-notice div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.success-notice p {
  grid-column: 1 / -1;
  margin: 0;
}

@media (max-width: 650px) {
  .success-notice {
    grid-template-columns: 1fr;
  }

  .success-notice p {
    grid-column: auto;
  }
}
</style>
