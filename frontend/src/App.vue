<template>
  <div id="app">
    <app-header />
    <transition name="banner-slide">
      <div class="email-banner" v-if="showEmailBanner">
        <div class="email-banner-inner">
          <i class="el-icon-warning-outline"></i>
          <span>你的邮箱未绑定或无效，部分功能（如密码重置、通知接收）将无法使用</span>
          <el-button size="mini" class="email-banner-btn" @click="goBindEmail">
            立即绑定 <i class="el-icon-right"></i>
          </el-button>
          <i class="el-icon-close email-banner-close" @click="dismissBanner"></i>
        </div>
      </div>
    </transition>
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
import { mapGetters, mapState } from 'vuex';
import { userApi } from './api';

export default {
  name: 'App',
  components: {
    AppHeader,
    AppFooter,
    BackToTop,
    OnboardingTour
  },
  data() {
    return {
      bannerDismissed: false,
      emailChecked: false,
      userEmail: ''
    };
  },
  computed: {
    ...mapGetters(['isLoggedIn']),
    ...mapState(['user']),
    showEmailBanner() {
      if (!this.isLoggedIn || this.bannerDismissed) return false;
      if (!this.emailChecked) return false;
      const email = this.userEmail;
      // 邮箱为空或不含 @ 即为无效
      if (!email) return true;
      if (!email.includes('@')) return true;
      return false;
    }
  },
  mounted() {
    setTimeout(() => {
      this.$refs.onboarding?.check();
    }, 800);
    this.checkEmail();
  },
  watch: {
    isLoggedIn(val) {
      if (val) {
        setTimeout(() => {
          this.$refs.onboarding?.check();
        }, 800);
        this.checkEmail();
      } else {
        this.bannerDismissed = false;
        this.emailChecked = false;
      }
    },
    user() {
      this.checkEmail();
    }
  },
  methods: {
    async checkEmail() {
      if (!this.isLoggedIn) return;
      try {
        const res = await userApi.getUserInfo();
        if (res.code === 200 && res.data) {
          this.userEmail = res.data.email || '';
        }
      } catch {
        // 静默失败
      } finally {
        this.emailChecked = true;
      }
    },
    goBindEmail() {
      this.$router.push('/profile');
    },
    dismissBanner() {
      this.bannerDismissed = true;
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

/* ===== 邮箱绑定提示条 ===== */
.email-banner {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  border-bottom: 1px solid #f59e0b33;
  position: relative;
  z-index: 999;
}
.email-banner-inner {
  max-width: var(--content-max-width, 1200px);
  margin: 0 auto;
  padding: 8px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #92400e;
}
.email-banner-inner i.el-icon-warning-outline {
  font-size: 18px;
  color: #d97706;
  flex-shrink: 0;
}
.email-banner-inner span {
  flex: 1;
}
.email-banner-btn {
  background: #d97706 !important;
  border-color: #d97706 !important;
  color: white !important;
  font-weight: 500 !important;
  border-radius: 6px !important;
  flex-shrink: 0;
  transition: all 0.2s !important;
}
.email-banner-btn:hover {
  background: #b45309 !important;
  transform: translateY(-1px);
}
.email-banner-close {
  font-size: 16px;
  cursor: pointer;
  opacity: 0.5;
  transition: opacity 0.2s;
  flex-shrink: 0;
}
.email-banner-close:hover { opacity: 1; }

.banner-slide-enter-active, .banner-slide-leave-active {
  transition: all 0.3s ease;
}
.banner-slide-enter, .banner-slide-leave-to {
  transform: translateY(-100%);
  opacity: 0;
}

</style>
