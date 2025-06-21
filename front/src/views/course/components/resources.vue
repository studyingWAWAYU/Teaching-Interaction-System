<template>
  <div class="resource-content">
    <!-- 教师权限：上传按钮 -->
    <div v-if="isTeacher" class="upload-section" style="background:none;box-shadow:none;padding:0;margin-bottom:24px;display:flex;justify-content:flex-end;">
      <Button
          type="primary"
          icon="ios-cloud-upload-outline"
          @click="showUploadModal = true"
          class="upload-btn"
          style="border-radius:22px;height:40px;padding:0 20px;font-size:15px;">
        Upload File
      </Button>
    </div>

    <div class="resource-list">
      <div class="file-item" v-for="file in files" :key="file.id">
        <div class="file-main-info">
          <div class="file-info">
            <Icon :type="getFileIcon(file.title)" class="file-icon" />
            <span class="file-name">{{ file.title }}</span>
          </div>
          <div v-if="file.description" class="file-description">
            <span class="desc-label">Tips：</span>{{ file.description }}
          </div>
        </div>
        <div class="file-meta">
          <span class="upload-time">
            <Icon type="ios-time-outline" />
            {{ file.uploadTime }}
          </span>

          <Button
              type="primary"
              size="middle"
              icon="ios-download-outline"
              @click="handleDownload(file)"
              class="download-btn">
            Download
          </Button>

          <!-- 教师权限：删除按钮 -->
          <div v-if="isTeacher" class="teacher-actions">
            <Button
                type="error"
                size="middle"
                icon="ios-trash-outline"
                @click="handleDelete(file)"
                class="del-btn">
              Delete
            </Button>
          </div>
        </div>
      </div>
    </div>

    <!-- 上传课件 -->
    <Modal v-model="showUploadModal" title="Upload Resources" @on-ok="handleUpload">
      <Form :model="uploadForm" :label-width="120">
        <FormItem label="title">
          <Input v-model="uploadForm.title" />
        </FormItem>
        <FormItem label="Tips (Optional)">
          <Input v-model="uploadForm.tip" />
        </FormItem>
        <FormItem label="Choose file">
          <Upload
              ref="upload"
              :show-upload-list="false"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :format="['pdf', 'docx', 'doc', 'ppt', 'pptx', 'zip', 'rar']"
              :max-size="20480"
              action="/api/upload"
              :before-upload="handleBeforeUpload" >
            <Button icon="ios-cloud-upload-outline">{{ uploadForm.file ? uploadForm.file.name : 'Choose file' }}</Button>
          </Upload>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<script>
import Cookies from 'js-cookie'
import { getAllCourseResources, addCourseResources, deleteCoursesResources, saveOrUpdateCourseResources } from '@/api/courseResources';
import { downloadFile } from '@/api/file';

