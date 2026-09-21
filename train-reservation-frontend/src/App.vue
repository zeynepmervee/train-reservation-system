<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="app">
    <header class="header">
      <div class="header-content">
        <a class="brand" href="/">RailGo</a>
        <span class="brand-subtitle">Hızlı Tren Rezervasyonu</span>
      </div>
    </header>

    <RouterView />
  </div>
</template>
