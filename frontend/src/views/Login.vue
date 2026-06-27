<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="auth-orb auth-orb-1"></div>
      <div class="auth-orb auth-orb-2"></div>
      <div class="auth-orb auth-orb-3"></div>
      <div class="auth-ring"></div>
      <span class="auth-float-icon afi-1">&lt;/&gt;</span>
      <span class="auth-float-icon afi-2">{ }</span>
      <span class="auth-float-icon afi-3">#</span>
      <span class="auth-float-icon afi-4">=></span>
      <span class="auth-float-icon afi-5">*</span>
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
          <div class="auth-brand-features">
            <div class="brand-feature" style="animation-delay: 0.1s">
              <i class="el-icon-success"></i>
              <span>AI 智能代码审查</span>
            </div>
            <div class="brand-feature" style="animation-delay: 0.2s">
              <i class="el-icon-success"></i>
              <span>实时编程问答</span>
            </div>
            <div class="brand-feature" style="animation-delay: 0.3s">
              <i class="el-icon-success"></i>
              <span>个性化学习路径</span>
            </div>
          </div>
        </div>
        <!-- 底部装饰点 -->
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
            <h2 class="auth-form-title">欢迎回来</h2>
            <p class="auth-form-desc">登录你的账号继续学习</p>
          </div>

          <el-form :model="form" :rules="rules" ref="formRef" label-width="0" size="large"
                   @submit.native.prevent="handleLogin">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="用户名"
                class="auth-input"
                @focus="focusedField = 'username'"
                @blur="focusedField = ''"
              >
                <i slot="prefix" class="el-icon-user auth-input-icon"></i>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                @keyup.enter="handleLogin"
                class="auth-input"
                show-password
                @focus="focusedField = 'password'"
                @blur="focusedField = ''"
              >
                <i slot="prefix" class="el-icon-lock auth-input-icon"></i>
              </el-input>
            </el-form-item>
            <!-- 验证码 (登录失败 3 次后显示) -->
            <el-form-item v-if="showCaptcha" prop="captchaAnswer">
              <div class="captcha-row">
                <img :src="captchaImage" alt="验证码" class="captcha-img" @click="refreshCaptcha" />
                <el-input v-model="form.captchaAnswer" placeholder="答案" class="captcha-input"
                  maxlength="3" size="medium" @keyup.enter="handleLogin" />
                <i class="el-icon-refresh captcha-refresh" @click="refreshCaptcha"></i>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" class="auth-submit-btn ripple-btn"
                         @click="handleLogin">
                <span v-if="!loading">登录</span>
                <span v-else class="loading-text">
                  <span class="loading-dot">.</span><span class="loading-dot">.</span><span class="loading-dot">.</span>
                </span>
              </el-button>
            </el-form-item>
          </el-form>

          <!-- 忘记密码 -->
          <div class="auth-forgot-row">
            <span class="auth-forgot-link" @click="showForgotDialog = true">
              忘记密码？
            </span>
          </div>

          <div class="auth-form-footer">
            <span>还没有账号？</span>
            <router-link to="/register" class="auth-link">
              立即注册 <i class="el-icon-right"></i>
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- 忘记密码对话框 -->
    <el-dialog title="重置密码" :visible.sync="showForgotDialog" width="400px" top="25vh"
               class="forgot-dialog" :close-on-click-modal="false"
               @closed="resetForgotDialog">
      <div class="dialog-step" v-if="forgotStep === 1">
        <p class="dialog-desc">输入你的注册邮箱，我们将发送验证码</p>
        <el-input v-model="forgotEmail" placeholder="请输入注册邮箱" class="dialog-input"
                  @keyup.enter="handleSendResetCode">
          <i slot="prefix" class="el-icon-message"></i>
        </el-input>
        <el-button type="primary" class="dialog-action-btn" @click="handleSendResetCode"
                   :loading="sendingResetCode" :disabled="resetCodeSent">
          <i class="el-icon-message"></i> {{ resetCodeSent ? '验证码已发送' : '发送验证码' }}
        </el-button>
        <p v-if="resetCodeSent" class="dialog-code-hint">验证码已发送，请查看你的邮箱</p>
      </div>
      <div class="dialog-step" v-if="forgotStep === 2">
        <p class="dialog-desc">验证身份后设置新密码</p>
        <el-form label-width="70px">
          <el-form-item label="验证码">
            <el-input v-model="forgotCode" placeholder="6 位验证码" maxlength="6" class="dialog-input" />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="forgotNewPwd" type="password" placeholder="至少 6 位"
                      class="dialog-input" show-password />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input v-model="forgotConfirmPwd" type="password" placeholder="再次输入新密码"
                      class="dialog-input" show-password />
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer">
        <el-button @click="showForgotDialog = false">取消</el-button>
        <el-button v-if="forgotStep === 2" type="primary" @click="handleResetPassword"
                   :loading="resettingPwd">
          <i class="el-icon-check"></i> 重置密码
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import { captchaApi, emailApi, userApi } from '../api';

