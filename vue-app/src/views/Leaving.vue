<template>
  <div class="leaving-page app-page">
    <div class="leaving-page__inner app-page__inner">
      <header class="leaving-hero app-hero">
        <div class="leaving-hero__left">
          <div class="leaving-hero__icon" aria-hidden="true">
            <el-icon :size="28"><ChatDotRound /></el-icon>
          </div>
          <div>
            <h1 class="leaving-hero__title">影院留言</h1>
            <p class="leaving-hero__sub">分享观影体验 · 我们会认真阅读每一条留言</p>
          </div>
        </div>
        <div class="leaving-hero__stats">
          <button
            type="button"
            class="hero-stat"
            :class="{ 'hero-stat--active': messageFilter === 'all' }"
            @click="setMessageFilter('all')"
          >
            <el-icon><ChatLineRound /></el-icon>
            <span class="hero-stat__num">{{ leavingMessageList.length }}</span>
            <span class="hero-stat__lbl">条留言</span>
          </button>
          <button
            v-if="isLogin"
            type="button"
            class="hero-stat hero-stat--mine"
            :class="{ 'hero-stat--active': messageFilter === 'mine' }"
            @click="setMessageFilter('mine')"
          >
            <el-icon><User /></el-icon>
            <span class="hero-stat__num">{{ myMessageCount }}</span>
            <span class="hero-stat__lbl">我的留言</span>
          </button>
        </div>
      </header>

      <!-- 联系客服：默认折叠 -->
      <section class="service-panel">
        <button type="button" class="service-panel__toggle" @click="serviceExpanded = !serviceExpanded">
          <div class="service-panel__toggle-left">
            <el-icon class="service-panel__toggle-icon"><Service /></el-icon>
            <span class="service-panel__toggle-title">联系客服</span>
            <span class="service-panel__toggle-hint">评价客服 · 查看服务团队</span>
          </div>
          <el-icon class="service-panel__arrow" :class="{ 'service-panel__arrow--open': serviceExpanded }">
            <ArrowDown />
          </el-icon>
        </button>
        <el-collapse-transition>
          <div v-show="serviceExpanded" class="service-panel__body">
            <el-empty v-if="workerList.length === 0" description="暂无在线客服" :image-size="72" />
            <div v-else class="service-grid">
              <div v-for="(item, index) in workerList" :key="index" class="service-card">
                <el-avatar class="service-card__avatar" :size="56" :src="item.avatar" />
                <div class="service-card__info">
                  <div class="service-card__name">{{ item.nickname || '客服' }}</div>
                  <div class="service-card__meta">
                    <span>{{ item.gender || '—' }}</span>
                    <span class="service-card__dot">·</span>
                    <span>{{ displayPhone(item.phone) }}</span>
                  </div>
                  <div class="service-card__actions">
                    <el-button link type="primary" @click="handleListEvaluate(item)">我的评价</el-button>
                    <el-button link type="primary" :disabled="!isLogin" @click="handleEvaluate(item)">
                      评价客服
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-collapse-transition>
      </section>

      <!-- 留言列表 -->
      <section class="message-section">
        <div class="message-section__head">
          <div class="message-section__title-wrap">
            <h2 class="message-section__title">{{ sectionTitle }}</h2>
            <p v-if="messageFilter === 'mine'" class="message-section__hint">仅显示您发表的留言及客服回复</p>
          </div>
          <el-button type="primary" round :disabled="!isLogin" @click="handleLeaving">
            <el-icon><EditPen /></el-icon>
            添加留言
          </el-button>
        </div>

        <div v-if="!isLogin" class="message-login-tip">
          <el-icon><InfoFilled /></el-icon>
          <span>登录后可发表留言并评价客服</span>
          <el-button link type="primary" @click="$router.push('/login')">去登录</el-button>
        </div>

        <div ref="messageFeed" class="message-feed scrollbar-hidden">
          <el-empty v-if="displayMessageList.length === 0" :description="emptyDescription" />

          <article
            v-for="(item, index) in displayMessageList"
            :key="item.leavingMessage?.id || index"
            class="message-card"
            :class="{ 'message-card--mine': isMyMessage(item) }"
          >

            <div class="message-card__main">
              <el-avatar class="message-card__avatar" :size="48" :src="item.user?.avatar" />
              <div class="message-card__body">
                <div class="message-card__header">
                  <span class="message-card__name">
                    {{ item.user?.nickname || '匿名用户' }}
                  </span>
                  <time class="message-card__time">{{ item.leavingMessage?.createAt }}</time>
                </div>
                <p class="message-card__content">{{ item.leavingMessage?.content }}</p>
              </div>
            </div>

            <div v-if="hasReply(item.leavingMessage)" class="message-card__reply">
              <div class="message-card__reply-label">
                <el-icon><ChatLineSquare /></el-icon>
                客服回复
              </div>
              <p class="message-card__reply-text">{{ item.leavingMessage.reply }}</p>
            </div>
          </article>
        </div>
      </section>
    </div>

    <el-dialog title="客服评价" v-model="dialogFormVisible1" width="480px" align-center class="leaving-dialog">
      <el-form :model="evaluate" label-width="88px">
        <el-form-item label="满意程度">
          <el-select v-model="evaluate.type" placeholder="请选择满意程度" style="width: 100%">
            <el-option label="满意" value="满意" />
            <el-option label="不满意" value="不满意" />
            <el-option label="非常满意" value="非常满意" />
          </el-select>
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="evaluate.content" type="textarea" :rows="5" placeholder="说说您的服务体验" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogFormVisible1 = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluate">提交评价</el-button>
      </template>
    </el-dialog>

    <el-dialog title="添加留言" v-model="dialogFormVisible2" width="520px" align-center class="leaving-dialog">
      <el-input
        v-model="leavingMessage.content"
        type="textarea"
        :rows="6"
        maxlength="500"
        show-word-limit
        placeholder="欢迎留下您对影院环境、购票体验或影片排片的建议…"
      />
      <template #footer>
        <el-button @click="dialogFormVisible2 = false">取消</el-button>
        <el-button type="primary" @click="submitLeaving">提交留言</el-button>
      </template>
    </el-dialog>

    <el-dialog title="我的评价" v-model="dialogFormVisible3" width="640px" align-center class="leaving-dialog">
      <el-table :data="evaluateList" stripe>
        <el-table-column property="createAt" label="日期" width="180" />
        <el-table-column property="content" label="评语" min-width="200" show-overflow-tooltip />
        <el-table-column property="type" label="满意度" width="120" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.type === '非常满意'" type="success" effect="light">非常满意</el-tag>
            <el-tag v-else-if="scope.row.type === '满意'" type="info" effect="light">满意</el-tag>
            <el-tag v-else-if="scope.row.type === '不满意'" type="danger" effect="light">不满意</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import {
  CreateEvaluate,
  CreateLeavingMessage,
  ListAllLeavingMessage,
  ListAllWorker,
  ListWorkerEvaluate,
} from '@/api/leaving'
import {
  ArrowDown,
  ChatDotRound,
  ChatLineRound,
  ChatLineSquare,
  EditPen,
  InfoFilled,
  Service,
  User,
} from '@element-plus/icons-vue'

