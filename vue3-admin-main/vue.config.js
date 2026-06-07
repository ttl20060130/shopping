const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // 保存文件的时候不进行eslink检查
  lintOnSave: false,
  // 开发环境
  devServer: {
    // 代理服务器
    proxy: {
      '/': {
        target: 'http://localhost:8001',
        changeOrigin: true,  
        // pathRewrite: {
        //   '^/api': ''  
        // }
      },
    }

  }
})
