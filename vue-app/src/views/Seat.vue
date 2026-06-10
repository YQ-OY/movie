<template>
  <div class="seat-main">
    <el-steps style="padding-bottom: 50px" :active="2" align-center>
      <el-step title="步骤1" description="选择电影场次"></el-step>
      <el-step title="步骤2" description="选择座位号"></el-step>
      <el-step title="步骤3" description="加入购物车"></el-step>
      <el-step title="步骤4" description="结算付款"></el-step>
    </el-steps>

    <div class="seat-content">
      <!-- 左侧信息栏 -->
      <div class="seat-aside">
        <div class="film-basic">
          <img :src="film.cover" alt="" class="film-cover" />
          <div class="film-info">
            <div class="film-title">{{ film.name }}</div>
            <div class="film-desc">类型：{{ formatFilmTypes(film.type) }}</div>
            <div class="film-desc">地区：{{ film.region }}</div>
            <div class="film-desc">时长：{{ film.duration }}分钟</div>
          </div>
        </div>

        <div class="order-panel">
          <div class="order-card">
            <div class="info-list">
              <div class="info-row">
                <span class="info-icon-wrap"><el-icon><VideoCamera /></el-icon></span>
                <span class="info-label">放映</span>
                <span class="show-type-tag" :class="showTypeClass">
                  <el-icon class="show-type-tag__icon"><Tickets /></el-icon>
                  {{ arrangement.type }}
                </span>
              </div>

              <div class="info-row">
                <span class="info-icon-wrap"><el-icon><Calendar /></el-icon></span>
                <span class="info-label">开场</span>
                <span class="info-value info-value--time">{{ arrangement.date }} {{ arrangement.startTime }}</span>
              </div>

              <div class="info-row">
                <span class="info-icon-wrap"><el-icon><Timer /></el-icon></span>
                <span class="info-label">散场</span>
                <span class="info-value info-value--time">{{ arrangement.endTime }}</span>
              </div>

              <div class="info-row">
                <span class="info-icon-wrap"><el-icon><PriceTag /></el-icon></span>
                <span class="info-label">票价</span>
                <span class="price-value">¥{{ arrangement.price }}<small>/张</small></span>
              </div>
            </div>

            <div class="order-divider" />

            <div class="info-row seats-row">
              <span class="info-icon-wrap"><el-icon><Ticket /></el-icon></span>
              <span class="info-label">已选座位</span>
              <div class="seats-list">
                <el-tag
                  v-for="(item, i) in userSelectSeats"
                  :key="i"
                  type="success"
                  effect="plain"
                  size="small"
                  class="seat-tag"
                >
                  {{ item }} 号
                </el-tag>
                <span v-if="!userSelectSeats.length" class="empty-seat">未选择</span>
              </div>
            </div>

            <div class="total-card">
              <span class="info-icon-wrap info-icon-wrap--accent"><el-icon><Wallet /></el-icon></span>
              <span class="info-label">总计</span>
              <span class="total-price">¥{{ cart.price }}</span>
            </div>

            <div class="order-divider" />

            <div class="payment-area">
              <el-input
                v-model="cart.phone"
                placeholder="请输入11位中国大陆手机号"
                maxlength="11"
                clearable
                class="phone-input"
              />
              <el-button
                :disabled="saleClosed"
                @click="submitSeat"
                class="add-cart-btn"
                type="danger"
                round
              >
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧选座区 -->
      <div class="hall seat-select">
        <div class="seat-legend">
          <div class="legend-item">
            <span class="legend-box legend-available"></span>
            <span>可选座位</span>
          </div>
          <div class="legend-item">
            <span class="legend-box legend-selected"></span>
            <span>已选座位</span>
          </div>
          <div class="legend-item">
            <span class="legend-box legend-sold"></span>
            <span>已售座位</span>
          </div>
          <div class="legend-item">
            <span class="legend-box legend-locked"></span>
            <span>已被占座位</span>
          </div>
        </div>

        <div class="cinema-hall">
          <div class="cinema-screen">
            <div class="cinema-screen__bar"></div>
            <div class="cinema-screen__label">银幕中央</div>
          </div>

          <div class="cinema-rows">
            <div v-for="row in seatRows" :key="row.rowIndex" class="cinema-row">
              <span class="cinema-row__label">{{ row.rowIndex }} 排</span>
              <div class="cinema-row__seats">
                <template v-for="(cell, ci) in row.cells" :key="ci">
                  <span v-if="cell.type === 'aisle'" class="cinema-aisle"></span>
                  <span
                    v-else
                    class="seat-box"
                    :class="'seat-box--' + cell.status"
                    :title="cell.seatNo + ' 号'"
                    @click="onSeatClick(cell)"
                  >{{ cell.seatNo }}</span>
                </template>
              </div>
              <span class="cinema-row__label cinema-row__label--right">{{ row.rowIndex }} 排</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 预订成功弹窗 -->
    <el-dialog
      v-model="bookingDialogVisible"
      title="预订成功"
      width="420px"
      :show-close="false"
      align-center
    >
      <el-result icon="success" title="预订成功" sub-title="请前往购物车完成付款">
        <template #extra>
          <div class="booking-info">
            <p><span>电影：</span>《{{ bookingInfo.filmName }}》</p>
            <p><span>场次：</span>{{ bookingInfo.showTime }}</p>
            <p><span>座位：</span>{{ bookingInfo.seats }}</p>
            <p><span>手机号：</span>{{ bookingInfo.phone }}</p>
            <p class="booking-price"><span>金额：</span>￥{{ bookingInfo.price }}</p>
          </div>
        </template>
      </el-result>
      <template #footer>
        <el-button @click="stayOnPage">继续选座</el-button>
        <el-button type="primary" @click="goToCart">前往购物车付款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  VideoCamera, Tickets, Calendar, Timer, PriceTag, Ticket, Wallet, ShoppingCart
} from '@element-plus/icons-vue'
import { FindArrangementById, GetArrangementSeats } from "@/api/film";
import { CreateCart } from "@/api/cart";
import { isValidMobileCN } from "@/utils/validate";
import { isTicketSaleAllowed, TICKET_CLOSE_BEFORE_MINUTES } from "@/utils/ticketSale";
import { buildSeatRows } from "@/utils/cinemaSeatLayout";
import { openProfileCenter } from "@/utils/profileCenter";
import { formatFilmTypes } from '@/utils/filmType'

