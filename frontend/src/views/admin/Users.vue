<template>
  <div class="admin-page">
    <div class="admin-page-header">
      <h3 class="admin-page-title">用户管理</h3>
      <p class="admin-page-subtitle">管理平台注册用户，支持启用/禁用操作</p>
    </div>
    <div class="admin-table-wrap">
      <el-table :data="users" v-loading="loading" stripe size="medium">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色" width="80">
          <template slot-scope="{row}">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="mini" effect="plain">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{row}">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="mini" effect="plain">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template slot-scope="{row}">
            <el-button size="mini"
              :type="row.status === 1 ? 'warning' : 'success'"
              :plain="true"
              @click="toggleStatus(row)"
              :disabled="row.role === 'ADMIN'">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
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
  name: 'AdminUsers',
  data() {
    return {
      users: [],
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
      const res = await adminApi.getUsers({ page: this.currentPage, pageSize: this.pageSize });
      if (res.code === 200) {
        this.users = res.data.list || [];
        this.total = res.data.total || 0;
      }
      this.loading = false;
    },
    async handlePageChange(page) {
      this.currentPage = page;
      await this.load();
    },
    async toggleStatus(row) {
      await adminApi.updateUserStatus(row.id, row.status === 1 ? 0 : 1);
      this.$message.success('更新成功');
      this.load();
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
