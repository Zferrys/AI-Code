<template>
  <transition name="back-top-fade">
    <button v-if="visible" class="back-to-top visible" @click="scrollToTop" title="回到顶部">
      <i class="el-icon-arrow-up"></i>
    </button>
  </transition>
</template>

<script>
export default {
  name: 'BackToTop',
  data() {
    return { visible: false, scrollTarget: null };
  },
  mounted() {
    this.scrollTarget = this.$el?.parentElement || window;
    window.addEventListener('scroll', this.onScroll);
    this.onScroll();
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.onScroll);
  },
  methods: {
    onScroll() {
      this.visible = window.scrollY > 400;
    },
    scrollToTop() {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }
};
</script>

<style scoped>
.back-top-fade-enter-active { transition: all 0.3s ease; }
.back-top-fade-leave-active { transition: all 0.2s ease; }
.back-top-fade-enter,
.back-top-fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
