import axios from 'axios';
import router from '../router';
import { Message } from 'element-ui';

// 创建 Axios 实例
const http = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
});

// 请求拦截器 - 添加 Token + 防缓存
http.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // FormData 请求让浏览器自动设置 Content-Type（含 boundary）
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }
    // GET 请求加时间戳防浏览器缓存
    if (config.method === 'get' && config.params) {
      config.params._t = Date.now();
    } else if (config.method === 'get') {
      config.params = { ...(config.params || {}), _t: Date.now() };
    }
    return config;
  },
  error => Promise.reject(error)
);

// 响应拦截器 - 统一处理错误
http.interceptors.response.use(
  response => {
    const res = response.data;
    // 业务错误
    if (res.code && res.code !== 200) {
      // 401 未认证：有 token 才跳转登录（无 token 时静默处理，用于首页等公开页）
      if (res.code === 401) {
        const token = sessionStorage.getItem('token');
        if (token) {
          sessionStorage.removeItem('token');
          sessionStorage.removeItem('user');
          router.push({ name: 'Login' });
        }
        return res;
      }
      Message.error(res.message || '请求失败');
      return res;
    }
    return res;
  },
  error => {
    if (error.response) {
      const status = error.response.status;
      const data = error.response.data;
      if (status === 401) {
        const token = sessionStorage.getItem('token');
        if (token) {
          // 有 token 但失效了 → 清登录态跳转
          sessionStorage.removeItem('token');
          sessionStorage.removeItem('user');
          router.push({ name: 'Login' });
          Message.error('登录已过期，请重新登录');
        }
        // 无 token → 静默返回错误数据（用于首页等公开页）
        return Promise.resolve(data || { code: 401, message: '请先登录' });
      } else if (status === 413) {
        Message.error('文件大小超过限制');
      } else if (status >= 500) {
        Message.error('服务器内部错误');
      }
    } else {
      Message.error('网络连接异常');
      // 已向用户显示错误，resolve 避免组件 catch 重复提示
      return Promise.resolve({ code: 0, message: '网络连接异常' });
    }
  }
);

// ========== API 封装 ==========

// 用户相关
export const userApi = {
  login: data => http.post('/user/login', data),
  register: data => http.post('/user/register', data),
  getUserInfo: () => http.get('/user/info'),
  updateUserInfo: data => http.put('/user/info', data),
  getTechTags: () => http.get('/user/tags'),
  addTechTag: tagId => http.post('/user/tags', { tagId }),
  removeTechTag: tagId => http.delete(`/user/tags/${tagId}`),
  uploadAvatar: formData => http.post('/user/avatar', formData),
  changeEmail: data => http.put('/user/email', data),
  changePassword: data => http.put('/user/password', data),
  resetPassword: data => http.post('/user/reset-password', data)
};

// 标签相关
export const tagApi = {
  getAll: () => http.get('/tags'),
  getByCategory: category => http.get(`/tags/category/${category}`)
};

// 代码审查
export const codeReviewApi = {
  submit: data => http.post('/code-review/submit', data, { timeout: 180000 }),
  getStatus: id => http.get(`/code-review/${id}/status`),
  getDetail: id => http.get(`/code-review/${id}`),
  getList: params => http.get('/code-review/list', { params })
};

// 问答
export const qaApi = {
  ask: data => http.post('/qa/ask', data, { timeout: 180000 }),
  getDetail: id => http.get(`/qa/${id}`),
  getList: params => http.get('/qa/list', { params }),
  followUp: data => http.post('/qa/follow-up', data, { timeout: 120000 }),
  search: params => http.get('/qa/search', { params })
};

// 学习路径
export const learningPathApi = {
  getAll: params => http.get('/learning-paths', { params }),
  getRecommended: () => http.get('/learning-paths/recommended'),
  getDetail: id => http.get(`/learning-paths/${id}`),
  getCourse: courseId => http.get(`/learning-paths/course/${courseId}`),
  getCourseResources: courseId => http.get(`/learning-paths/course/${courseId}/resources`),
  startCourse: data => http.post('/progress/start', data),
  completeCourse: data => http.post('/progress/complete', data),
  resetCourse: data => http.post('/progress/reset', data),
  getMyProgress: () => http.get('/progress/my')
};

// 反馈
export const feedbackApi = {
  submit: data => http.post('/feedback', data),
  getByTarget: (type, id) => http.get(`/feedback/target/${type}/${id}`)
};

// 收藏
export const favoriteApi = {
  add: data => http.post('/favorite', data),
  remove: data => http.delete('/favorite', { data }),
  getList: params => http.get('/favorite/list', { params }),
  check: params => http.get('/favorite/check', { params })
};

// 管理后台
export const adminApi = {
  getStats: () => http.get('/admin/stats'),
  getUsers: params => http.get('/admin/users', { params }),
  updateUserStatus: (id, status) => http.put(`/admin/user/${id}/status`, { status }),
  getReviews: params => http.get('/admin/reviews', { params }),
  getQuestions: params => http.get('/admin/questions', { params }),
  getPaths: params => http.get('/admin/paths', { params }),
  createPath: data => http.post('/admin/paths', data),
  updatePath: data => http.put('/admin/paths', data),
  deletePath: id => http.delete(`/admin/paths/${id}`),
  getConfig: () => http.get('/admin/config'),
  updateConfig: data => http.put('/admin/config', data),
  getLogs: params => http.get('/admin/logs', { params }),
  generatePath: data => http.post('/admin/paths/generate', data, { timeout: 180000 }),
  // 邮件通知
  sendNotification: data => http.post('/admin/notify', data),
  // 路径课程管理
  getPathCourses: pathId => http.get(`/admin/paths/${pathId}/courses`),
  createCourse: (pathId, data) => http.post(`/admin/paths/${pathId}/courses`, data),
  updateCourse: data => http.put('/admin/paths/courses', data),
  deleteCourse: id => http.delete(`/admin/paths/courses/${id}`),
  // 课程资源管理
  getCourseResources: courseId => http.get(`/admin/paths/courses/${courseId}/resources`),
  addCourseResource: (courseId, data) => http.post(`/admin/paths/courses/${courseId}/resources`, data),
  deleteCourseResource: id => http.delete(`/admin/paths/courses/resources/${id}`)
};

// 验证码
export const captchaApi = {
  getCaptcha: () => http.get('/captcha')
};

// AI 排名
export const rankingApi = {
  getRankings: () => http.get('/rankings'),
  refreshRankings: () => http.post('/rankings/refresh')
};

// 邮箱验证
export const emailApi = {
  sendCode: email => http.post('/email/send-code', { email }),
  verifyCode: (email, code) => http.post('/email/verify-code', { email, code })
};

export default http;
