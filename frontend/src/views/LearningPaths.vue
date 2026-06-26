<template>
  <div class="lp-page">
    <!-- ===== 头部 ===== -->
    <header class="lp-hero">
      <div class="lp-hero-inner">
        <div class="lp-hero-text">
          <span class="lp-hero-kicker">LEARNING PATHS</span>
          <h1 class="lp-hero-title">
            <span class="title-line">系统化学习</span>
            <span class="title-line title-line-accent">进阶路线</span>
          </h1>
          <p class="lp-hero-desc">覆盖主流技术栈，从基础到实战，每一步都清晰可见</p>
        </div>
        <div class="lp-hero-stats">
          <div class="hero-stat">
            <span class="hero-stat-val">{{ allPaths.length || 6 }}</span>
            <span class="hero-stat-lbl">学习路径</span>
          </div>
          <div class="hero-stat">
            <span class="hero-stat-val">{{ totalCourses }}</span>
            <span class="hero-stat-lbl">课程总数</span>
          </div>
          <div class="hero-stat">
            <span class="hero-stat-val">{{ totalDays }}</span>
            <span class="hero-stat-lbl">学习天数</span>
          </div>
        </div>
      </div>
      <!-- 筛选条 -->
      <div class="lp-filter-bar">
        <div class="filter-item">
          <span class="filter-lbl">难度</span>
          <div class="filter-chips">
            <button :class="['chip', diffFilter === '' && 'chip-on']" @click="diffFilter = ''">全部</button>
            <button :class="['chip', diffFilter === 'BEGINNER' && 'chip-on']" @click="diffFilter = 'BEGINNER'">入门</button>
            <button :class="['chip', diffFilter === 'INTERMEDIATE' && 'chip-on']" @click="diffFilter = 'INTERMEDIATE'">进阶</button>
            <button :class="['chip', diffFilter === 'ADVANCED' && 'chip-on']" @click="diffFilter = 'ADVANCED'">高级</button>
          </div>
        </div>
        <div class="filter-item">
          <span class="filter-lbl">排序</span>
          <div class="filter-chips">
            <button :class="['chip', sortBy === 'default' && 'chip-on']" @click="sortBy = 'default'">推荐</button>
            <button :class="['chip', sortBy === 'courses' && 'chip-on']" @click="sortBy = 'courses'">课程最多</button>
            <button :class="['chip', sortBy === 'days' && 'chip-on']" @click="sortBy = 'days'">时长最长</button>
          </div>
        </div>
        <span class="filter-result-count">{{ displayPaths.length }} 条路径</span>
      </div>
    </header>

    <!-- ===== 推荐区 — 横排三卡 ===== -->
    <section v-if="recommended.length" class="lp-section">
      <div class="sec-head">
        <span class="sec-marker">精选</span>
        <h2 class="sec-title">为你推荐</h2>
      </div>
      <div class="rec-grid">
        <article
          v-for="(path, i) in recommended.slice(0, 3)"
          :key="path.id"
          class="rec-card"
          :class="diffClass(path.difficulty)"
          :style="{ animationDelay: `${i * 0.1}s` }"
          @click="go(path.id)"
        >
          <div class="rec-card-inner">
            <div class="rec-card-badge">{{ diffLabel(path.difficulty) }}</div>
            <div class="rec-card-body">
              <span class="rec-count">{{ path.courseCount }} 课 · {{ path.estimatedDays }} 天</span>
              <h3 class="rec-title">{{ path.title }}</h3>
              <p v-if="i === 0" class="rec-desc">{{ path.description }}</p>
              <p v-else class="rec-desc rec-desc-sm">{{ path.description }}</p>
              <div class="rec-tags">
                <span v-for="t in (path.tags || []).slice(0, i === 0 ? 5 : 4)" :key="t" class="tag">{{ t }}</span>
              </div>
            </div>
          </div>
        </article>
      </div>
    </section>

    <!-- ===== 全部路径 ===== -->
    <section class="lp-section">
      <div class="sec-head">
        <span class="sec-marker">全部</span>
        <h2 class="sec-title">所有路径 <span class="sec-count">({{ displayPaths.length }})</span></h2>
      </div>

      <!-- 空状态 -->
      <div v-if="!displayPaths.length" class="lp-empty">
        <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2" opacity=".35"><path d="M4 19.5A2.5 2.5 0 016.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"/></svg>
        <p>暂无匹配的学习路径</p>
      </div>

      <!-- 卡片网格 -->
      <div v-else class="all-grid">
        <article
          v-for="(path, i) in displayPaths"
          :key="path.id"
          class="all-card"
          :class="diffClass(path.difficulty)"
          :style="{ animationDelay: `${i * 0.05}s` }"
          @click="go(path.id)"
        >
          <div class="all-card-bar"></div>
          <div class="all-card-body">
            <div class="all-card-top">
              <span class="all-badge">{{ diffLabel(path.difficulty) }}</span>
              <span class="all-meta">{{ path.courseCount }} 课 · {{ path.estimatedDays }} 天</span>
            </div>
            <h3 class="all-title">{{ path.title }}</h3>
            <p class="all-desc">{{ path.description }}</p>
            <div class="all-tags">
              <span v-for="t in (path.tags || []).slice(0, 4)" :key="t" class="tag">{{ t }}</span>
            </div>
            <div class="all-footer">
              <span class="all-updated">更新于 {{ formatDate(path.updateTime) }}</span>
            </div>
          </div>
        </article>
      </div>
      <div v-if="totalPaths > pageSize" class="lp-pagination">
        <el-pagination
          background layout="prev, pager, next"
          :total="totalPaths" :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="handlePageChange">
        </el-pagination>
      </div>
    </section>
  </div>
