<template>
  <footer class="app-footer">
    <div class="footer-wave">
      <svg viewBox="0 0 1440 60" preserveAspectRatio="none">
        <path d="M0,30 C360,60 720,0 1440,30 L1440,60 L0,60 Z" fill="var(--bg)"/>
      </svg>
    </div>

    <div class="footer-main">
      <div class="footer-inner">
        <!-- 平台简介 -->
        <div class="footer-section brand-section">
          <div class="footer-logo">
            <div class="footer-logo-mark">&lt;/&gt;</div>
            <span>AI-Code</span>
          </div>
          <p class="footer-desc">
            基于 AI 驱动的智能编程学习平台，提供代码审查、智能问答、个性化学习路径等一站式服务，帮助你更高效地学习编程。
          </p>
          <div class="footer-tech-tags">
            <span class="tech-tag" v-for="tech in techStack" :key="tech">{{ tech }}</span>
          </div>
        </div>

        <!-- 快速导航 -->
        <div class="footer-section">
          <h4 class="footer-title">快速导航</h4>
          <ul class="footer-links">
            <li><router-link to="/">首页</router-link></li>
            <li><router-link to="/code-review">代码审查</router-link></li>
            <li><router-link to="/qa">智能问答</router-link></li>
            <li><router-link to="/learning-paths">学习路径</router-link></li>
          </ul>
        </div>

        <!-- 学习资源 -->
        <div class="footer-section">
          <h4 class="footer-title">学习资源</h4>
          <ul class="footer-links">
            <li><a href="#" @click.prevent>Java 基础教程</a></li>
            <li><a href="#" @click.prevent>Spring 系列</a></li>
            <li><a href="#" @click.prevent>数据库专题</a></li>
            <li><a href="#" @click.prevent>算法与数据结构</a></li>
          </ul>
        </div>

        <!-- 关于我们 -->
        <div class="footer-section">
          <h4 class="footer-title">关于</h4>
          <ul class="footer-links">
            <li><a href="#" @click.prevent>平台介绍</a></li>
            <li><a href="#" @click.prevent>使用帮助</a></li>
            <li><a href="#" @click.prevent>意见反馈</a></li>
            <li><a href="#" @click.prevent>隐私政策</a></li>
          </ul>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      <div class="footer-inner">
        <p class="copyright">© 2026 AI-Code 智能编程学习平台. All rights reserved.</p>
        <p class="footer-extra">
          <span><i class="el-icon-info"></i> 浙ICP备2026XXXXXXXX号-1</span>
          <span><i class="el-icon-s-platform"></i> v1.0.0</span>
        </p>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'AppFooter',
  data() {
    return {
      techStack: ['Spring Boot', 'MyBatis', 'MySQL', 'Redis', 'Vue.js', 'Element UI', 'Docker']
    };
  }
};
</script>

<style scoped>
.app-footer {
  margin-top: 60px;
  background: var(--bg-card, #ffffff);
  border-top: 1px solid var(--border, #e2e8f0);
}

.footer-wave {
  margin-top: -1px;
  line-height: 0;
}

.footer-wave svg {
  display: block;
  width: 100%;
  height: 50px;
}

.footer-main {
  padding: 48px 0 32px;
}

.footer-inner {
  max-width: var(--content-max-width, 1200px);
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 1.6fr 1fr 1fr 1fr;
  gap: 40px;
}

.footer-section h4 {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary, #0f172a);
  margin-bottom: 16px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 品牌区 */
.brand-section .footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary, #0f172a);
  margin-bottom: 12px;
}

.footer-logo-mark {
  background: var(--primary-gradient);
  width: 34px;
  height: 34px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-family: var(--font-mono, monospace);
  font-size: 14px;
  font-weight: 700;
}

.footer-desc {
  font-size: 13px;
  color: var(--text-secondary, #475569);
  line-height: 1.7;
  margin-bottom: 16px;
}

.footer-tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tech-tag {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  background: linear-gradient(135deg, #eff6ff, #eef2ff);
  color: var(--primary, #1a56db);
  font-weight: 500;
  border: 1px solid rgba(26, 86, 219, 0.1);
}

/* 链接列表 */
.footer-links {
  list-style: none;
}

.footer-links li {
  margin-bottom: 10px;
}

.footer-links a {
  font-size: 13px;
  color: var(--text-secondary, #475569);
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.footer-links a:hover {
  color: var(--primary, #1a56db);
  transform: translateX(3px);
}

/* 底部栏 */
.footer-bottom {
  border-top: 1px solid var(--border, #e2e8f0);
  padding: 16px 0;
}

.footer-bottom .footer-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.copyright {
  font-size: 12px;
  color: var(--text-tertiary, #94a3b8);
}

.footer-extra {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: var(--text-tertiary, #94a3b8);
}

.footer-extra span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 响应式 */
@media (max-width: 768px) {
  .footer-inner {
    grid-template-columns: 1fr 1fr;
    gap: 24px;
  }
  .brand-section {
    grid-column: 1 / -1;
  }
  .footer-bottom .footer-inner {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
}
</style>
