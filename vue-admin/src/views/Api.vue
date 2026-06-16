<template>
  <div class="film-list">
    <!-- 页面头部 - 统一风格 -->
    <div class="page-header">
      <div class="search-container">
        <div class="page-title">
          <el-icon class="page-title__icon">
            <Document />
          </el-icon>
          <span>API接口文档</span>
        </div>
        <div class="header-actions">
          <el-button type="primary" plain @click="refreshSwagger">
            <el-icon class="btn-icon">
              <Refresh />
            </el-icon>
            <span>刷新文档</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Swagger文档容器 -->
    <div class="table-card swagger-container">
      <div v-if="loading" class="swagger-loading">
        <el-icon class="loading-icon">
          <Loading />
        </el-icon>
        <div class="loading-text">正在加载API文档...</div>
      </div>
      <iframe ref="swaggerIframe" class="swagger-iframe" :src="url" frameborder="0" title="Swagger UI"
        @load="handleIframeLoad"></iframe>
    </div>
  </div>
</template>

<script>
import config from "@/config";
import { Document, Refresh, Loading } from '@element-plus/icons-vue'

export default {
  name: "ApiDoc",
  components: { Document, Refresh, Loading },
  data() {
    return {
      url: config.SWAGGER_URL,
      loading: true,
    };
  },
  methods: {
    // 刷新Swagger文档
    refreshSwagger() {
      this.loading = true
      const iframe = this.$refs.swaggerIframe
      if (iframe) {
        iframe.src = this.url
      }
    },
    // iframe加载完成
    handleIframeLoad() {
      this.loading = false
    }
  }
};
</script>

<style scoped>
/* 整体布局 - 统一风格 */
.film-list {
  height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: hidden;
  padding: 20px;
  box-sizing: border-box;
  background: rgb(250, 251, 252);
}

/* 页面头部 - 统一风格 */
.page-header {
  margin-bottom: 0;
  padding: 20px 24px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
}

.search-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
}

.page-title__icon {
  font-size: 24px;
  color: #409eff;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

/* 基础按钮样式 - 统一风格 */
.el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 40px;
  padding: 0 18px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  box-shadow: none;
}

.btn-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.btn-primary {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.25);
}

.btn-primary:hover {
  background: linear-gradient(135deg, #337ecc 0%, #409eff 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
}

.btn-primary:active {
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.3);
}

/* Swagger文档容器 */
.table-card {
  flex: 1;
  min-height: 0;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 0;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.swagger-container {
  border-radius: 16px;
}

/* 加载状态 */
.swagger-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 10;
  transition: opacity 0.3s ease;
}

.loading-icon {
  font-size: 48px;
  color: #409eff;
  animation: rotate 1.5s linear infinite;
  margin-bottom: 16px;
}

.loading-text {
  font-size: 16px;
  color: #64748b;
  font-weight: 500;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

/* Swagger iframe */
.swagger-iframe {
  width: 100%;
  flex: 1;
  border: none;
  background: #fff;
  transition: opacity 0.3s ease;
}

/* 按钮内图标和文字对齐 */
.el-button [class*="el-icon"]+span {
  margin-left: 6px;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .search-container {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .page-title {
    justify-content: center;
  }

  .header-actions {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .film-list {
    padding: 12px;
  }

  .page-header {
    padding: 16px;
  }

  .page-title {
    font-size: 18px;
  }

  .page-title__icon {
    font-size: 20px;
  }
}

@media (max-width: 576px) {
  .film-list {
    padding: 12px;
  }

  .header-actions {
    width: 100%;
  }

  .el-button {
    width: 100%;
    padding: 0 12px;
    font-size: 13px;
  }
}
</style>