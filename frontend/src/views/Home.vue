<template>
  <div class="home">
    <!-- ========== Hero 区域 ========== -->
    <section class="hero" ref="hero">
      <three-background />
      <div class="hero-content">
        <div class="hero-badge animate-fade-in-up">🚀 AI 驱动的智能编程学习平台</div>
        <h1 class="hero-title animate-fade-in-up" style="animation-delay: 0.1s">
          写代码 · 查问题 · 学技术
          <span class="gradient-text">一个平台就够了</span>
        </h1>
        <p class="hero-desc animate-fade-in-up" style="animation-delay: 0.2s">
          基于 AI 的智能代码审查、实时编程问答、个性化学习路径，<br class="hide-mobile" />
          帮助你从入门到精通，高效提升编程能力
        </p>
        <div class="hero-actions animate-fade-in-up" style="animation-delay: 0.3s">
          <button class="btn-primary btn-hero-primary" @click="$router.push(isLoggedIn ? '/code-review' : '/register')">
            {{ isLoggedIn ? '开始代码审查' : '免费开始学习' }}
            <i class="el-icon-arrow-right"></i>
          </button>
          <button class="btn-hero-secondary" @click="$router.push('/learning-paths')">
            浏览学习路径
            <i class="el-icon-right"></i>
          </button>
        </div>
        <div class="hero-stats animate-fade-in-up" style="animation-delay: 0.4s">
          <div class="hero-stat-item">
            <span class="hero-stat-num">{{ formatNum(stats.userCount) }}</span>
            <span class="hero-stat-label">注册用户</span>
          </div>
          <div class="hero-stat-divider"></div>
          <div class="hero-stat-item">
            <span class="hero-stat-num">{{ formatNum(stats.reviewCount) }}</span>
            <span class="hero-stat-label">代码审查</span>
          </div>
          <div class="hero-stat-divider"></div>
          <div class="hero-stat-item">
            <span class="hero-stat-num">{{ formatNum(stats.questionCount) }}</span>
            <span class="hero-stat-label">AI 问答</span>
          </div>
          <div class="hero-stat-divider"></div>
          <div class="hero-stat-item">
            <span class="hero-stat-num">{{ stats.pathCount }}</span>
            <span class="hero-stat-label">学习路径</span>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 核心功能 ========== -->
    <section class="section features-section">
      <div class="section-bg">
        <div class="section-orb section-orb-1"></div>
        <div class="section-orb section-orb-2"></div>
      </div>
      <div class="section-header">
        <span class="section-label">核心功能</span>
        <h2 class="section-title">为什么选择 AI-Code？</h2>
        <p class="section-subtitle">三大核心功能，覆盖编程学习全链路</p>
      </div>
      <div class="features-grid">
        <div
          class="feature-card tilt-card"
          @click="navigateTo('/code-review')"
          @mousemove="onCardTilt"
          @mouseleave="onCardTiltLeave"
          data-tilt
        >
          <div class="feature-icon-wrap code-icon-wrap">
            <i class="el-icon-document"></i>
          </div>
          <h3>智能代码审查</h3>
          <p>提交你的代码，AI 自动分析代码质量、检测潜在 Bug、性能瓶颈和安全漏洞，并提供优化建议和最佳实践。</p>
          <div class="feature-tags">
            <span class="badge badge-green">质量检测</span>
            <span class="badge badge-blue">安全扫描</span>
            <span class="badge badge-yellow">性能优化</span>
          </div>
        </div>
        <div
          class="feature-card tilt-card"
          @click="navigateTo('/qa')"
          @mousemove="onCardTilt"
          @mouseleave="onCardTiltLeave"
          data-tilt
        >
          <div class="feature-icon-wrap qa-icon-wrap">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <h3>AI 编程问答</h3>
          <p>遇到编程问题？直接提问 AI 助手。支持多轮对话、代码示例、最佳实践推荐，比搜索引擎更精准。</p>
          <div class="feature-tags">
            <span class="badge badge-blue">实时回答</span>
            <span class="badge badge-green">代码示例</span>
            <span class="badge badge-gray">多轮对话</span>
          </div>
        </div>
        <div
          class="feature-card tilt-card"
          @click="$router.push('/learning-paths')"
          @mousemove="onCardTilt"
          @mouseleave="onCardTiltLeave"
          data-tilt
        >
          <div class="feature-icon-wrap path-icon-wrap">
            <i class="el-icon-notebook-2"></i>
          </div>
          <h3>个性化学习路径</h3>
          <p>根据你的技术栈和水平目标，智能推荐最合适的学习路线。从入门到精通，循序渐进，学练结合。</p>
          <div class="feature-tags">
            <span class="badge badge-yellow">分层教学</span>
            <span class="badge badge-red">实战驱动</span>
            <span class="badge badge-green">进度追踪</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 分隔线 -->
    <div class="section-divider-home"></div>

    <!-- ========== 推荐学习路径 ========== -->
    <section class="section paths-section" v-if="displayPaths.length > 0">
      <div class="section-header">
        <span class="section-label">精选路线</span>
        <h2 class="section-title">热门学习路径</h2>
        <p class="section-subtitle">覆盖主流技术栈，从入门到高级系统化学习</p>
      </div>
      <div class="paths-grid">
        <div
          v-for="(path, idx) in displayPaths"
          :key="path.id"
          class="hp-card"
          :class="'hp-' + (path.difficulty || '').toLowerCase()"
          :style="{ animationDelay: `${idx * 0.08}s` }"
          @click="$router.push(`/learning-paths/${path.id}`)"
        >
          <div class="hp-bar"></div>
          <div class="hp-body">
            <div class="hp-meta">
              <span class="hp-badge">{{ diffLabel(path.difficulty) }}</span>
              <span class="hp-stat">{{ path.courseCount || 0 }} 课 · {{ path.estimatedDays }} 天</span>
            </div>
            <h3 class="hp-title">{{ path.title }}</h3>
            <p class="hp-desc">{{ path.description }}</p>
            <div class="hp-footer">
              <span class="hp-date">{{ path.updateTime ? new Date(path.updateTime).toLocaleDateString('zh-CN') : '' }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="section-action">
        <button class="btn-outline" @click="$router.push('/learning-paths')">
          查看全部路径 <i class="el-icon-arrow-right"></i>
        </button>
      </div>
    </section>

    <!-- 分隔线 -->
    <div class="section-divider-home"></div>

    <!-- ========== 热门问答 ========== -->
    <section class="section qa-section" v-if="displayQuestions.length > 0">
      <div class="section-header">
        <span class="section-label">热门问答</span>
        <h2 class="section-title">大家都在问</h2>
        <p class="section-subtitle">精选高质量编程问答，由 AI 深度解答</p>
      </div>
      <div class="qa-grid">
        <div
          v-for="(q, idx) in displayQuestions"
          :key="q.id"
          class="qa-card"
          :style="{ animationDelay: `${idx * 0.06}s` }"
          @click="navigateTo(`/qa/${q.id}`)"
        >
          <div class="qa-stats">
            <div class="qa-stat">
              <i class="el-icon-view"></i>
              <span>{{ q.viewCount || 0 }}</span>
            </div>
            <div class="qa-stat">
              <i class="el-icon-chat-dot-round"></i>
              <span>{{ q.answerCount || 0 }}</span>
            </div>
            <div class="qa-stat">
              <i class="el-icon-star-off"></i>
              <span>{{ q.favoriteCount || 0 }}</span>
            </div>
          </div>
          <div class="qa-body">
            <h3 class="qa-title">{{ q.title }}</h3>
            <p class="qa-excerpt">{{ q.content?.substring(0, 80) }}...</p>
            <div class="qa-meta">
              <span class="badge badge-blue">{{ q.category }}</span>
              <span v-if="q.tags" class="qa-tags">{{ q.tags.split(',').slice(0, 2).join(' · ') }}</span>
              <span class="qa-time">{{ timeAgo(q.createTime) }}</span>
            </div>
          </div>
          <div v-if="q.isResolved" class="qa-resolved-badge">
            <i class="el-icon-success"></i> 已解决
          </div>
        </div>
      </div>
      <div class="section-action">
        <button class="btn-outline" @click="navigateTo('/qa')">
          更多问答 <i class="el-icon-arrow-right"></i>
        </button>
      </div>
    </section>

    <!-- 分隔线 -->
    <div class="section-divider-home"></div>

    <!-- ========== 平台优势 ========== -->
    <section class="section advantages-section">
      <div class="section-bg">
        <div class="section-orb section-orb-1" style="width:350px;height:350px;top:-80px;left:10%;"></div>
        <div class="section-orb section-orb-2" style="width:250px;height:250px;bottom:-60px;right:5%;"></div>
      </div>
      <div class="section-header">
        <span class="section-label">为什么选择我们</span>
        <h2 class="section-title">四大核心优势</h2>
      </div>
      <div class="advantages-grid">
        <div class="advantage-item">
          <div class="advantage-icon"><i class="el-icon-document-copy"></i></div>
          <h4>AI 深度分析</h4>
          <p>基于大语言模型，代码检测精度达到专业开发者水平，覆盖上千种常见代码问题模式</p>
        </div>
        <div class="advantage-item">
          <div class="advantage-icon"><i class="el-icon-connection"></i></div>
          <h4>实时互动学习</h4>
          <p>AI 问答支持多轮对话，不限次数追问，让每个技术难点都能得到充分解答</p>
        </div>
        <div class="advantage-item">
          <div class="advantage-icon"><i class="el-icon-s-marketing"></i></div>
          <h4>个性化推荐</h4>
          <p>基于你的技术标签和学习进度，智能匹配最适合的学习路径，不走弯路</p>
        </div>
        <div class="advantage-item">
          <div class="advantage-icon"><i class="el-icon-safety"></i></div>
          <h4>安全可靠</h4>
          <p>代码加密传输，不存储用户代码到第三方，JWT 认证 + BCrypt 加密，全方位保障数据安全</p>
        </div>
      </div>
    </section>

    <!-- ========== 用户评价 ========== -->
    <section class="section testimonials-section">
      <div class="section-header">
        <span class="section-label">用户评价</span>
        <h2 class="section-title">看看他们怎么说</h2>
      </div>
      <div class="testimonials-grid">
        <div v-for="(t, idx) in displayTestimonials" :key="t.id" class="testimonial-card" :style="{ animationDelay: `${idx * 0.1}s` }">
          <div class="testimonial-stars">
            <i v-for="s in t.rating" :key="s" class="el-icon-star-on"></i>
          </div>
          <p class="testimonial-content">"{{ t.content }}"</p>
          <div class="testimonial-author">
            <el-avatar :size="36" class="testimonial-avatar">{{ t.name.charAt(0) }}</el-avatar>
            <div>
              <div class="testimonial-name">{{ t.name }}</div>
              <div class="testimonial-role">{{ t.role }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== CTA 区域 ========== -->
    <section class="section cta-section">
      <div class="cta-card">
        <h2 class="cta-title">准备好提升你的编程能力了吗？</h2>
        <p class="cta-desc">立即注册，开启 AI 驱动的编程学习之旅</p>
        <button class="btn-primary btn-cta" @click="$router.push(isLoggedIn ? '/code-review' : '/register')">
          {{ isLoggedIn ? '开始学习' : '免费注册' }}
          <i class="el-icon-arrow-right"></i>
        </button>
      </div>
    </section>
  </div>
</template>

<script>
import http from '../api';
import { mapGetters } from 'vuex';
import { learningPathApi, qaApi, codeReviewApi } from '../api';
import { mockTestimonials, mockStats } from '../mock';
import ThreeBackground from '../components/ThreeBackground.vue';

export default {
  name: 'Home',
  components: { ThreeBackground },
  data() {
    return {
      stats: { ...mockStats },
      paths: [],
      questions: [],
      testimonials: [...mockTestimonials]
    };
  },
  computed: {
    ...mapGetters(['isLoggedIn']),
    displayPaths() {
      const arr = this.paths || [];
      return [...arr].sort(() => Math.random() - 0.5).slice(0, 3);
    },
    displayQuestions() {
      return (this.questions || []).slice(0, 4);
    },
    displayTestimonials() {
      return this.testimonials;
    }
  },
  created() {
    this.loadData();
  },
  methods: {
    // ---- 3D 卡片倾角 ----
    onCardTilt(e) {
      const card = e.currentTarget;
      const rect = card.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;
      const cx = rect.width / 2;
      const cy = rect.height / 2;
      const rotateX = ((y - cy) / cy) * -6;
      const rotateY = ((x - cx) / cx) * 6;
      card.style.transform = `perspective(600px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateY(-6px)`;
    },
    onCardTiltLeave(e) {
      const card = e.currentTarget;
      card.style.transform = 'perspective(600px) rotateX(0deg) rotateY(0deg) translateY(0)';
    },

    async loadData() {
      try {
        const [pathRes, qaRes, statsRes] = await Promise.all([
          learningPathApi.getAll().catch(() => ({ code: 200, data: [] })),
          qaApi.getList({ page: 1, pageSize: 10 }).catch(() => ({ code: 200, data: { list: [] } })),
          http.get('/stats').catch(() => ({ data: { data: null } }))
        ]);
        if (pathRes.code === 200) this.paths = (pathRes.data && pathRes.data.list) || pathRes.data || [];
        if (qaRes.code === 200) this.questions = (qaRes.data && qaRes.data.list) || [];
        const statsData = statsRes.data;
        if (statsData && statsData.code === 200 && statsData.data) {
          this.stats = statsData.data;
        }
      } catch {}
    },
    diffLabel(d) {
      const map = { BEGINNER: '入门', INTERMEDIATE: '进阶', ADVANCED: '高级' };
      return map[d] || d;
    },
    formatNum(n) {
      if (!n) return '0';
      if (n >= 10000) return (n / 10000).toFixed(1) + 'w+';
      if (n >= 1000) return (n / 1000).toFixed(1) + 'k+';
      return n + '+';
    },
    navigateTo(path) {
      if (this.isLoggedIn) {
        this.$router.push(path);
      } else {
        this.$message.info('请先登录');
        this.$router.push('/login');
      }
    },
    timeAgo(time) {
      if (!time) return '';
      const now = Date.now();
      const t = new Date(time).getTime();
      const diff = now - t;
      const m = Math.floor(diff / 60000);
      if (m < 1) return '刚刚';
      if (m < 60) return `${m} 分钟前`;
      const h = Math.floor(m / 60);
      if (h < 24) return `${h} 小时前`;
      const d = Math.floor(h / 24);
      if (d < 30) return `${d} 天前`;
      return new Date(time).toLocaleDateString('zh-CN');
    }
  }
};
</script>

<style scoped>
.home { overflow-x: hidden; }

/* ===== Hero ===== */
.hero {
  position: relative;
  padding: 100px 20px 80px;
  text-align: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
  overflow: hidden;
  margin: calc(-1 * var(--header-height, 64px)) -20px 0;
  padding-top: calc(var(--header-height, 64px) + 100px);
}

.hero-bg { /* replaced by ThreeBackground canvas */ }

.hero-grid { display: none; }
.hero-orb { display: none; }

/* Hero 背景由 ThreeBackground 组件提供 */

.hero-content { position: relative; z-index: 1; }

.hero-badge {
  display: inline-block;
  padding: 6px 16px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 20px;
  color: #94a3b8;
  font-size: 13px;
  margin-bottom: 24px;
}

.hero-title {
  font-size: clamp(32px, 5vw, 56px);
  font-weight: 800;
  color: white;
  line-height: 1.2;
  margin-bottom: 20px;
  letter-spacing: -1px;
}

.hero-title .gradient-text {
  background: linear-gradient(135deg, #60a5fa, #38bdf8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 18px;
  color: #94a3b8;
  line-height: 1.7;
  margin-bottom: 36px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 48px;
}

.btn-hero-primary {
  padding: 14px 32px;
  font-size: 16px;
  background: linear-gradient(135deg, #3b82f6, #0ea5e9);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.3);
}
.btn-hero-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(59, 130, 246, 0.4);
}

.btn-hero-secondary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 13px 28px;
  background: rgba(255,255,255,0.06);
  border: 1.5px solid rgba(255,255,255,0.15);
  border-radius: 12px;
  color: #e2e8f0;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.25s;
}
.btn-hero-secondary:hover {
  background: rgba(255,255,255,0.1);
  border-color: rgba(255,255,255,0.25);
}

.hero-stats {
  display: inline-flex;
  align-items: center;
  gap: 32px;
  padding: 20px 40px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
}

.hero-stat-item { text-align: center; }

.hero-stat-num {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: white;
  margin-bottom: 2px;
}

.hero-stat-label {
  font-size: 13px;
  color: #64748b;
}

.hero-stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255,255,255,0.1);
}

