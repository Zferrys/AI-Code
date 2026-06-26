<template>
  <div class="path-card" @click="$emit('click')">
    <div class="path-header" :style="{ background: gradientColor }">
      <span class="path-difficulty">{{ difficultyText }}</span>
    </div>
    <div class="path-body">
      <h3 class="path-title">{{ path.title }}</h3>
      <p class="path-desc">{{ path.description }}</p>
      <div class="path-stats">
        <span class="stat"><i class="el-icon-notebook-2"></i> {{ path.courseCount || 0 }} 课程</span>
        <span class="stat"><i class="el-icon-time"></i> {{ path.estimatedDays || '--' }} 天</span>
      </div>
      <div class="path-progress" v-if="path.userProgress">
        <el-progress :percentage="path.userProgress.percentage" :stroke-width="6" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PathCard',
  props: {
    path: { type: Object, required: true }
  },
  computed: {
    difficultyText() {
      const map = { BEGINNER: '入门', INTERMEDIATE: '进阶', ADVANCED: '高级' };
      return map[this.path.difficulty] || this.path.difficulty;
    },
    gradientColor() {
      const map = {
        BEGINNER: 'linear-gradient(135deg, #059669, #10b981)',
        INTERMEDIATE: 'linear-gradient(135deg, #d97706, #f59e0b)',
        ADVANCED: 'linear-gradient(135deg, #dc2626, #ef4444)'
      };
      return map[this.path.difficulty] || 'linear-gradient(135deg, #1a56db, #3b82f6)';
    }
  }
};
</script>

<style scoped>
.path-card {
  background: var(--card-bg);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.3s;
}

.path-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}

.path-header {
  height: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: 12px;
}

.path-difficulty {
  background: rgba(255,255,255,0.9);
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.path-body { padding: 16px; }

.path-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.path-desc {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.path-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--text-secondary);
}

.stat { display: flex; align-items: center; gap: 4px; }

.path-progress { margin-top: 12px; }
</style>
