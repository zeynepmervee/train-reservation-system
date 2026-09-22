import { defineStore } from 'pinia'

export const useReservationStore = defineStore('reservation', {
  state: () => ({
    trip: null,
    selectedSeats: [],
  }),

  getters: {
    passengerCount: (state) => state.selectedSeats.length,
  },

  actions: {
    setSelection(trip, seats) {
      this.trip = trip
      this.selectedSeats = seats
    },

    clear() {
      this.trip = null
      this.selectedSeats = []
    },
  },
})
