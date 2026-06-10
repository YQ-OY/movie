<template>
  <div class="ticket-page">
    <el-page-header @back="goBack" content="电影购票" />

    <div v-if="dayOptions.length" class="day-filter">
      <button
        v-for="day in dayOptions"
        :key="day.dateKey"
        type="button"
        class="day-filter__item"
        :class="{ 'day-filter__item--active': selectedDateKey === day.dateKey }"
        @click="selectedDateKey = day.dateKey"
      >
        <span class="day-filter__label">{{ day.label }}</span>
        <span class="day-filter__week">{{ day.weekLabel }}</span>
      </button>
    </div>

    <div v-if="loading" class="ticket-empty">加载场次中...</div>
    <div v-else-if="!hasUpcomingShow" class="ticket-empty ticket-empty--warn">
      近 {{ dayOptions.length }} 天内暂无可购场次
    </div>
    <div v-else-if="list.length === 0" class="ticket-empty">
      该日期暂无可购场次
    </div>

    <template v-if="list.length > 0">
      <div class="item" v-for="item in list" :key="item.id">

      <div class="item-des">
        <div class="d1">{{ film.name }}</div>
        <div class="d2">{{ item.date }} {{ formatStartTime(item.startTime) }}</div>
        <el-tag effect="plain">{{ item.type }}</el-tag>
      </div>
      <div class="item-btn">
        <el-button
          v-if="isTicketSaleAllowed(item.date, item.startTime)"
          type="primary"
          class="app-btn-brand"
          @click="handleSelectSeat(item.id)"
          round
        >选座购票</el-button>
        <el-tag v-else type="info" effect="plain" size="large">已停售</el-tag>
      </div>
      <div class="item-price">￥{{ item.price }}</div>
      </div>
    </template>
  </div>
</template>

<script>
import { FindArrangements } from '@/api/film'
import {
  buildTicketDayOptions,
  filterUpcomingArrangements,
  formatDateKey,
  isTicketSaleAllowed,
  normalizeDateKey,
  TICKET_FILTER_DAY_COUNT,
} from '@/utils/ticketSale'

export default {
  data() {
    return {
      loading: false,
      allArrangements: [],
      film: {},
      dayOptions: buildTicketDayOptions(TICKET_FILTER_DAY_COUNT),
      selectedDateKey: formatDateKey(new Date()),
    }
  },

  computed: {
    list() {
      return filterUpcomingArrangements(this.allArrangements, this.selectedDateKey, TICKET_FILTER_DAY_COUNT)
    },
    hasUpcomingShow() {
      return filterUpcomingArrangements(this.allArrangements, '', TICKET_FILTER_DAY_COUNT).length > 0
    },
  },

  mounted() {
    this.loadArrangements()
  },

  methods: {
    isTicketSaleAllowed,

    loadArrangements() {
      const fid = this.$route.query.fid
      if (!fid) return
      this.loading = true
      FindArrangements(fid)
        .then(res => {
          this.allArrangements = res.data?.arrangements || []
          this.film = res.data?.film || {}
          this.syncSelectedDate()
        })
        .finally(() => {
          this.loading = false
        })
    },

    syncSelectedDate() {
      const upcoming = filterUpcomingArrangements(this.allArrangements, '', TICKET_FILTER_DAY_COUNT)
      if (!upcoming.length) return
      const selectedKey = normalizeDateKey(this.selectedDateKey)
      const selectedStillValid = upcoming.some(item => normalizeDateKey(item.date) === selectedKey)
      if (!selectedStillValid) {
        this.selectedDateKey = normalizeDateKey(upcoming[0].date)
      }
    },

    formatStartTime(startTime) {
      if (!startTime) return ''
      const parts = String(startTime).split(':')
      if (parts.length >= 2) {
        return `${parts[0]}:${parts[1]}`
      }
      return startTime
    },

    goBack() {
      this.$router.go(-1)
    },

    handleSelectSeat(id) {
      if (!localStorage.getItem('uid')) {
        this.$confirm('系统还没有检测到您的登陆信息, 是否去登录?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          this.$router.push('/login')
        })
      } else {
        this.$router.push('/seat?id=' + id)
      }
    },
  },
}
</script>

<style scoped>
.ticket-page {
  padding-bottom: 24px;
}

.day-filter {
  display: flex;
  gap: 10px;
  margin: 20px 0 8px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.day-filter__item {
  flex: 0 0 auto;
  min-width: 72px;
  padding: 10px 12px;
  border: 1px solid var(--app-border-strong);
  border-radius: 10px;
  background: var(--app-surface);
  color: var(--app-text-secondary);
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s, color 0.2s;
}

.day-filter__item--active {
  border-color: var(--app-primary-border);
  background: var(--app-primary-bg);
  color: var(--app-text-strong);
}

.day-filter__label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.3;
}

.day-filter__week {
  display: block;
  margin-top: 2px;
  font-size: 12px;
  opacity: 0.85;
}

.ticket-empty {
  margin-top: 24px;
  font-size: 15px;
  letter-spacing: 1px;
  color: var(--app-text-secondary);
}

.ticket-empty--warn {
  color: var(--app-price);
  font-weight: 600;
}

.item {
  padding-top: 20px;
  height: 100px;
}

.d1 {
  font-size: 15px;
  letter-spacing: 2px;
  padding-bottom: 7px;
  font-weight: 600;
  color: var(--app-title);
}

.d2 {
  font-size: 13px;
  letter-spacing: 1px;
  padding-bottom: 7px;
  color: var(--app-text);
}

.item-des {
  float: left;
}

.item-btn {
  float: right;
  padding-top: 30px;
  padding-left: 50px;
}

.item-price {
  float: right;
  color: var(--app-price);
  font-weight: 700;
  line-height: 100px;
}
</style>
