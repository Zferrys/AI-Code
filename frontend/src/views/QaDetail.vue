<template>
  <div class="qa-detail">
    <el-button type="text" @click="$router.push('/qa')" class="back-btn">
      ← 返回问答列表
    </el-button>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else-if="error" class="error">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra>
          <el-button type="primary" @click="loadDetail">重新加载</el-button>
        </template>
      </el-result>
    </div>

    <template v-else-if="question">
      <!-- 问题信息 -->
      <el-card class="question-card">
        <h2 class="q-title">{{ question.title }}</h2>
        <div class="q-meta">
          <el-tag size="mini" type="info">{{ question.category }}</el-tag>
          <span class="q-user">{{ question.username }}</span>
          <span class="q-time">{{ formatTime(question.createTime) }}</span>
          <span class="q-stat"><i class="el-icon-view"></i> {{ question.viewCount || 0 }}</span>
        </div>
        <div class="q-content">
          <markdown-renderer :content="question.content" />
        </div>
      </el-card>

      <!-- 回答列表 -->
      <div class="answers-section">
        <h3 class="section-title">回答（{{ (question.answers || []).length }}）</h3>

        <div v-if="!question.answers || question.answers.length === 0" class="no-answers">
          <waiting-indicator
            message="AI 正在生成回答..."
            sub-message="深度分析问题中，请稍候"
            :elapsed="qaElapsed"
            :stages="qaStages"
            :current-stage="qaStageIndex"
          />
        </div>

        <div v-for="answer in question.answers" :key="answer.id" class="answer-card">
          <div class="answer-header">
            <el-tag :type="answer.type === 'AI' ? 'success' : 'primary'" size="small">
              {{ answer.type === 'AI' ? 'AI 回答' : '用户追问' }}
            </el-tag>
            <span class="answer-time">{{ formatTime(answer.createTime) }}</span>
            <span class="answer-duration" v-if="answer.aiResponseTime">
              耗时 {{ (answer.aiResponseTime / 1000).toFixed(1) }}s
            </span>
          </div>
          <div class="answer-content">
            <markdown-renderer :content="answer.content" />
          </div>
        </div>
      </div>

      <!-- 追问输入 -->
      <el-card class="follow-up-card">
        <template #header><span>追问</span></template>
        <el-input type="textarea" :rows="3" v-model="followUpContent"
          placeholder="输入你的追问..." />
        <el-button type="primary" class="mt-10" :loading="followUping"
          @click="handleFollowUp" :disabled="!followUpContent">
          发送追问
        </el-button>
      </el-card>
    </template>
  </div>
</template>

<script>
import MarkdownRenderer from '../components/MarkdownRenderer.vue';
import WaitingIndicator from '../components/WaitingIndicator.vue';
import { qaApi } from '../api';

export default {
  name: 'QaDetail',
  components: { MarkdownRenderer, WaitingIndicator },
  data() {
    return {
      question: null,
      loading: true,
      error: '',
      followUpContent: '',
      followUping: false,
      pollingTimer: null,
      qaStartTime: 0,
      qaElapsed: 0,
      qaStages: ['接收问题', 'AI 分析', '生成回答'],
      qaStageIndex: 0
    };
  },
  created() { this.loadDetail(); },
  beforeDestroy() { this.stopPolling(); },
  methods: {
    async loadDetail() {
      this.loading = true;
      this.error = '';
      try {
        const res = await qaApi.getDetail(this.$route.params.id);
        if (res.code === 200) {
          this.question = res.data;
          // 如果没有回答，开始轮询
          if (!res.data.answers || res.data.answers.length === 0) {
            this.qaStartTime = Date.now();
            this.startPolling();
          }
        } else {
          this.error = res.message || '加载失败';
        }
      } catch (e) {
        this.error = '网络请求失败';
      } finally {
        this.loading = false;
      }
    },
    startPolling() {
      this.stopPolling();
      this.pollingTimer = setInterval(async () => {
        this.qaElapsed = Date.now() - this.qaStartTime;
        // 根据耗时推进阶段
        if (this.qaElapsed > 5000) this.qaStageIndex = 1;
        if (this.qaElapsed > 12000) this.qaStageIndex = 2;

        try {
          const res = await qaApi.getDetail(this.$route.params.id);
          if (res.code === 200 && res.data.answers && res.data.answers.length > 0) {
            this.question = res.data;
            this.stopPolling();
          }
        } catch {}
      }, 3000);
    },
    stopPolling() {
      if (this.pollingTimer) {
        clearInterval(this.pollingTimer);
        this.pollingTimer = null;
      }
    },
    async handleFollowUp() {
      if (!this.followUpContent) return;
      this.followUping = true;
      try {
        const res = await qaApi.followUp({
          questionId: this.question.id,
          content: this.followUpContent
        });
        if (res.code === 200) {
          this.followUpContent = '';
          this.loadDetail();
        }
      } finally {
        this.followUping = false;
      }
    },
    formatTime(time) {
      if (!time) return '';
      const d = new Date(time);
      return `${d.getFullYear()}/${d.getMonth()+1}/${d.getDate()} ${d.getHours()}:${String(d.getMinutes()).padStart(2,'0')}`;
    }
  }
};
</script>

<style scoped>
.back-btn { margin-bottom: 16px; }
.loading { padding: 40px; }
.error { margin-top: 40px; }

.question-card { margin-bottom: 20px; }

.q-title { font-size: 22px; margin-bottom: 12px; }

.q-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.q-user { font-weight: 500; }
.q-time { color: var(--text-secondary); }
.q-stat { display: flex; align-items: center; gap: 2px; }

.q-content {
  font-size: 15px;
  line-height: 1.8;
}

.section-title {
  font-size: 18px;
  margin: 20px 0 12px;
}

.no-answers { margin-bottom: 20px; }

.answers-section { margin-bottom: 20px; }

.answer-card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 12px;
}

.answer-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.answer-time { font-size: 13px; color: var(--text-secondary); }
.answer-duration { font-size: 12px; color: var(--text-secondary); margin-left: auto; }

.answer-content { font-size: 15px; line-height: 1.8; }

.follow-up-card { margin-bottom: 20px; }
</style>
