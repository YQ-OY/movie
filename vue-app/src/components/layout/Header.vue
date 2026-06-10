<template>
  <header class="header">
    <div class="header-inner">
      <router-link to="/" class="header-logo" aria-label="返回首页">
        <img class="header-logo__img" src="../../assets/img/logo.png" alt="厅有独钟">
        <span class="header-logo__text">厅有独钟</span>
      </router-link>

      <div class="header-center">
        <nav class="header-nav" aria-label="主导航">
          <router-link
            v-for="item in navItems"
            :key="item.label"
            :to="item.to"
            class="nav-link"
            :class="{ 'nav-link--active': isNavActive(item) }"
          >
            {{ item.label }}
          </router-link>
        </nav>

        <div class="header-search">
          <div class="search-box">
            <button class="search-filter-btn" type="button" @click="goToFilmsFilter">
              筛选
            </button>
            <span class="search-filter-divider" aria-hidden="true" />
            <el-autocomplete
              v-model="state"
              class="search-autocomplete"
              popper-class="header-search-popper"
              :fetch-suggestions="querySearchAsync"
              placeholder="搜索电影名称"
              clearable
              :trigger-on-focus="false"
              @select="handleSelect"
              @keyup.enter="handleSearch"
            />
            <button class="search-btn" type="button" @click="handleSearch">
              搜索
            </button>
          </div>
        </div>
      </div>

      <div class="header-actions">
        <router-link v-if="!isLogin" to="/login" class="login-link">
          登录
        </router-link>
        <button
          v-else
          class="avatar-trigger"
          type="button"
          aria-label="打开个人中心"
          @click="openProfileDrawer('setting')"
        >
          <span class="header-name">{{ user.nickname }}</span>
          <el-avatar class="user-avatar" :size="42" :src="user.avatar || defaultAvatar" />
        </button>
      </div>

      <ProfileDrawer
        v-if="isLogin"
        v-model="drawerVisible"
        :initial-tab="drawerTab"
        @logout="handleLogout"
        @user-updated="handleUserUpdated"
      />
    </div>
  </header>
</template>

<script>
import { findById } from '@/api/user'
import { SearchFilm } from '@/api/film'
import ProfileDrawer from '@/components/layout/ProfileDrawer.vue'
import { PROFILE_TABS } from '@/utils/profileCenter'

const NAV_ITEMS = [
  {
    label: '首页',
    to: '/',
    match: (path) => path === '/',
  },
  {
    label: '电影',
    to: { path: '/films', query: { region: '全部', type: '全部' } },
    match: (path) => path.startsWith('/films') || path.startsWith('/film') || path === '/seat',
  },
  {
    label: '活动',
    to: '/active',
    match: (path) => path.startsWith('/active'),
  },
  {
    label: '留言',
    to: '/leaving',
    match: (path) => path.startsWith('/leaving'),
  },
]

