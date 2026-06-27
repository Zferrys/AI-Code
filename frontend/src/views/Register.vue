<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="auth-orb auth-orb-1"></div>
      <div class="auth-orb auth-orb-2"></div>
      <div class="auth-orb auth-orb-3"></div>
      <div class="auth-ring"></div>
      <span class="auth-float-icon afi-1">&lt;/&gt;</span>
      <span class="auth-float-icon afi-2">{ }</span>
      <span class="auth-float-icon afi-3">=>"</span>
    </div>
    <div class="auth-container" :class="{ 'shake-error': shaking }">
      <!-- 左侧品牌区 -->
      <div class="auth-brand animate-fade-in">
        <div class="auth-brand-content">
          <div class="auth-brand-logo">
            <span class="brand-bracket">&lt;</span>
            <span class="brand-slash">/</span>
            <span class="brand-bracket">&gt;</span>
          </div>
          <h2 class="auth-brand-title">AI-Code</h2>
          <p class="auth-brand-desc">智能编程学习平台</p>
          <div class="auth-stats">
            <div class="auth-stat-item">
              <span class="auth-stat-num">2.8k+</span>
              <span class="auth-stat-label">注册用户</span>
            </div>
            <div class="auth-stat-divider"></div>
            <div class="auth-stat-item">
              <span class="auth-stat-num">1.5w+</span>
              <span class="auth-stat-label">代码审查</span>
            </div>
            <div class="auth-stat-divider"></div>
            <div class="auth-stat-item">
              <span class="auth-stat-num">8.9k+</span>
              <span class="auth-stat-label">AI 问答</span>
            </div>
          </div>
        </div>
        <div class="brand-dots">
          <span class="brand-dot"></span>
          <span class="brand-dot"></span>
          <span class="brand-dot"></span>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="auth-form-wrap animate-fade-in-up" style="animation-delay: 0.15s">
        <div class="auth-form-card">
          <div class="auth-form-header">
            <h2 class="auth-form-title">创建账号</h2>
            <p class="auth-form-desc">免费注册，开启 AI 编程学习之旅</p>
          </div>

          <el-form :model="form" :rules="rules" ref="formRef" label-width="0" size="large"
                   @submit.native.prevent="handleRegister">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="用户名（4-20位字母/数字/下划线）"
                class="auth-input"
                @focus="focusedField = 'username'"
                @blur="focusedField = ''"
              >
                <i slot="prefix" class="el-icon-user auth-input-icon"></i>
              </el-input>
            </el-form-item>
            <el-form-item prop="email">
              <el-input
                v-model="form.email"
                placeholder="邮箱"
                class="auth-input"
                @focus="focusedField = 'email'"
                @blur="focusedField = ''"
              >
                <i slot="prefix" class="el-icon-message auth-input-icon"></i>
              </el-input>
            </el-form-item>
            <!-- 邮箱验证码 -->
            <el-form-item prop="emailCode">
              <div class="email-code-row">
                <el-input v-model="form.emailCode" placeholder="验证码" class="ec-input"
                  maxlength="6" @keyup.enter="handleRegister" />
                <el-button type="primary" size="small" class="ec-btn"
                  :disabled="codeSending || codeCountdown > 0"
                  @click="handleSendCode">
                  {{ codeCountdown > 0 ? codeCountdown + 's' : '发送验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                class="auth-input"
                show-password
                @focus="focusedField = 'password'"
                @blur="focusedField = ''"
              >
                <i slot="prefix" class="el-icon-lock auth-input-icon"></i>
              </el-input>
              <div class="password-hint">
                至少12位，需包含大小写字母、数字和特殊字符
              </div>
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="确认密码"
                class="auth-input"
                show-password
                @keyup.enter="handleRegister"
                @focus="focusedField = 'confirm'"
                @blur="focusedField = ''"
              >
                <i slot="prefix" class="el-icon-lock auth-input-icon"></i>
              </el-input>
            </el-form-item>
            <!-- 验证码 (注册必须验证) -->
            <el-form-item prop="captchaAnswer">
              <div class="captcha-row">
                <img :src="captchaImage" alt="验证码" class="captcha-img" @click="refreshCaptcha" />
                <el-input v-model="form.captchaAnswer" placeholder="答案" class="captcha-input"
                  maxlength="3" size="medium" @keyup.enter="handleRegister" />
                <i class="el-icon-refresh captcha-refresh" @click="refreshCaptcha"></i>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" class="auth-submit-btn ripple-btn"
                         @click="handleRegister">
                <span v-if="!loading">注册</span>
                <span v-else class="loading-text">
                  <span class="loading-dot">.</span><span class="loading-dot">.</span><span class="loading-dot">.</span>
                </span>
              </el-button>
            </el-form-item>
          </el-form>

          <div class="auth-form-footer">
            <span>已有账号？</span>
            <router-link to="/login" class="auth-link">
              立即登录 <i class="el-icon-right"></i>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import { captchaApi, emailApi } from '../api';

export default {
  name: 'Register',
  data() {
    const validatePass2 = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    return {
      form: { username: '', email: '', password: '', confirmPassword: '', captchaId: '', captchaAnswer: '', emailCode: '' },
      loading: false,
      focusedField: '',
      shaking: false,
      captchaId: '',
      captchaImage: '',
      codeSending: false,
      codeCountdown: 0,
      codeTimer: null,
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 4, max: 20, message: '用户名长度 4-20 位', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '仅支持字母、数字、下划线', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ],
        emailCode: [
          { required: true, message: '请先获取邮箱验证码', trigger: 'blur' },
          { pattern: /^\d{6}$/, message: '验证码为6位数字', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 12, message: '密码至少 12 位', trigger: 'blur' },
          { pattern: /[a-z]/, message: '需包含小写字母', trigger: 'blur' },
          { pattern: /[A-Z]/, message: '需包含大写字母', trigger: 'blur' },
          { pattern: /\d/, message: '需包含数字', trigger: 'blur' },
          { pattern: /[!@#$%^&*()_+\-={}\[\]:";,.<>/?~`]/, message: '需包含特殊字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validatePass2, trigger: 'blur' }
        ],
        captchaAnswer: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ]
      }
    };
  },
  mounted() { this.refreshCaptcha(); },
  beforeDestroy() { this.clearCodeTimer(); },
  methods: {
    ...mapActions(['register']),
    clearCodeTimer() {
      if (this.codeTimer) { clearInterval(this.codeTimer); this.codeTimer = null; }
    },
    async refreshCaptcha() {
      try {
        const res = await captchaApi.getCaptcha();
        if (res.code === 200 && res.data) {
          this.captchaId = res.data.captchaId;
          this.captchaImage = res.data.captchaImage;
          this.form.captchaId = res.data.captchaId;
          this.form.captchaAnswer = '';
        }
      } catch (e) {
        console.warn('获取验证码失败', e);
      }
    },
    async handleSendCode() {
      if (!this.form.email || !/^[\w.-]+@[\w.-]+\.\w{2,}$/.test(this.form.email)) {
        this.$message.warning('请先输入正确的邮箱地址');
        return;
      }
      this.codeSending = true;
      try {
        const res = await emailApi.sendCode(this.form.email);
        if (res.code === 200) {
          this.$message.success('验证码已发送到邮箱，请查收');
          this.codeCountdown = 60;
          this.codeTimer = setInterval(() => {
            this.codeCountdown--;
            if (this.codeCountdown <= 0) {
              this.clearCodeTimer();
            }
          }, 1000);
        } else {
          this.$message.error(res.message || '发送失败');
        }
      } catch (e) {
        this.$message.error('发送失败，请检查邮箱是否正确');
      } finally {
        this.codeSending = false;
      }
    },
    async handleRegister() {
      const valid = await this.$refs.formRef.validate().catch(() => false);
      if (!valid) {
        this.triggerShake();
        return;
      }

      this.form.captchaId = this.captchaId;
      this.loading = true;
      try {
        const res = await this.register({
          username: this.form.username,
          password: this.form.password,
          email: this.form.email,
          captchaId: this.form.captchaId,
          captchaAnswer: Number(this.form.captchaAnswer),
          emailCode: this.form.emailCode
        });
        if (res.code === 200) {
          this.$message.success('注册成功 🎉 请登录');
          this.$router.push('/login');
        } else {
          this.$message.error(res.message || '注册失败');
          this.refreshCaptcha();
          this.triggerShake();
        }
      } catch (e) {
        this.$message.error('网络错误，请重试');
        this.refreshCaptcha();
      } finally {
        this.loading = false;
      }
    },
    triggerShake() {
      this.shaking = true;
      setTimeout(() => { this.shaking = false; }, 500);
    }
  }
};
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - var(--header-height, 64px) - 180px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

/* ===== 背景装饰 ===== */
.auth-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}
.auth-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.2;
  will-change: transform;
}
.auth-orb-1 {
  width: 500px; height: 500px;
  background: linear-gradient(135deg, #1a56db, #3b82f6);
  top: -200px; right: -100px;
  animation: authFloat 8s ease-in-out infinite;
}
.auth-orb-2 {
  width: 400px; height: 400px;
  background: linear-gradient(135deg, #0ea5e9, #38bdf8);
  bottom: -150px; left: -80px;
  animation: authFloat 10s ease-in-out infinite reverse;
}
.auth-orb-3 {
  width: 250px; height: 250px;
  background: linear-gradient(135deg, #059669, #10b981);
  top: 25%; left: 25%;
  animation: authFloat 12s ease-in-out infinite 3s;
}
@keyframes authFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.08); }
}
.auth-ring {
  position: absolute;
  width: 600px; height: 600px;
  border: 1.5px solid rgba(59, 130, 246, 0.08);
  border-radius: 50%;
  top: 50%; left: 50%;
  margin-top: -300px; margin-left: -300px;
  animation: authRing 20s linear infinite;
  transform-style: preserve-3d;
  perspective: 800px;
}
@keyframes authRing {
  0% { transform: rotateX(60deg) rotateY(0deg); }
  100% { transform: rotateX(60deg) rotateY(360deg); }
}
.auth-float-icon {
  position: absolute;
  font-family: var(--font-mono, monospace);
  font-size: 24px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.06);
}
.afi-1 { top: 15%; left: 10%; animation: authFloat3d 7s ease-in-out infinite; }
.afi-2 { bottom: 20%; right: 15%; animation: authFloat3d 9s ease-in-out infinite reverse; }
.afi-3 { top: 55%; left: 6%; animation: authFloat3d 8s ease-in-out infinite 2s; }
@keyframes authFloat3d {
  0%, 100% { transform: translateZ(0) translateY(0); opacity: 0.06; }
  50% { transform: translateZ(30px) translateY(-15px); opacity: 0.12; }
}

