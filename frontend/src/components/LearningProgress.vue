<template>
  <div class="learning-progress" v-if="progress">
    <div class="progress-header">
      <span class="progress-label">学习进度</span>
      <span class="progress-text">{{ progress.completedCourses }}/{{ progress.totalCourses }} 课程</span>
    </div>
    <el-progress :percentage="progress.percentage" :stroke-width="8" :color="progressColor" />
  </div>
</template>

<script>
export default {
  name: 'LearningProgress',
  props: {
    progress: { type: Object, default: null }
  },
  computed: {
    progressColor() {
      if (!this.progress) return '#1a56db';
      const pct = this.progress.percentage;
      if (pct >= 80) return '#059669';
      if (pct >= 40) return '#d97706';
      return '#1a56db';
    }
  }
};
</script>

<style scoped>
.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.progress-label { font-size: 14px; font-weight: 500; }
.progress-text { font-size: 13px; color: var(--text-secondary); }
</style>
