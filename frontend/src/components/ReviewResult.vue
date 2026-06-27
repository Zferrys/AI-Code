<template>
  <div class="review-result">
    <!-- 加载骨架屏 -->
    <div class="result-skeleton" v-if="loading">
      <div class="skeleton-score"></div>
      <div class="skeleton-card"></div>
      <div class="skeleton-card"></div>
    </div>

    <!-- 错误状态 -->
    <el-result v-else-if="error" icon="error" title="审查结果解析失败" subTitle="AI 返回数据格式异常">
      <template #extra>
        <el-button type="primary" size="small" @click="$emit('retry')">重新加载</el-button>
      </template>
    </el-result>

    <!-- 正常结果 -->
    <template v-else-if="result">
      <!-- 质量评分 -->
      <div class="score-section">
        <div class="score-circle" :class="scoreClass">
          <span class="score-value">{{ result.qualityScore || '--' }}</span>
          <span class="score-label">评分</span>
        </div>
        <div class="score-meta">
          <el-tag :type="statusTagType" size="small">{{ statusText }}</el-tag>
          <span class="meta-item" v-if="result.bugCount !== undefined">
            <i class="el-icon-warning"></i> 发现 {{ result.bugCount }} 个问题
          </span>
          <span class="meta-item" v-if="result.suggestionCount !== undefined">
            <i class="el-icon-s-promotion"></i> {{ result.suggestionCount }} 条优化建议
          </span>
          <span class="meta-item" v-if="result.aiResponseTime">
            <i class="el-icon-time"></i> {{ (result.aiResponseTime / 1000).toFixed(1) }}s
          </span>
        </div>
      </div>

      <!-- 审查结果 JSON 渲染 -->
      <div class="result-content" v-if="parsedResult">
        <!-- 总结 -->
        <el-card class="result-card" v-if="parsedResult.summary">
          <template #header><span>📋 审查总结</span></template>
          <markdown-renderer :content="parsedResult.summary" />
        </el-card>

        <!-- Bug 列表 -->
        <el-card class="result-card" v-if="parsedResult.bugs && parsedResult.bugs.length > 0">
          <template #header><span>🐛 潜在 Bug（{{ parsedResult.bugs.length }} 个）</span></template>
          <div v-for="(bug, i) in parsedResult.bugs" :key="i" class="issue-item">
            <div class="issue-header">
              <el-tag :type="severityTag(bug.severity)" size="mini">{{ bug.severity }}</el-tag>
              <span class="issue-location">{{ bug.location }}</span>
            </div>
            <p class="issue-desc">{{ bug.description }}</p>
          </div>
        </el-card>

        <!-- 代码坏味道 -->
        <el-card class="result-card" v-if="parsedResult.codeSmells && parsedResult.codeSmells.length > 0">
          <template #header><span>🔍 代码坏味道（{{ parsedResult.codeSmells.length }} 处）</span></template>
          <div v-for="(smell, i) in parsedResult.codeSmells" :key="i" class="issue-item">
            <div class="issue-header">
              <span class="issue-location">{{ smell.location }}</span>
            </div>
            <p class="issue-desc">{{ smell.description }}</p>
            <div class="issue-suggestion" v-if="smell.refactor">
              <span class="suggestion-label">重构建议：</span>{{ smell.refactor }}
            </div>
          </div>
        </el-card>

        <!-- 优化建议 -->
        <el-card class="result-card" v-if="parsedResult.optimizations && parsedResult.optimizations.length > 0">
          <template #header><span>⚡ 优化建议（{{ parsedResult.optimizations.length }} 条）</span></template>
          <div v-for="(opt, i) in parsedResult.optimizations" :key="i" class="issue-item">
            <div class="issue-header">
              <span class="issue-location">{{ opt.location }}</span>
            </div>
            <p class="issue-desc">{{ opt.description }}</p>
            <div class="issue-suggestion" v-if="opt.suggestion">
              <span class="suggestion-label">建议：</span>{{ opt.suggestion }}
            </div>
          </div>
        </el-card>

        <!-- 安全漏洞 -->
        <el-card class="result-card" v-if="parsedResult.security && parsedResult.security.length > 0">
          <template #header><span>🔒 安全漏洞（{{ parsedResult.security.length }} 个）</span></template>
          <div v-for="(sec, i) in parsedResult.security" :key="i" class="issue-item">
            <div class="issue-header">
              <el-tag type="danger" size="mini">{{ sec.risk || '高危' }}</el-tag>
              <span class="issue-location">{{ sec.location }}</span>
            </div>
            <p class="issue-desc">{{ sec.description }}</p>
          </div>
        </el-card>

        <!-- 最佳实践 -->
        <el-card class="result-card" v-if="parsedResult.bestPractices && parsedResult.bestPractices.length > 0">
          <template #header><span>🏆 最佳实践（{{ parsedResult.bestPractices.length }} 条）</span></template>
          <div v-for="(bp, i) in parsedResult.bestPractices" :key="i" class="issue-item">
            <div class="issue-header">
              <span class="issue-location bp-title">{{ bp.title }}</span>
            </div>
            <p class="issue-desc">{{ bp.description }}</p>
            <div class="issue-suggestion" v-if="bp.example">
              <span class="suggestion-label">示例：</span><code class="bp-example">{{ bp.example }}</code>
            </div>
          </div>
        </el-card>

        <el-empty v-if="!parsedResult.bugs && !parsedResult.optimizations && !parsedResult.security && !parsedResult.codeSmells && !parsedResult.bestPractices && !parsedResult.summary"
          description="暂无审查数据" />
      </div>
    </template>
  </div>
