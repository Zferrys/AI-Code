<template>
  <div class="admin-page">
    <div class="admin-page-header">
      <h3 class="admin-page-title">操作日志</h3>
      <p class="admin-page-subtitle">查看平台操作审计记录</p>
    </div>
    <div class="admin-table-wrap">
      <el-table :data="logs" v-loading="loading" stripe size="medium">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="actionType" label="操作" width="120" />
        <el-table-column prop="targetType" label="目标类型" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="ipAddress" label="IP" width="140" />
        <el-table-column prop="createTime" label="时间" width="160" />
      </el-table>
      <div v-if="total > pageSize" class="table-pagination">
        <el-pagination
          background layout="prev, pager, next"
          :total="total" :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="handlePageChange">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import { adminApi } from '../../api';
export default {
  name: 'AdminLogs',
  data() {
    return {
      logs: [],
      loading: true,
      currentPage: 1,
      pageSize: 10,
      total: 0
    };
  },
  created() { this.load(); },
  methods: {
    async load() {
      this.loading = true;
      const res = await adminApi.getLogs({ page: this.currentPage, pageSize: this.pageSize });
      if (res.code === 200) {
        this.logs = res.data.list || [];
        this.total = res.data.total || 0;
      }
      this.loading = false;
    },
    async handlePageChange(page) {
      this.currentPage = page;
      await this.load();
    }
  }
};
</script>

<style scoped>
.table-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