export default {
  name: 'Login',
  data() {
    return {
      form: { username: '', password: '', captchaId: '', captchaAnswer: '' },
      loading: false,
      focusedField: '',
      shaking: false,
      failedAttempts: 0,
      captchaId: '',
      captchaImage: '',
      // 忘记密码
      showForgotDialog: false,
      forgotStep: 1,
      forgotEmail: '',
      forgotCode: '',
      forgotNewPwd: '',
      forgotConfirmPwd: '',
      sendingResetCode: false,
      resetCodeSent: false,
      resettingPwd: false,
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        captchaAnswer: [{
          required: true, message: '请输入验证码', trigger: 'blur',
          validator: (rule, value, callback) => {
            if (this.showCaptcha && !value) callback(new Error('请输入验证码'));
            else callback();
          }
        }]
      }
    };
  },
  computed: {
    showCaptcha() { return this.failedAttempts >= 3; }
  },
  methods: {
    ...mapActions(['login']),
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
    async handleLogin() {
      const valid = await this.$refs.formRef.validate().catch(() => false);
      if (!valid) {
        this.triggerShake();
        return;
      }

      this.form.captchaId = this.captchaId;
      this.loading = true;
      try {
        const res = await this.login(this.form);
        if (res.code === 200) {
          this.failedAttempts = 0;
          this.$message.success('登录成功！🎉');
          let redirect = this.$route.query.redirect || '/';
          const user = JSON.parse(sessionStorage.getItem('user') || 'null');
          if (redirect.startsWith('/admin') && user && user.role !== 'ADMIN') {
            redirect = '/';
          }
          this.$router.push(redirect);
        } else {
          this.failedAttempts++;
          if (this.failedAttempts >= 3) this.refreshCaptcha();
          this.triggerShake();
        }
      } finally {
        this.loading = false;
      }
    },
    triggerShake() {
      this.shaking = true;
      setTimeout(() => { this.shaking = false; }, 500);
    },
    // ---- 忘记密码 ----
    resetForgotDialog() {
      this.forgotStep = 1;
      this.forgotEmail = '';
      this.forgotCode = '';
      this.forgotNewPwd = '';
      this.forgotConfirmPwd = '';
      this.resetCodeSent = false;
      this.sendingResetCode = false;
      this.resettingPwd = false;
    },
    async handleSendResetCode() {
      if (!this.forgotEmail || !/^[\w.-]+@[\w.-]+\.\w{2,}$/.test(this.forgotEmail)) {
        this.$message.warning('请输入正确的邮箱地址');
        return;
      }
      this.sendingResetCode = true;
      try {
        const res = await emailApi.sendCode(this.forgotEmail);
        if (res.code === 200) {
          this.$message.success('验证码已发送');
          this.resetCodeSent = true;
          this.forgotStep = 2;
        } else {
          this.$message.error(res.message || '发送失败');
        }
      } catch {
        this.$message.error('网络异常，请稍后重试');
      } finally {
        this.sendingResetCode = false;
      }
    },
    async handleResetPassword() {
      if (!this.forgotCode || this.forgotCode.length !== 6) {
        this.$message.warning('请输入 6 位验证码');
        return;
      }
      if (!this.forgotNewPwd || this.forgotNewPwd.length < 6) {
        this.$message.warning('新密码不能少于 6 位');
        return;
      }
      if (this.forgotNewPwd !== this.forgotConfirmPwd) {
        this.$message.warning('两次输入的密码不一致');
        return;
      }
      this.resettingPwd = true;
      try {
        const res = await userApi.resetPassword({
          email: this.forgotEmail,
          code: this.forgotCode,
          newPassword: this.forgotNewPwd
        });
        if (res.code === 200) {
          this.$message.success('密码重置成功，请使用新密码登录 🎉');
          this.showForgotDialog = false;
          this.resetForgotDialog();
        } else {
          this.$message.error(res.message || '重置失败');
        }
      } catch {
        this.$message.error('网络异常，请稍后重试');
      } finally {
        this.resettingPwd = false;
      }
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
  top: 20%; left: 30%;
  animation: authFloat 12s ease-in-out infinite 3s;
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
.auth-float-icon {
  position: absolute;
  font-family: var(--font-mono, monospace);
  font-size: 24px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.06);
  transition: color 0.5s;
}
.afi-1 { top: 12%; left: 8%; animation: authFloat3d 7s ease-in-out infinite; }
.afi-2 { bottom: 18%; right: 12%; animation: authFloat3d 9s ease-in-out infinite reverse; }
.afi-3 { top: 45%; left: 4%; font-size: 18px; animation: authFloat3d 8s ease-in-out infinite 2s; }
.afi-4 { top: 30%; right: 6%; animation: authFloat3d 11s ease-in-out infinite 1s; }
.afi-5 { bottom: 35%; left: 15%; animation: authFloat3d 6s ease-in-out infinite 3s; }

@keyframes authFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.08); }
}
@keyframes authRing {
  0% { transform: rotateX(60deg) rotateY(0deg); }
  100% { transform: rotateX(60deg) rotateY(360deg); }
}
@keyframes authFloat3d {
  0%, 100% { transform: translateZ(0) translateY(0); opacity: 0.06; }
  50% { transform: translateZ(30px) translateY(-15px); opacity: 0.12; }
}