export default {
  name: "SeatSelection",
  components: {
    VideoCamera, Tickets, Calendar, Timer, PriceTag, Ticket, Wallet, ShoppingCart
  },
  data() {
    return {
      cart: {
        uid: localStorage.getItem("uid"),
        aid: this.$route.query.id,
        seats: '',
        phone: "",
        price: 0
      },
      arrangement: {},
      film: {},
      seatRows: [],
      userSelectSeats: [],
      soldSeats: [],
      lockedSeats: [],
      seatRefreshTimer: null,
      saleClosed: false,
      bookingDialogVisible: false,
      bookingInfo: {
        filmName: '',
        showTime: '',
        seats: '',
        phone: '',
        price: 0,
      },
    }
  },
  computed: {
    showTypeClass() {
      const type = (this.arrangement.type || '').toUpperCase()
      if (type.includes('IMAX')) return 'show-type-tag--imax'
      if (type.includes('3D')) return 'show-type-tag--3d'
      if (type.includes('2D')) return 'show-type-tag--2d'
      return 'show-type-tag--default'
    },
  },
  mounted() {
    FindArrangementById(this.$route.query.id).then((res) => {
      this.arrangement = res.data.arrangement;
      this.film = res.data.film;
      this.saleClosed = !isTicketSaleAllowed(
        this.arrangement.date,
        this.arrangement.startTime
      );
      if (this.saleClosed) {
        this.$message.error(
          `该场次已停售（开场前${TICKET_CLOSE_BEFORE_MINUTES}分钟停止售票）`
        );
      }
      GetArrangementSeats(this.$route.query.id).then(res => {
        this.soldSeats = res.data.soldSeats || []
        this.lockedSeats = res.data.lockedSeats || []
        this.rebuildLayout()
      })
      this.seatRefreshTimer = setInterval(() => {
        this.refreshSeatStatus()
      }, 30000)
    });
  },
  beforeUnmount() {
    if (this.seatRefreshTimer) {
      clearInterval(this.seatRefreshTimer)
    }
  },
  methods: {
    formatFilmTypes,

    statusContext() {
      return {
        userSelectSeats: this.userSelectSeats,
        soldSeats: this.soldSeats,
        lockedSeats: this.lockedSeats,
      }
    },
    rebuildLayout() {
      if (!this.arrangement.seatNumber) {
        this.seatRows = []
        return
      }
      this.seatRows = buildSeatRows(this.arrangement.seatNumber, this.statusContext())
    },
    refreshSeatStatus() {
      GetArrangementSeats(this.$route.query.id).then(res => {
        this.soldSeats = res.data.soldSeats || []
        this.lockedSeats = res.data.lockedSeats || []
        this.rebuildLayout()
      })
    },
    onSeatClick(cell) {
      if (cell.status === 'sold' || cell.status === 'locked') {
        return
      }
      if (cell.status === 'selected') {
        this.handleDisSelect(cell.seatNo)
      } else {
        this.handleSelect(cell.seatNo)
      }
    },
    handleSelect(seatNo) {
      if (this.userSelectSeats.length >= 4) {
        this.userSelectSeats.shift()
      }
      this.userSelectSeats.push(seatNo)
      this.cart.price = this.arrangement.price * this.userSelectSeats.length
      this.rebuildLayout()
    },
    handleDisSelect(seatNo) {
      const idx = this.userSelectSeats.indexOf(seatNo)
      if (idx !== -1) {
        this.userSelectSeats.splice(idx, 1)
      }
      this.cart.price = this.arrangement.price * this.userSelectSeats.length
      this.rebuildLayout()
    },
    checkPhoneAndSeats() {
      if (this.userSelectSeats.length === 0) {
        this.$message.warning('请选择要订购的座位')
        return false
      }
      if (!isValidMobileCN(this.cart.phone)) {
        this.$message.warning('请输入11位有效中国大陆手机号')
        return false
      }
      return true
    },
    submitSeat() {
      if (this.saleClosed) {
        this.$message.error(`开场前${TICKET_CLOSE_BEFORE_MINUTES}分钟停止售票`)
        return
      }
      if (this.checkPhoneAndSeats()) {
        const seatsStr = this.userSelectSeats.map(s => s + '号').join('')
        const payload = { ...this.cart, seats: seatsStr }
        CreateCart(payload).then(res => {
          if (res.success) {
            this.bookingInfo = {
              filmName: this.film.name,
              showTime: `${this.arrangement.date} ${this.arrangement.startTime}`,
              seats: seatsStr,
              phone: this.cart.phone,
              price: this.cart.price,
            }
            this.bookingDialogVisible = true
            this.resetSelection()
            this.refreshSeatStatus()
          }
        })
      }
    },
    resetSelection() {
      this.cart.seats = ''
      this.cart.phone = ''
      this.cart.price = 0
      this.userSelectSeats = []
      this.rebuildLayout()
    },
    stayOnPage() {
      this.bookingDialogVisible = false
    },
    goToCart() {
      this.bookingDialogVisible = false
      openProfileCenter(this.$router, 'cart')
    },
  },
};
</script>

