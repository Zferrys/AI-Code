<template>
  <div class="code-editor">
    <textarea ref="textarea" :value="value" @input="handleInput"></textarea>
  </div>
</template>

<script>
// 使用原生 textarea 实现代码编辑（避免 vue-codemirror 构建复杂依赖）
// 后续可替换为 CodeMirror 集成
export default {
  name: 'CodeEditor',
  props: {
    value: { type: String, default: '' },
    language: { type: String, default: 'java' }
  },
  methods: {
    handleInput(e) {
      this.$emit('input', e.target.value);
    }
  }
};
</script>

<style scoped>
.code-editor {
  border: 1px solid var(--border-color);
  border-radius: 6px;
  overflow: hidden;
}

.code-editor textarea {
  width: 100%;
  min-height: 350px;
  padding: 16px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  border: none;
  outline: none;
  resize: vertical;
  background: #1e293b;
  color: #e2e8f0;
  tab-size: 4;
}
</style>