export default {
  name: 'Header',
  components: { ProfileDrawer },

  data() {
    return {
      navItems: NAV_ITEMS,
      isLogin: false,
      searchList: [],
      state: '',
      user: {},
      drawerVisible: false,
      drawerTab: 'setting',
      defaultAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    }
  },

  watch: {
    '$route.query.profile'(tab) {
      if (tab && PROFILE_TABS.includes(tab) && this.isLogin) {
        this.openProfileDrawer(tab, false)
        this.clearProfileQuery()
      }
    },
    isLogin(val) {
      if (val) {
        this.tryOpenProfileFromQuery()
      }
    },
  },

  mounted() {
    if (localStorage.getItem('uid') !== null) {
      findById(localStorage.getItem('uid')).then(res => {
        this.isLogin = true
        this.user = res.data
        this.tryOpenProfileFromQuery()
      })
    }
  },

  methods: {
    isNavActive(item) {
      return item.match(this.$route.path)
    },

    tryOpenProfileFromQuery() {
      const tab = this.$route.query.profile
      if (tab && PROFILE_TABS.includes(tab)) {
        this.openProfileDrawer(tab, false)
        this.clearProfileQuery()
      }
    },

    openProfileDrawer(tab = 'setting', clearQuery = true) {
      this.drawerTab = PROFILE_TABS.includes(tab) ? tab : 'setting'
      this.drawerVisible = true
      if (clearQuery) {
        this.clearProfileQuery()
      }
    },

    clearProfileQuery() {
      if (!this.$route.query.profile) return
      const { profile, ...query } = this.$route.query
      this.$router.replace({ path: this.$route.path, query })
    },

    querySearchAsync(queryString, cb) {
      if (!queryString) {
        cb([])
        return
      }
      SearchFilm(queryString).then(res => {
        setTimeout(() => {
          const data = res && res.data
          this.searchList = Array.isArray(data) ? data : []
          cb(this.loadSearchList())
        }, 400)
      }).catch(() => {
        this.searchList = []
        cb([])
      })
    },

    loadSearchList() {
      return (this.searchList || []).map(item => ({
        id: item.id,
        value: item.name,
      }))
    },

    handleSelect(item) {
      this.$router.push('/film/info?fid=' + item.id)
    },

    goToFilmsFilter() {
      this.$router.push({
        path: '/films',
        query: {
          region: '全部',
          type: '全部',
          phase: 'showing',
        },
      })
    },

    async handleSearch() {
      const keyword = (this.state || '').trim()
      if (!keyword) {
        this.$message.warning('请输入电影名称')
        return
      }
      try {
        const res = await SearchFilm(keyword)
        const list = Array.isArray(res?.data) ? res.data : []
        if (list.length === 0) {
          this.$message.info('未找到相关电影')
          return
        }
        const exact = list.find(item => item.name === keyword)
        const target = exact || list[0]
        this.$router.push('/film/info?fid=' + target.id)
      } catch {
        // 全局 request 拦截器已提示
      }
    },

    handleLogout() {
      localStorage.removeItem('uid')
      localStorage.removeItem('token')
      this.isLogin = false
      this.user = {}
      this.$router.push('/login')
    },

    handleUserUpdated(user) {
      this.user = user
    },
  },
}
</script>

<style scoped>
.header {
  height: 72px;
  background: var(--app-surface-header);
  border-bottom: 1px solid var(--app-border);
}

.header-inner {
  height: 100%;
  width: 100%;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  box-sizing: border-box;
}

.header-center {
  display: flex;
  align-items: center;
  gap: clamp(20px, 3vw, 48px);
  flex: 1;
  min-width: 0;
  justify-content: center;
}

.header-logo {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  text-decoration: none;
  color: inherit;
}

.header-logo__img {
  width: 46px;
  height: 46px;
  object-fit: contain;
}

.header-logo__text {
  font-size: 26px;
  font-weight: 800;
  letter-spacing: 0.08em;
  color: var(--app-title);
  white-space: nowrap;
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.nav-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 64px;
  padding: 8px 20px;
  border-radius: 999px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.04em;
  color: var(--app-text-muted);
  text-decoration: none;
  transition: color 0.2s ease, background 0.2s ease, box-shadow 0.2s ease;
}

.nav-link:hover {
  color: var(--app-primary);
  background: var(--app-primary-bg);
}

.nav-link--active {
  color: var(--app-primary-dark);
  font-weight: 700;
  background: var(--app-gradient-soft);
  box-shadow:
    inset 0 0 0 1px var(--app-primary-border-soft),
    0 4px 14px var(--app-primary-shadow);
}

.nav-link--active:hover {
  color: var(--app-primary-dark);
  background: var(--app-gradient-soft);
}

.header-search {
  flex-shrink: 0;
  width: min(340px, 32vw);
}

