<template>
  <div class="profile">
    <!-- 背景装饰 -->
    <div class="profile-bg">
      <div class="bg-orb bg-orb-1"></div>
      <div class="bg-orb bg-orb-2"></div>
      <div class="bg-orb bg-orb-3"></div>
    </div>

    <div class="page-header animate-fade-in-up">
      <h2 class="page-title">个人中心</h2>
      <p class="page-subtitle">管理你的个人资料和技术标签</p>
    </div>

    <el-row :gutter="24">
      <!-- 左侧用户信息 -->
      <el-col :xs="24" :sm="24" :md="8">
        <div class="profile-card user-sidebar animate-fade-in-up" style="animation-delay: 0.1s">
          <div class="user-card">
            <div class="user-avatar-wrap" @click="triggerUpload" ref="avatarWrap"
                 @mousemove="onAvatarMouseMove" @mouseleave="onAvatarMouseLeave">
              <!-- 加载骨架 -->
              <div v-if="loadingAvatar" class="avatar-skeleton"></div>
              <template v-else>
                <el-avatar :size="88" class="user-avatar" v-if="!hasAvatar">
                  {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <el-image v-else :src="avatarUrl" class="user-avatar-img" :size="88" fit="cover">
                  <div slot="error" class="image-slot">
                    <el-avatar :size="88" class="user-avatar">
                      {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
                    </el-avatar>
                  </div>
                </el-image>
              </template>
              <input ref="fileInput" type="file" accept="image/jpeg,image/png,image/gif,image/webp"
                     class="avatar-file-input" @change="handleAvatarUpload" />
              <div class="avatar-overlay" :class="{ 'is-uploading': uploading }">
                <i :class="uploading ? 'el-icon-loading' : 'el-icon-camera'"></i>
                <span>{{ uploading ? '上传中...' : '更换头像' }}</span>
              </div>
              <div class="avatar-ring"></div>
              <div class="avatar-glow-ring"></div>
            </div>

            <h3 class="user-name">{{ userInfo?.username || '—' }}</h3>
            <span class="user-role-badge"
                  :class="userInfo?.role === 'ADMIN' ? 'role-admin' : 'role-user'">
              <i :class="userInfo?.role === 'ADMIN' ? 'el-icon-s-custom' : 'el-icon-user'"></i>
              {{ userInfo?.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </span>
            <p class="user-email" v-if="userInfo?.email">
              <i class="el-icon-message"></i> {{ userInfo.email }}
            </p>

            <el-divider class="user-divider" />

            <div class="user-stats">
              <div class="user-stat-item">
                <span class="user-stat-num" ref="statReview">{{ animatedStats.review }}</span>
                <span class="user-stat-label">代码审查</span>
              </div>
              <div class="user-stat-divider"></div>
              <div class="user-stat-item">
                <span class="user-stat-num" ref="statQa">{{ animatedStats.qa }}</span>
                <span class="user-stat-label">问答</span>
              </div>
              <div class="user-stat-divider"></div>
              <div class="user-stat-item">
                <span class="user-stat-num" ref="statTags">{{ animatedStats.tags }}</span>
                <span class="user-stat-label">技术标签</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 右侧编辑 -->
      <el-col :xs="24" :sm="24" :md="16">
        <!-- 编辑资料 -->
        <div class="profile-card animate-fade-in-up" style="animation-delay: 0.2s">
          <div class="profile-card-header">
            <div class="header-icon-wrap">
              <i class="el-icon-edit"></i>
            </div>
            <span>编辑资料</span>
          </div>
          <el-form :model="editForm" label-width="80px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="真实姓名">
                  <el-input v-model="editForm.realName" class="pi-input" placeholder="你的真实姓名">
                    <i slot="prefix" class="el-icon-user"></i>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱">
                  <el-input v-model="editForm.email" class="pi-input" placeholder="请输入邮箱地址">
                    <i slot="prefix" class="el-icon-message"></i>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="手机">
                  <el-input v-model="editForm.phone" class="pi-input" placeholder="请输入手机号码">
                    <i slot="prefix" class="el-icon-phone"></i>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="学校">
                  <el-input v-model="editForm.school" class="pi-input" placeholder="所在学校">
                    <i slot="prefix" class="el-icon-school"></i>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="专业">
                  <el-input v-model="editForm.major" class="pi-input" placeholder="所学专业">
                    <i slot="prefix" class="el-icon-reading"></i>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="年级">
                  <el-select v-model="editForm.grade" class="pi-input" placeholder="选择年级" clearable>
                    <el-option label="大一" value="大一" />
                    <el-option label="大二" value="大二" />
                    <el-option label="大三" value="大三" />
                    <el-option label="大四" value="大四" />
                    <el-option label="研一" value="研一" />
                    <el-option label="研二" value="研二" />
                    <el-option label="研三" value="研三" />
                    <el-option label="已毕业" value="已毕业" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="GitHub">
                  <el-input v-model="editForm.githubUrl" class="pi-input" placeholder="https://github.com/yourname">
                    <i slot="prefix" class="el-icon-link"></i>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="技术等级">
                  <span class="experience-tag" :class="'level-' + (userInfo.experienceLevel || 'BEGINNER').toLowerCase()">
                    {{ levelLabel }}
                  </span>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="个人简介">
              <el-input v-model="editForm.bio" type="textarea" class="pi-input"
                        :rows="3" placeholder="介绍一下自己，让其他同学了解你" maxlength="200"
                        show-word-limit />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="pi-save-btn" @click="handleUpdate"
                         :loading="saving">
                <i class="el-icon-check"></i> 保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 技术标签 -->
        <div class="profile-card mt-20 animate-fade-in-up" style="animation-delay: 0.3s">
          <div class="profile-card-header">
            <div class="header-icon-wrap tag-icon-wrap">
              <i class="el-icon-collection-tag"></i>
            </div>
            <span>技术标签 <em class="tag-count">({{ userTags.length }})</em></span>
          </div>
          <div class="tags-area">
            <div v-if="userTags.length === 0" class="tags-empty">
              <div class="tags-empty-icon">
                <i class="el-icon-collection-tag"></i>
              </div>
              <p>还没有添加技术标签</p>
              <p class="tags-hint">添加标签后，系统会为你推荐更精准的学习路径</p>
            </div>
            <transition-group name="tag" tag="div" class="tags-list">
              <span v-for="tag in userTags" :key="tag.id" class="user-tag">
                {{ tag.name }}
                <i class="el-icon-close tag-remove" @click="handleRemoveTag(tag.id)"></i>
              </span>
            </transition-group>
            <button class="add-tag-btn" @click="showTagDialog = true">
              <i class="el-icon-plus"></i> 添加标签
            </button>
          </div>
        </div>

        <!-- 学习进度 -->
        <div class="profile-card mt-20 animate-fade-in-up" style="animation-delay: 0.4s"
             @click="$router.push('/learning-paths')">
          <div class="profile-card-header">
            <div class="header-icon-wrap progress-icon-wrap">
              <i class="el-icon-notebook-2"></i>
            </div>
            <span>学习进度</span>
          </div>
          <div class="progress-placeholder">
            <div class="progress-arrow-circle">
              <i class="el-icon-arrow-right"></i>
            </div>
            <div class="progress-text">
              <router-link to="/learning-paths" class="progress-link">浏览学习路径</router-link>
              <span class="progress-hint">开始你的学习之旅</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 添加标签对话框 -->
    <el-dialog title="添加技术标签" :visible.sync="showTagDialog" width="400px" top="30vh"
               class="tag-dialog">
      <el-select v-model="selectedTagId" placeholder="选择标签" style="width: 100%">
        <el-option v-for="tag in allTags" :key="tag.id" :label="tag.name" :value="tag.id" />
      </el-select>
      <span slot="footer">
        <el-button @click="showTagDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddTag" :loading="addingTag">确认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { userApi, tagApi } from '../api';

export default {
  name: 'Profile',
  data() {
    return {
      userInfo: {},
      editForm: { email: '', phone: '' },
      userTags: [],
      allTags: [],
      showTagDialog: false,
      selectedTagId: null,
      uploading: false,
      saving: false,
      addingTag: false,
      loadingAvatar: false,
      animatedStats: { review: 0, qa: 0, tags: 0 },
      statsLoaded: false
    };
  },
  computed: {
    hasAvatar() {
      const av = this.userInfo?.avatar;
      return av && av !== '/default.png' && av !== '';
    },
    avatarUrl() {
      const av = this.userInfo?.avatar;
      if (!av || av === '/default.png') return '';
      if (av.startsWith('http')) return av;
      return av.startsWith('/') ? av : '/' + av;
    },
    levelLabel() {
      const map = {
        'BEGINNER': '🏁 入门新手',
        'INTERMEDIATE': '📈 初级开发者',
        'ADVANCED': '🚀 中级开发者',
        'EXPERT': '🏆 高级开发者'
      };
      return map[this.userInfo?.experienceLevel] || '🏁 入门新手';
    }
  },
  created() { this.loadData(); },
  mounted() {
    this.$nextTick(() => {
      this.initStatCounter();
    });
  },
  methods: {
    triggerUpload() {
      this.$refs.fileInput.click();
    },
    async handleAvatarUpload(e) {
      const file = e.target.files?.[0];
      if (!file) return;

      const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
      if (!allowedTypes.includes(file.type)) {
        this.$message.warning('仅支持 JPG/PNG/GIF/WebP 格式的图片');
        return;
      }
      if (file.size > 5 * 1024 * 1024) {
        this.$message.warning('图片大小不能超过 5MB');
        return;
      }

      this.uploading = true;
      this.loadingAvatar = true;
      try {
        const formData = new FormData();
        formData.append('file', file);
        const res = await userApi.uploadAvatar(formData);
        if (res.code === 200) {
          this.$message.success('头像上传成功 🎉');
          await this.loadData();
          await this.$store.dispatch('fetchUserInfo');
        } else {
          this.$message.error(res.message || '头像上传失败');
        }
      } catch (err) {
        this.$message.error('头像上传失败，请检查网络连接');
      } finally {
        this.uploading = false;
        this.loadingAvatar = false;
        e.target.value = '';
      }
    },
    async loadData() {
      try {
        const [userRes, tagRes, allTagRes] = await Promise.all([
          userApi.getUserInfo(),
          userApi.getTechTags(),
          tagApi.getAll()
        ]);
        if (userRes.code === 200) {
          this.userInfo = userRes.data;
          this.editForm = {
            email: userRes.data.email || '',
            phone: userRes.data.phone || '',
            realName: userRes.data.realName || '',
            school: userRes.data.school || '',
            major: userRes.data.major || '',
            grade: userRes.data.grade || '',
            bio: userRes.data.bio || '',
            githubUrl: userRes.data.githubUrl || ''
          };
          this.statsLoaded = true;
        }
        if (tagRes.code === 200) this.userTags = tagRes.data;
        if (allTagRes.code === 200) this.allTags = allTagRes.data;
      } catch {}
    },
    async handleUpdate() {
      this.saving = true;
      try {
        const res = await userApi.updateUserInfo(this.editForm);
        if (res.code === 200) {
          this.$message.success('保存成功 ✓');
          await this.loadData();
        }
      } finally {
        this.saving = false;
      }
    },
    async handleAddTag() {
      if (!this.selectedTagId) return;
      this.addingTag = true;
      try {
        const res = await userApi.addTechTag(this.selectedTagId);
        if (res.code === 200) {
          this.$message.success('标签添加成功');
          this.showTagDialog = false;
          this.selectedTagId = null;
          await this.loadData();
        }
      } finally {
        this.addingTag = false;
      }
    },
    async handleRemoveTag(tagId) {
      const res = await userApi.removeTechTag(tagId);
      if (res.code === 200) {
        this.$message.success('标签已删除');
        await this.loadData();
      }
    },

    // ---- 3D 头像倾斜效果 ----
    onAvatarMouseMove(e) {
      const wrap = this.$refs.avatarWrap;
      if (!wrap) return;
      const rect = wrap.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;
      const centerX = rect.width / 2;
      const centerY = rect.height / 2;
      const rotateX = ((y - centerY) / centerY) * -12;
      const rotateY = ((x - centerX) / centerX) * 12;
      wrap.style.transform = `perspective(400px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
    },
    onAvatarMouseLeave() {
      const wrap = this.$refs.avatarWrap;
      if (!wrap) return;
      wrap.style.transform = 'perspective(400px) rotateX(0deg) rotateY(0deg)';
    },

    // ---- 统计数字动画 ----
    initStatCounter() {
      const targets = {
        review: this.userInfo?.reviewCount || 0,
        qa: this.userInfo?.qaCount || 0,
        tags: this.userTags.length || 0
      };
      // 如果数据还未加载，用 0
      if (!this.statsLoaded && !this.userInfo?.reviewCount) return;
      // 使用 requestAnimationFrame 做计数动画
      const duration = 1200;
      const start = performance.now();
      const animate = (now) => {
        const elapsed = now - start;
        const progress = Math.min(elapsed / duration, 1);
        // easeOutCubic
        const ease = 1 - Math.pow(1 - progress, 3);
        this.animatedStats.review = Math.floor(targets.review * ease);
        this.animatedStats.qa = Math.floor(targets.qa * ease);
        this.animatedStats.tags = Math.floor(targets.tags * ease);
        if (progress < 1) {
          requestAnimationFrame(animate);
        }
      };
      requestAnimationFrame(animate);
    }
  },
  watch: {
    userTags() {
      if (this.statsLoaded) this.initStatCounter();
    },
    'userInfo.reviewCount'() {
      if (this.statsLoaded) this.initStatCounter();
    },
    'userInfo.qaCount'() {
      if (this.statsLoaded) this.initStatCounter();
    },
    statsLoaded(val) {
      if (val) this.initStatCounter();
    }
  }
};
</script>

<style scoped>
.profile {
  max-width: var(--content-max-width, 1200px);
  margin: 0 auto;
  padding: 24px 20px;
  position: relative;
}

/* ===== 背景装饰 ===== */
.profile-bg {
  position: fixed;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}
.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.12;
}
.bg-orb-1 {
  width: 500px; height: 500px;
  background: linear-gradient(135deg, #1a56db, #3b82f6);
  top: -150px; right: -80px;
  animation: orbFloat 12s ease-in-out infinite;
}
.bg-orb-2 {
  width: 400px; height: 400px;
  background: linear-gradient(135deg, #0ea5e9, #38bdf8);
  bottom: -120px; left: -80px;
  animation: orbFloat 16s ease-in-out infinite reverse;
}
.bg-orb-3 {
  width: 300px; height: 300px;
  background: linear-gradient(135deg, #059669, #10b981);
  top: 40%; right: 10%;
  animation: orbFloat 14s ease-in-out infinite 2s;
}

@keyframes orbFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-40px) scale(1.15); }
}

/* ===== 页头 ===== */
.page-header {
  margin-bottom: 28px;
  position: relative;
  z-index: 1;
}
.page-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-primary, #0f172a);
  letter-spacing: -0.5px;
}
.page-subtitle {
  font-size: 14px;
  color: var(--text-secondary, #475569);
  margin-top: 4px;
}

/* ===== 通用卡片 ===== */
.profile-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 1;
  overflow: hidden;
}
.profile-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--primary, #1a56db), #0ea5e9, transparent);
  opacity: 0;
  transition: opacity 0.5s;
}
.profile-card:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: rgba(26, 86, 219, 0.15);
}
.profile-card:hover::before {
  opacity: 1;
}

.profile-card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary, #0f172a);
  margin-bottom: 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
}
.header-icon-wrap {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #eff6ff, #eef2ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: var(--primary, #1a56db);
  transition: all 0.3s;
}
.profile-card:hover .header-icon-wrap {
  background: linear-gradient(135deg, #dbeafe, #e0e7ff);
  transform: scale(1.05);
}
.tag-icon-wrap {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #d97706;
}
.profile-card:hover .tag-icon-wrap {
  background: linear-gradient(135deg, #fde68a, #fcd34d);
}
.progress-icon-wrap {
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  color: #059669;
}
.profile-card:hover .progress-icon-wrap {
  background: linear-gradient(135deg, #a7f3d0, #6ee7b7);
}

.tag-count {
  font-style: normal;
  font-weight: 400;
  color: var(--text-tertiary, #94a3b8);
  font-size: 14px;
}

/* ===== 用户侧边栏 ===== */
.user-sidebar {
  text-align: center;
  background: rgba(255, 255, 255, 0.88);
}

/* 头像 3D 交互 */
.user-avatar-wrap {
  position: relative;
  display: inline-block;
  margin-bottom: 18px;
  cursor: pointer;
  transition: transform 0.3s ease;
  transform-style: preserve-3d;
  will-change: transform;
}

.user-avatar {
  background: linear-gradient(135deg, #1a56db, #0ea5e9) !important;
  border: 3px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 20px rgba(26, 86, 219, 0.25);
  position: relative;
  z-index: 1;
  font-weight: 700;
  font-size: 28px !important;
}
.user-avatar-img {
  width: 88px !important;
  height: 88px !important;
  border-radius: 50% !important;
  border: 3px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 20px rgba(26, 86, 219, 0.25);
  position: relative;
  z-index: 1;
  display: block;
  object-fit: cover;
}

.avatar-skeleton {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: linear-gradient(90deg, #e2e8f0 25%, #f1f5f9 37%, #e2e8f0 63%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border: 3px solid rgba(255, 255, 255, 0.9);
}
@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.avatar-file-input { display: none; }

.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s, background 0.3s;
  z-index: 2;
  cursor: pointer;
  backdrop-filter: blur(4px);
}
.avatar-overlay i {
  font-size: 22px;
  margin-bottom: 2px;
  transition: transform 0.3s;
}
.avatar-overlay.is-uploading {
  opacity: 1;
  background: rgba(0, 0, 0, 0.6);
}
.user-avatar-wrap:hover .avatar-overlay {
  opacity: 1;
}
.user-avatar-wrap:hover .avatar-overlay i {
  transform: scale(1.1);
}

.avatar-ring {
  position: absolute;
  inset: -6px;
  border-radius: 50%;
  border: 2px solid rgba(226, 232, 240, 0.5);
  z-index: 0;
}

.avatar-glow-ring {
  position: absolute;
  inset: -12px;
  border-radius: 50%;
  border: 1.5px solid transparent;
  background: linear-gradient(135deg, #1a56db, #0ea5e9, #1a56db) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.5s;
  z-index: 0;
}
.user-avatar-wrap:hover .avatar-glow-ring {
  opacity: 1;
  animation: glowRotate 3s linear infinite;
}
@keyframes glowRotate {
  to { transform: rotate(360deg); }
}

.user-name {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 6px;
  color: var(--text-primary, #0f172a);
}

.user-role-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  padding: 3px 14px;
  border-radius: 20px;
  font-weight: 500;
  margin-bottom: 10px;
  transition: all 0.3s;
}
.role-user {
  background: #eff6ff;
  color: var(--primary, #1a56db);
}
.role-admin {
  background: #fef3c7;
  color: #d97706;
}

.user-email {
  font-size: 13px;
  color: var(--text-secondary, #475569);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
.user-email i {
  font-size: 14px;
  color: var(--text-tertiary, #94a3b8);
}

.user-divider {
  margin: 16px 0 12px !important;
  background: linear-gradient(90deg, transparent, rgba(226, 232, 240, 0.6), transparent) !important;
}

.user-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}
.user-stat-item {
  text-align: center;
  transition: transform 0.3s;
  cursor: default;
}
.user-stat-item:hover {
  transform: translateY(-2px);
}
.user-stat-num {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary, #0f172a);
  margin-bottom: 2px;
  font-variant-numeric: tabular-nums;
}
.user-stat-label {
  font-size: 12px;
  color: var(--text-tertiary, #94a3b8);
  font-weight: 500;
}
.user-stat-divider {
  width: 1px;
  height: 36px;
  background: linear-gradient(180deg, transparent, rgba(226, 232, 240, 0.5), transparent);
}

/* ===== 编辑区域 ===== */
.pi-input >>> .el-input__inner {
  border-radius: 10px;
  height: 42px;
  border: 1.5px solid var(--border, #e2e8f0);
  transition: all 0.3s;
  padding-left: 36px;
}
.pi-input >>> .el-input__inner:focus {
  border-color: var(--primary, #1a56db);
  box-shadow: 0 0 0 3px rgba(26, 86, 219, 0.1);
  transform: translateY(-1px);
}
.pi-input >>> .el-input__prefix {
  left: 10px;
  font-size: 16px;
  color: var(--text-tertiary, #94a3b8);
}
.pi-save-btn {
  background: linear-gradient(135deg, #1a56db, #0ea5e9) !important;
  border: none !important;
  border-radius: 10px !important;
  padding: 10px 28px !important;
  font-weight: 500 !important;
  transition: all 0.3s !important;
}
.pi-save-btn:hover {
  box-shadow: 0 6px 24px rgba(26, 86, 219, 0.35) !important;
  transform: translateY(-2px) !important;
}

/* ===== 标签区域 ===== */
.tags-area {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tags-empty {
  width: 100%;
  text-align: center;
  padding: 24px 20px;
  color: var(--text-tertiary, #94a3b8);
}
.tags-empty-icon {
  font-size: 36px;
  margin-bottom: 10px;
  opacity: 0.4;
}
.tags-empty p {
  margin-bottom: 4px;
  font-size: 14px;
}
.tags-hint {
  font-size: 12px;
  opacity: 0.7;
}

.user-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 14px;
  background: linear-gradient(135deg, #eff6ff, #f0f4ff);
  color: var(--primary, #1a56db);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid rgba(26, 86, 219, 0.1);
  transition: all 0.25s;
}
.user-tag:hover {
  background: linear-gradient(135deg, #dbeafe, #e0e7ff);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(26, 86, 219, 0.12);
}

.tag-remove {
  font-size: 12px;
  cursor: pointer;
  opacity: 0.4;
  transition: all 0.2s;
  border-radius: 50%;
}
.tag-remove:hover {
  opacity: 1;
  color: var(--danger, #dc2626);
  transform: scale(1.2);
}

/* tag 动画 — 添加/删除过渡 */
.tag-enter-active {
  animation: tagIn 0.3s ease;
}
.tag-leave-active {
  animation: tagOut 0.25s ease;
}
@keyframes tagIn {
  from { opacity: 0; transform: scale(0.8) translateY(-8px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
@keyframes tagOut {
  from { opacity: 1; transform: scale(1); }
  to { opacity: 0; transform: scale(0.8); }
}

.add-tag-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 16px;
  border: 1.5px dashed var(--border, #e2e8f0);
  border-radius: 20px;
  background: transparent;
  color: var(--text-secondary, #475569);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.25s;
}
.add-tag-btn:hover {
  border-color: var(--primary, #1a56db);
  color: var(--primary, #1a56db);
  background: #f8faff;
  transform: translateY(-1px);
}
.add-tag-btn i {
  transition: transform 0.3s;
}
.add-tag-btn:hover i {
  transform: rotate(90deg);
}

/* ===== 学习进度 ===== */
.progress-placeholder {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s;
}
.progress-placeholder:hover {
  background: rgba(5, 150, 105, 0.04);
}
.progress-arrow-circle {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #059669;
  transition: all 0.3s;
  flex-shrink: 0;
}
.progress-placeholder:hover .progress-arrow-circle {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.2);
}
.progress-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.progress-link {
  font-size: 15px;
  font-weight: 600;
  color: var(--primary, #1a56db);
}
.progress-hint {
  font-size: 13px;
  color: var(--text-tertiary, #94a3b8);
}

/* ===== 对话框 ===== */
.tag-dialog >>> .el-dialog {
  border-radius: 14px;
}
.tag-dialog >>> .el-dialog__header {
  border-bottom: 1px solid var(--border, #e2e8f0);
  padding-bottom: 16px;
}
.tag-dialog >>> .el-dialog__title {
  font-weight: 600;
  font-size: 16px;
}

/* ===== 技术等级标签 ===== */
.experience-tag {
  display: inline-flex;
  align-items: center;
  padding: 5px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
}
.level-beginner {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #92400e;
}
.level-intermediate {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1e40af;
}
.level-advanced {
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  color: #065f46;
}
.level-expert {
  background: linear-gradient(135deg, #fae8ff, #e9d5ff);
  color: #6b21a8;
}

/* select 在编辑表单中的样式 */
.pi-input >>> .el-select .el-input__inner {
  border-radius: 10px;
  height: 42px;
  border: 1.5px solid var(--border, #e2e8f0);
  transition: all 0.3s;
}
.pi-input >>> .el-select .el-input__inner:focus {
  border-color: var(--primary, #1a56db);
  box-shadow: 0 0 0 3px rgba(26, 86, 219, 0.1);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .profile { padding: 16px 12px; }
  .page-title { font-size: 24px; }
  .user-stats { gap: 12px; }
  .user-stat-num { font-size: 20px; }
  .bg-orb { opacity: 0.06; }
}
</style>
