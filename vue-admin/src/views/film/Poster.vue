<template>
  <div>
    <el-carousel v-if="posterList.length !== 0" height="500px" indicator-position="outside">
      <el-carousel-item v-for="(item, index) in posterList" :key="index">
        <img alt=""
             :src="item.url"
             class="image">
      </el-carousel-item>
    </el-carousel>

    <div class="content">
      <el-button
          @click="dialogFormVisible = true"
          type="primary"
          :icon="Upload">
        上传轮播海报
      </el-button>
      <el-button
          style="margin-left: 10px"
          type="danger"
          :icon="Delete"
          @click="deleteAllPoster">
        删除所有海报
      </el-button>
      <el-table
          ref="filterTable"
          :data="posterList"
          style="width: 100%;margin-top: 15px;">
        <el-table-column
            prop="createAt"
            label="上传时间"
            width="220"
        >
        </el-table-column>
        <el-table-column
            prop="title"
            label="标题"
            width="180">
        </el-table-column>
        <el-table-column label="地址预览">
          <template #default="scope">
            <a target="_blank" :href="scope.row.url">{{ scope.row.url }}</a>
          </template>
        </el-table-column>
        <el-table-column
            prop="status"
            label="上架状态"
            width="220">
          <template #default="scope">
            <el-tag
                v-if="scope.row.status"
                type="success"
                :disable-transitions="true">上架
            </el-tag>
            <el-tag
                v-else
                type="warning"
                :disable-transitions="true">下架
            </el-tag>
            <el-button v-if="!scope.row.status"
                       style="padding: 0 15px;color: #67C23A"
                       link
                       @click="changePosterStatus(scope.$index, scope.row, true)"
                       :icon="UploadFilled">上架
            </el-button>
            <el-button v-else
                       style="padding: 0 15px;color: #E6A23C"
                       link
                       @click="changePosterStatus(scope.$index, scope.row, false)"
                       :icon="Download">下架
            </el-button>
            <span style="padding-left: 100px">
              <el-popconfirm @confirm="handleDeletePoster(scope.$index, scope.row.id)" title="确定要删除这条内容吗？">
                <template #reference>
                  <el-button
                      style="color: #F56C6C" link
                      :icon="Delete">删除
                  </el-button>
                </template>
              </el-popconfirm>
            </span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog title="轮播海报" v-model="dialogFormVisible">
      <el-form :model="uploadPoster">
        <el-form-item label="海报标题" style="margin-bottom: 35px" label-width="120px">
          <el-input style="width: 360px" v-model="uploadPoster.title" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="上架状态" style="margin-bottom: 35px" label-width="120px">
          <el-select style="width: 360px" v-model="uploadPoster.status" placeholder="请选择活动区域">
            <el-option label="上架" value="true"></el-option>
            <el-option label="下架" value="false"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择图片" label-width="120px">
          <el-upload
              style="float: left"
              class="upload-demo"
              accept=".png,.jpg"
              :headers="header"
              :action="uploadAction"
              :on-success="handleUploadSuccess"
              multiple
              :limit="1">
            <el-button type="primary" :icon="Upload">上传轮播海报</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitUpload">确 定</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script>
import config from "@/config";
import {AddPoster, DeleteAllPoster, DeletePosterById, ListAllPoster, UpdatePoster} from "@/api/film";
import { Upload, Delete, UploadFilled, Download } from '@element-plus/icons-vue'

export default {
  components: { Upload, Delete, UploadFilled, Download },

  mounted() {
    ListAllPoster().then(res => {
      this.posterList = res.data
    })
  },

  data() {
    return {
      header: {
        "Authorization": localStorage.getItem("token")
      },
      dialogFormVisible: false,
      uploadAction: config.API_URL + '/upload',
      uploadPoster: {
        url: '',
        title: '',
        status: '',
        createAt: '',
      },
      posterList: [],
    }
  },

  methods: {

    handleUploadSuccess(res) {
      this.uploadPoster.url = typeof res === 'string' ? res : (res && typeof res.data === 'string' ? res.data : '')
    },

    submitUpload() {
      AddPoster(this.uploadPoster).then(res => {
        console.log(res)
        this.dialogFormVisible = false;
      })
    },

    deleteAllPoster() {
      DeleteAllPoster().then(res => {
        this.posterList = null;
      })
    },

    changePosterStatus(index, poster, status) {
      poster.status = status
      UpdatePoster(poster).then(res => {
        this.posterList[index].status = status
        this.$message({
          message: '成功消息：操作成功',
          type: 'success'
        });
      })
    },

    handleDeletePoster(index, id) {
      DeletePosterById(id).then(res => {
        this.posterList.splice(index, 1);
        this.$message({
          message: '成功消息：删除成功',
          type: 'success'
        });
      })
    },

  }
}
</script>

<style scoped>

.content {
  padding: 20px 40px;
}

.el-carousel__item h3 {
  color: #475669;
  font-size: 18px;
  opacity: 0.75;
  line-height: 300px;
  margin: 0;
}

.image {
  width: 100%;
  height: 500px;
}

</style>
