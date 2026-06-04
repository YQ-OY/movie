<template>
  <div class="header">
    <div class="a">
      <div class="header-avatar">
        <el-dropdown>
          <el-button link>
            <div class="header-name">Admin</div>
            <el-avatar style="margin-top: 5px" :size="29"
                       src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png">
            </el-avatar>
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
              <el-dropdown-item v-for="item in list" :key="item">{{ item.content }}</el-dropdown-item>
              <el-dropdown-item v-if="!list || list.length === 0">今日工作还没有上传</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="header-icon">
        <el-tooltip content="厅有独钟 - 管理系统" placement="top">
          <el-icon style="font-size: 20px"><Warning /></el-icon>
        </el-tooltip>
      </div>
    </div>
  </div>
</template>

<script>
import {ListDailyWork} from "@/api/worker";
import { Setting, SwitchButton, Bell, Warning } from '@element-plus/icons-vue'
import { clearWorkerPermissions } from '@/utils/workerPermissions'

export default {
  name: "Header",
  components: { Setting, SwitchButton, Bell, Warning },

  data() {
    return {
      list: []
    }
  },

  mounted() {
    ListDailyWork().then(res => {
      setTimeout(() => {
        const data = res && res.data
        this.list = Array.isArray(data) ? data : []
      }, 700)
    }).catch(() => {
      this.list = []
    })
  },


  methods: {

    handleLogout() {
      localStorage.removeItem("token")
      localStorage.removeItem("role")
      localStorage.removeItem("wid")
      clearWorkerPermissions()
      this.$router.push("/login")
    }

  },

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
