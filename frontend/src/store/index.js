import Vue from 'vue';
import Vuex from 'vuex';
import { userApi } from '../api';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    token: sessionStorage.getItem('token') || '',
    user: JSON.parse(sessionStorage.getItem('user') || 'null')
  },

  mutations: {
    SET_TOKEN(state, token) {
      state.token = token;
      sessionStorage.setItem('token', token);
    },

    SET_USER(state, user) {
      state.user = user;
      sessionStorage.setItem('user', JSON.stringify(user));
    },

    CLEAR_AUTH(state) {
      state.token = '';
      state.user = null;
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('user');
    }
  },

  actions: {
    // 登录
    async login({ commit }, credentials) {
      const res = await userApi.login(credentials);
      if (res.code === 200 && res.data) {
        commit('SET_TOKEN', res.data.token);
        commit('SET_USER', res.data.user);
      }
      return res;
    },

    // 注册
    async register({ commit }, data) {
      const res = await userApi.register(data);
      return res;
    },

    // 退出登录
    logout({ commit }) {
      commit('CLEAR_AUTH');
    },

    // 获取用户信息（刷新用）
    async fetchUserInfo({ commit }) {
      try {
        const res = await userApi.getUserInfo();
        if (res.code === 200 && res.data) {
          // UserProfileVO 现已扁平化，顶层有 username/avatar 等字段
          // 同时也保留 nested user 对象供需要完整 UserVO 的场景使用
          commit('SET_USER', res.data);
        }
      } catch {
        // Token 失效，清除认证
        commit('CLEAR_AUTH');
      }
    }
  },

  getters: {
    isLoggedIn: state => !!state.token,
    isAdmin: state => state.user?.role === 'ADMIN',
    currentUser: state => state.user
  }
});