<style scoped>
/* 全局容器 */
.seat-main {
  padding: 80px;
  background: var(--app-bg);
  min-height: calc(100vh - 72px);
}

/* 左右布局容器 */
.seat-content {
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.45);
  min-height: 850px;
  overflow: hidden;
  display: flex;
  flex-wrap: wrap;
  border-radius: 8px;
  border: 1px solid var(--app-border-strong);
  background: var(--app-surface);
}

/* ========= 左侧信息栏 ========= */
.seat-aside {
  width: 30%;
  min-height: 850px;
  background: linear-gradient(175deg, var(--app-surface-muted) 0%, var(--app-bg) 100%);
  display: flex;
  flex-direction: column;
  color: var(--app-text);
}

.film-basic {
  display: flex;
  padding: 26px 22px 22px;
  gap: 16px;
  border-bottom: 1px solid var(--app-border);
}

.film-cover {
  width: 118px;
  height: 168px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.35);
  flex-shrink: 0;
}

.film-info {
  flex: 1;
  min-width: 0;
  padding-top: 2px;
}

.film-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 10px;
  line-height: 1.35;
  color: var(--app-title-strong);
  letter-spacing: 0.5px;
}

.film-desc {
  font-size: 13px;
  color: var(--app-text-secondary);
  margin-bottom: 5px;
  line-height: 1.55;
}

