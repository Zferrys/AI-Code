<template>
  <div id="app">
    <app-header />
    <main class="main-container">
      <router-view :key="$route.fullPath" />
    </main>
    <app-footer />
    <back-to-top />
    <onboarding-tour ref="onboarding" />
  </div>
</template>

<script>
import AppHeader from './components/AppHeader.vue';
import AppFooter from './components/AppFooter.vue';
import OnboardingTour from './components/OnboardingTour.vue';
import BackToTop from './components/BackToTop.vue';
import { mapGetters } from 'vuex';

export default {
  name: 'App',
  components: {
    AppHeader,
    AppFooter,
    BackToTop,
    OnboardingTour
  },
  data() {
    return {};
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  mounted() {
    if (this.isLoggedIn) {
      setTimeout(() => {
        this.$refs.onboarding?.check();
      }, 800);
    }
  },
  watch: {
    $route() {
      window.scrollTo({ top: 0, behavior: 'instant' });
    },
    isLoggedIn(val) {
      if (val) {
        setTimeout(() => {
          this.$refs.onboarding?.check();
        }, 800);
      }
    }
  }
};
</script>

<style>
#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  max-width: 100%;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

</style>
