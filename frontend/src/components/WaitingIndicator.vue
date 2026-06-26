<template>
  <div class="waiting-indicator">
    <div class="wi-animation">
      <!-- 脉冲环 -->
      <div class="wi-ring"></div>
      <!-- 图标 -->
      <div class="wi-icon">
        <i :class="iconClass"></i>
      </div>
    </div>

    <div class="wi-content">
      <div class="wi-message">{{ message }}</div>
      <div class="wi-sub" v-if="subMessage">{{ subMessage }}</div>
      <div class="wi-timer" v-if="showTimer">
        <i class="el-icon-time"></i>
        <span>{{ formattedElapsed }}</span>
      </div>
      <!-- 进度阶段指示 -->
      <div class="wi-stages" v-if="stages.length > 0">
        <div v-for="(s, i) in stages" :key="i" class="wi-stage" :class="{ 'stage-active': i === currentStage, 'stage-done': i < currentStage }">
          <span class="stage-dot"></span>
          <span class="stage-label">{{ s }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WaitingIndicator',
  props: {
    message: { type: String, default: '处理中...' },
    subMessage: { type: String, default: '' },
    icon: { type: String, default: 'el-icon-loading' },
    showTimer: { type: Boolean, default: true },
    elapsed: { type: Number, default: 0 },
    stages: { type: Array, default: () => [] },
    currentStage: { type: Number, default: 0 }
  },
  computed: {
    iconClass() {
      return this.icon === 'el-icon-loading' ? 'el-icon-loading wi-spin' : this.icon;
    },
    formattedElapsed() {
      if (!this.elapsed) return '0 秒';
      const secs = Math.floor(this.elapsed / 1000);
      if (secs < 60) return `${secs} 秒`;
      const m = Math.floor(secs / 60);
      const s = secs % 60;
      return s > 0 ? `${m} 分 ${s} 秒` : `${m} 分钟`;
    }
  }
};
</script>

<style scoped>
.waiting-indicator {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 28px 24px;
  background: #f8faff;
  border: 1px solid #e0e7ff;
  border-radius: 14px;
}

.wi-animation {
  position: relative;
  width: 56px;
  height: 56px;
  flex-shrink: 0;
}

.wi-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 3px solid transparent;
  border-top-color: var(--primary, #1a56db);
  animation: wi-spin 1s linear infinite;
}

.wi-icon {
  position: absolute;
  inset: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #eff6ff, #eef2ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: var(--primary, #1a56db);
}

.wi-spin {
  animation: wi-spin 1.2s linear infinite;
}

.wi-content {
  flex: 1;
  min-width: 0;
}

.wi-message {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary, #0f172a);
  margin-bottom: 4px;
}

.wi-sub {
  font-size: 13px;
  color: var(--text-tertiary, #94a3b8);
  margin-bottom: 6px;
}

.wi-timer {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary, #475569);
}

.wi-stages {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.wi-stage {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: var(--text-tertiary, #94a3b8);
  transition: color 0.3s;
}

.stage-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--border, #d1d5db);
  transition: all 0.3s;
}

.stage-active {
  color: var(--primary, #1a56db);
  font-weight: 600;
}
.stage-active .stage-dot {
  background: var(--primary, #1a56db);
  box-shadow: 0 0 6px rgba(26,86,219,0.4);
  animation: wi-pulse 1.2s ease-in-out infinite;
}

.stage-done {
  color: var(--success, #059669);
}
.stage-done .stage-dot {
  background: var(--success, #059669);
}

@keyframes wi-spin {
  to { transform: rotate(360deg); }
}

@keyframes wi-pulse {
  0%, 100% { box-shadow: 0 0 4px rgba(26,86,219,0.3); }
  50% { box-shadow: 0 0 10px rgba(26,86,219,0.5); }
}
</style>
