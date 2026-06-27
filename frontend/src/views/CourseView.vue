<template>
  <div class="course-view">
    <el-button type="text" @click="goBack" class="back-btn">
      ← 返回
    </el-button>

    <div v-if="loading"><el-skeleton :rows="10" animated /></div>

    <div v-else-if="error">
      <el-result icon="error" title="加载失败" :sub-title="error" />
    </div>

    <template v-else-if="course">
      <el-card>
        <h2 class="course-title">{{ course.title }}</h2>
        <div class="course-meta">
          <el-tag size="mini" type="info">{{ contentTypeText }}</el-tag>
          <span class="course-time">{{ course.estimatedMinutes }} 分钟</span>
          <span class="meta-spacer"></span>
          <!-- 未开始 → 开始学习 -->
          <el-button v-if="isNotStarted" type="primary" size="small"
            :loading="starting" @click="handleStart" class="start-btn">
            <i class="el-icon-video-play"></i> 开始学习
          </el-button>
          <!-- 学习中 → 标记完成 -->
          <el-button v-else-if="isInProgress" type="warning" size="small"
            :loading="completing" @click="handleComplete" class="complete-btn">
            <i class="el-icon-check"></i> 标记完成
          </el-button>
          <!-- 已完成 -->
          <span v-else class="completed-group">
            <el-tag type="success" size="medium">✓ 已完成</el-tag>
            <el-button type="default" size="small" :loading="resetting"
              @click="handleReset" class="reset-btn">
              重新学习
            </el-button>
          </span>
        </div>
        <el-divider />
        <div class="course-content">
          <markdown-renderer :content="course.contentMarkdown || '暂无内容'" />
        </div>

        <!-- 参考资料区块 -->
        <div class="resources-section" v-if="courseResources.length > 0">
          <el-divider />
          <h3 class="resources-title">📚 参考资料</h3>
          <div v-for="group in resourceGroups" :key="group.type" class="resource-group">
            <h4 class="resource-group-title">{{ group.label }}</h4>
            <div v-for="(r, i) in group.items" :key="r.id" class="resource-item">
              <span class="resource-index">{{ i + 1 }}.</span>
              <a v-if="r.type !== 'DOWNLOAD'" :href="r.url" target="_blank" class="resource-link">
                <i :class="resourceIcon(r.type)"></i>
                <span>{{ r.title }}</span>
              </a>
              <div v-else class="resource-link">
                <i :class="resourceIcon(r.type)"></i>
                <span>{{ r.title }}</span>
                <el-tag size="mini" type="warning" class="resource-download-tag">下载</el-tag>
              </div>
              <p class="resource-desc" v-if="r.description">{{ r.description }}</p>
            </div>
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script>
import MarkdownRenderer from '../components/MarkdownRenderer.vue';
import { learningPathApi } from '../api';
import { CONTENT_TYPE_LABEL, RESOURCE_TYPE_LABEL, RESOURCE_TYPE_ICON } from '../constants';