export default {
  name: 'CourseSlides',
  data() {
    return {
      files: [],
      loading: false,
      error: null,
      showUploadModal: false,
      showEditModal: false,
      uploadForm: {
        title: '',
        file: null,
        tip: ''
      },
      editForm: {
        id: null,
        title: ''
      }
    }
  },
  computed: {
    // 判断当前用户是否为教师
    isTeacher() {
      try {
        const userInfo = Cookies.get('userInfo')
        if (userInfo) {
          const user = JSON.parse(userInfo)
          // 检查role
          if (user.role && user.role.name) {
            return user.role.name === 'ROLE_TEACHER' || user.role.name === 'ROLE_ADMIN'
          }
          if (user.roleName) {
            return user.roleName === 'ROLE_TEACHER' || user.roleName === 'ROLE_ADMIN'
          }
        }
        return false
      } catch (error) {
        console.error('User Info error:', error)
        return false
      }
    }
  },
  created() {
    this.fetchFiles()
  },
  methods: {
    async fetchFiles() {
      try {
        this.loading = true
        const response = await getAllCourseResources()
        if (response && response.data) {
          this.files = response.data
        }
      } catch (error) {
        this.error = error.message
        this.$Message.error('Failed to fetch resources')
      } finally {
        this.loading = false
      }
    },

    // 下载课件
    async handleDownload(file) {
      try {
        this.$Message.success(`Start to download: ${file.title}`)
        const response = await downloadFile(file.id)

        // 创建下载链接
        if (response && response.data) {
          const blob = new Blob([response.data])
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = file.title
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)
        }

        this.$Message.success('Download completed')
      } catch (error) {
        console.error('Download error:', error)
        this.$Message.error('Download failed')
      }
    },

    handleEdit(file) {
      this.editForm = {
        id: file.id,
        title: file.title
      }
      this.showEditModal = true
    },

    // 提交编辑
    async handleEditSubmit() {
      try {
        const params = {
          id: this.editForm.id,
          title: this.editForm.title
        }
        await saveOrUpdateCourseResources(params)
        this.$Message.success('Edit Successfully')
        this.showEditModal = false
        this.fetchFiles()
      } catch (error) {
        this.$Message.error('Edit failed')
      }
    },

    handleDelete(file) {
      this.$Modal.confirm({
        title: 'Confirm deletion',
        content: `Are you sure you want to delete "${file.title}"? `,
        okText: 'del',
        cancelText: 'Cancel',
        onOk: async () => {
          try {
            const params = {
              ids: [file.id]
            }
            await deleteCoursesResources(params)
            this.$Message.success('Delete Successfully')
            this.fetchFiles()
          } catch (error) {
            this.$Message.error('Delete failed')
          }
        }
      })
    },

    // 上传课件
    async handleUpload() {
      if (!this.uploadForm.title.trim()) {
        this.$Message.error('Please input the file name')
        return
      }

      try {
        const params = {
          title: this.uploadForm.title,
          description: this.uploadForm.tip,
          uploadTime: new Date().toLocaleString()
        }
        await addCourseResources(params)

        this.$Message.success('Upload Successfully')
        this.showUploadModal = false
        this.uploadForm = { title: '', file: null, tip: '' }
        this.fetchFiles()
      } catch (error) {
        this.$Message.error('Upload failed')
      }
    },

    // 文件上传成功回调
    handleUploadSuccess(response, file) {
      this.uploadForm.file = file
      // 自动填充title
      if (file && file.name) {
        this.uploadForm.title = file.name
      }
      this.$Message.success('File selected successfully')
    },

    // 文件上传错误回调
    handleUploadError(error) {
      this.$Message.error('File upload failed')
    },

    handleBeforeUpload(file) {
      this.uploadForm.file = file
      if (file && file.name) {
        this.uploadForm.title = file.name
      }
      return true
    },

    // 文件类型对应图标
    getFileIcon(fileName) {
      const extension = fileName.split('.').pop().toLowerCase()
      const iconMap = {
        'pdf': 'ios-document',
        'docx': 'ios-paper',
        'doc': 'ios-paper',
        'ppt': 'md-albums',
        'pptx': 'md-albums',
        'zip': 'ios-folder',
        'rar': 'ios-folder',
      }
      return iconMap[extension] || 'ios-document'
    }
  }
}
</script>

<style lang="less" scoped>
.resource-content {
  padding: 15px 25px;

  .upload-section {
    margin-bottom: 20px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 16px;
    box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.08);

    .upload-btn {
      background: rgba(0,122,255,0.6);
      border: none;
      padding: 8px 24px;
      font-size: 16px;
      border-radius: 22px;
      box-shadow: 0 2px 6px rgba(45, 140, 240, 0.2);

      &:hover {
        background: rgba(0,122,255,0.8);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(45, 140, 240, 0.3);
      }
    }
  }

  .resource-list {
    padding: 30px;

    .file-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 30px;
      background: rgba(255, 255, 255, 0.8);
      backdrop-filter: blur(8px);
      border-radius: 16px;
      margin-bottom: 20px;
      box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.08);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 2px 6px 16px rgba(0, 0, 0, 0.08);
        background: rgba(255, 255, 255, 0.9);
      }

      &:last-child {
        margin-bottom: 0;
      }

      .file-main-info {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        flex: 1;
      }

      .file-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .file-icon {
          font-size: 24px;
          color: #515a6e;
        }

        .file-name {
          color: #515a6e;
          font-size: 18px;
        }
      }

      .file-meta {
        display: flex;
        align-items: center;
        gap: 36px;

        .upload-time {
          color: #808695;
          font-size: 15px;
          display: flex;
          align-items: center;

          .ivu-icon {
            margin-right: 6px;
            font-size: 18px;
          }
        }

        .download-btn {
          transform: translateX(5px);
          background: rgba(0,122,255,0.6);
          border: none;
          padding: 5px 20px;
          font-size: 15px;
          border-radius: 22px;
          box-shadow: 0 2px 6px rgba(45, 140, 240, 0.2);

          &:hover {
            background: rgba(0,122,255,0.8);
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(45, 140, 240, 0.3);
          }

          .ivu-icon {
            margin-right: 6px;
            font-size: 18px;
          }
        }

        .teacher-actions {
          display: flex;
          gap: 8px;

          .del-btn {
            opacity: 0.7;
            transform: translateX(5px);
            border-radius: 22px;
            padding: 4px 12px;
            font-size: 14px;
            border: none;

            &:hover {
              opacity: 1.2;
              transform: translateY(-1px);
            }
          }
        }
      }

      .file-description {
        margin-top: 6px;
        background: #f6f7fa;
        color: #515a6e;
        border-radius: 8px;
        padding: 6px 14px;
        font-size: 15px;
        display: flex;
        align-items: center;

        .desc-label {
          font-weight: bold;
          color: #2d8cf0;
          margin-right: 6px;
        }
      }
    }
  }
}
</style>

