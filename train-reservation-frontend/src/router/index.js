import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/trips/:tripId/seats',
      name: 'seat-selection',
      component: () => import('../views/SeatSelectionView.vue'),
    },
    {
      path: '/trips/:tripId/passengers',
      name: 'passenger-details',
      component: () => import('../views/PassengerDetailsView.vue'),
    },
  ],
})

export default router
