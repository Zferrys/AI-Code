<template>
  <div class="onboarding-overlay" v-if="visible" @click.self="dismiss">
    <div class="onboarding-card">
      <!-- 步骤指示器 -->
      <div class="ob-steps">
        <span v-for="(s, i) in steps" :key="i"
          class="ob-step-dot" :class="{ 'ob-dot-active': i === currentStep, 'ob-dot-done': i < currentStep }">
        </span>
      </div>

      <!-- 步骤内容 -->
      <div class="ob-body">
        <div class="ob-icon">{{ steps[currentStep].icon }}</div>
        <h3 class="ob-title">{{ steps[currentStep].title }}</h3>
        <p class="ob-desc">{{ steps[currentStep].content }}</p>
        <div class="ob-actions">
          <el-button size="small" @click="dismiss" class="ob-skip">跳过引导</el-button>
          <el-button size="small" type="primary" @click="next" class="ob-next">
            {{ currentStep < steps.length - 1 ? '下一步' : '开始使用' }}
            <i class="el-icon-arrow-right"></i>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const STORAGE_KEY = 'aicode_onboarding_done';

export default {
  name: 'OnboardingTour',
  data() {
    return {
      visible: false,
      currentStep: 0,
      steps: [
        { icon: '👋', title: '欢迎来到 AI-Code！', content: 'AI-Code 是一个基于 AI 的编程学习平台，帮助你写代码、查问题、学技术。我们为你准备了 3 个核心功能，让我带你快速了解。' },
        { icon: '📝', title: '智能代码审查', content: '提交你的代码，AI 会自动分析代码质量、检测 Bug、安全漏洞和性能问题。点击顶部导航栏"代码审查"开始使用。' },
        { icon: '💬', title: 'AI 编程问答', content: '遇到编程问题？直接向 AI 提问。支持多轮对话，可以追问细节，比搜索引擎更精准。点击"智能问答"试试看。' },
        { icon: '📚', title: '学习路径', content: '系统化学习路线，覆盖 Java、Python、前端、数据库等主流技术栈。从入门到高级，循序渐进。随时可以浏览"学习路径"。' },
        { icon: '🎯', title: '准备好了！', content: '现在就去试试吧！你可以先浏览学习路径，或者直接去代码审查体验 AI 的能力。遇到问题随时可以提问哦！' }
      ]
    };
  },
  methods: {
    // 检查是否需要显示引导
    check() {
      const done = localStorage.getItem(STORAGE_KEY);
      if (!done) {
        this.visible = true;
        localStorage.setItem(STORAGE_KEY, 'true');
      }
    },
    next() {
      if (this.currentStep < this.steps.length - 1) {
        this.currentStep++;
      } else {
        this.dismiss();
      }
    },
    dismiss() {
      this.visible = false;
    },
    // 重置引导（用于测试或重新体验）
    reset() {
      localStorage.removeItem(STORAGE_KEY);
    }
  }
};
</script>

<style scoped>
.onboarding-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(4px);
  animation: obFadeIn 0.3s ease;
}

.onboarding-card {
  background: white;
  border-radius: 20px;
  padding: 40px 36px 32px;
  width: 420px;
  max-width: 90vw;
  box-shadow: 0 24px 48px rgba(0,0,0,0.2);
  animation: obSlideUp 0.35s ease;
}

.ob-steps {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 28px;
}

.ob-step-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e2e8f0;
  transition: all 0.3s;
}

.ob-dot-active {
  width: 24px;
  border-radius: 4px;
  background: var(--primary, #1a56db);
}

.ob-dot-done {
  background: var(--primary, #1a56db);
  opacity: 0.4;
}

.ob-body {
  text-align: center;
}

.ob-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.ob-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary, #0f172a);
  margin-bottom: 12px;
}

.ob-desc {
  font-size: 15px;
  color: var(--text-secondary, #475569);
  line-height: 1.7;
  margin-bottom: 28px;
}

.ob-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.ob-skip {
  color: var(--text-tertiary, #94a3b8);
  border-color: var(--border, #e2e8f0);
}

.ob-next {
  background: var(--primary-gradient) !important;
  border: none !important;
}

@keyframes obFadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes obSlideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
