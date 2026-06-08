<template>
  <div class="film-list">

    <el-form ref="form" :model="form" label-width="80px">

      <el-form-item label="电影封面">
        <el-upload class="upload-demo" drag accept=".png,.jpg" :headers="header" :action="uploadAction"
          :on-success="handleUploadSuccess" multiple>
          <el-icon class="el-icon--upload">
            <Upload />
          </el-icon>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">只能上传jpg/png文件，且不超过50mb</div>
          </template>
        </el-upload>
      </el-form-item>

      <el-form-item label="电影名称">
        <el-input v-model="form.name"></el-input>
      </el-form-item>

      <el-form-item label="上映地区">
        <el-select v-model="form.region" placeholder="请选择上映区域">
          <el-option label="内地" value="内地"></el-option>
          <el-option label="香港" value="香港"></el-option>
          <el-option label="台湾" value="台湾"></el-option>
          <el-option label="日本" value="日本"></el-option>
          <el-option label="美国" value="美国"></el-option>
          <el-option label="韩国" value="韩国"></el-option>
          <el-option label="英国" value="英国"></el-option>
          <el-option label="英国" value="英国"></el-option>
          <el-option label="法国" value="法国"></el-option>
          <el-option label="印度" value="印度"></el-option>
          <el-option label="其他" value="其他"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="全片时长">
        <el-input-number v-model="form.duration" :min="10" :max="550"></el-input-number>
      </el-form-item>

      <el-form-item label="上映时间">
        <el-col :span="11">
          <el-date-picker type="date" placeholder="选择日期" value-format="YYYY-MM-DD" v-model="form.releaseTime"
            style="width: 100%;"></el-date-picker>
        </el-col>
      </el-form-item>

      <el-form-item label="即时上架">
        <el-switch v-model="form.status"></el-switch>
      </el-form-item>

      <el-form-item label="电影类型" class="film-type-item">
        <el-radio-group v-model="form.type" class="film-type-radios">
          <el-radio label="爱情">爱情</el-radio>
          <el-radio label="喜剧">喜剧</el-radio>
          <el-radio label="科幻">科幻</el-radio>
          <el-radio label="动画">动画</el-radio>
          <el-radio label="恐怖">恐怖</el-radio>
          <el-radio label="悬疑">悬疑</el-radio>
          <el-radio label="冒险">冒险</el-radio>
          <el-radio label="动作">动作</el-radio>
          <el-radio label="犯罪">犯罪</el-radio>
          <el-radio label="历史">历史</el-radio>
          <el-radio label="古装">古装</el-radio>
          <el-radio label="战争">战争</el-radio>
          <el-radio label="纪录片">纪录片</el-radio>
          <el-radio label="家庭">家庭</el-radio>
          <el-radio label="传记">传记</el-radio>
          <el-radio label="武侠">武侠</el-radio>
          <el-radio label="儿童">儿童</el-radio>
          <el-radio label="短片">短片</el-radio>
          <el-radio label="其他">其他</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="内容简介">
        <el-input rows="8" type="textarea" v-model="form.introduction"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="onSubmit">确认提交</el-button>
        <el-button>取消</el-button>
      </el-form-item>

    </el-form>
  </div>
</template>

<script>
import { AddFilm } from "@/api/film";
import config from "@/config";
import { Upload } from '@element-plus/icons-vue'

export default {
  components: { Upload },
  data() {
    return {
      header: {
        "Authorization": localStorage.getItem("token")
      },
      uploadAction: config.API_URL + '/upload',
      url: '',
      form: {
        cover: 'null',
        name: '',
        region: '',
        releaseTime: '',
        duration: 120,
        introduction: '',
        type: '',
        status: true,
      }
    }
  },
  methods: {

    onSubmit() {
      this.form.cover = this.url;
      const film = this.form;
      AddFilm(film).then(res => {
        this.$message({
          type: 'success',
          message: '电影添加成功!'
        });
        this.$router.push("/film/list")
      })
    },

    handleUploadSuccess(res) {
      this.url = typeof res === 'string' ? res : (res && typeof res.data === 'string' ? res.data : '')
    },

  }
}
</script>

<style scoped>
.film-list {
  height: 100vh;
  /* 固定高度为视口高度，而非最小高度 */
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: hidden;
  /* 禁止整个页面滚动，让内部表格滚动 */
  padding: 20px;
  box-sizing: border-box;
  background: rgb(250, 251, 252);
}

/* 多行单选项：统一换行起点与圆点、文字垂直对齐 */
.film-type-item :deep(.el-form-item__content) {
  align-items: flex-start;
  line-height: 1.5;
}

.film-type-radios {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 20px;
  max-width: 100%;
}

.film-type-radios :deep(.el-radio) {
  margin-right: 0;
  height: auto;
  align-items: center;
}

.film-type-radios :deep(.el-radio__label) {
  line-height: 1;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
