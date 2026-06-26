<template>
  <div class="markdown-body" v-html="rendered"></div>
</template>

<script>
import hljs from 'highlight.js';
import mermaid from 'mermaid';

// 初始化 Mermaid
let mermaidInitialized = false;
function initMermaid() {
  if (mermaidInitialized) return;
  mermaid.initialize({
    startOnLoad: false,
    theme: 'default',
    securityLevel: 'loose',
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
  });
  mermaidInitialized = true;
}

export default {
  name: 'MarkdownRenderer',
  props: {
    content: { type: String, default: '' }
  },
  computed: {
    rendered() {
      if (!this.content) return '';

      // 阶段 1: 提取代码块和 mermaid 块，替换为占位符
      const blocks = [];
      let blockId = 0;
      let html = this.content;

      // 提取 ```mermaid 块
      const mermaidBlocks = [];
      html = html.replace(/```mermaid\n?([\s\S]*?)```/g, (match, code) => {
        const id = `mermaid-${Date.now()}-${blockId++}`;
        const placeholder = `\x00MERMAID_${id}\x00`;
        mermaidBlocks.push({ id, code: code.trim(), placeholder });
        return placeholder;
      });

      // 提取 ``` 代码块
      html = html.replace(/```(\w*)\n?([\s\S]*?)```/g, (match, lang, code) => {
        const id = blockId++;
        const placeholder = `\x00CODE_${id}\x00`;
        const trimmed = code.trim();
        let highlighted;
        if (lang && hljs.getLanguage(lang)) {
          try { highlighted = hljs.highlight(trimmed, { language: lang }).value; }
          catch { highlighted = this.escapeHtml(trimmed); }
        } else {
          try { highlighted = hljs.highlightAuto(trimmed).value; }
          catch { highlighted = this.escapeHtml(trimmed); }
        }
        const rendered = `<pre class="code-block"><code class="hljs lang-${lang}">${highlighted}</code></pre>`;
        blocks[placeholder] = rendered;
        return placeholder;
      });

      // 阶段 2: 非代码内容做 HTML 转义（防 XSS）
      html = this.escapeHtml(html);

      // 恢复代码块占位符（不转义，它们已经是安全的 HTML）
      for (const [placeholder, rendered] of Object.entries(blocks)) {
        html = html.replace(placeholder, rendered);
      }

      // 恢复 mermaid 占位符
      for (const mb of mermaidBlocks) {
        html = html.replace(mb.placeholder,
          `<div class="mermaid-wrapper"><div class="mermaid" id="${mb.id}">${this.escapeHtml(mb.code)}</div></div>`);
      }

      // 阶段 3: 处理 Markdown 语法
      // 行内代码 (`)
      html = html.replace(/`([^`]+)`/g, '<code class="inline-code">$1</code>');

      // 标题 (## / ###)
      html = html.replace(/### (.+)/g, '<h3>$1</h3>');
      html = html.replace(/## (.+)/g, '<h2>$1</h2>');

      // 加粗 **
      html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');

      // 无序列表
      html = html.replace(/^- (.+)/gm, '<li>$1</li>');
      html = html.replace(/(<li>.*<\/li>\n?)+/g, '<ul>$&</ul>');

      // 有序列表
      html = html.replace(/^\d+\. (.+)/gm, '<li>$1</li>');

      // 链接
      html = html.replace(/\[(.+?)\]\((.+?)\)/g, '<a href="$2" target="_blank">$1</a>');

      // 段落（双换行分割）
      const paragraphs = html.split(/\n\n+/);
      html = paragraphs.map(p => {
        p = p.trim();
        if (!p) return '';
        if (p.startsWith('<h') || p.startsWith('<ul') || p.startsWith('<pre') || p.startsWith('<li')) return p;
        return `<p>${p}</p>`;
      }).join('\n');

      return html;
    }
  },
  mounted() {
    this.$nextTick(() => this.renderMermaid());
  },
  updated() {
    this.$nextTick(() => this.renderMermaid());
  },
  methods: {
    renderMermaid() {
      initMermaid();
      this.$nextTick(() => {
        try {
          mermaid.run({ querySelector: '.mermaid' });
        } catch (e) {
          console.warn('Mermaid 渲染失败:', e);
        }
      });
    },
    escapeHtml(text) {
      const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
      return text.replace(/[&<>"']/g, c => map[c]);
    }
  }
};
</script>

<style scoped>
.markdown-body {
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-primary);
}

.markdown-body >>> h2 { font-size: 22px; margin: 24px 0 12px; }
.markdown-body >>> h3 { font-size: 18px; margin: 20px 0 10px; }
.markdown-body >>> p { margin: 12px 0; }
.markdown-body >>> ul, .markdown-body >>> ol { padding-left: 24px; margin: 8px 0; }
.markdown-body >>> li { margin: 4px 0; }

.markdown-body >>> .code-block {
  background: #1e293b;
  color: #e2e8f0;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 13px;
  line-height: 1.6;
  margin: 16px 0;
}

.markdown-body >>> .inline-code {
  background: #f1f5f9;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
  color: #e11d48;
}

.markdown-body >>> a { color: var(--primary-color); text-decoration: underline; }
.markdown-body >>> strong { font-weight: 600; }

.markdown-body >>> .mermaid-wrapper {
  overflow-x: auto;
  padding: 16px 0;
  margin: 16px 0;
  text-align: center;
}
.markdown-body >>> .mermaid svg {
  max-width: 100%;
  height: auto;
}
</style>
