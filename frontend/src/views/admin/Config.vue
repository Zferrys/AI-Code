<template>
  <div class="admin-page">
    <div class="admin-page-header">
      <h3 class="admin-page-title">系统配置</h3>
      <p class="admin-page-subtitle">管理系统运行参数和全局设置</p>
    </div>

    <!-- ===== AI 配置快捷面板 ===== -->
    <div class="ai-config-card">
      <div class="ai-config-header">
        <i class="el-icon-ai"></i>
        <span>AI 服务配置</span>
      </div>
      <el-form label-width="100px" size="medium" class="ai-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="AI 供应商">
              <el-select v-model="aiConfig.provider" @change="onProviderChange" style="width:100%">
                <el-option label="Agnes AI" value="agnes" />
                <el-option label="DeepSeek" value="deepseek" />
                <el-option label="OpenAI" value="openai" />
                <el-option label="自定义" value="custom" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模型">
              <el-select v-model="aiConfig.model" style="width:100%">
                <el-option v-for="m in availableModels" :key="m.value" :label="m.label" :value="m.value" />
                <el-option v-if="aiConfig.provider === 'custom'" label="自定义（请在下方输入）" value="__custom__" disabled />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="API Key">
              <el-input v-model="aiConfig.apiKey" type="password" show-password
                placeholder="输入你的 API Key" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="API 地址">
              <el-input v-model="aiConfig.endpoint" placeholder="API 端点地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="saveAiConfig" :loading="savingAi">
            <i class="el-icon-check"></i> 保存 AI 配置
          </el-button>
          <span class="ai-config-hint">保存后立即生效，无需重启服务</span>
        </el-form-item>
      </el-form>
    </div>

    <!-- ===== 其他配置列表（AI 相关已移至上方面板） ===== -->
    <div class="admin-table-wrap" style="margin-top:20px">
      <el-table :data="otherConfigs" v-loading="loading" stripe size="medium">
        <el-table-column prop="configKey" label="配置键" width="220" />
        <el-table-column label="配置值" min-width="200">
          <template slot-scope="{row}">
            <span v-if="isSensitiveKey(row.configKey)" class="sensitive-value">
              <i class="el-icon-lock"></i> ●●●●●●●●
            </span>
            <span v-else class="config-value-text">{{ row.configValue || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" min-width="160" />
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="{row}">
            <el-button size="mini" type="primary" plain @click="editRow(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog title="编辑配置" :visible.sync="showDialog" width="560px" class="admin-dialog"
      :close-on-click-modal="false" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="配置键">
          <el-input :value="editForm.configKey" disabled />
        </el-form-item>
        <el-form-item :label="isSensitiveKey(editForm.configKey) ? '密钥值' : '值'">
          <el-input
            v-if="isSensitiveKey(editForm.configKey)"
            v-model="editForm.configValue"
            type="password" show-password
            placeholder="输入新的 API Key 值"
          />
          <el-input v-else type="textarea" v-model="editForm.configValue" :rows="3" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input :value="editForm.description" disabled type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showDialog=false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { adminApi } from '../../api';

const PROVIDER_PRESETS = {
  agnes: {
    endpoint: 'https://apihub.agnes-ai.com/v1/chat/completions',
    models: [
      { value: 'agnes-2.0-flash', label: 'Agnes 2.0 Flash（推荐）' },
      { value: 'agnes-1.5-flash', label: 'Agnes 1.5 Flash（轻量）' },
    ]
  },
  deepseek: {
    endpoint: 'https://api.deepseek.com/v1/chat/completions',
    models: [
      { value: 'deepseek-chat', label: 'DeepSeek Chat' },
      { value: 'deepseek-reasoner', label: 'DeepSeek Reasoner' },
    ]
  },
  openai: {
    endpoint: 'https://api.openai.com/v1/chat/completions',
    models: [
      { value: 'gpt-4o', label: 'GPT-4o' },
      { value: 'gpt-4o-mini', label: 'GPT-4o Mini' },
      { value: 'gpt-4-turbo', label: 'GPT-4 Turbo' },
    ]
  },
  custom: {
    endpoint: '',
    models: []
  }
};

export default {
  name: 'AdminConfig',
  data() {
    return {
      configs: [],
      loading: true,
      showDialog: false,
      editForm: {},
      saving: false,
      savingAi: false,
      sensitiveKeys: ['ai.api.key'],
      // AI 配置
      aiConfig: {
        provider: 'agnes',
        model: 'agnes-2.0-flash',
        apiKey: '',
        endpoint: 'https://apihub.agnes-ai.com/v1/chat/completions'
      }
    };
  },
  computed: {
    /** 过滤掉上面 AI 面板已管的配置 */
    otherConfigs() {
      const hidden = ['ai.api.endpoint', 'ai.api.key', 'ai.api.model'];
      return (this.configs || []).filter(c => !hidden.includes(c.configKey));
    },
    availableModels() {
      const preset = PROVIDER_PRESETS[this.aiConfig.provider];
      return preset ? preset.models : [];
    }
  },
  created() { this.load(); },
  methods: {
    isSensitiveKey(key) {
      return this.sensitiveKeys.includes(key);
    },
    async load() {
      this.loading = true;
      const res = await adminApi.getConfig();
      if (res.code === 200) {
        this.configs = res.data || [];
        this.syncAiConfig();
      }
      this.loading = false;
    },
    /** 从数据库配置同步到 AI 快捷面板 */
    syncAiConfig() {
      const cfg = {};
      this.configs.forEach(c => { cfg[c.configKey] = c.configValue; });

      this.aiConfig.endpoint = cfg['ai.api.endpoint'] || 'https://apihub.agnes-ai.com/v1/chat/completions';
      this.aiConfig.apiKey = cfg['ai.api.key'] || '';
      this.aiConfig.model = cfg['ai.api.model'] || 'agnes-2.0-flash';

      // 根据 endpoint 猜测供应商
      if (this.aiConfig.endpoint.includes('agnes')) this.aiConfig.provider = 'agnes';
      else if (this.aiConfig.endpoint.includes('deepseek')) this.aiConfig.provider = 'deepseek';
      else if (this.aiConfig.endpoint.includes('openai')) this.aiConfig.provider = 'openai';
      else this.aiConfig.provider = 'custom';
    },
    /** 切换供应商时自动填充默认值 */
    onProviderChange() {
      const preset = PROVIDER_PRESETS[this.aiConfig.provider];
      if (preset) {
        this.aiConfig.endpoint = preset.endpoint;
        if (preset.models.length > 0) {
          this.aiConfig.model = preset.models[0].value;
        }
      }
    },
    /** 一键保存 AI 配置 */
    async saveAiConfig() {
      if (!this.aiConfig.apiKey) {
        this.$message.warning('请输入 API Key');
        return;
      }
      this.savingAi = true;
      try {
        await Promise.all([
          adminApi.updateConfig({ configKey: 'ai.api.endpoint', configValue: this.aiConfig.endpoint }),
          adminApi.updateConfig({ configKey: 'ai.api.key', configValue: this.aiConfig.apiKey }),
          adminApi.updateConfig({ configKey: 'ai.api.model', configValue: this.aiConfig.model }),
        ]);
        this.$message.success('AI 配置保存成功，已生效');
        this.load();
      } catch {
        this.$message.error('保存失败');
      } finally {
        this.savingAi = false;
      }
    },
    // ---- 单条配置编辑 ----
    editRow(row) { this.editForm = { ...row }; this.showDialog = true; },
    async handleSave() {
      this.saving = true;
      await adminApi.updateConfig(this.editForm);
      this.$message.success('保存成功');
      this.showDialog = false;
      this.saving = false;
      this.load();
    }
  }
};
</script>

<style scoped>
.config-value-text {
  font-family: var(--font-mono, monospace);
  font-size: 13px;
  word-break: break-all;
}
.sensitive-value {
  color: var(--text-tertiary, #94a3b8);
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* AI 配置快捷面板 */
.ai-config-card {
  background: linear-gradient(135deg, #f8faff, #f0f4ff);
  border: 1px solid var(--border, #e2e8f0);
  border-radius: var(--radius-lg);
  padding: 24px;
  margin-bottom: 20px;
}
.ai-config-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border);
}
.ai-config-header i {
  font-size: 20px;
  color: var(--primary);
}
.ai-form .el-form-item {
  margin-bottom: 18px;
}
.ai-config-hint {
  font-size: 12px;
  color: var(--text-tertiary, #94a3b8);
  margin-left: 12px;
}
</style>