/* ===== Section 通用 ===== */
.section {
  max-width: var(--content-max-width, 1200px);
  margin: 0 auto;
  padding: 60px 20px;
  position: relative;
}

/* 区块背景光晕 */
.section-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}
.section-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.07;
}
.section-orb-1 {
  width: 400px; height: 400px;
  background: linear-gradient(135deg, #3b82f6, #0ea5e9);
  top: -100px; right: -80px;
  animation: sectionOrbFloat 10s ease-in-out infinite;
}
.section-orb-2 {
  width: 300px; height: 300px;
  background: linear-gradient(135deg, #0ea5e9, #38bdf8);
  bottom: -80px; left: -60px;
  animation: sectionOrbFloat 14s ease-in-out infinite reverse;
}
@keyframes sectionOrbFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.1); }
}

/* 区块分割线 */
.section-divider-home {
  height: 1px;
  max-width: var(--content-max-width, 1200px);
  margin: 0 auto;
  background: linear-gradient(to right, transparent, rgba(59,130,246,0.12), rgba(124,58,237,0.12), transparent);
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.section-label {
  display: inline-block;
  padding: 4px 14px;
  font-size: 12px;
  font-weight: 600;
  color: var(--primary, #1a56db);
  background: #eff6ff;
  border-radius: 20px;
  margin-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.section-title {
  font-size: 30px;
  font-weight: 700;
  color: var(--text-primary, #0f172a);
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.section-subtitle {
  font-size: 16px;
  color: var(--text-secondary, #475569);
}

.section-action {
  text-align: center;
  margin-top: 32px;
  position: relative;
  z-index: 1;
}

/* ===== 功能卡片（3D 倾角） ===== */
.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  position: relative;
  z-index: 1;
}

.feature-card {
  background: white;
  border-radius: 16px;
  padding: 36px 28px;
  border: 1px solid var(--border, #e2e8f0);
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
              box-shadow 0.3s ease,
              border-color 0.3s ease;
  animation: fadeInUp 0.5s both;
  transform-style: preserve-3d;
  will-change: transform;
  position: relative;
  overflow: hidden;
}
.feature-card:nth-child(1) { animation-delay: 0.1s; }
.feature-card:nth-child(2) { animation-delay: 0.2s; }
.feature-card:nth-child(3) { animation-delay: 0.3s; }
.feature-card::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(59,130,246,0.03), rgba(124,58,237,0.03));
  opacity: 0;
  transition: opacity 0.5s;
  pointer-events: none;
}
.feature-card:hover::after {
  opacity: 1;
}
.feature-card:hover {
  box-shadow: 0 20px 48px -8px rgba(0,0,0,0.1), 0 0 24px rgba(59,130,246,0.08);
  border-color: rgba(59,130,246,0.2);
}

.feature-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  font-size: 26px;
  color: white;
}
.code-icon-wrap { background: linear-gradient(135deg, #059669, #10b981); }
.qa-icon-wrap { background: linear-gradient(135deg, #d97706, #f59e0b); }
.path-icon-wrap { background: linear-gradient(135deg, #1a56db, #3b82f6); }

.feature-card h3 { font-size: 20px; font-weight: 600; margin-bottom: 10px; }
.feature-card p {
  font-size: 14px;
  color: var(--text-secondary, #475569);
  line-height: 1.7;
  margin-bottom: 16px;
}

.feature-tags { display: flex; gap: 6px; flex-wrap: wrap; }

/* ===== 首页路径卡片（简洁杂志风格） ===== */
.paths-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.hp-card {
  background: white;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid var(--border, #e8ecf1);
  cursor: pointer;
  transition: transform 0.35s cubic-bezier(0.4,0,0.2,1), box-shadow 0.35s ease, border-color 0.3s;
  animation: fadeInUp 0.5s both;
  display: flex;
  position: relative;
}

.hp-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 32px -8px rgba(0,0,0,0.1);
  border-color: var(--accent, #059669);
}

/* 左侧色条 */
.hp-bar {
  width: 4px;
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.3s;
}
.hp-card:hover .hp-bar { opacity: 1; }

.hp-beginner { --accent: #059669; --accent-bg: #ecfdf5; }
.hp-intermediate { --accent: #d97706; --accent-bg: #fffbeb; }
.hp-advanced { --accent: #0ea5e9; --accent-bg: #f5f3ff; }
.hp-default { --accent: #1a56db; --accent-bg: #eff6ff; }

.hp-beginner .hp-bar { background: linear-gradient(180deg, #059669, #10b981); }
.hp-intermediate .hp-bar { background: linear-gradient(180deg, #d97706, #f59e0b); }
.hp-advanced .hp-bar { background: linear-gradient(180deg, #0ea5e9, #38bdf8); }
.hp-default .hp-bar { background: linear-gradient(180deg, #1a56db, #3b82f6); }

/* 卡片体 */
.hp-body {
  padding: 20px 22px 18px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* 第一行：徽章 + 统计 */
.hp-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.hp-badge {
  font-size: 11px;
  font-weight: 600;
  color: var(--accent, #059669);
  background: var(--accent-bg, #ecfdf5);
  padding: 2px 10px;
  border-radius: 4px;
  letter-spacing: 0.2px;
}

.hp-stat {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

/* 标题 */
.hp-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary, #0f172a);
  line-height: 1.35;
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
}

/* 描述 */
.hp-desc {
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-secondary, #475569);
  margin: 0 0 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
  flex: 1;
}

/* 底部 */
.hp-footer {
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hp-date {
  font-size: 12px;
  color: #b0b8c4;
}

/* ===== 问答卡片 ===== */
.qa-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.qa-card {
  display: flex;
  gap: 16px;
  background: white;
  border-radius: 14px;
  padding: 20px;
  border: 1px solid var(--border, #e2e8f0);
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  animation: fadeInUp 0.5s both;
}
.qa-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  border-color: var(--primary, #1a56db);
}

.qa-stats {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 50px;
  align-items: center;
}

.qa-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 11px;
  color: var(--text-tertiary, #94a3b8);
}
.qa-stat i { font-size: 16px; margin-bottom: 1px; }

.qa-body { flex: 1; min-width: 0; }

.qa-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary, #0f172a);
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.qa-excerpt {
  font-size: 13px;
  color: var(--text-secondary, #475569);
  margin-bottom: 10px;
}

.qa-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}
.qa-tags { color: var(--text-tertiary, #94a3b8); }
.qa-time { color: var(--text-tertiary, #94a3b8); margin-left: auto; }

.qa-resolved-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  font-size: 11px;
  color: var(--success, #059669);
  display: flex;
  align-items: center;
  gap: 2px;
}

/* ===== 优势 ===== */
.advantages-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.advantage-item {
  text-align: center;
  padding: 28px 16px;
  animation: fadeInUp 0.5s both;
}
.advantage-item:nth-child(1) { animation-delay: 0s; }
.advantage-item:nth-child(2) { animation-delay: 0.08s; }
.advantage-item:nth-child(3) { animation-delay: 0.16s; }
.advantage-item:nth-child(4) { animation-delay: 0.24s; }

.advantage-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(135deg, #eff6ff, #eef2ff);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 24px;
  color: var(--primary, #1a56db);
}

.advantage-item h4 { font-size: 16px; font-weight: 600; margin-bottom: 8px; }
.advantage-item p {
  font-size: 13px;
  color: var(--text-secondary, #475569);
  line-height: 1.6;
}

/* ===== 评价 ===== */
.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.testimonial-card {
  background: white;
  border-radius: 14px;
  padding: 28px;
  border: 1px solid var(--border, #e2e8f0);
  animation: fadeInUp 0.5s both;
}

.testimonial-stars {
  color: #f59e0b;
  margin-bottom: 14px;
  font-size: 14px;
}

.testimonial-content {
  font-size: 14px;
  color: var(--text-primary, #0f172a);
  line-height: 1.7;
  font-style: italic;
  margin-bottom: 20px;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: 10px;
}
.testimonial-avatar {
  background: var(--primary-gradient) !important;
  border: none;
  font-weight: 600;
}
.testimonial-name { font-size: 14px; font-weight: 600; }
.testimonial-role { font-size: 12px; color: var(--text-tertiary, #94a3b8); }

/* ===== CTA ===== */
.cta-card {
  background: linear-gradient(135deg, #1e293b, #334155);
  border-radius: 20px;
  padding: 60px 40px;
  text-align: center;
}

.cta-title {
  font-size: 28px;
  font-weight: 700;
  color: white;
  margin-bottom: 12px;
}

.cta-desc {
  font-size: 16px;
  color: #94a3b8;
  margin-bottom: 28px;
}

.btn-cta {
  padding: 14px 40px;
  font-size: 16px;
  background: linear-gradient(135deg, #3b82f6, #0ea5e9);
  border-radius: 12px;
}
.btn-cta:hover {
  box-shadow: 0 8px 30px rgba(59, 130, 246, 0.4);
  transform: translateY(-2px);
}

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .features-grid, .advantages-grid, .testimonials-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .paths-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .qa-grid { grid-template-columns: 1fr; }
  .hero-stats { gap: 16px; padding: 16px 24px; flex-wrap: wrap; }
  .hero-stat-divider:nth-child(4) { display: none; }
  .hide-mobile { display: none; }
}

@media (max-width: 600px) {
  .features-grid, .advantages-grid, .testimonials-grid {
    grid-template-columns: 1fr;
  }
  .paths-grid {
    grid-template-columns: 1fr;
  }
  .hero { padding: 80px 16px 60px; }
  .hero-title { font-size: 28px; }
  .hero-desc { font-size: 15px; }
  .hero-stats { gap: 12px; padding: 12px 16px; }
  .hero-stat-num { font-size: 22px; }
  .hero-stat-divider { display: none !important; }
}
</style>
