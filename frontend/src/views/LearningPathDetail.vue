<template>
  <div class="learning-path-detail">
    <el-button type="text" @click="$router.push('/learning-paths')" class="back-btn">
      ← 返回路径列表
    </el-button>

    <div v-if="loading"><el-skeleton :rows="6" animated /></div>

    <div v-else-if="error">
      <el-result icon="error" title="加载失败" :sub-title="error" />
    </div>

    <template v-else-if="path">
      <!-- 路径头部 -->
      <div class="path-hero">
        <h1 class="path-title">{{ path.title }}</h1>
        <p class="path-desc">{{ path.description }}</p>
        <div class="path-meta">
          <el-tag :type="difficultyType" size="small">{{ difficultyText }}</el-tag>
          <span class="meta-item"><i class="el-icon-notebook-2"></i> {{ path.courseCount }} 课程</span>
          <span class="meta-item"><i class="el-icon-time"></i> {{ path.estimatedDays || '--' }} 天</span>
        </div>
        <learning-progress v-if="path.userProgress" :progress="path.userProgress" />
      </div>

      <!-- 课程列表 -->
      <div class="course-list">
        <h3 class="section-title">课程目录</h3>
        <div class="card-stagger">
        <div v-for="(course, i) in path.courses" :key="course.id" class="course-item card-3d card-glow-hover">
          <div class="course-number">{{ i + 1 }}</div>
          <div class="course-info">
            <div class="course-title">{{ course.title }}</div>
            <div class="course-desc">{{ course.description }}</div>
            <div class="course-meta">
              <el-tag size="mini" type="info">{{ contentTypeText(course.contentType) }}</el-tag>
              <span class="course-time">{{ course.estimatedMinutes || '--' }} 分钟</span>
              <el-tag v-if="course.userStatus === 'COMPLETED'" type="success" size="mini" class="status-tag">已完成</el-tag>
              <el-tag v-else-if="course.userStatus === 'IN_PROGRESS'" type="warning" size="mini" class="status-tag">学习中</el-tag>
            </div>
          </div>
          <el-button type="primary" size="small"
            @click="$router.push(`/course/${course.id}?pathId=${path.id}`)">
            {{ course.userStatus === 'COMPLETED' ? '复习' : '开始' }}
          </el-button>
        </div>
        </div><!-- /.stagger-list -->
      </div>
    </template>
  </div>
</template>

<script>
import LearningProgress from '../components/LearningProgress.vue';
import { learningPathApi } from '../api';
import { CONTENT_TYPE_LABEL } from '../constants';

export default {
  name: 'LearningPathDetail',
  components: { LearningProgress },
  data() {
    return { path: null, loading: true, error: '' };
  },
  created() { this.loadDetail(); },
  computed: {
    difficultyText() {
      const map = { BEGINNER: '入门', INTERMEDIATE: '进阶', ADVANCED: '高级' };
      return map[this.path?.difficulty] || '';
    },
    difficultyType() {
      const map = { BEGINNER: 'success', INTERMEDIATE: 'warning', ADVANCED: 'danger' };
      return map[this.path?.difficulty] || 'info';
    }
  },
  methods: {
    async loadDetail() {
      this.loading = true;
      const id = Number(this.$route.params.id);
      try {
        const res = await learningPathApi.getDetail(id);
        if (res.code === 200 && res.data) {
          this.path = res.data;
        } else {
          this.error = res.message || '加载失败，请确认登录状态';
        }
      } catch {
        this.error = '网络请求失败，请检查登录状态';
      } finally {
        this.loading = false;
      }
    },
    contentTypeText(type) {
      return CONTENT_TYPE_LABEL[type] || type;
    }
  }
};
</script>

<style scoped>
.back-btn { margin-bottom: 16px; }

.path-hero {
  background: linear-gradient(135deg, #1a56db, #0ea5e9);
  color: white;
  padding: 32px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.path-title { font-size: 28px; margin-bottom: 8px; }
.path-desc { font-size: 15px; opacity: 0.9; margin-bottom: 16px; }

.path-meta {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
}

.meta-item { display: flex; align-items: center; gap: 4px; font-size: 14px; }

.section-title {
  font-size: 20px;
  margin-bottom: 16px;
}

.course-list { display: flex; flex-direction: column; gap: 8px; }

.course-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  transition: all 0.2s;
}

.course-item:hover { border-color: var(--primary-color); }

.course-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #eff6ff;
  color: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  flex-shrink: 0;
}

.course-info { flex: 1; }

.course-title { font-size: 15px; font-weight: 500; margin-bottom: 4px; }

.course-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.course-meta { display: flex; gap: 8px; align-items: center; }
.course-time { font-size: 12px; color: var(--text-secondary); }
.status-tag { margin-left: 4px; }
</style>
