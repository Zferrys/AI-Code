<template>
  <div class="qa-home">
    <div class="page-header">
      <div>
        <h2 class="page-title">智能问答</h2>
        <p class="page-subtitle">向 AI 提问编程问题，获取专业解答</p>
      </div>
    </div>

    <el-row :gutter="24">
      <!-- 提问区 -->
      <el-col :xs="24" :sm="24" :md="14">
        <div class="qa-card-main">
          <div class="qa-card-header">
            <i class="el-icon-edit"></i>
            <span>向 AI 提问</span>
          </div>
          <el-form :model="form" label-width="0">
            <el-form-item>
              <el-select v-model="form.category" placeholder="选择分类" class="qa-select">
                <el-option label="Java 基础" value="java-basics" />
                <el-option label="Spring 框架" value="spring" />
                <el-option label="数据库" value="database" />
                <el-option label="前端" value="frontend" />
                <el-option label="算法" value="algorithm" />
                <el-option label="工具" value="tools" />
                <el-option label="其他" value="general" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input type="textarea" :rows="8" v-model="form.content" placeholder="描述你的编程问题，可以包含代码、报错信息等..." class="qa-textarea" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="asking" class="qa-submit-btn" @click="handleAsk" :disabled="!form.content">
                <i class="el-icon-position"></i>
                {{ asking ? 'AI 思考中...' : '发送问题' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>

      <!-- 问题列表 -->
      <el-col :xs="24" :sm="24" :md="10">
        <div class="qa-card-side">
          <div class="qa-card-header">
            <i class="el-icon-tickets"></i>
            <span>最近问题 ({{ displayQuestions.length }})</span>
          </div>

          <div v-if="displayQuestions.length === 0" class="empty-state">
            <div class="empty-illustration">
              <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.2" opacity="0.5">
                <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
                <line x1="9" y1="10" x2="15" y2="10"/>
                <line x1="12" y1="7" x2="12" y2="13"/>
              </svg>
            </div>
            <p class="empty-title">还没有提问，快来提问吧</p>
            <p class="empty-hint">试试问以下问题：</p>
            <div class="example-questions">
              <el-tag v-for="(q, i) in exampleQuestions" :key="i"
                class="example-q" size="medium" effect="plain"
                @click="fillExample(q)">
                {{ q }}
              </el-tag>
            </div>
          </div>

          <div v-else class="question-list">
            <div v-for="q in displayQuestions" :key="q.id" class="question-item card-3d" @click="$router.push(`/qa/${q.id}`)">
              <div class="q-left">
                <span class="q-count">{{ q.answerCount || 0 }}</span>
                <span class="q-count-label">回答</span>
              </div>
              <div class="q-right">
                <div class="q-title">
                  {{ q.title }}
                  <span v-if="q.isResolved" class="q-resolved"><i class="el-icon-success"></i></span>
                </div>
                <div class="q-meta">
                  <span class="badge badge-blue">{{ categoryLabel(q.category) }}</span>
                  <span class="q-stat"><i class="el-icon-view"></i> {{ q.viewCount || 0 }}</span>
                  <span class="q-stat"><i class="el-icon-star-off"></i> {{ q.favoriteCount || 0 }}</span>
                  <span class="q-time">{{ timeAgo(q.createTime) }}</span>
                </div>
              </div>
            </div>
            <!-- 分页 -->
            <div v-if="totalQuestions > pageSize" class="qa-pagination">
              <el-pagination
                background layout="prev, pager, next"
                :total="totalQuestions" :page-size="pageSize"
                :current-page.sync="currentPage"
                @current-change="handlePageChange" small>
              </el-pagination>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { qaApi } from '../api';
import { mockQuestions, fallbackArray } from '../mock';

export default {
  name: 'QaHome',
  data() {
    return {
      form: { content: '', category: 'general' },
      asking: false,
      questions: [],
      currentPage: 1,
      pageSize: 10,
      totalQuestions: 0,
      exampleQuestions: [
        'Java 8 Stream API 中 map 和 flatMap 的区别？',
        'Spring Boot @Transactional 事务不生效的常见原因',
        'MySQL 索引失效的常见场景有哪些？',
        'Redis 缓存穿透/击穿/雪崩的区别及解决方案'
      ]
    };
  },
  computed: {
    displayQuestions() {
      return fallbackArray(this.questions, mockQuestions);
    }
  },
  created() { this.loadQuestions(); },
  methods: {
    async loadQuestions() {
      try {
        const res = await qaApi.getList({ page: this.currentPage, pageSize: this.pageSize });
        if (res.code === 200) {
          this.questions = (res.data && res.data.list) || [];
          this.totalQuestions = (res.data && res.data.total) || this.questions.length;
        }
      } catch {}
    },
    async handlePageChange(page) {
      this.currentPage = page;
      await this.loadQuestions();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    async handleAsk() {
      if (!this.form.content) return;
      this.asking = true;
      try {
        const payload = {
          ...this.form,
          title: this.form.content.substring(0, 80)
        };
        const res = await qaApi.ask(payload);
        if (res.code === 200) {
          this.$message.success('提问成功，AI 正在回答...');
          this.form = { content: '', category: 'general' };
          this.$router.push(`/qa/${res.data.id}`);
        } else {
          this.$message.error(res.message || '提问失败');
        }
      } catch (e) {
        if (e) this.$message.error('提问失败：' + (e?.response?.data?.message || '网络异常'));
      } finally {
        this.asking = false;
      }
    },
    categoryLabel(c) {
      const map = {
        'java-basics': 'Java', spring: 'Spring',
        database: '数据库', frontend: '前端',
        algorithm: '算法', tools: '工具', general: '其他'
      };
      return map[c] || c;
    },
    fillExample(q) {
      this.form.content = q;
      this.$message.info('已填入示例问题，点击"发送问题"开始提问');
    },
    timeAgo(time) {
      if (!time) return '';
      const diff = Date.now() - new Date(time).getTime();
      const m = Math.floor(diff / 60000);
      if (m < 1) return '刚刚';
      if (m < 60) return `${m} 分钟前`;
      const h = Math.floor(m / 60);
      if (h < 24) return `${h} 小时前`;
      const d = Math.floor(h / 24);
      if (d < 30) return `${d} 天前`;
      return new Date(time).toLocaleDateString('zh-CN');
    }
  }
};
</script>

<style scoped>
.qa-home {
  max-width: var(--content-max-width, 1200px);
  margin: 0 auto;
  padding: 24px 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 4px;
  color: var(--text-primary, #0f172a);
}

.page-subtitle {
  font-size: 14px;
  color: var(--text-secondary, #475569);
}

/* 卡片 */
.qa-card-main, .qa-card-side {
  background: white;
  border-radius: 14px;
  border: 1px solid var(--border, #e2e8f0);
  padding: 24px;
}

.qa-card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary, #0f172a);
  margin-bottom: 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border, #e2e8f0);
}

.qa-card-header i {
  color: var(--primary, #1a56db);
  font-size: 18px;
}

.qa-input >>> .el-input__inner {
  height: 46px;
  border-radius: 10px;
  border: 1.5px solid var(--border, #e2e8f0);
}
.qa-input >>> .el-input__inner:focus {
  border-color: var(--primary, #1a56db);
  box-shadow: 0 0 0 3px rgba(26,86,219,0.1);
}
.qa-input >>> .el-input__prefix { font-size: 16px; color: var(--text-tertiary, #94a3b8); }

.qa-select { width: 100%; }
.qa-select >>> .el-input__inner {
  border-radius: 10px;
  height: 42px;
  border: 1.5px solid var(--border, #e2e8f0);
}

.qa-textarea >>> .el-textarea__inner {
  border-radius: 10px;
  border: 1.5px solid var(--border, #e2e8f0);
  resize: vertical;
  min-height: 120px;
}
.qa-textarea >>> .el-textarea__inner:focus {
  border-color: var(--primary, #1a56db);
  box-shadow: 0 0 0 3px rgba(26,86,219,0.1);
}

.qa-submit-btn {
  background: var(--primary-gradient) !important;
  border: none !important;
  border-radius: 10px !important;
  padding: 12px 28px !important;
  font-size: 15px;
  font-weight: 500;
}

/* 问题列表 */
.question-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.question-item {
  display: flex;
  gap: 14px;
  padding: 14px;
  border: 1px solid var(--border, #e2e8f0);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}
.question-item:hover {
  border-color: var(--primary, #1a56db);
  background: #f8faff;
}

.q-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 44px;
}
.q-count {
  font-size: 16px;
  font-weight: 700;
  color: var(--primary, #1a56db);
}
.q-count-label {
  font-size: 11px;
  color: var(--text-tertiary, #94a3b8);
}

.q-right { flex: 1; min-width: 0; }

.q-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary, #0f172a);
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.q-resolved { color: var(--success, #059669); font-size: 13px; margin-left: 4px; }

.q-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  flex-wrap: wrap;
}

.q-stat {
  display: flex;
  align-items: center;
  gap: 2px;
  color: var(--text-tertiary, #94a3b8);
}

.q-time {
  color: var(--text-tertiary, #94a3b8);
  margin-left: auto;
  font-size: 11px;
}

/* 分页 */
.qa-pagination { display: flex; justify-content: center; padding-top: 16px; }
.qa-pagination >>> .el-pagination button,
.qa-pagination >>> .el-pager li { border-radius: 6px !important; min-width: 28px; height: 28px; line-height: 28px; font-size: 12px; }
.qa-pagination >>> .el-pager li.active { background: var(--primary-gradient) !important; border: none; }

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: var(--text-tertiary, #94a3b8);
}
.empty-illustration {
  margin-bottom: 12px;
  display: flex;
  justify-content: center;
}
.empty-title {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 6px;
  color: var(--text-secondary, #475569);
}
.empty-hint {
  font-size: 13px;
  margin-bottom: 12px;
}
.example-questions {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: center;
}
.example-q {
  cursor: pointer !important;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 13px;
  transition: all 0.2s;
  border: 1px solid var(--border, #e2e8f0) !important;
}
.example-q:hover {
  border-color: var(--primary, #1a56db) !important;
  color: var(--primary, #1a56db) !important;
  background: #f8faff !important;
}
</style>
