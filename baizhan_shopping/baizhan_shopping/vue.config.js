const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  css: {
    loaderOptions: {
      less: {
        lessOptions: {
          modifyVars: {
            'primary-color': "#00b683",
            // 'link-color': '#1DA57A',
            // 'border-radius-base': '2px',
          },
          javascriptEnabled: true,
        },
      },
    },
  },
  devServer: {
    port: 8081,   
    // 代理服务器
    proxy: {
      '/': {
        target: 'http://www.ttl.com/',
        changeOrigin: true,  
          // pathRewrite: {
          //   '^/api': ''  
          // }
      }
    }
  }
})