/* ===== 主容器 ===== */
.auth-container {
  display: flex;
  width: 100%;
  max-width: 880px;
  min-height: 560px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  animation: fadeInUp 0.5s ease;
  position: relative;
  z-index: 1;
}
.shake-error {
  animation: shakeX 0.5s ease;
}
@keyframes shakeX {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-6px); }
  20%, 40%, 60%, 80% { transform: translateX(6px); }
}

/* ===== 左侧品牌区 ===== */
.auth-brand {
  flex: 1;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  overflow: hidden;
}
.auth-brand::before {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.15), transparent);
  top: -80px;
  right: -80px;
  border-radius: 50%;
  animation: brandPulse 4s ease-in-out infinite;
}
.auth-brand::after {
  content: '';
  position: absolute;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(124, 58, 237, 0.12), transparent);
  bottom: -40px;
  left: -40px;
  border-radius: 50%;
  animation: brandPulse 5s ease-in-out infinite reverse;
}
@keyframes brandPulse {
  0%, 100% { transform: scale(1); opacity: 0.6; }
  50% { transform: scale(1.15); opacity: 1; }
}
.auth-brand-content {
  position: relative;
  z-index: 1;
  text-align: center;
}
.auth-brand-logo {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-family: var(--font-mono, monospace);
  font-weight: 700;
  font-size: 24px;
  color: white;
  background: linear-gradient(135deg, #1a56db, #0ea5e9);
  width: 56px;
  height: 56px;
  border-radius: 14px;
  justify-content: center;
  margin-bottom: 20px;
  transition: transform 0.4s, box-shadow 0.4s;
}
.auth-brand-logo:hover {
  transform: rotate(-5deg) scale(1.05);
  box-shadow: 0 8px 24px rgba(26, 86, 219, 0.3);
}
.brand-slash { color: #fbbf24; }
.auth-brand-title {
  font-size: 28px;
  font-weight: 700;
  color: white;
  margin-bottom: 6px;
}
.auth-brand-desc {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 36px;
}
.auth-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  justify-content: center;
}
.auth-stat-item { text-align: center; }
.auth-stat-num { display: block; font-size: 22px; font-weight: 700; color: white; margin-bottom: 2px; }
.auth-stat-label { font-size: 12px; color: #64748b; }
.auth-stat-divider { width: 1px; height: 32px; background: rgba(255,255,255,0.1); }

.brand-dots {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}
.brand-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: dotPulseSelf 2s ease-in-out infinite;
}
.brand-dot:nth-child(2) { animation-delay: 0.3s; }
.brand-dot:nth-child(3) { animation-delay: 0.6s; }
@keyframes dotPulseSelf {
  0%, 100% { opacity: 0.2; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.3); }
}

