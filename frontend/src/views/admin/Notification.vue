<template>
  <div class="admin-page">
    <div class="admin-page-header">
      <h3 class="admin-page-title">📧 邮件通知</h3>
      <p class="admin-page-subtitle">向用户发送系统通知邮件</p>
    </div>

    <el-card class="notify-card" shadow="never">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="80px">
        <el-form-item label="接收方式" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="all">全部用户</el-radio>
            <el-radio label="single">指定用户</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="接收邮箱" prop="email" v-if="form.type === 'single'">
          <el-input v-model="form.email" placeholder="输入用户邮箱" class="notify-input" />
        </el-form-item>

        <el-form-item label="通知标题" prop="subject">
          <el-input v-model="form.subject" placeholder="邮件主题" class="notify-input" />
        </el-form-item>

        <el-form-item label="通知内容" prop="content">
          <el-input type="textarea" v-model="form.content" :rows="8"
            placeholder="输入通知内容..." class="notify-textarea" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="sending" @click="handleSend" class="notify-send-btn">
            <i class="el-icon-s-promotion"></i> {{ sending ? '发送中...' : '发送通知' }}
          </el-button>
          <span class="notify-hint" v-if="form.type === 'all'">
            将发送给所有已注册用户的邮箱
          </span>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="history-card" shadow="never" v-if="sentLog.length > 0">
      <template #header><span>发送记录</span></template>
      <div v-for="(log, i) in sentLog" :key="i" class="sent-log-item">
        <span class="log-time">{{ log.time }}</span>
        <span class="log-target">{{ log.target }}</span>
        <span class="log-subject">{{ log.subject }}</span>
      </div>
    </el-card>
  </div>
</template>

<script>
import { adminApi, emailApi } from '../../api';

export default {
  name: 'AdminNotification',
  data() {
    return {
      form: { type: 'all', email: '', subject: '', content: '' },
      sending: false,
      sentLog: [],
      rules: {
        subject: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }],
        email: [{ required: true, message: '请输入接收邮箱', trigger: 'blur' }]
      }
    };
  },
  methods: {
    async handleSend() {
      const valid = await this.$refs.formRef.validate().catch(() => false);
      if (!valid) return;

      this.sending = true;
      try {
        const res = await adminApi.sendNotification(this.form);
        if (res.code === 200) {
          this.$message.success(res.message || '发送成功');
          this.sentLog.unshift({
            time: new Date().toLocaleString('zh-CN'),
            target: this.form.type === 'all' ? '全部用户' : this.form.email,
            subject: this.form.subject
          });
          if (this.form.type === 'single') {
            this.form.email = '';
          }
          this.form.subject = '';
          this.form.content = '';
        } else {
          this.$message.error(res.message || '发送失败');
        }
      } catch (e) {
        this.$message.error('发送失败：' + (e.message || '网络异常，请检查控制台'));
      } finally {
        this.sending = false;
      }
    }
  }
};
</script>

<style scoped>
.notify-card { margin-bottom: 20px; }
.notify-input { width: 100%; max-width: 500px; }
.notify-textarea { max-width: 600px; }
.notify-send-btn { margin-right: 12px; }
.notify-hint { font-size: 12px; color: var(--text-tertiary); }
.history-card { margin-top: 16px; }
.sent-log-item {
  display: flex;
  gap: 16px;
  padding: 8px 0;
  border-bottom: 1px solid var(--border);
  font-size: 13px;
}
.sent-log-item:last-child { border-bottom: none; }
.log-time { color: var(--text-tertiary); min-width: 140px; }
.log-target { color: var(--primary); min-width: 120px; }
.log-subject { color: var(--text-primary); }
</style>
