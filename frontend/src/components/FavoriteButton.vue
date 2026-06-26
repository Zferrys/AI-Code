<template>
  <el-button :type="favorited ? 'warning' : 'default'" :icon="favorited ? 'el-icon-star-on' : 'el-icon-star-off'"
    size="small" @click="toggle" :loading="loading">
    {{ favorited ? '已收藏' : '收藏' }}
  </el-button>
</template>

<script>
import { favoriteApi } from '../api';

export default {
  name: 'FavoriteButton',
  props: { contentType: String, contentId: Number },
  data() { return { favorited: false, loading: false }; },
  created() { this.check(); },
  methods: {
    async check() {
      try {
        const res = await favoriteApi.check({ contentType: this.contentType, contentId: this.contentId });
        if (res.code === 200) this.favorited = res.data.favorited;
      } catch {}
    },
    async toggle() {
      this.loading = true;
      try {
        if (this.favorited) {
          await favoriteApi.remove({ contentType: this.contentType, contentId: this.contentId });
          this.favorited = false;
        } else {
          await favoriteApi.add({ contentType: this.contentType, contentId: this.contentId });
          this.favorited = true;
        }
      } finally { this.loading = false; }
    }
  }
};
</script>