.search-box {
  display: flex;
  align-items: stretch;
  width: 100%;
  border-radius: 999px;
  border: 1px solid var(--app-border);
  background: var(--app-surface);
  overflow: hidden;
  box-shadow: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-box:focus-within {
  border-color: var(--app-primary-light);
  background: var(--app-surface-elevated);
  box-shadow:
    0 0 0 2px var(--app-primary-bg),
    0 2px 8px rgba(0, 0, 0, 0.3);
}

.search-filter-btn {
  flex-shrink: 0;
  padding: 0 12px 0 14px;
  border: none;
  background: transparent;
  color: var(--app-text-muted);
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.08em;
  cursor: pointer;
  transition: color 0.2s ease;
}

.search-filter-btn:hover {
  color: var(--app-primary-light);
}

.search-filter-divider {
  flex-shrink: 0;
  width: 1px;
  align-self: stretch;
  margin: 8px 0;
  background: var(--app-border);
}

.search-autocomplete {
  flex: 1;
  min-width: 0;
}

.search-autocomplete :deep(.el-input__wrapper) {
  border-radius: 0;
  min-height: 40px;
  padding: 0 12px 0 10px;
  background: transparent;
  border: none;
  box-shadow: none !important;
}

.search-autocomplete :deep(.el-input__wrapper:hover),
.search-autocomplete :deep(.el-input__wrapper.is-focus) {
  background: transparent;
  border: none;
  box-shadow: none !important;
}

.search-btn {
  flex-shrink: 0;
  padding: 0 18px;
  border: none;
  background: var(--app-gradient-brand);
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.06em;
  cursor: pointer;
  transition: filter 0.2s ease, opacity 0.2s ease;
}

.search-btn:hover {
  filter: brightness(1.06);
}

.search-btn:active {
  opacity: 0.92;
}

.search-autocomplete :deep(.el-input__inner) {
  font-size: 15px;
  color: var(--app-text);
}

.search-autocomplete :deep(.el-input__inner::placeholder) {
  color: var(--app-text-muted);
}

.search-autocomplete :deep(.el-input__prefix) {
  color: var(--app-text-muted);
}

.search-autocomplete :deep(.el-input__wrapper.is-focus .el-input__prefix) {
  color: var(--app-primary);
}

.header-actions {
  flex-shrink: 0;
}

.login-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 88px;
  padding: 10px 22px;
  border-radius: 999px;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 0.06em;
  color: var(--app-primary);
  text-decoration: none;
  background: var(--app-primary-bg);
  border: 1px solid var(--app-primary-border-soft);
  transition: background 0.2s ease, box-shadow 0.2s ease, color 0.2s ease;
}

.login-link:hover {
  color: var(--app-primary-dark);
  background: var(--app-primary-bg-soft);
  box-shadow: 0 4px 12px var(--app-primary-shadow);
}

.avatar-trigger {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 4px 6px 4px 14px;
  border: 1px solid var(--app-border);
  border-radius: 999px;
  background: var(--app-surface-muted);
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.avatar-trigger:hover {
  background: var(--app-surface-elevated);
  border-color: var(--app-primary-border);
  box-shadow: 0 4px 14px var(--app-primary-shadow);
}

.header-name {
  color: var(--app-text);
  font-weight: 600;
  font-size: 15px;
  letter-spacing: 0.04em;
  max-width: 96px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.1);
}

@media (max-width: 960px) {
  .header-inner {
    padding: 0 12px;
    gap: 12px;
  }

  .header-center {
    gap: 12px;
  }

  .header-logo__text {
    display: none;
  }

  .nav-link {
    min-width: 48px;
    padding: 6px 12px;
    font-size: 15px;
  }

  .header-search {
    width: min(180px, 32vw);
  }

  .search-btn {
    padding: 0 12px;
    font-size: 13px;
  }
}
</style>

<style>
.header-search-popper.el-popper {
  border-radius: 14px;
  border: 1px solid var(--app-border);
  background: var(--app-surface-elevated);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.4);
  overflow: hidden;
}

.header-search-popper .el-autocomplete-suggestion li {
  font-size: 14px;
  color: var(--app-text);
  padding: 10px 16px;
}

.header-search-popper .el-autocomplete-suggestion li:hover,
.header-search-popper .el-autocomplete-suggestion li.highlighted {
  background: var(--app-primary-bg);
  color: var(--app-primary-dark);
}
</style>