export default {
  name: 'Leaving',
  components: {
    ArrowDown,
    ChatDotRound,
    ChatLineRound,
    ChatLineSquare,
    EditPen,
    InfoFilled,
    Service,
    User,
  },
  data() {
    return {
      isLogin: false,
      serviceExpanded: false,
      messageFilter: 'all',
      evaluate: {
        wid: '',
        uid: localStorage.getItem('uid'),
        type: '非常满意',
        content: '',
      },
      evaluateList: [],
      workerList: [],
      leavingMessageList: [],
      leavingMessage: {
        uid: localStorage.getItem('uid'),
        reply: '',
        content: '',
        createAt: '',
      },
      dialogFormVisible1: false,
      dialogFormVisible2: false,
      dialogFormVisible3: false,
      currentUid: localStorage.getItem('uid'),
    }
  },
  computed: {
    displayMessageList() {
      let list = [...(this.leavingMessageList || [])]
      if (this.messageFilter === 'mine') {
        list = list.filter(item => this.isMyMessage(item))
      }
      return list.sort((a, b) => {
        const ta = this.parseTime(a.leavingMessage?.createAt)
        const tb = this.parseTime(b.leavingMessage?.createAt)
        return tb - ta
      })
    },
    myMessageCount() {
      return this.leavingMessageList.filter(item => this.isMyMessage(item)).length
    },
    sectionTitle() {
      return this.messageFilter === 'mine' ? '我的留言' : '全部留言'
    },
    emptyDescription() {
      if (this.messageFilter === 'mine') {
        return '你还没有发表过留言，快来写下第一条吧'
      }
      return '还没有留言，来抢沙发吧'
    },
  },
  mounted() {
    if (localStorage.getItem('uid')) {
      this.isLogin = true
    }
    this.loadMessages()
    ListAllWorker().then(res => {
      this.workerList = res.data || []
    })
  },
  methods: {
    loadMessages() {
      ListAllLeavingMessage().then(res => {
        this.leavingMessageList = res.data || []
      })
    },
    isMyMessage(item) {
      const uid = this.currentUid
      if (!uid) return false
      return item?.leavingMessage?.uid === uid || item?.user?.id === uid
    },
    parseTime(str) {
      if (!str) return 0
      const t = Date.parse(String(str).replace(/-/g, '/'))
      return Number.isNaN(t) ? 0 : t
    },
    setMessageFilter(filter) {
      if (filter === this.messageFilter) return
      this.messageFilter = filter
      this.$nextTick(() => {
        this.$refs.messageFeed?.scrollTo({ top: 0, behavior: 'smooth' })
      })
    },
    handleListEvaluate(item) {
      this.dialogFormVisible3 = true
      ListWorkerEvaluate(item.id).then(res => {
        this.evaluateList = res.data || []
      })
    },
    handleEvaluate(item) {
      this.dialogFormVisible1 = true
      this.evaluate.wid = item.id
    },
    handleLeaving() {
      this.dialogFormVisible2 = true
    },
    submitEvaluate() {
      CreateEvaluate(this.evaluate).then(res => {
        if (res.success) {
          this.dialogFormVisible1 = false
          this.$message.success('感谢您对工作人员作出的评价')
        }
      })
    },
    submitLeaving() {
      if (!this.leavingMessage.content?.trim()) {
        this.$message.warning('请输入留言内容')
        return
      }
      CreateLeavingMessage(this.leavingMessage).then(res => {
        if (res.success) {
          this.dialogFormVisible2 = false
          this.leavingMessage.content = ''
          this.$message.success('感谢您对小小电影留下的宝贵意见')
          this.loadMessages()
          this.messageFilter = 'mine'
        }
      })
    },
    displayPhone(phone) {
      if (phone == null || phone === '' || phone === 'null') return '—'
      return phone
    },
    hasReply(lm) {
      const r = lm && lm.reply
      return r != null && String(r).trim() !== '' && String(r).trim() !== 'null'
    },
  },
}
</script>

