<template>
  <div class="code-review">
    <div class="page-header">
      <div>
        <h2 class="page-title">智能代码审查</h2>
        <p class="page-subtitle">AI 自动分析代码质量、检测 Bug 和安全漏洞</p>
      </div>
    </div>

    <el-row :gutter="24">
      <!-- 提交审查 -->
      <el-col :xs="24" :sm="24" :md="14">
        <div class="cr-card-main card-3d">
          <div class="cr-card-header glow-hover">
            <i class="el-icon-document"></i>
            <span>提交代码审查</span>
          </div>
          <el-form :model="form" label-width="60px">
            <el-form-item label="标题">
              <el-input v-model="form.title" placeholder="给这次审查起个标题" class="cr-input" />
            </el-form-item>
            <el-form-item label="语言">
              <el-select v-model="form.language" class="cr-select">
                <el-option label="Java" value="java" />
                <el-option label="Python" value="python" />
                <el-option label="JavaScript" value="javascript" />
                <el-option label="TypeScript" value="typescript" />
                <el-option label="Go" value="go" />
                <el-option label="C++" value="cpp" />
                <el-option label="SQL" value="sql" />
              </el-select>
            </el-form-item>
            <el-form-item label="代码">
              <el-input type="textarea" v-model="form.codeContent" :rows="15"
                placeholder="粘贴你的代码到这里..."
                class="cr-code-input" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" class="cr-submit-btn" @click="handleSubmit">
                <i class="el-icon-upload2"></i>
                {{ submitting ? '提交中...' : '提交审查' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>

      <!-- 审查历史 -->
      <el-col :xs="24" :sm="24" :md="10">
        <div class="cr-card-side">
          <div class="cr-card-header">
            <i class="el-icon-clock"></i>
            <span>审查历史 ({{ displayReviews.length }})</span>
          </div>

          <div v-if="displayReviews.length === 0" class="empty-state">
            <div class="empty-illustration">
              <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.2" opacity="0.5">
                <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="9" y1="13" x2="15" y2="13"/>
                <line x1="9" y1="17" x2="13" y2="17"/>
              </svg>
            </div>
            <p class="empty-title">还没有提交过代码审查</p>
            <p class="empty-hint">将你的代码粘贴到左侧编辑区，点击"提交审查"开始</p>
          </div>

          <div v-else class="review-list">
            <div v-for="item in displayReviews" :key="item.id" class="review-item card-glow-hover" @click="$router.push(`/code-review/${item.id}`)">
              <div class="review-top">
                <span class="review-title">{{ item.title }}</span>
                <el-tag :type="statusTag(item.status)" size="mini" effect="plain" class="review-status">
                  {{ statusText(item.status) }}
                </el-tag>
              </div>
              <div class="review-mid">
                <span class="review-lang">{{ item.language }}</span>
                <span v-if="item.qualityScore != null" class="review-score" :class="scoreClass(item.qualityScore)">
                  <i class="el-icon-star-on"></i> {{ item.qualityScore }}
                </span>
              </div>
              <div class="review-bottom">
                <span v-if="item.bugCount != null" class="review-bugs">
                  <i class="el-icon-warning"></i> {{ item.bugCount }} bugs
                </span>
                <span class="review-time">{{ formatTime(item.createTime) }}</span>
              </div>
            </div>
            <div v-if="totalReviews > pageSize" class="cr-pagination">
              <el-pagination
                background layout="prev, pager, next"
                :total="totalReviews" :page-size="pageSize"
                :current-page.sync="currentPage"
                @current-change="handlePageChange" small>
              </el-pagination>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 轮询状态面板 -->
    <div class="polling-panel" v-if="showPolling">
      <div class="polling-header">
        <span>AI 代码审查</span>
        <el-button type="text" size="mini" @click="showPolling = false" class="polling-close">
          <i class="el-icon-close"></i>
        </el-button>
      </div>
      <waiting-indicator
        :message="pollingMessage"
        :sub-message="'审查 ID: ' + currentReviewId"
        :elapsed="pollingElapsed"
        :stages="stages"
        :current-stage="currentStageIndex"
        :show-timer="true"
      />
    </div>
  </div>
</template>

<script>
import WaitingIndicator from '../components/WaitingIndicator.vue';
import { codeReviewApi } from '../api';
import { mockReviews, fallbackArray } from '../mock';

export default {
  name: 'CodeReview',
  components: { WaitingIndicator },
  data() {
    return {
      form: { title: '', language: 'java', codeContent: '' },
      submitting: false,
      reviews: [],
      currentPage: 1,
      pageSize: 5,
      totalReviews: 0,
      showPolling: false,
      pollingMessage: '正在提交审查请求...',
      pollingTimer: null,
      pollingStartTime: 0,
      pollingElapsed: 0,
      currentReviewId: null,
      stages: ['排队等待', 'AI 分析代码', '检测问题', '生成报告'],
      currentStageIndex: 0
    };
  },
  computed: {
    displayReviews() {
      return fallbackArray(this.reviews, mockReviews);
    }
  },
  created() { this.loadReviews(); },
  beforeDestroy() { this.stopPolling(); },
  methods: {
    async loadReviews() {
      try {
        const res = await codeReviewApi.getList({ page: this.currentPage, pageSize: this.pageSize });
        if (res.code === 200) {
          this.reviews = (res.data && res.data.list) || [];
          this.totalReviews = (res.data && res.data.total) || 0;
        }
      } catch (e) {
        console.warn('审查历史加载失败:', e?.message);
      }
    },
    async handlePageChange(page) {
      this.currentPage = page;
      await this.loadReviews();
    },
    async handleSubmit() {
      // 校验表单
      if (!this.form.title && !this.form.codeContent) {
        this.$message.warning('请输入标题和代码内容');
        return;
      }
      if (!this.form.title) {
        this.$message.warning('请输入审查标题');
        return;
      }
      if (!this.form.codeContent || !this.form.codeContent.trim()) {
        this.$message.warning('代码内容不能为空，请在编辑器中粘贴你的代码');
        return;
      }

      this.submitting = true;
      try {
        const payload = {
          title: this.form.title,
          language: this.form.language,
          codeContent: this.form.codeContent
        };
        const res = await codeReviewApi.submit(payload);
        if (res.code === 200) {
          this.$message.success('提交成功，AI 审查中...');
          this.form.codeContent = '';
          this.currentReviewId = res.data.id;
          this.startPolling(res.data.id);
          this.loadReviews();
        } else {
          this.$message.error(res.message || '提交失败');
        }
      } catch (e) {
        if (e) this.$message.error('提交失败：' + (e?.response?.data?.message || '网络异常'));
      } finally {
        this.submitting = false;
      }
    },
    startPolling(id) {
      this.showPolling = true;
      this.pollingMessage = '等待处理中...';
      this.pollingStartTime = Date.now();
      this.currentStageIndex = 0;
      this.pollingElapsed = 0;
      this.stopPolling();

      this.pollingTimer = setInterval(async () => {
        // 更新已耗时间
        this.pollingElapsed = Date.now() - this.pollingStartTime;

        try {
          const res = await codeReviewApi.getStatus(id);
          if (res.code === 200) {
            const data = res.data;
            const status = data.status;

            if (status === 'PENDING') {
              this.pollingMessage = '排队等待中...';
              this.currentStageIndex = 0;
            } else if (status === 'PROCESSING') {
              this.pollingMessage = `AI 正在审查代码...`;
              this.currentStageIndex = 1;
              // 根据已耗时间推进阶段（模拟）
              if (this.pollingElapsed > 8000) this.currentStageIndex = 2;
              if (this.pollingElapsed > 15000) this.currentStageIndex = 3;
            } else if (status === 'COMPLETED') {
              this.pollingMessage = '审查完成！';
              this.currentStageIndex = 3;
              setTimeout(() => {
                this.showPolling = false;
                this.$router.push(`/code-review/${id}`);
              }, 1000);
              this.stopPolling();
            } else if (status === 'FAILED') {
              this.pollingMessage = '审查失败';
              this.currentStageIndex = 0;
              setTimeout(() => this.showPolling = false, 2000);
              this.stopPolling();
              this.loadReviews();
            }
          }
        } catch {
          this.stopPolling();
          this.showPolling = false;
        }
      }, 3000);
    },
    stopPolling() {
      if (this.pollingTimer) {
        clearInterval(this.pollingTimer);
        this.pollingTimer = null;
      }
    },
    statusText(s) { const m = { PENDING: '等待', PROCESSING: '审查中', COMPLETED: '已完成', FAILED: '失败' }; return m[s] || s; },
    statusTag(s) { const m = { PENDING: 'info', PROCESSING: 'warning', COMPLETED: 'success', FAILED: 'danger' }; return m[s] || 'info'; },
    scoreClass(s) {
      if (s >= 85) return 'score-high';
      if (s >= 70) return 'score-mid';
      return 'score-low';
    },
    formatTime(t) {
      if (!t) return '';
      const d = new Date(t);
      return `${d.getMonth()+1}/${d.getDate()} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`;
    }
  }
};
</script>

<style scoped>
.code-review {
  max-width: var(--content-max-width, 1200px);
  margin: 0 auto;
  padding: 24px 20px;
}

.page-header { margin-bottom: 24px; }
.page-title { font-size: 26px; font-weight: 700; margin-bottom: 4px; color: var(--text-primary, #0f172a); }
.page-subtitle { font-size: 14px; color: var(--text-secondary, #475569); }

.cr-card-main, .cr-card-side {
  background: white;
  border-radius: 14px;
  border: 1px solid var(--border, #e2e8f0);
  padding: 24px;
}

.cr-card-header {
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
.cr-card-header i { color: var(--primary, #1a56db); font-size: 18px; }

.cr-input >>> .el-input__inner {
  border-radius: 8px;
  height: 40px;
  border: 1.5px solid var(--border, #e2e8f0);
}

.cr-select { width: 100%; }
.cr-select >>> .el-input__inner { border-radius: 8px; height: 40px; border: 1.5px solid var(--border, #e2e8f0); }
.cr-code-input >>> .el-textarea__inner {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  min-height: 350px;
  border-radius: 8px;
  background: #1e293b;
  color: #e2e8f0;
  border: 1px solid #334155;
}

.cr-submit-btn {
  background: var(--primary-gradient) !important;
  border: none !important;
  border-radius: 10px !important;
  padding: 11px 24px !important;
  font-size: 14px;
}

/* 审查历史 */
.review-list { display: flex; flex-direction: column; gap: 10px; max-height: 520px; overflow-y: auto; padding-right: 4px; }

.review-item {
  padding: 14px;
  border: 1px solid var(--border, #e2e8f0);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}
.review-item:hover {
  border-color: var(--primary, #1a56db);
  background: #f8faff;
}

.review-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.review-title { font-size: 14px; font-weight: 500; color: var(--text-primary, #0f172a); flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-right: 8px; }
.review-status { flex-shrink: 0; }

.review-mid { display: flex; gap: 8px; align-items: center; margin-bottom: 6px; }
.review-lang { font-size: 12px; color: var(--text-secondary, #475569); background: #f1f5f9; padding: 2px 8px; border-radius: 4px; }
.review-score { font-size: 13px; font-weight: 600; display: flex; align-items: center; gap: 2px; }
.score-high { color: var(--success, #059669); }
.score-mid { color: var(--warning, #d97706); }
.score-low { color: var(--danger, #dc2626); }

.review-bottom { display: flex; justify-content: space-between; font-size: 12px; }
.review-bugs { color: var(--danger, #dc2626); display: flex; align-items: center; gap: 3px; }
.review-time { color: var(--text-tertiary, #94a3b8); }

/* 轮询面板 */
.polling-panel {
  margin-top: 20px;
  background: white;
  border-radius: 14px;
  border: 1px solid var(--border, #e2e8f0);
  overflow: hidden;
}
.polling-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary, #0f172a);
  border-bottom: 1px solid var(--border, #e2e8f0);
  background: #f8faff;
}
.polling-close {
  color: var(--text-tertiary, #94a3b8);
  padding: 0;
}

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
  color: var(--text-tertiary, #94a3b8);
  line-height: 1.5;
}

.cr-pagination {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}
</style>
