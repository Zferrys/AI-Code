<template>
  <div class="ai-rankings">
    <div class="page-header">
      <div>
        <h2 class="page-title">🤖 AI 模型排行榜</h2>
        <p class="page-subtitle">基于 Chatbot Arena ELO 评分的大语言模型综合排名，数据不定期更新</p>
      </div>
      <div class="header-actions">
        <span class="update-info" v-if="lastUpdated">
          <i class="el-icon-time"></i> 更新于 {{ lastUpdated }}
        </span>
        <el-button size="small" :loading="refreshing" icon="el-icon-refresh" @click="handleRefresh">
          刷新
        </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 错误状态 -->
    <el-result v-else-if="error" icon="error" title="加载失败" :sub-title="error">
      <template #extra>
        <el-button type="primary" @click="loadData">重新加载</el-button>
      </template>
    </el-result>

    <!-- 空状态 -->
    <el-empty v-else-if="!rankings.length" description="暂无排名数据" />

    <!-- 排名表格 -->
    <div v-else class="rankings-content">
      <!-- 统计卡片 -->
      <div class="stats-bar">
        <div class="stat-item">
          <span class="stat-num">{{ rankings.length }}</span>
          <span class="stat-lbl">模型数量</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-num">{{ topElo }}</span>
          <span class="stat-lbl">最高 ELO</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-num">{{ totalVotes }}</span>
          <span class="stat-lbl">总投票数</span>
        </div>
      </div>

      <!-- 分类过滤 -->
      <div class="filter-bar">
        <span class="filter-label">分类：</span>
        <el-radio-group v-model="categoryFilter" size="small">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="chat">对话</el-radio-button>
          <el-radio-button label="coding">编程</el-radio-button>
          <el-radio-button label="reasoning">推理</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 表格 -->
      <el-table :data="paginatedRankings" stripe style="width: 100%" class="rankings-table" :row-class-name="() => 'row-scale-hover'">
        <el-table-column label="排名" width="70" align="center">
          <template slot-scope="{ row, $index }">
            <span :class="['rank-badge', rankClass(pageOffset + $index)]">{{ pageOffset + $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="模型名称" min-width="200">
          <template slot-scope="{ row }">
            <div class="model-cell">
              <span class="model-name">{{ row.modelName }}</span>
              <el-tag v-if="row.category === 'coding'" size="mini" type="warning" class="model-tag">编程</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="提供商" width="140">
          <template slot-scope="{ row }">
            <span class="provider-name">{{ row.provider }}</span>
          </template>
        </el-table-column>
        <el-table-column label="ELO 评分" width="120" sortable prop="eloScore">
          <template slot-scope="{ row }">
            <span :class="'elo-score elo-' + eloLevel(row.eloScore)">
              {{ row.eloScore.toFixed ? row.eloScore.toFixed(0) : row.eloScore }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="投票数" width="100" sortable prop="votes">
          <template slot-scope="{ row }">
            <span class="votes-num">{{ formatVotes(row.votes) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="displayRankings.length > pageSize" class="table-pagination">
        <el-pagination
          background layout="prev, pager, next"
          :total="displayRankings.length" :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="handlePageChange">
        </el-pagination>
      </div>

      <div class="table-footer">
        <span class="footer-note">数据来源：Chatbot Arena Leaderboard &amp; 各大评测基准</span>
      </div>
    </div>
  </div>
</template>

<script>
import { rankingApi } from '../api';

export default {
  name: 'AIRankings',
  data() {
    return {
      rankings: [],
      loading: true,
      refreshing: false,
      error: '',
      categoryFilter: '',
      lastUpdated: '',
      currentPage: 1,
      pageSize: 15
    };
  },
  computed: {
    /** 按分类过滤后的完整列表（用于统计和分页总数） */
    displayRankings() {
      if (!this.categoryFilter) return this.rankings;
      return this.rankings.filter(r => r.category === this.categoryFilter);
    },
    /** 当前页显示的数据 */
    paginatedRankings() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.displayRankings.slice(start, start + this.pageSize);
    },
    /** 当前页第一条的全局序号偏移 */
    pageOffset() {
      return (this.currentPage - 1) * this.pageSize;
    },
    topElo() {
      if (!this.rankings.length) return 0;
      return Math.max(...this.rankings.map(r => r.eloScore));
    },
    totalVotes() {
      return this.rankings.reduce((s, r) => s + (r.votes || 0), 0);
    }
  },
  created() { this.loadData(); },
  methods: {
    async loadData() {
      this.loading = true;
      this.error = '';
      try {
        const res = await rankingApi.getRankings();
        if (res.code === 200 && res.data) {
          this.rankings = res.data;
          if (res.data.length > 0 && res.data[0].lastUpdated) {
            this.lastUpdated = res.data[0].lastUpdated;
          }
        } else {
          this.error = res.message || '加载失败';
        }
      } catch {
        this.error = '网络请求失败';
      } finally {
        this.loading = false;
      }
    },
    async handleRefresh() {
      this.refreshing = true;
      try {
        const res = await rankingApi.refreshRankings();
        if (res.code === 200 && res.data) {
          this.rankings = res.data;
          this.$message.success('排名已刷新');
        }
      } catch {
        this.$message.error('刷新失败');
      } finally {
        this.refreshing = false;
      }
    },
    rankClass(index) {
      if (index === 0) return 'rank-gold';
      if (index === 1) return 'rank-silver';
      if (index === 2) return 'rank-bronze';
      return '';
    },
    eloLevel(score) {
      if (score >= 1300) return 'high';
      if (score >= 1200) return 'mid';
      return 'low';
    },
    formatVotes(votes) {
      if (!votes) return '0';
      if (votes >= 10000) return (votes / 10000).toFixed(1) + 'w';
      if (votes >= 1000) return (votes / 1000).toFixed(1) + 'k';
      return String(votes);
    },
    handlePageChange(page) {
      this.currentPage = page;
    }
  },
  watch: {
    /** 切换分类筛选时回到第一页 */
    categoryFilter() {
      this.currentPage = 1;
    }
  }
};
</script>

<style scoped>
.ai-rankings {
  max-width: var(--content-max-width, 1200px);
  margin: 0 auto;
  padding: 24px 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.page-title { font-size: 26px; font-weight: 700; margin-bottom: 4px; }
.page-subtitle { font-size: 14px; color: var(--text-secondary, #475569); }

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.update-info {
  font-size: 13px;
  color: var(--text-tertiary, #94a3b8);
  display: flex;
  align-items: center;
  gap: 4px;
}

.loading-state { padding: 40px; }

/* 统计卡片 */
.stats-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
  padding: 20px 32px;
  background: white;
  border-radius: 14px;
  border: 1px solid var(--border, #e2e8f0);
  margin-bottom: 20px;
}

.stat-item { text-align: center; }
.stat-num { display: block; font-size: 26px; font-weight: 800; color: var(--primary, #1a56db); }
.stat-lbl { font-size: 12px; color: var(--text-tertiary, #94a3b8); }
.stat-divider { width: 1px; height: 36px; background: var(--border, #e2e8f0); }

/* 筛选 */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}
.filter-label { font-size: 13px; color: var(--text-secondary, #475569); }

/* 表格 */
.rankings-table { border-radius: 10px; overflow: hidden; }

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  font-size: 13px;
  font-weight: 700;
  color: var(--text-secondary, #475569);
  background: #f1f5f9;
}

.rank-gold { background: linear-gradient(135deg, #f59e0b, #fbbf24); color: white; }
.rank-silver { background: linear-gradient(135deg, #94a3b8, #cbd5e1); color: white; }
.rank-bronze { background: linear-gradient(135deg, #b45309, #d97706); color: white; }

.model-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.model-name { font-weight: 500; color: var(--text-primary, #0f172a); }
.model-tag { flex-shrink: 0; }

.provider-name { font-size: 14px; color: var(--text-secondary, #475569); }

.elo-score {
  font-weight: 700;
  font-size: 16px;
}
.elo-high { color: var(--success, #059669); }
.elo-mid { color: var(--warning, #d97706); }
.elo-low { color: var(--text-secondary, #475569); }

.votes-num { font-size: 14px; color: var(--text-secondary, #475569); }

.table-footer {
  text-align: center;
  padding: 16px;
  font-size: 12px;
  color: var(--text-tertiary, #94a3b8);
}

.table-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式 */
@media (max-width: 600px) {
  .page-header { flex-direction: column; }
  .stats-bar { flex-wrap: wrap; gap: 16px; }
  .stat-divider:nth-child(4) { display: none; }
}
</style>