.order-panel {
  padding: 18px 20px 24px;
  flex: 1;
}

.order-card {
  padding: 0;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 11px;
}

.info-row {
  display: grid;
  grid-template-columns: 36px 68px 1fr;
  align-items: center;
  gap: 10px;
  min-height: 36px;
}

.info-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 9px;
  background: rgba(255, 255, 255, 0.06);
  color: var(--app-primary);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.info-icon-wrap .el-icon {
  font-size: 18px;
}

.info-icon-wrap--accent {
  background: rgba(239, 68, 68, 0.15);
  color: #fca5a5;
}

.info-label {
  color: var(--app-text-secondary);
  font-size: 14px;
  flex-shrink: 0;
  white-space: nowrap;
}

.info-value {
  color: var(--app-title);
  font-size: 15px;
  font-weight: 500;
  line-height: 1.4;
  word-break: break-all;
}

.info-value--time {
  color: var(--app-text);
  font-weight: 400;
}

.price-value {
  color: var(--app-price);
  font-size: 19px;
  font-weight: 700;
  line-height: 1;
}

.price-value small {
  font-size: 13px;
  font-weight: 500;
  color: var(--app-text-muted);
  margin-left: 1px;
}

/* 放映类型标签 */
.show-type-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  line-height: 1;
  white-space: nowrap;
}

.show-type-tag__icon {
  font-size: 14px;
}

.show-type-tag--2d {
  background: linear-gradient(135deg, #4a4a4a, #666666);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.show-type-tag--3d {
  background: linear-gradient(135deg, #7c3aed, #8b5cf6);
  box-shadow: 0 2px 10px rgba(124, 58, 237, 0.35);
}

.show-type-tag--imax {
  background: linear-gradient(135deg, #b45309, #f59e0b);
  box-shadow: 0 2px 10px rgba(180, 83, 9, 0.35);
}

.show-type-tag--default {
  background: var(--app-gradient-brand);
  box-shadow: 0 2px 10px var(--app-primary-shadow);
}

.order-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.08);
  margin: 14px 0;
}

/* 已选座位 */
.seats-row {
  align-items: flex-start;
  margin-bottom: 10px;
}

.seats-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
  min-height: 26px;
}

.seat-tag {
  margin: 0;
  font-size: 13px;
  border-radius: 6px;
}

.empty-seat {
  font-size: 14px;
  color: var(--app-text-muted);
}

.total-card {
  display: grid;
  grid-template-columns: 36px 68px 1fr;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  background: rgba(239, 68, 68, 0.1);
  border-radius: 10px;
  border: 1px solid rgba(239, 68, 68, 0.22);
}

.total-card .info-label {
  color: var(--app-text);
}

.total-price {
  font-size: 26px;
  font-weight: 800;
  color: var(--app-price);
  line-height: 1;
  letter-spacing: -0.5px;
}

/* 支付区域 */
.payment-area {
  margin-top: 2px;
}

.phone-input {
  margin-bottom: 14px;
}

.phone-input :deep(.el-input__wrapper) {
  border-radius: 50px;
  height: 48px;
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
}

.phone-input :deep(.el-input__wrapper:hover),
.phone-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--app-primary-border);
  box-shadow: 0 0 0 3px var(--app-primary-bg);
  background: rgba(255, 255, 255, 0.08);
}

.phone-input :deep(.el-input__inner) {
  font-size: 15px;
  color: var(--app-title);
}

.phone-input :deep(.el-input__inner::placeholder) {
  color: var(--app-text-muted);
}

.add-cart-btn {
  width: 100%;
  height: 50px;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 600;
  background: var(--app-gradient-brand);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  letter-spacing: 0.5px;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.add-cart-btn .el-icon {
  font-size: 18px;
}

.add-cart-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 18px var(--app-primary-shadow-strong);
}

