<template>
  <div class="not-found">
    <!-- 3D 背景元素 -->
    <div class="nf-bg-3d">
      <div class="nf-orb nf-orb-1"></div>
      <div class="nf-orb nf-orb-2"></div>
      <div class="nf-orb nf-orb-3"></div>
    </div>

    <div class="nf-bg">
      <span class="nf-code" style="--delay: 0s">4</span>
      <span class="nf-code nf-zero" style="--delay: 0.3s">0</span>
      <span class="nf-code" style="--delay: 0.6s">4</span>
    </div>

    <div class="nf-float-icons">
      <span class="nf-icon fi-1">&lt;/&gt;</span>
      <span class="nf-icon fi-2">{ }</span>
      <span class="nf-icon fi-3">#</span>
    </div>

    <div class="nf-content">
      <h1 class="nf-title">页面迷路了</h1>
      <p class="nf-desc">你访问的页面不存在，可能已被移除或地址输入错误</p>
      <div class="nf-actions">
        <button class="btn-primary" @click="$router.push('/')">
          <i class="el-icon-s-home"></i> 返回首页
        </button>
        <button class="btn-outline" @click="$router.back()">
          <i class="el-icon-arrow-left"></i> 后退
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default { name: 'NotFound' };
</script>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

/* ===== 3D 背景光晕 ===== */
.nf-bg-3d {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.nf-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.08;
}
.nf-orb-1 {
  width: 400px; height: 400px;
  background: linear-gradient(135deg, #3b82f6, #0ea5e9);
  top: -100px; left: -80px;
  animation: nfFloat 10s ease-in-out infinite;
}
.nf-orb-2 {
  width: 350px; height: 350px;
  background: linear-gradient(135deg, #0ea5e9, #38bdf8);
  bottom: -100px; right: -60px;
  animation: nfFloat 14s ease-in-out infinite reverse;
}
.nf-orb-3 {
  width: 200px; height: 200px;
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  top: 30%; left: 50%;
  animation: nfFloat 8s ease-in-out infinite 2s;
}
@keyframes nfFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.1); }
}

/* ===== 浮动代码图标 ===== */
.nf-float-icons {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.nf-icon {
  position: absolute;
  font-family: var(--font-mono, monospace);
  font-weight: 700;
  color: rgba(59, 130, 246, 0.05);
}
.fi-1 {
  top: 12%; left: 10%;
  font-size: 36px;
  animation: nfIconFloat 7s ease-in-out infinite;
}
.fi-2 {
  bottom: 15%; right: 12%;
  font-size: 28px;
  animation: nfIconFloat 9s ease-in-out infinite reverse;
}
.fi-3 {
  top: 55%; left: 5%;
  font-size: 24px;
  animation: nfIconFloat 8s ease-in-out infinite 3s;
}
@keyframes nfIconFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.05; }
  50% { transform: translateY(-15px) rotate(5deg); opacity: 0.1; }
}

/* ===== 数字背景 ===== */
.nf-bg {
  position: absolute;
  display: flex;
  gap: 10px;
  user-select: none;
  pointer-events: none;
  z-index: 0;
}

.nf-code {
  font-size: 180px;
  font-weight: 900;
  color: var(--primary, #1a56db);
  opacity: 0.06;
  line-height: 1;
  animation: nfCodePop 0.6s ease both;
  animation-delay: var(--delay, 0s);
}
@keyframes nfCodePop {
  from { opacity: 0; transform: scale(0.5) translateY(20px); }
  to { opacity: 0.06; transform: scale(1) translateY(0); }
}

.nf-zero {
  color: var(--warning, #d97706);
}

.nf-content {
  position: relative;
  z-index: 1;
  text-align: center;
  animation: fadeInUp 0.5s ease 0.4s both;
}

.nf-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary, #0f172a);
  margin-bottom: 12px;
  letter-spacing: -0.5px;
}

.nf-desc {
  font-size: 16px;
  color: var(--text-secondary, #475569);
  margin-bottom: 28px;
}

.nf-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 600px) {
  .nf-code { font-size: 120px; }
  .nf-title { font-size: 24px; }
  .nf-actions { flex-direction: column; align-items: center; }
}
</style>
