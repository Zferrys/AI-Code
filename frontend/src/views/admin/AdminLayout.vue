<template>
  <div class="admin-wrapper">
    <!-- 背景装饰 -->
    <div class="admin-bg">
      <div class="admin-bg-orb admin-bg-orb-1"></div>
      <div class="admin-bg-orb admin-bg-orb-2"></div>
      <div class="admin-bg-orb admin-bg-orb-3"></div>
    </div>

    <div class="admin-container">
      <!-- 侧边栏 -->
      <aside class="admin-sidebar">
        <div class="sidebar-header">
          <div class="sidebar-logo" @click="$router.push('/')">
            <div class="sidebar-logo-mark">&lt;/&gt;</div>
            <span class="sidebar-logo-text">AI-Code</span>
          </div>
          <div class="sidebar-subtitle">管理后台</div>
        </div>

        <el-menu
          :default-active="activeMenu"
          router
          class="sidebar-menu"
          background-color="transparent"
          text-color="#94a3b8"
          active-text-color="#3b82f6"
        >
          <el-menu-item index="/admin">
            <i class="el-icon-s-data"></i>
            <span slot="title">控制台</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <i class="el-icon-user"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/paths">
            <i class="el-icon-notebook-2"></i>
            <span slot="title">路径管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/tags">
            <i class="el-icon-collection-tag"></i>
            <span slot="title">标签管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/config">
            <i class="el-icon-setting"></i>
            <span slot="title">系统配置</span>
          </el-menu-item>
          <el-menu-item index="/admin/logs">
            <i class="el-icon-document"></i>
            <span slot="title">操作日志</span>
          </el-menu-item>
          <el-menu-item index="/admin/notify">
            <i class="el-icon-message"></i>
            <span slot="title">邮件通知</span>
          </el-menu-item>
        </el-menu>

        <div class="sidebar-footer">
          <el-button class="sidebar-back-btn" @click="$router.push('/')">
            <i class="el-icon-back"></i> 返回前台
          </el-button>
        </div>
      </aside>

      <!-- 主内容区 -->
      <main class="admin-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminLayout',
  computed: {
    activeMenu() {
      return this.$route.path;
    }
  }
};
</script>

<style scoped>
.admin-wrapper {
  min-height: calc(100vh - var(--header-height, 64px));
  position: relative;
  overflow: hidden;
}

/* ===== 背景装饰 ===== */
.admin-bg {
  position: fixed;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}
.admin-bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.08;
}
.admin-bg-orb-1 {
  width: 500px; height: 500px;
  background: linear-gradient(135deg, #3b82f6, #0ea5e9);
  top: -150px; left: -100px;
  animation: adminOrbFloat 12s ease-in-out infinite;
}
.admin-bg-orb-2 {
  width: 400px; height: 400px;
  background: linear-gradient(135deg, #0ea5e9, #38bdf8);
  bottom: -120px; right: -80px;
  animation: adminOrbFloat 16s ease-in-out infinite reverse;
}
.admin-bg-orb-3 {
  width: 300px; height: 300px;
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  top: 40%; right: 20%;
  animation: adminOrbFloat 14s ease-in-out infinite 2s;
}
@keyframes adminOrbFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.12); }
}

/* ===== 主容器 ===== */
.admin-container {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 20px;
  gap: 24px;
  position: relative;
  z-index: 1;
  min-height: calc(100vh - var(--header-height, 64px) - 48px);
}

/* ===== 侧边栏 ===== */
.admin-sidebar {
  width: 220px;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-radius: var(--radius-lg);
  border: 1px solid rgba(255, 255, 255, 0.4);
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.04);
  height: fit-content;
  position: sticky;
  top: calc(var(--header-height, 64px) + 24px);
  transition: all var(--transition-slow);
}
.admin-sidebar:hover {
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.08);
  border-color: rgba(59, 130, 246, 0.15);
}

/* 侧边栏头部 */
.sidebar-header {
  padding: 0 20px 16px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.5);
  margin-bottom: 8px;
}
.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.3s;
  margin-bottom: 6px;
}
.sidebar-logo:hover {
  transform: translateX(2px);
}
.sidebar-logo-mark {
  width: 30px;
  height: 30px;
  background: var(--primary-gradient);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-family: var(--font-mono, monospace);
  font-size: 12px;
  font-weight: 700;
}
.sidebar-logo-text {
  font-size: 16px;
  font-weight: 700;
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.3px;
}
.sidebar-subtitle {
  font-size: 11px;
  color: var(--text-tertiary);
  letter-spacing: 1px;
  text-transform: uppercase;
  font-weight: 500;
  padding-left: 38px;
}

/* 侧边栏菜单 */
.sidebar-menu {
  flex: 1;
  border-right: none !important;
  padding: 4px 8px;
}
.sidebar-menu .el-menu-item {
  border-radius: 8px;
  margin: 2px 0;
  height: 42px;
  line-height: 42px;
  font-size: 13px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}
.sidebar-menu .el-menu-item i {
  font-size: 16px;
  margin-right: 4px;
}
.sidebar-menu .el-menu-item:hover {
  background: rgba(59, 130, 246, 0.08) !important;
  color: #3b82f6 !important;
}
.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(124, 58, 237, 0.08)) !important;
  color: #3b82f6 !important;
  font-weight: 600;
  position: relative;
}
.sidebar-menu .el-menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--primary-gradient);
  border-radius: 0 2px 2px 0;
}

/* 底部返回按钮 */
.sidebar-footer {
  padding: 12px 16px 4px;
  border-top: 1px solid rgba(226, 232, 240, 0.4);
  margin-top: 8px;
}
.sidebar-back-btn {
  width: 100%;
  border: 1px solid var(--border) !important;
  border-radius: 8px !important;
  font-size: 12px !important;
  padding: 8px 12px !important;
  color: var(--text-tertiary) !important;
  background: transparent !important;
  transition: all 0.25s !important;
}
.sidebar-back-btn:hover {
  border-color: var(--primary) !important;
  color: var(--primary) !important;
  background: rgba(59, 130, 246, 0.04) !important;
}

/* ===== 主内容 ===== */
.admin-main {
  flex: 1;
  min-width: 0;
  animation: adminMainIn 0.4s ease;
}
@keyframes adminMainIn {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .admin-container {
    flex-direction: column;
    padding: 16px 12px;
    gap: 16px;
  }
  .admin-sidebar {
    width: 100%;
    position: static;
    padding: 12px 0;
  }
  .sidebar-menu .el-menu-item {
    height: 38px;
    line-height: 38px;
  }
  .sidebar-footer { display: none; }
}
</style>
