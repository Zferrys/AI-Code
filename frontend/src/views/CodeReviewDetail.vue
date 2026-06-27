<template>
  <div class="code-review-detail">
    <el-button type="text" @click="$router.push('/code-review')" class="back-btn">
      ← 返回列表
    </el-button>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="error" class="error">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra>
          <el-button type="primary" @click="loadDetail">重新加载</el-button>
        </template>
      </el-result>
    </div>

    <template v-else-if="review">
      <h2 class="page-title">{{ review.title }}</h2>
      <review-result :result="review" />
    </template>
  </div>
</template>

<script>
import ReviewResult from '../components/ReviewResult.vue';
import { codeReviewApi } from '../api';

export default {
  name: 'CodeReviewDetail',
  components: { ReviewResult },
  data() {
    return {
      review: null,
      loading: true,
      error: ''
    };
  },
  created() { this.loadDetail(); },
  methods: {
    async loadDetail() {
      this.loading = true;
      this.error = '';
      const id = Number(this.$route.params.id);
      try {
        const res = await codeReviewApi.getDetail(id);
        if (res.code === 200 && res.data) {
          this.review = res.data;
        } else {
          this.error = res.message || '审查记录不存在';
        }
      } catch (e) {
        this.error = '网络请求失败';
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.back-btn { margin-bottom: 16px; }
.loading { padding: 40px; }
.error { margin-top: 40px; }
</style>