export default {
  name: 'CourseView',
  components: { MarkdownRenderer },
  data() {
    return {
      course: null,
      loading: true,
      error: '',
      starting: false,
      completing: false,
      resetting: false,
      resources: []
    };
  },
  created() { this.loadCourse(); },
  methods: {
    async loadCourse() {
      this.loading = true;
      const courseId = Number(this.$route.params.courseId);
      try {
        const [courseRes, resRes] = await Promise.all([
          learningPathApi.getCourse(courseId),
          learningPathApi.getCourseResources(courseId).catch(() => null)
        ]);
        if (courseRes.code === 200 && courseRes.data) {
          this.course = courseRes.data;
        } else {
          this.error = courseRes.message || '课程未找到';
        }
        if (resRes && resRes.code === 200) {
          this.resources = resRes.data || [];
        }
      } catch {
        this.error = '网络请求失败，请检查登录状态';
      } finally {
        this.loading = false;
      }
    },
    goBack() {
      if (window.history.length > 1) this.$router.back();
      else this.$router.push('/learning-paths');
    },
    async handleStart() {
      this.starting = true;
      try {
        const pathId = this.course.pathId || (
          this.$route.query.pathId ? Number(this.$route.query.pathId) : 0
        );
        const res = await learningPathApi.startCourse({
          courseId: Number(this.$route.params.courseId),
          pathId: pathId
        });
        if (res.code === 200) {
          this.course.userStatus = 'IN_PROGRESS';
          this.$message.success('开始学习！完成课程后记得点"标记完成"');
        } else {
          this.$message.error(res.message || '操作失败');
        }
      } catch (e) {
        this.$message.error('网络错误，请稍后重试');
      } finally {
        this.starting = false;
      }
    },
    async handleComplete() {
      this.completing = true;
      try {
        const pathId = this.course.pathId || (
          this.$route.query.pathId ? Number(this.$route.query.pathId) : 0
        );
        const res = await learningPathApi.completeCourse({
          courseId: Number(this.$route.params.courseId),
          pathId: pathId
        });
        if (res.code === 200) {
          this.course.userStatus = 'COMPLETED';
          this.$message.success('🎉 恭喜完成本课程！');
        } else {
          this.$message.error(res.message || '完成失败');
        }
      } catch (e) {
        this.$message.error('网络错误，请稍后重试');
      } finally {
        this.completing = false;
      }
    },
    async handleReset() {
      this.resetting = true;
      try {
        const res = await learningPathApi.resetCourse({
          courseId: Number(this.$route.params.courseId)
        });
        if (res.code === 200) {
          this.course.userStatus = 'NOT_STARTED';
          this.$message.info('已重置为未开始');
        } else {
          this.$message.error(res.message || '重置失败');
        }
      } catch (e) {
        this.$message.error('网络错误，请稍后重试');
      } finally {
        this.resetting = false;
      }
    },
    resourceIcon(type) {
      const icons = {
        LINK: 'el-icon-link', VIDEO: 'el-icon-video-camera',
        EXERCISE: 'el-icon-edit-outline', REFERENCE: 'el-icon-document',
        DOWNLOAD: 'el-icon-download'
      };
      return icons[type] || 'el-icon-link';
    }
  },
  computed: {
    isNotStarted() {
      return !this.course?.userStatus || this.course?.userStatus === 'NOT_STARTED';
    },
    isInProgress() {
      return this.course?.userStatus === 'IN_PROGRESS';
    },
    isCompleted() {
      return this.course?.userStatus === 'COMPLETED';
    },
    contentTypeText() {
      return CONTENT_TYPE_LABEL[this.course?.contentType] || this.course?.contentType || '';
    },
    courseResources() {
      return this.resources || [];
    },
    resourceGroups() {
      const types = ['LINK', 'VIDEO', 'EXERCISE', 'REFERENCE', 'DOWNLOAD'];
      const groups = [];
      for (const t of types) {
        const items = this.courseResources.filter(r => r.type === t);
        if (items.length > 0) {
          groups.push({ type: t, label: RESOURCE_TYPE_LABEL[t] || t, items });
        }
      }
      return groups;
    }
  }
};
</script>

<style scoped>
.back-btn { margin-bottom: 16px; }

.course-title { font-size: 24px; margin-bottom: 12px; }

.course-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-secondary);
  font-size: 14px;
}

.complete-btn { margin-left: auto; }
.meta-spacer { flex: 1; }
.completed-group { display: flex; align-items: center; gap: 8px; margin-left: auto; }
.reset-btn { font-size: 12px; }

.course-content {
  font-size: 15px;
  line-height: 1.8;
  min-height: 300px;
}

/* 参考资料区 */
.resources-section { margin-top: 8px; }
.resources-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-primary);
}
.resource-group { margin-bottom: 16px; }
.resource-group-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 8px;
  padding-bottom: 4px;
  border-bottom: 1px solid var(--border);
}
.resource-item {
  padding: 8px 12px;
  border-radius: 6px;
  transition: background 0.2s;
}
.resource-item:hover { background: #f8faff; }
.resource-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--primary, #1a56db);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}
.resource-link:hover { color: #1e40af; text-decoration: underline; }
.resource-link i { font-size: 16px; }
.resource-desc { font-size: 13px; color: var(--text-tertiary); margin: 4px 0 0 24px; }
.resource-download-tag { margin-left: auto; }
</style>
