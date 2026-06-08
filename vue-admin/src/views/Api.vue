<template>
  <div class="film-list">

    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-search-bar">
        <div class="page-search-bar__title">
          <div class="page-title">API 接口文档</div>
          <div class="page-subtitle">在线浏览所有 RESTful 接口，支持在线调试</div>
        </div>
      </div>
    </div>

    <!-- 卡片容器 -->
    <div class="table-card">
      <iframe class="swagger-iframe" :src="url" frameborder="0" title="Swagger UI"></iframe>
    </div>

  </div>
</template>

<script>
import config from "@/config";

export default {
  name: "ApiDoc",
  data() {
    return {
      url: config.SWAGGER_URL,
    };
  },
};
</script>

<style scoped>
/* 整体布局 - 与电影管理页面完全一致 */
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

.page-header {
  flex: 0 0 auto;
  padding: 20px 22px;
  border-radius: 18px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.page-search-bar__title {
  min-width: 220px;
}

.page-title {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: 0.2px;
}

.page-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
}

/* 卡片容器 - 用于包裹 iframe */
.table-card {
  flex: 1;
  min-height: 0;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
  border-radius: 18px;
  padding: 0;
  /* 内部 iframe 直接占满 */
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.swagger-iframe {
  width: 100%;
  flex: 1;
  border: none;
  background: #fff;
}

/* 响应式小屏适配（可选） */
@media (max-width: 768px) {
  .film-list {
    padding: 12px;
  }

  .page-header {
    padding: 14px 16px;
  }

  .page-title {
    font-size: 18px;
  }
}
</style>