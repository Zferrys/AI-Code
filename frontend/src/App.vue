<template>
  <div id="app">
    <app-header />
    <main class="main-container">
      <transition :name="transitionName" mode="out-in">
        <router-view :key="$route.fullPath" />
      </transition>
    </main>
    <app-footer />
    <onboarding-tour ref="onboarding" />
  </div>
</template>

<script>
import AppHeader from './components/AppHeader.vue';
import AppFooter from './components/AppFooter.vue';
import OnboardingTour from './components/OnboardingTour.vue';
import { mapGetters } from 'vuex';

export default {
  name: 'App',
  components: {
    AppHeader,
    AppFooter,
    OnboardingTour
  },
  data() {
    return {
      transitionName: 'route-fade'
    };
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
    $route(to, from) {
      // 根据路由深度决定过渡方向
      if (from && to) {
        const toDepth = to.path.split('/').length;
        const fromDepth = from.path.split('/').length;
        this.transitionName = toDepth >= fromDepth ? 'route-forward' : 'route-backward';
      }
      // 切换页面时滚动到顶部
      window.scrollTo({ top: 0, behavior: 'smooth' });
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

/* 路由过渡动画 — 向前导航 */
.route-forward-enter-active {
  animation: routeForwardIn 0.35s ease both;
}
.route-forward-leave-active {
  animation: routeForwardOut 0.2s ease both;
}

@keyframes routeForwardIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
@keyframes routeForwardOut {
  from {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateY(-12px) scale(0.98);
  }
}

/* 路由过渡动画 — 后退导航 */
.route-backward-enter-active {
  animation: routeBackIn 0.35s ease both;
}
.route-backward-leave-active {
  animation: routeBackOut 0.2s ease both;
}

@keyframes routeBackIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
@keyframes routeBackOut {
  from {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateY(12px) scale(0.98);
  }
}

/* 回退动画（默认） */
.route-fade-enter-active {
  animation: routeFadeIn 0.3s ease both;
}
.route-fade-leave-active {
  animation: routeFadeOut 0.15s ease both;
}
@keyframes routeFadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes routeFadeOut {
  from { opacity: 1; }
  to { opacity: 0; }
}
</style>