/* ===== 右侧表单 ===== */
.auth-form-wrap {
  flex: 1.1;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}
.auth-form-card {
  width: 100%;
  max-width: 340px;
}
.auth-form-header {
  margin-bottom: 24px;
}
.auth-form-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary, #0f172a);
  margin-bottom: 6px;
}
.auth-form-desc {
  font-size: 14px;
  color: var(--text-secondary, #475569);
}

/* ===== 输入框 ===== */
.auth-input {
  position: relative;
  transition: transform 0.3s;
}
.auth-input:focus-within {
  transform: translateY(-1px);
}
.auth-input >>> .el-input__inner {
  height: 46px;
  border-radius: 10px;
  border: 1.5px solid var(--border, #e2e8f0);
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.8);
  padding-left: 40px;
}
.auth-input >>> .el-input__inner:focus {
  border-color: var(--primary, #1a56db);
  box-shadow: 0 0 0 3px rgba(26, 86, 219, 0.1), 0 0 16px rgba(26, 86, 219, 0.06);
}
.auth-input >>> .el-input__prefix {
  left: 12px;
}
.auth-input-icon {
  font-size: 16px;
  color: var(--text-tertiary, #94a3b8);
  transition: color 0.3s;
}
.auth-input:focus-within .auth-input-icon {
  color: var(--primary, #1a56db);
}

/* 邮箱验证码 */
.email-code-row {
  display: flex;
  gap: 8px;
  width: 100%;
}
.ec-input { flex: 1; }
.ec-input >>> .el-input__inner { padding-left: 12px !important; border-radius: 10px; }
.ec-btn {
  flex-shrink: 0;
  min-width: 100px;
  border-radius: 10px !important;
  font-size: 13px;
}

.password-hint {
  font-size: 11px;
  color: var(--text-tertiary, #94a3b8);
  line-height: 1.4;
  margin-top: 4px;
  padding-left: 2px;
}

/* ===== 按钮 ===== */
.auth-submit-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px !important;
  margin-top: 4px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s !important;
}
.auth-submit-btn:hover {
  box-shadow: 0 6px 24px rgba(26, 86, 219, 0.35) !important;
  transform: translateY(-2px) !important;
}
.ripple-btn {
  position: relative;
  overflow: hidden;
}
.ripple-btn::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.25) 10%, transparent 10%);
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0;
  transition: opacity 0.6s, background-size 0.6s;
}
.ripple-btn:active::after {
  background-size: 0% 0%;
  opacity: 1;
  transition: 0s;
}

.loading-text {
  display: inline-flex;
}
.loading-dot {
  animation: dotBounce 1.4s ease-in-out infinite both;
  font-size: 24px;
  line-height: 0;
  margin: 0 1px;
}
.loading-dot:nth-child(1) { animation-delay: 0s; }
.loading-dot:nth-child(2) { animation-delay: 0.2s; }
.loading-dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes dotBounce {
  0%, 80%, 100% { transform: translateY(0); opacity: 0.3; }
  40% { transform: translateY(-8px); opacity: 1; }
}

.auth-form-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-secondary, #475569);
}
.auth-link {
  color: var(--primary, #1a56db);
  font-weight: 500;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 2px;
}
.auth-link:hover {
  gap: 6px;
  color: #1e40af;
}
.auth-link i {
  font-size: 12px;
  transition: transform 0.3s;
}
.auth-link:hover i {
  transform: translateX(2px);
}

@media (max-width: 640px) {
  .auth-brand { display: none; }
  .auth-container { max-width: 400px; }
  .auth-form-wrap { flex: 1; }
  .auth-page { padding: 20px 12px; }
}

/* 验证码 */
.captcha-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}
.captcha-img {
  width: 110px;
  height: 38px;
  border-radius: 6px;
  cursor: pointer;
  border: 1px solid var(--border, #e2e8f0);
  flex-shrink: 0;
}
.captcha-img:hover { opacity: 0.8; }
.captcha-input { flex: 1; }
.captcha-input >>> .el-input__inner { padding-left: 12px !important; }
.captcha-refresh {
  font-size: 18px;
  color: var(--text-tertiary, #94a3b8);
  cursor: pointer;
  flex-shrink: 0;
  transition: color 0.3s, transform 0.3s;
}
.captcha-refresh:hover { color: var(--primary, #1a56db); transform: rotate(180deg); }
</style>
