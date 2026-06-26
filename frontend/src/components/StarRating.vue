<template>
  <div class="star-rating">
    <span v-for="n in 5" :key="n" class="star" :class="{ active: n <= value }" @click="$emit('input', n)">
      ★
    </span>
  </div>
</template>

<script>
export default { name: 'StarRating', props: { value: { type: Number, default: 0 } } };
</script>

<style scoped>
.star { font-size: 24px; color: #d1d5db; cursor: pointer; transition: color 0.2s; }
.star.active, .star:hover { color: #f59e0b; }
</style>
