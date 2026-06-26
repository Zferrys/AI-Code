import Vue from 'vue';
import Router from 'vue-router';
import store from '../store';

Vue.use(Router);

// 全局忽略重复导航 + 守卫重定向错误（Vue Router 3.x 默认抛错）
const originalPush = Router.prototype.push;
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    // name 'NavigationDuplicated' = 重复点击同一路由
    // type 4 = Abort by guard redirect (守卫内部重定向是正常行为)
    if (err.name !== 'NavigationDuplicated' && err.type !== 4) throw err;
  });
};

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('../views/Home.vue'),
      meta: { title: '首页' }
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue'),
      meta: { title: '登录', noAuth: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/Register.vue'),
      meta: { title: '注册', noAuth: true }
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('../views/Profile.vue'),
      meta: { title: '个人中心', requiresAuth: true }
    },
    {
      path: '/code-review',
      name: 'CodeReview',
      component: () => import('../views/CodeReview.vue'),
      meta: { title: '代码审查', requiresAuth: true }
    },
    {
      path: '/code-review/:id',
      name: 'CodeReviewDetail',
      component: () => import('../views/CodeReviewDetail.vue'),
      meta: { title: '审查详情', requiresAuth: true }
    },
    {
      path: '/qa',
      name: 'QaHome',
      component: () => import('../views/QaHome.vue'),
      meta: { title: '智能问答', requiresAuth: true }
    },
    {
      path: '/qa/:id',
      name: 'QaDetail',
      component: () => import('../views/QaDetail.vue'),
      meta: { title: '问答详情', requiresAuth: true }
    },
    {
      path: '/rankings',
      name: 'AIRankings',
      component: () => import('../views/AIRankings.vue'),
      meta: { title: 'AI 模型排行榜' }
    },
    {
      path: '/learning-paths',
      name: 'LearningPaths',
      component: () => import('../views/LearningPaths.vue'),
      meta: { title: '学习路径' }
    },
    {
      path: '/learning-paths/:id',
      name: 'LearningPathDetail',
      component: () => import('../views/LearningPathDetail.vue'),
      meta: { title: '路径详情' }
    },
    {
      path: '/course/:courseId',
      name: 'CourseView',
      component: () => import('../views/CourseView.vue'),
      meta: { title: '课程学习', requiresAuth: true }
    },
    {
      path: '/admin',
      component: () => import('../views/admin/AdminLayout.vue'),
      meta: { title: '后台管理', requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '',
          name: 'AdminDashboard',
          component: () => import('../views/admin/Dashboard.vue'),
          meta: { title: '控制台' }
        },
        {
          path: 'users',
          name: 'AdminUsers',
          component: () => import('../views/admin/Users.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'paths',
          name: 'AdminPaths',
          component: () => import('../views/admin/Paths.vue'),
          meta: { title: '路径管理' }
        },
        {
          path: 'tags',
          name: 'AdminTags',
          component: () => import('../views/admin/Tags.vue'),
          meta: { title: '标签管理' }
        },
        {
          path: 'config',
          name: 'AdminConfig',
          component: () => import('../views/admin/Config.vue'),
          meta: { title: '系统配置' }
        },
        {
          path: 'logs',
          name: 'AdminLogs',
          component: () => import('../views/admin/Logs.vue'),
          meta: { title: '操作日志' }
        }
      ]
    },
    {
      path: '*',
      name: 'NotFound',
      component: () => import('../views/NotFound.vue'),
      meta: { title: '404' }
    }
  ]
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const titleMeta = to.matched.slice().reverse().find(r => r.meta.title);
  document.title = titleMeta ? `${titleMeta.meta.title} - AI-Code` : 'AI-Code';

  const token = store.state.token || sessionStorage.getItem('token');
  const user = store.state.user || JSON.parse(sessionStorage.getItem('user') || 'null');

  // 需要认证的页面（遍历所有匹配路由）
  if (to.matched.some(r => r.meta.requiresAuth) && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
    return;
  }

  // 需要管理员权限（to.matched.some: Vue Router 3 的 to.meta 只取最后一级，
  // 子路由不继承父级的 requiresAdmin meta，需遍历所有匹配路由）
  if (to.matched.some(r => r.meta.requiresAdmin) && (!user || user.role !== 'ADMIN')) {
    next({ name: 'Home' });
    return;
  }

  next();
});

export default router;
