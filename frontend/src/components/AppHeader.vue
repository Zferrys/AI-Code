<template>
  <header class="app-header" :class="{ 'header-scrolled': scrolled }">
    <div class="header-inner">
      <!-- Logo -->
      <div class="logo" @click="$router.push('/')">
        <div class="logo-mark">
          <span class="logo-bracket">&lt;</span>
          <span class="logo-slash">/</span>
          <span class="logo-bracket">&gt;</span>
        </div>
        <span class="logo-text">AI-Code</span>
      </div>

      <!-- 导航 -->
      <nav class="nav-menu">
        <router-link to="/" class="nav-item" exact active-class="nav-active">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
          <span class="nav-indicator"></span>
        </router-link>
        <router-link to="/learning-paths" class="nav-item" active-class="nav-active">
          <i class="el-icon-notebook-2"></i>
          <span>学习路径</span>
          <span class="nav-indicator"></span>
        </router-link>
        <router-link to="/code-review" class="nav-item" v-if="isLoggedIn" active-class="nav-active">
          <i class="el-icon-document"></i>
          <span>代码审查</span>
          <span class="nav-indicator"></span>
        </router-link>
        <router-link to="/qa" class="nav-item" v-if="isLoggedIn" active-class="nav-active">
          <i class="el-icon-chat-dot-round"></i>
          <span>智能问答</span>
          <span class="nav-indicator"></span>
        </router-link>
        <router-link to="/rankings" class="nav-item" active-class="nav-active">
          <i class="el-icon-s-data"></i>
          <span>AI 排行</span>
          <span class="nav-indicator"></span>
        </router-link>
        <router-link to="/admin" class="nav-item admin-link" v-if="isAdmin" active-class="nav-active">
          <i class="el-icon-setting"></i>
          <span>管理</span>
          <span class="nav-indicator"></span>
        </router-link>
      </nav>

      <!-- 用户区域 -->
      <div class="user-area">
        <template v-if="isLoggedIn">
          <div class="user-info pulse-ring" @click="$router.push('/profile')">
            <el-avatar :size="32" class="user-avatar" v-if="!user?.avatar || user.avatar === '/default.png'">
              {{ user?.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <el-image v-else :src="avatarSrc" class="header-avatar-img" fit="cover">
              <div slot="error" class="image-slot">
                <el-avatar :size="32" class="user-avatar">
                  {{ user?.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
              </div>
            </el-image>
            <span class="user-name">{{ user?.username }}</span>
          </div>
          <el-button class="logout-btn" @click="handleLogout">
            <i class="el-icon-switch-button"></i>
          </el-button>
        </template>
        <template v-else>
          <el-button class="nav-login-btn" @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" class="nav-register-btn" @click="$router.push('/register')">免费注册</el-button>
        </template>
      </div>
    </div>
    <!-- 底部光泽条 -->
    <div class="header-glow"></div>
  </header>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';

export default {
  name: 'AppHeader',
  data() { return { scrolled: false }; },
  computed: {
    ...mapGetters(['isLoggedIn', 'isAdmin', 'currentUser']),
    user() { return this.currentUser; },
    // 头像路径处理：确保以 / 开头
    avatarSrc() {
      const av = this.user?.avatar;
      if (!av) return '';
      if (av.startsWith('http')) return av;
      return av.startsWith('/') ? av : '/' + av;
    }
  },
  mounted() {
    window.addEventListener('scroll', this.onScroll, { passive: true });
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.onScroll);
  },
  methods: {
    ...mapActions(['logout']),
    onScroll() { this.scrolled = window.scrollY > 20; },
    handleLogout() {
      this.logout();
      this.$router.push('/');
      this.$message.success('已退出登录');
    }
  }
};
</script>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}
.header-scrolled {
  background: rgba(255, 255, 255, 0.88);
  border-bottom-color: rgba(226, 232, 240, 0.6);
  box-shadow: 0 1px 20px rgba(0,0,0,0.06);
}
.header-glow {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(59,130,246,0.3), rgba(124,58,237,0.3), transparent);
  opacity: 0;
  transition: opacity 0.4s;
}
.header-scrolled .header-glow { opacity: 1; }

.header-inner {
  max-width: var(--content-max-width, 1200px);
  margin: 0 auto;
  padding: 0 20px;
  height: var(--header-height, 64px);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo */
.logo { display: flex; align-items: center; gap: 10px; cursor: pointer; user-select: none; }
.logo-mark {
  display: flex; align-items: center;
  font-family: var(--font-mono, monospace);
  font-weight: 700; font-size: 18px; color: white;
  background: var(--primary-gradient);
  width: 36px; height: 36px;
  border-radius: 10px; justify-content: center;
  transition: transform 0.4s ease, box-shadow 0.4s ease;
}
.logo:hover .logo-mark { transform: rotate(-5deg) scale(1.05); box-shadow: 0 4px 14px rgba(26,86,219,0.3); }
.logo-bracket { opacity: 0.8; }
.logo-slash { color: #fbbf24; }
.logo-text {
  font-size: 20px; font-weight: 800;
  background: var(--primary-gradient);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
  letter-spacing: -0.5px;
}

/* 导航 */
.nav-menu { display: flex; gap: 2px; }
.nav-item {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 8px 14px; border-radius: 8px;
  font-size: 14px; font-weight: 500;
  color: var(--text-secondary, #475569);
  text-decoration: none;
  position: relative;
  transition: all 0.25s ease;
}
.nav-item:hover { color: var(--primary); background: rgba(26,86,219,0.06); }
.nav-active { color: var(--primary) !important; background: rgba(26,86,219,0.08) !important; font-weight: 600; }
.nav-indicator {
  position: absolute; bottom: 0; left: 50%;
  width: 0; height: 2px;
  background: var(--primary-gradient);
  border-radius: 1px;
  transition: width 0.3s ease, left 0.3s ease;
}
.nav-active .nav-indicator { width: 50%; left: 25%; }

/* 管理员按钮 */
.admin-link { color: var(--warning) !important; }
.admin-link:hover { background: rgba(217,119,6,0.08) !important; }
.admin-link.nav-active { color: var(--warning) !important; background: rgba(217,119,6,0.1) !important; }

/* 用户区 */
.user-area { display: flex; align-items: center; gap: 8px; }
.user-info {
  display: flex; align-items: center; gap: 8px;
  padding: 4px 12px 4px 4px; border-radius: 20px;
  cursor: pointer; transition: all 0.25s ease;
}
.user-info:hover { background: rgba(26,86,219,0.06); }
.user-avatar { background: var(--primary-gradient) !important; border: none; font-weight: 600; }
.header-avatar-img { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; }
.user-name { font-size: 14px; font-weight: 500; color: var(--text-primary); max-width: 80px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.logout-btn {
  border: none !important;
  font-size: 18px !important;
  padding: 6px !important;
  background: transparent !important;
  color: var(--text-tertiary) !important;
  transition: all 0.25s ease !important;
}
.logout-btn:hover { color: var(--danger) !important; transform: scale(1.1); }

.nav-login-btn {
  border-radius: 8px !important;
  border: 1.5px solid var(--border) !important;
  padding: 7px 18px !important;
}
.nav-register-btn {
  border-radius: 8px !important;
  padding: 7px 18px !important;
  background: var(--primary-gradient) !important;
  border: none !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .nav-menu { display: none; }
  .user-name { display: none; }
}
</style>