</template>

<script>
import { learningPathApi } from '../api';
import { mockLearningPaths, fallbackArray } from '../mock';

export default {
  name: 'LearningPaths',
  data() {
    return {
      allPaths: [],
      recommended: [],
      diffFilter: '',
      currentPage: 1,
      pageSize: 12,
      totalPaths: 0,
      sortBy: 'default',
    };
  },
  computed: {
    totalCourses() {
      return (this.allPaths.length ? this.allPaths : mockLearningPaths).reduce((s, p) => s + (p.courseCount || 0), 0);
    },
    totalDays() {
      return (this.allPaths.length ? this.allPaths : mockLearningPaths).reduce((s, p) => s + (p.estimatedDays || 0), 0);
    },
    displayPaths() {
      let p = fallbackArray(this.allPaths, mockLearningPaths);
      if (this.diffFilter) p = p.filter(x => x.difficulty === this.diffFilter);
      if (this.sortBy === 'courses') p = [...p].sort((a, b) => (b.courseCount || 0) - (a.courseCount || 0));
      if (this.sortBy === 'days') p = [...p].sort((a, b) => (b.estimatedDays || 0) - (a.estimatedDays || 0));
      return p;
    },
  },
  created() { this.load(); },
  methods: {
    async load() {
      try {
        const params = { page: this.currentPage, pageSize: this.pageSize };
        const [a, r] = await Promise.all([
          learningPathApi.getAll(params),
          learningPathApi.getRecommended().catch(() => ({ data: [] })),
        ]);
        if (a.code === 200) {
          this.allPaths = a.data.list || a.data || [];
          this.totalPaths = a.data.total || 0;
        }
        if (r.code === 200) this.recommended = r.data || [];
        if (!this.recommended.length) this.recommended = mockLearningPaths.slice(0, 3);
      } catch { /* mock fallback in computed */ }
    },
    async handlePageChange(page) {
      this.currentPage = page;
      await this.load();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    go(id) { this.$router.push(`/learning-paths/${id}`); },
    diffLabel(d) { return { BEGINNER: '入门', INTERMEDIATE: '进阶', ADVANCED: '高级' }[d] || d; },
    diffClass(d) {
      return { BEGINNER: 'd-green', INTERMEDIATE: 'd-yellow', ADVANCED: 'd-purple' }[d] || 'd-blue';
    },
    formatDate(t) { return t ? new Date(t).toLocaleDateString('zh-CN') : ''; },
  },
};
</script>

<style scoped>
/* =========================================================
   学习路径页 — Editorial Magazine 风格
   配色：60% 浅灰 #f8fafc + 30% 深灰 #0f172a + 10% 翡翠绿
   ========================================================= */

.lp-page {
  max-width: 1240px;
  margin: 0 auto;
  padding: 0 28px 60px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* ===== 英雄区 ===== */
.lp-hero {
  padding: 48px 0 32px;
  margin-bottom: 8px;
  position: relative;
}

.lp-hero::after {
  content: '';
  position: absolute;
  bottom: 0; left: 0; right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, #e2e8f0 30%, #e2e8f0 70%, transparent 100%);
}

.lp-hero-inner {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 40px;
  margin-bottom: 32px;
}

.lp-hero-kicker {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: #059669;
  margin-bottom: 12px;
  display: block;
}

.lp-hero-title {
  font-size: 40px;
  font-weight: 800;
  line-height: 1.15;
  color: #0f172a;
  margin: 0 0 14px;
  letter-spacing: -1px;
}

.title-line { display: block; }
.title-line-accent {
  color: #059669;
  position: relative;
  display: inline-block;
}
.title-line-accent::after {
  content: '';
  position: absolute;
  bottom: 2px; left: 0; right: -4px;
  height: 8px;
  background: rgba(5, 150, 105, 0.12);
  border-radius: 2px;
  z-index: -1;
}

.lp-hero-desc {
  font-size: 16px;
  line-height: 1.6;
  color: #64748b;
  margin: 0;
  max-width: 440px;
}

.lp-hero-stats {
  display: flex;
  gap: 28px;
  flex-shrink: 0;
}

.hero-stat {
  text-align: center;
}

.hero-stat-val {
  display: block;
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1;
  margin-bottom: 4px;
}

.hero-stat-lbl {
  font-size: 12px;
  font-weight: 500;
  color: #94a3b8;
  letter-spacing: 0.3px;
}

/* ===== 筛选条 ===== */
.lp-filter-bar {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-lbl {
  font-size: 11px;
  font-weight: 600;
  color: #94a3b8;
  letter-spacing: 0.8px;
  text-transform: uppercase;
}

.filter-chips {
  display: flex;
  gap: 4px;
}

.chip {
  padding: 5px 14px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  background: #f1f5f9;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.chip:hover { background: #e2e8f0; color: #0f172a; }

.chip-on {
  background: #0f172a;
  color: #fff;
}
.chip-on:hover { background: #1e293b; color: #fff; }

.filter-result-count {
  margin-left: auto;
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
}

/* ===== 通用区块 ===== */
.lp-section {
  margin-top: 48px;
}

.sec-head {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 24px;
}

.sec-marker {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1.8px;
  text-transform: uppercase;
  color: #fff;
  background: #0f172a;
  padding: 3px 10px;
  border-radius: 4px;
}

.sec-title {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.sec-count {
  font-weight: 400;
  color: #94a3b8;
  font-size: 19px;
}

/* ===== 推荐区 — 三卡等宽 ===== */
.rec-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.rec-card {
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  animation: lpFadeUp 0.5s both;
}

.rec-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 32px -10px rgba(0, 0, 0, 0.1);
}

/* 难度主题色 */
.d-green { --accent: #059669; --accent-bg: #ecfdf5; }
.d-yellow { --accent: #d97706; --accent-bg: #fffbeb; }
.d-purple { --accent: #0ea5e9; --accent-bg: #f0f9ff; }
.d-blue { --accent: #2563eb; --accent-bg: #eff6ff; }

.rec-card-inner {
  background: #fff;
  border: 1px solid #e8ecf1;
  border-radius: 14px;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.rec-card:hover .rec-card-inner {
  border-color: var(--accent, #059669);
  box-shadow: 0 12px 28px -8px rgba(0, 0, 0, 0.08);
}

.rec-card-badge {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  background: var(--accent, #059669);
  padding: 3px 12px;
  border-radius: 0 0 8px 0;
  letter-spacing: 0.3px;
  align-self: flex-start;
}

.rec-card-body {
  padding: 20px 20px 22px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.rec-count {
  font-size: 12px;
  font-weight: 600;
  color: var(--accent, #059669);
  letter-spacing: 0.2px;
  margin-bottom: 8px;
  display: block;
}

.rec-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.35;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rec-desc {
  font-size: 13.5px;
  line-height: 1.6;
  color: #475569;
  margin: 0 0 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rec-desc-sm {
  font-size: 12.5px;
  margin-bottom: 10px;
  -webkit-line-clamp: 2;
}

.rec-tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
  margin-top: auto;
}

.tag {
  font-size: 11px;
  font-weight: 500;
  padding: 3px 9px;
  border-radius: 5px;
  background: #f1f5f9;
  color: #475569;
  transition: background 0.2s, color 0.2s;
}

.rec-card:hover .tag {
  background: var(--accent-bg, #ecfdf5);
  color: var(--accent, #059669);
}

/* ===== 全部路径 — 3列网格 ===== */
.all-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.all-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e8ecf1;
  cursor: pointer;
  transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.35s ease, border-color 0.3s;
  animation: lpFadeUp 0.5s both;
  position: relative;
  display: flex;
  flex-direction: column;
}

.all-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 16px 32px -8px rgba(0, 0, 0, 0.1);
  border-color: var(--accent, #059669);
}

/* 左侧色条 */
.all-card-bar {
  position: absolute;
  top: 0; left: 0;
  width: 4px;
  height: 100%;
  background: var(--accent, #059669);
  opacity: 0;
  transition: opacity 0.3s;
}

.all-card:hover .all-card-bar {
  opacity: 1;
}

.all-card-body {
  padding: 20px 22px 22px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.all-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.all-badge {
  font-size: 11px;
  font-weight: 600;
  color: var(--accent, #059669);
  background: var(--accent-bg, #ecfdf5);
  padding: 2px 10px;
  border-radius: 4px;
  letter-spacing: 0.2px;
}

.all-meta {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

.all-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.35;
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.all-desc {
  font-size: 13.5px;
  line-height: 1.6;
  color: #64748b;
  margin: 0 0 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.all-tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.all-tags .tag {
  font-size: 11px;
  font-weight: 500;
  padding: 3px 9px;
  border-radius: 5px;
  background: #f1f5f9;
  color: #475569;
  transition: all 0.2s;
}

.all-card:hover .tag {
  background: var(--accent-bg, #ecfdf5);
  color: var(--accent, #059669);
}

.all-footer {
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.all-updated {
  font-size: 12px;
  color: #b0b8c4;
}

/* ===== 空状态 ===== */
.lp-empty {
  text-align: center;
  padding: 80px 20px;
  color: #94a3b8;
}
.lp-empty p {
  font-size: 15px;
  margin-top: 14px;
  color: #94a3b8;
}

/* ===== 动画 ===== */
@keyframes lpFadeUp {
  from { opacity: 0; transform: translateY(24px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .rec-grid { grid-template-columns: 1fr; }
  .all-grid { grid-template-columns: repeat(2, 1fr); }
  .lp-hero-inner { flex-direction: column; align-items: flex-start; }
  .lp-hero-stats { width: 100%; justify-content: space-around; }
  .lp-hero-title { font-size: 32px; }
}

@media (max-width: 600px) {
  .lp-page { padding: 0 16px 40px; }
  .all-grid { grid-template-columns: 1fr; }
  .lp-hero-title { font-size: 26px; }
  .lp-filter-bar { flex-direction: column; align-items: flex-start; gap: 12px; }
  .filter-result-count { margin-left: 0; }
}
</style>
