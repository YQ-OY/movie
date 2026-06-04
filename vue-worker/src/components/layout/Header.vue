<template>
  <div class="header">
    <div class="a">
      <div class="header-avatar">
        <el-dropdown>
          <el-button link>
            <div class="header-name">{{ worker.nickname }}</div>
            <img style="margin-top: 5px;width: 29px;height: 29px;border-radius: 50%"
                 :src="worker.avatar" alt="null"/>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>
                <el-link :underline="false" href="/setting">
                  <el-icon style="font-size: 15px; margin-right: 3px"><Setting /></el-icon>个人设置
                </el-link>
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-button link @click="handleLogout">
                  <el-icon style="font-size: 15px; margin-right: 3px"><SwitchButton /></el-icon>退出登录
                </el-button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <div class="header-icon" style="padding-top: 16px">
        <el-dropdown>
          <el-badge value="new" class="item">
            <el-icon style="font-size: 18px"><Bell /></el-icon>
          </el-badge>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>这里还没有消息</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="header-icon">
        <el-tooltip content="厅有独钟 - 客服系统" placement="top">
          <el-icon style="font-size: 20px"><Warning /></el-icon>
        </el-tooltip>
      </div>
    </div>
  </div>
</template>

<script>
import {FindWorkerById} from "@/api/worker";
import { Setting, SwitchButton, Bell, Warning } from '@element-plus/icons-vue'

export default {
  name: "Header",
  components: { Setting, SwitchButton, Bell, Warning },

  data() {
    return {
      worker: {
        nickname: '',
        password: '',
        phone: '',
        gender: '',
        avatar: '',
        department: '',
      }
    }
  },

  mounted() {
    FindWorkerById(localStorage.getItem("wid")).then(res => {
      this.worker = res.data;
    })
  },

  methods: {
    handleLogout() {
      localStorage.removeItem("wid")
      localStorage.removeItem("token")
      this.$router.push('/login')
    }
  }


}
</script>

<style scoped>

.header {

}

.header-icon {
  float: right;
  font-size: 20px;
  padding-top: 19px;
  padding-right: 30px;
}

.header-avatar {
  float: right;
  padding-left: 20px;
}

.header-name {
  float: right;
  padding-top: 12px;
  font-size: 15px;
  padding-right: 10px;
  padding-left: 9px;
}

.el-button {
  padding-top: 8px;
  color: #000000;
  letter-spacing: 2px;
}
</style>