<style scoped>
.leaving-page__inner {
  max-width: 960px;
}

.leaving-hero.app-hero {
  background: rgba(24, 24, 24, 0.55);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.28);
}

.leaving-hero__left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.leaving-hero__icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: var(--app-gradient-soft);
  color: var(--app-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.leaving-hero__title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--app-title);
  letter-spacing: 1px;
}

.leaving-hero__sub {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--app-text-secondary);
}

.leaving-hero__stats {
  display: flex;
  gap: 12px;
}

.hero-stat {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 12px;
  background: rgba(24, 24, 24, 0.52);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--app-text);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.hero-stat:hover {
  border-color: rgba(255, 255, 255, 0.16);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

.hero-stat--active {
  border-color: transparent;
  background: var(--app-gradient-brand);
  color: #ffffff;
  box-shadow: 0 4px 14px var(--app-primary-shadow);
}

.hero-stat--active .hero-stat__num,
.hero-stat--active .hero-stat__lbl,
.hero-stat--active .el-icon {
  color: #ffffff;
}

.hero-stat--mine {
  background: rgba(0, 204, 54, 0.1);
  border-color: rgba(0, 204, 54, 0.22);
  color: var(--app-primary);
}

.hero-stat--mine.hero-stat--active {
  background: var(--app-gradient-brand);
  border-color: transparent;
  color: #ffffff;
}

.hero-stat--mine.hero-stat--active .hero-stat__lbl {
  color: #ffffff;
}

.hero-stat__num {
  font-size: 18px;
  font-weight: 700;
  color: var(--app-highlight);
}

.hero-stat--active .hero-stat__num {
  color: var(--app-highlight);
}

.hero-stat__lbl {
  color: var(--app-text-secondary);
}

.hero-stat--mine .hero-stat__lbl {
  color: var(--app-primary);
}

/* 联系客服折叠面板 */
.service-panel {
  margin-bottom: 18px;
  border-radius: 16px;
  background: rgba(24, 24, 24, 0.55);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.28);
  overflow: hidden;
}

.service-panel__toggle {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 16px 20px;
  border: 0;
  background: transparent;
  cursor: pointer;
  transition: background 0.2s ease;
}

.service-panel__toggle:hover {
  background: rgba(255, 255, 255, 0.04);
}

.service-panel__toggle-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.service-panel__toggle-icon {
  font-size: 22px;
  color: var(--app-primary);
}

.service-panel__toggle-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--app-title);
}

.service-panel__toggle-hint {
  font-size: 13px;
  color: var(--app-text-muted);
  margin-left: 4px;
}

.service-panel__arrow {
  font-size: 18px;
  color: var(--app-text-muted);
  transition: transform 0.25s ease;
  flex-shrink: 0;
}

.service-panel__arrow--open {
  transform: rotate(180deg);
}

.service-panel__body {
  padding: 0 20px 20px;
  border-top: 1px solid var(--app-border);
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
  padding-top: 16px;
}

