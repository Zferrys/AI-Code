import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import 'highlight.js/styles/github-dark.css';
import App from './App.vue';
import router from './router';
import store from './store';
import './styles/global.css';

Vue.use(ElementUI);

Vue.config.productionTip = false;

// 全局错误处理 — 防止 [object Object] 运行时错误泄露到控制台
Vue.config.errorHandler = (err, vm, info) => {
  console.warn('[Vue 全局错误]', info, err?.message || err);
  // 不传播到 webpack-dev-server overlay
};
window.addEventListener('error', e => {
  if (e.target === window && e.message === '[object Object]') {
    e.preventDefault(); // 静默过滤 Object 错误
  }
});
window.addEventListener('unhandledrejection', e => {
  e.preventDefault(); // 防止未处理的 Promise 抛到 overlay
});

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');