.add-cart-btn:active {
  transform: translateY(1px);
}

/* ========= 右侧选座区 ========= */
.seat-select {
  padding: 30px 24px 40px;
  width: 70%;
  box-sizing: border-box;
  background: var(--app-surface-elevated);
}

/* 图例 */
.seat-legend {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 24px;
  margin-bottom: 28px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--app-text-muted);
}

.legend-box {
  width: 22px;
  height: 22px;
  border-radius: 4px;
  box-sizing: border-box;
  flex-shrink: 0;
}

.legend-available {
  background: transparent;
  border: 2px solid #c0c4cc;
}

.legend-selected {
  background: #67c23a;
  border: 2px solid #67c23a;
}

.legend-sold {
  background: #f56c6c;
  border: 2px solid #f56c6c;
}

.legend-locked {
  background: #e6a23c;
  border: 2px solid #e6a23c;
}

/* 影厅 */
.cinema-hall {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.cinema-screen {
  width: 72%;
  max-width: 520px;
  margin-bottom: 36px;
  text-align: center;
}

.cinema-screen__bar {
  height: 8px;
  border-radius: 4px 4px 0 0;
  background: linear-gradient(to bottom, #e4e7ed, #c0c4cc);
  transform: perspective(120px) rotateX(-8deg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.cinema-screen__label {
  margin-top: 10px;
  font-size: 14px;
  color: var(--app-text-muted);
  letter-spacing: 4px;
}

.cinema-rows {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.cinema-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cinema-row__label {
  width: 36px;
  font-size: 12px;
  color: var(--app-text-muted);
  text-align: center;
  flex-shrink: 0;
}

.cinema-row__label--right {
  visibility: hidden;
}

.cinema-row__seats {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
}

.cinema-aisle {
  width: 28px;
  flex-shrink: 0;
}

/* 座位方块 */
.seat-box {
  width: 32px;
  height: 32px;
  margin: 0 3px;
  border-radius: 4px;
  box-sizing: border-box;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 500;
  user-select: none;
  transition: transform 0.12s ease, box-shadow 0.12s ease;
}

.seat-box--available {
  background: transparent;
  border: 2px solid #c0c4cc;
  color: var(--app-text-muted);
  cursor: pointer;
}

.seat-box--available:hover {
  border-color: var(--app-primary, #409eff);
  color: var(--app-primary, #409eff);
  transform: scale(1.06);
}

.seat-box--selected {
  background: #67c23a;
  border: 2px solid #67c23a;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(103, 194, 58, 0.35);
}

.seat-box--sold {
  background: #f56c6c;
  border: 2px solid #f56c6c;
  color: #fff;
  cursor: not-allowed;
}

.seat-box--locked {
  background: #e6a23c;
  border: 2px solid #e6a23c;
  color: #fff;
  cursor: not-allowed;
}

/* 弹窗样式 */
.booking-info {
  width: 100%;
  text-align: left;
  font-size: 14px;
  line-height: 1.9;
  color: var(--app-text-secondary);
}

.booking-info span {
  color: var(--app-text-muted);
}

.booking-price {
  font-size: 16px;
  font-weight: bold;
  color: var(--app-price, #f56c6c);
}

/* 响应式 */
@media (max-width: 1000px) {
  .seat-main {
    padding: 40px;
  }
  .seat-aside {
    width: 35%;
  }
  .film-basic {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  .order-panel {
    padding: 14px 16px 20px;
  }
  .info-row,
  .total-card {
    grid-template-columns: 32px 60px 1fr;
    gap: 8px;
  }
  .info-icon-wrap {
    width: 32px;
    height: 32px;
  }
  .info-icon-wrap .el-icon {
    font-size: 16px;
  }
  .seat-select {
    width: 65%;
  }
}

@media (max-width: 768px) {
  .seat-main {
    padding: 20px;
  }
  .seat-content {
    flex-direction: column;
  }
  .seat-aside {
    width: 100%;
    min-height: auto;
  }
  .seat-select {
    width: 100%;
  }
  .cinema-row__label--right {
    visibility: visible;
  }
}
</style>