const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: [],
  devServer: {
    port: 3000,
    proxy: {
      '/api': {
        // IDEA Tomcat 部署到 /aicode_platform 上下文
        target: 'http://localhost:8080/aicode_platform',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080/aicode_platform',
        changeOrigin: true
      }
    }
  },
  // 构建输出到 target（不影响 src/main/webapp/WEB-INF）
  outputDir: '../backend/target/frontend-dist',
  assetsDir: 'static'
});