/* ===== 主容器 ===== */
.auth-container {
  display: flex;
  width: 100%;
  max-width: 880px;
  min-height: 520px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  animation: fadeInUp 0.5s ease;
  position: relative;
  z-index: 1;
}

/* 表单验证错误抖动 */
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
  background: radial-gradient(circle, rgba(14, 165, 233, 0.12), transparent);
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

.brand-feature {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #cbd5e1;
  font-size: 14px;
  margin-bottom: 14px;
  justify-content: center;
  animation: featureSlide 0.5s ease both;
  transition: transform 0.3s;
}
.brand-feature:hover {
  transform: translateX(4px);
  color: white;
}
.brand-feature i {
  color: #10b981;
  font-size: 16px;
}
@keyframes featureSlide {
  from { opacity: 0; transform: translateX(-16px); }
  to { opacity: 1; transform: translateX(0); }
}

/* 底部装饰点 */
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
  margin-bottom: 28px;
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

/* ===== 输入框增强 ===== */
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
  transition: color 0.3s;
}
.auth-input-icon {
  font-size: 16px;
  color: var(--text-tertiary, #94a3b8);
  transition: color 0.3s;
}
.auth-input:focus-within .auth-input-icon {
  color: var(--primary, #1a56db);
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
.auth-submit-btn:active {
  transform: translateY(0) !important;
}

/* 涟漪按钮 */
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

/* 加载动画 */
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

/* ===== 忘记密码 ===== */
.auth-forgot-row {
  text-align: right;
  margin-top: -12px;
  margin-bottom: 4px;
}
.auth-forgot-link {
  font-size: 13px;
  color: var(--text-tertiary, #94a3b8);
  cursor: pointer;
  transition: color 0.3s;
}
.auth-forgot-link:hover {
  color: var(--primary, #1a56db);
}

/* ===== 底部链接 ===== */
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

/* ===== 响应式 ===== */
/* ===== 验证码 ===== */
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

/* ===== 忘记密码对话框 ===== */
.forgot-dialog >>> .el-dialog {
  border-radius: 14px;
}
.forgot-dialog >>> .el-dialog__header {
  border-bottom: 1px solid var(--border, #e2e8f0);
  padding: 18px 24px;
}
.forgot-dialog >>> .el-dialog__title {
  font-weight: 600;
  font-size: 16px;
}
.forgot-dialog >>> .el-dialog__body {
  padding: 24px;
  min-height: 120px;
}
.forgot-dialog >>> .el-dialog__footer {
  border-top: 1px solid var(--border, #e2e8f0);
  padding: 14px 24px;
}
.dialog-step {
  min-height: 60px;
}
.dialog-desc {
  font-size: 14px;
  color: var(--text-secondary, #475569);
  margin-bottom: 14px;
  line-height: 1.6;
}
.dialog-input {
  margin-bottom: 12px;
}
.dialog-input >>> .el-input__inner {
  border-radius: 10px;
  height: 42px;
  border: 1.5px solid var(--border, #e2e8f0);
  padding-left: 36px !important;
  transition: all 0.3s;
}
.dialog-input >>> .el-input__inner:focus {
  border-color: var(--primary, #1a56db);
  box-shadow: 0 0 0 3px rgba(26, 86, 219, 0.1);
}
.dialog-input >>> .el-input__prefix {
  left: 10px;
  font-size: 16px;
  color: var(--text-tertiary, #94a3b8);
}
.dialog-action-btn {
  border-radius: 10px !important;
  width: 100%;
  margin-top: 4px;
}
.dialog-code-hint {
  font-size: 13px;
  color: var(--primary, #1a56db);
  margin-top: 10px;
}

@media (max-width: 640px) {
  .auth-brand { display: none; }
  .auth-container { max-width: 400px; }
  .auth-form-wrap { flex: 1; }
  .auth-page { padding: 20px 12px; }
}
</style>
