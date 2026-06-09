<template>
  <div class="seat-main">
    <el-steps style="padding-bottom: 50px" :active="2" align-center>
      <el-step title="步骤1" description="选择电影场次"></el-step>
      <el-step title="步骤2" description="选择座位号"></el-step>
      <el-step title="步骤3" description="加入购物车"></el-step>
      <el-step title="步骤4" description="结算付款"></el-step>
    </el-steps>
    <div class="seat-content">
      <div class="seat-aside">
        <div style="display: flex">
          <img style="padding: 30px" :src="film.cover" alt=""/>
          <div>
            <div class="film-title">{{ film.name }}</div>
            <div class="seat-aside-text">类型：{{ film.type }}</div>
            <div class="seat-aside-text">地区：{{ film.region }}</div>
            <div class="seat-aside-text">时长：{{ film.duration }}分钟</div>
          </div>
        </div>
        <div style="padding: 5px 30px">
          <div class="d1"><span>放映：</span>{{ arrangement.type }}</div>
          <div class="d1" style="color: #f56c6c">
            <span>开场：</span>{{ arrangement.date }} {{ arrangement.startTime }}
          </div>
          <div class="d1"><span>散场：</span>{{ arrangement.endTime }}</div>
          <div class="d1"><span>票价：</span>¥{{ arrangement.price }}/张</div>
          <el-divider></el-divider>
          <div class="d1">
            已选座位：
            <el-tag v-for="(item, i) in userSelectSeats" :key="i"
                    type="success"
                    style="margin-right: 5px"
                    effect="plain">
              {{ item }} 号
            </el-tag>
          </div>
          <div class="d1" style="padding-top: 10px">
            总计：
            <span style="color: #f56c6c">¥ </span>
            <span class="price-total">{{ cart.price }}</span>
          </div>
          <el-divider></el-divider>
          <div style="padding: 0 50px">
            <el-input
                v-model="cart.phone"
                style="padding-top: 20px; padding-bottom: 30px"
                placeholder="请输入11位中国大陆手机号"
                maxlength="11"
                clearable
            />
            <el-button
                :disabled="saleClosed"
                @click="submitSeat"
                class="add-cart-btn"
                type="danger"
                round
            >加入购物车
            </el-button>
          </div>
        </div>
      </div>

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
import {FindArrangementById, GetArrangementSeats} from "@/api/film";
import {CreateCart} from "@/api/cart";
import { isValidMobileCN } from "@/utils/validate";
import { isTicketSaleAllowed, TICKET_CLOSE_BEFORE_MINUTES } from "@/utils/ticketSale";
import { buildSeatRows } from "@/utils/cinemaSeatLayout";

export default {
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
      this.$router.push('/me/cart')
    },
  },
};
</script>

<style scoped>
.seat-main {
  padding: 80px;
}

.seat-content {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
  min-height: 850px;
  overflow: hidden;
}

.seat-aside {
  width: 30%;
  min-height: 850px;
  float: left;
  background: #f5f6f7;
}

.seat-aside img {
  width: 140px;
  height: 200px;
}

.film-title {
  padding-top: 40px;
  padding-bottom: 10px;
  font-size: 25px;
  letter-spacing: 2px;
}

.seat-select {
  padding: 30px 24px 40px;
  width: 65%;
  float: left;
  box-sizing: border-box;
}

.seat-aside-text {
  padding-top: 8px;
  font-size: 13px;
  letter-spacing: 1px;
  color: #666;
}

.d1 {
  font-size: 16px;
  letter-spacing: 2px;
  padding-bottom: 15px;
}

.d1 span {
  color: #91949c;
}

.price-total {
  color: #f56c6c;
  font-size: 25px;
  font-weight: bold;
}

.add-cart-btn {
  width: 100%;
  height: 60px;
  border-radius: 50px;
}

:deep(.el-input__inner) {
  border-radius: 50px;
  height: 55px;
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
  color: #606266;
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
  color: #909399;
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
  color: #909399;
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
  color: #909399;
  cursor: pointer;
}

.seat-box--available:hover {
  border-color: #409eff;
  color: #409eff;
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

.booking-info {
  width: 100%;
  text-align: left;
  font-size: 14px;
  line-height: 1.9;
  color: #606266;
}

.booking-info span {
  color: #909399;
}

.booking-price {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}
</style>
