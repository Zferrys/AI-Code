<template>
  <div class="admin-page">
    <div class="admin-page-header">
      <h3 class="admin-page-title">标签管理</h3>
      <p class="admin-page-subtitle">查看系统已有标签及其分类</p>
    </div>
    <div class="admin-table-wrap">
      <el-table :data="paginatedTags" v-loading="loading" stripe size="medium">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="category" label="分类">
          <template slot-scope="{row}">
            <el-tag size="mini" effect="plain">{{ row.category || '未分类' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="色标" width="80" align="center">
          <template slot-scope="{row}">
            <span class="color-dot" :style="{ background: row.color || '#3b82f6' }"></span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
      </el-table>
      <div v-if="tags.length > pageSize" class="table-pagination">
        <el-pagination
          background layout="prev, pager, next"
          :total="tags.length" :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="handlePageChange">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import { tagApi } from '../../api';
export default {
  name: 'AdminTags',
  data() {
    return {
      tags: [],
      loading: true,
      currentPage: 1,
      pageSize: 10
    };
  },
  computed: {
    paginatedTags() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.tags.slice(start, start + this.pageSize);
    }
  },
  created() { this.load(); },
  methods: {
    async load() {
      this.loading = true;
      const res = await tagApi.getAll();
      if (res.code === 200) this.tags = res.data || [];
      this.loading = false;
    },
    handlePageChange(page) {
      this.currentPage = page;
    }
  }
};
</script>

<style scoped>
.color-dot {
  display: inline-block;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  vertical-align: middle;
  border: 2px solid rgba(255,255,255,0.8);
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}
.color-dot:hover {
  transform: scale(1.3);
}
.table-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
