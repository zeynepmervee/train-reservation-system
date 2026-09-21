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
  ],
})

export default router
