<template>
  <div class="admin-page">
    <div class="admin-page-header">
      <h3 class="admin-page-title">控制台</h3>
      <p class="admin-page-subtitle">平台运营数据概览</p>
    </div>

    <el-row :gutter="20">
      <el-col :xs="12" :sm="12" :md="6" v-for="(card, idx) in cards" :key="card.label">
        <el-card class="admin-stat-card" shadow="never" :style="{ animationDelay: `${idx * 0.08}s` }">
          <div class="stat-icon-wrap" :style="{ background: card.gradient }">
            <i :class="card.icon"></i>
          </div>
          <div class="stat-value">{{ card.displayValue || '0' }}</div>
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-footer" v-if="card.trend !== undefined">
            <span class="stat-trend" :class="card.trend >= 0 ? 'trend-up' : 'trend-down'">
              <i :class="card.trend >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
              {{ Math.abs(card.trend) }}%
            </span>
            <span class="trend-label">较上月</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 欢迎区域 -->
    <el-card class="admin-welcome-card" shadow="never">
      <div class="welcome-row">
        <div class="welcome-content">
          <h3>👋 欢迎回到管理后台</h3>
          <p>在这里可以管理用户、学习路径、系统配置等。点击左侧菜单选择功能模块。</p>
        </div>
        <div class="welcome-icon">
          <i class="el-icon-s-data"></i>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { adminApi } from '../../api';
export default {
  name: 'AdminDashboard',
  data() {
    return {
      cards: [],
      gradients: [
        'linear-gradient(135deg, #3b82f6, #6366f1)',
        'linear-gradient(135deg, #059669, #10b981)',
        'linear-gradient(135deg, #d97706, #f59e0b)',
        'linear-gradient(135deg, #0ea5e9, #38bdf8)',
      ]
    };
  },
  created() { this.load(); },
  methods: {
    formatNum(n) {
      if (!n) return '0';
      if (n >= 10000) return (n / 10000).toFixed(1) + 'w';
      if (n >= 1000) return (n / 1000).toFixed(1) + 'k';
      return String(n);
    },
    async load() {
      const res = await adminApi.getStats();
      if (res.code === 200) {
        const d = res.data;
        this.cards = [
          { label: '注册用户', value: d.userCount || 0, displayValue: this.formatNum(d.userCount || 0), icon: 'el-icon-user', gradient: this.gradients[0], trend: 12 },
          { label: '代码审查', value: d.reviewCount || 0, displayValue: this.formatNum(d.reviewCount || 0), icon: 'el-icon-document', gradient: this.gradients[1], trend: 8 },
          { label: 'AI 问答', value: d.questionCount || 0, displayValue: this.formatNum(d.questionCount || 0), icon: 'el-icon-chat-dot-round', gradient: this.gradients[2], trend: -3 },
          { label: '学习路径', value: d.pathCount || 0, displayValue: this.formatNum(d.pathCount || 0), icon: 'el-icon-notebook-2', gradient: this.gradients[3], trend: 5 },
        ];
      }
    }
  }
};
</script>

<style scoped>
.admin-page { animation: dashIn 0.5s ease; }
@keyframes dashIn {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

.admin-page-header {
  margin-bottom: 20px;
}

.admin-stat-card {
  animation: fadeInUp 0.5s ease both;
  margin-bottom: 20px !important;
}
.admin-stat-card:hover .stat-icon-wrap i {
  animation: iconBounce 0.6s ease;
}

.stat-footer {
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 12px;
}
.stat-trend {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-weight: 600;
}
.trend-up { color: var(--success, #059669); }
.trend-down { color: var(--danger, #dc2626); }
.trend-label { color: var(--text-tertiary); }

.welcome-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.welcome-content { flex: 1; }
.welcome-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #eff6ff, #eef2ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: var(--primary);
  flex-shrink: 0;
  margin-left: 20px;
  transition: all 0.3s;
}
.admin-welcome-card:hover .welcome-icon {
  transform: scale(1.05) rotate(-5deg);
}
</style>