.service-card {
  display: flex;
  gap: 14px;
  padding: 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.service-card__avatar {
  flex-shrink: 0;
  border: 2px solid var(--app-border);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.service-card__name {
  font-size: 15px;
  font-weight: 600;
  color: var(--app-title);
  margin-bottom: 4px;
}

.service-card__meta {
  font-size: 13px;
  color: var(--app-text-secondary);
  margin-bottom: 8px;
}

.service-card__dot {
  margin: 0 4px;
}

.service-card__actions {
  display: flex;
  gap: 8px;
}

/* 留言区 */
.message-section {
  border-radius: 18px;
  background: rgba(24, 24, 24, 0.55);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.28);
  overflow: hidden;
}

.message-section__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 18px 22px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.03);
}

.message-section__title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: var(--app-title);
  letter-spacing: 0.5px;
}

.message-section__hint {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--app-text-muted);
}

.message-login-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 14px 22px 0;
  padding: 10px 14px;
  border-radius: 10px;
  background: rgba(0, 204, 54, 0.1);
  border: 1px solid rgba(0, 204, 54, 0.22);
  font-size: 13px;
  color: var(--app-primary-light);
}

.message-feed {
  max-height: min(68vh, 720px);
  overflow-y: auto;
  padding: 18px 22px 24px;
}

/* 隐形滚轮：默认隐藏，悬停或滚动时显示 */
.scrollbar-hidden {
  scrollbar-width: thin;
  scrollbar-color: transparent transparent;
}

.scrollbar-hidden:hover {
  scrollbar-color: rgba(148, 163, 184, 0.45) transparent;
}

.scrollbar-hidden::-webkit-scrollbar {
  width: 6px;
}

.scrollbar-hidden::-webkit-scrollbar-track {
  background: transparent;
}

.scrollbar-hidden::-webkit-scrollbar-thumb {
  background: transparent;
  border-radius: 999px;
}

.scrollbar-hidden:hover::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.45);
}

.message-card {
  position: relative;
  padding: 18px;
  margin-bottom: 14px;
  border-radius: 14px;
  background: rgba(24, 24, 24, 0.52);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.message-card:last-child {
  margin-bottom: 0;
}

.message-card:hover {
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.14);
}

.message-card--mine {
  background: rgba(0, 204, 54, 0.08);
  border: 1px solid rgba(0, 204, 54, 0.22);
  box-shadow: 0 4px 16px rgba(0, 204, 54, 0.1);
}

.message-card--mine:hover {
  box-shadow: 0 8px 22px rgba(0, 204, 54, 0.16);
}



.message-card__main {
  display: flex;
  gap: 14px;
  align-items: flex-start;
}

.message-card__avatar {
  flex-shrink: 0;
  border: 2px solid var(--app-border);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.message-card--mine .message-card__avatar {
  box-shadow: 0 2px 10px rgba(0, 204, 54, 0.2);
}

.message-card__body {
  flex: 1;
  min-width: 0;
}

.message-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.message-card__name {
  font-size: 15px;
  font-weight: 600;
  color: var(--app-title);
}

.message-card--mine .message-card__name {
  color: var(--app-primary-light);
}

.message-card__time {
  font-size: 12px;
  color: var(--app-text-muted);
  white-space: nowrap;
}

.message-card__content {
  margin: 0;
  font-size: 15px;
  line-height: 1.75;
  color: var(--app-text);
  word-break: break-word;
  white-space: pre-wrap;
}

.message-card--mine .message-card__content {
  color: var(--app-title);
  font-weight: 500;
}

.message-card__reply {
  margin-top: 14px;
  margin-left: 62px;
  padding: 12px 14px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.04);
  border-left: 3px solid var(--app-primary);
}

.message-card--mine .message-card__reply {
  background: rgba(0, 204, 54, 0.06);
}

.message-card__reply-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--app-primary);
  margin-bottom: 6px;
}

.message-card__reply-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: var(--app-text);
  white-space: pre-wrap;
  word-break: break-word;
}

:deep(.leaving-dialog .el-dialog__header) {
  border-bottom: 1px solid var(--app-border);
  margin-right: 0;
  padding-bottom: 14px;
}

:deep(.leaving-dialog .el-dialog__title) {
  font-weight: 600;
  color: var(--app-title);
}

@media (max-width: 640px) {
  .leaving-hero__stats {
    width: 100%;
  }

  .hero-stat {
    flex: 1;
    justify-content: center;
  }

  .service-panel__toggle-hint {
    display: none;
  }

  .message-card__reply {
    margin-left: 0;
  }

  .message-feed {
    max-height: 60vh;
  }
}
</style>