</template>

<script>
import MarkdownRenderer from './MarkdownRenderer.vue';

export default {
  name: 'ReviewResult',
  components: { MarkdownRenderer },
  props: {
    result: { type: Object, default: null },
    loading: { type: Boolean, default: false },
    error: { type: Boolean, default: false }
  },
  computed: {
    parsedResult() {
      if (!this.result || !this.result.reviewResult) return null;
      try {
        let text = String(this.result.reviewResult);
        // 清理可能的 markdown 标记
        text = text.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim();
        // 尝试提取 JSON 对象（如果 AI 掺杂了其他文本）
        const firstBrace = text.indexOf('{');
        const lastBrace = text.lastIndexOf('}');
        if (firstBrace !== -1 && lastBrace > firstBrace) {
          text = text.substring(firstBrace, lastBrace + 1);
        }
        return JSON.parse(text);
      } catch {
        // 非 JSON 格式，返回提示
        return { summary: 'AI 返回结果格式异常，无法解析。请返回列表页重试。' };
      }
    },
    scoreClass() {
      if (!this.result || !this.result.qualityScore) return '';
      const s = this.result.qualityScore;
      if (s >= 80) return 'score-high';
      if (s >= 60) return 'score-mid';
      return 'score-low';
    },
    statusText() {
      if (!this.result) return '';
      const map = { PENDING: '等待中', PROCESSING: '审查中', COMPLETED: '已完成', FAILED: '失败' };
      return map[this.result.status] || this.result.status;
    },
    statusTagType() {
      const map = { PENDING: 'info', PROCESSING: 'warning', COMPLETED: 'success', FAILED: 'danger' };
      return map[this.result.status] || 'info';
    }
  },
  methods: {
    severityTag(severity) {
      const map = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' };
      return map[severity] || 'info';
    }
  }
};
</script>

<style scoped>
.score-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 20px;
  padding: 20px;
  background: var(--card-bg);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.score-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
}

.score-high { background: linear-gradient(135deg, #059669, #10b981); }
.score-mid { background: linear-gradient(135deg, #d97706, #f59e0b); }
.score-low { background: linear-gradient(135deg, #dc2626, #ef4444); }

.score-value { font-size: 28px; line-height: 1; }
.score-label { font-size: 12px; opacity: 0.9; }

.score-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  color: var(--text-secondary);
}

.meta-item { display: flex; align-items: center; gap: 4px; }

.result-content { display: flex; flex-direction: column; gap: 16px; }

.result-card { margin-bottom: 0; }

.summary-text {
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-primary);
  white-space: pre-wrap;
}

.issue-item {
  padding: 12px 0;
  border-bottom: 1px solid var(--border-color);
}

.issue-item:last-child { border-bottom: none; }

.issue-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.issue-location {
  font-size: 13px;
  color: var(--text-secondary);
  font-family: monospace;
}

.issue-desc {
  font-size: 14px;
  color: var(--text-primary);
  margin-top: 4px;
  line-height: 1.6;
}

.issue-suggestion {
  margin-top: 6px;
  padding: 8px 12px;
  background: #f0f9ff;
  border-radius: 4px;
  font-size: 13px;
  color: #1a56db;
  line-height: 1.5;
}

.suggestion-label { font-weight: 600; }

.bp-title {
  font-weight: 600;
  color: var(--primary-color, #1a56db);
}

.bp-example {
  background: #f1f5f9;
  padding: 1px 6px;
  border-radius: 4px;
  font-size: 13px;
  color: #e11d48;
}

/* 骨架屏 */
.result-skeleton {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skeleton-score, .skeleton-card {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
  border-radius: 8px;
}

.skeleton-score {
  width: 200px;
  height: 80px;
}

.skeleton-card {
  height: 120px;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}
</style>
