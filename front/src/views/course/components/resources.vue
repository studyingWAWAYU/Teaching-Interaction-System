<template>
  <div class="resource-content">
    <!-- 教师权限：显示上传按钮 -->
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
        <div class="file-info">
          <Icon :type="getFileIcon(file.title)" class="file-icon" />
          <span class="file-name">{{ file.title }}</span>
        </div>
        <div class="file-meta">
          <span class="upload-time">
            <Icon type="ios-time-outline" />
            {{ file.uploadTime }}
          </span>
          
          <!-- 学生权限：只显示下载按钮 -->
          <Button 
            v-if="!isTeacher"
            type="primary"
            size="middle"
            icon="ios-download-outline"
            @click="handleDownload(file)"
            class="download-btn">
            Download
          </Button>
          
          <!-- 教师权限：显示编辑和删除按钮 -->
          <div v-if="isTeacher" class="teacher-actions">
            <Button 
              type="primary"
              size="small"
              icon="ios-download-outline"
              @click="handleDownload(file)"
              class="action-btn">
              Download
            </Button>
            <Button 
              type="error"
              size="small"
              icon="ios-trash-outline"
              @click="handleDelete(file)"
              class="action-btn">
              Delete
            </Button>
          </div>
        </div>
      </div>
    </div>

    <!-- 上传课件模态框 -->
    <Modal v-model="showUploadModal" title="上传课件" @on-ok="handleUpload">
      <Form :model="uploadForm" :label-width="80">
        <FormItem label="课件名称">
          <Input v-model="uploadForm.title" placeholder="请输入课件名称" />
        </FormItem>
        <FormItem label="选择文件">
          <Upload
            ref="upload"
            :show-upload-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :format="['pdf', 'docx', 'doc', 'ppt', 'pptx', 'zip', 'rar']"
            :max-size="20480"
            action="/api/upload">
            <Button icon="ios-cloud-upload-outline">选择文件</Button>
          </Upload>
        </FormItem>
      </Form>
    </Modal>

    <!-- 编辑课件模态框 -->
    <Modal v-model="showEditModal" title="编辑课件" @on-ok="handleEditSubmit">
      <Form :model="editForm" :label-width="80">
        <FormItem label="课件名称">
          <Input v-model="editForm.title" placeholder="请输入课件名称" />
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<script>
import Cookies from 'js-cookie'
import { getAllCourseResources, addCourseResources, deleteCoursesResources, saveOrUpdateCourseResources } from '@/api/courseResources';
import { getAllUsers } from '@/views/roster/user/api';

// 模拟数据，后续可替换为API调用
const mockFiles = [
  {
    id: 1,
    title: 'Java Basic Grammar.pdf',
    uploadTime: '2024-03-15 14:30'
  },
  {
    id: 2,
    title: 'Handout1.docx',
    uploadTime: '2024-03-15 14:30'
  },
  {
    id: 3,
    title: 'Java Object.pptx',
    uploadTime: '2024-03-20 10:15'
  },
  {
    id: 4,
    title: 'Java Code.zip',
    uploadTime: '2024-03-25 16:45'
  }
]

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
        file: null
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
          // 检查role对象中的name字段
          if (user.role && user.role.name) {
            return user.role.name === 'ROLE_TEACHER' || user.role.name === 'ROLE_ADMIN'
          }
          // 兼容旧版本，检查roleName字段
          if (user.roleName) {
            return user.roleName === 'ROLE_TEACHER' || user.roleName === 'ROLE_ADMIN'
          }
        }
        return false
      } catch (error) {
        console.error('解析用户信息失败:', error)
        return false
      }
    }
  },
  created() {
    this.fetchFiles()
  },
  methods: {
    // 获取课件列表
    async fetchFiles() {
      try {
        this.loading = true
        // TODO: 替换为实际的API调用
        // const response = await this.$api.getCourseFiles(this.$route.params.id)
        // this.files = response.data
        
        // 临时使用模拟数据
        this.files = mockFiles
      } catch (error) {
        this.error = error.message
        this.$Message.error('Failed to fetch the resources list.')
      } finally {
        this.loading = false
      }
    },

    // 下载课件
    async handleDownload(file) {
      try {
        // TODO: 替换为实际的下载API调用
        // const response = await this.$api.downloadFile(file.id)
        // 处理文件下载逻辑
        
        this.$Message.success(`开始下载: ${file.title}`)
      } catch (error) {
        this.$Message.error('下载失败，请稍后重试.')
      }
    },

    // 编辑课件
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
        // TODO: 替换为实际的编辑API调用
        // await this.$api.updateFile(this.editForm)
        
        // 更新本地数据
        const index = this.files.findIndex(f => f.id === this.editForm.id)
        if (index !== -1) {
          this.files[index].title = this.editForm.title
        }
        
        this.$Message.success('编辑成功')
        this.showEditModal = false
      } catch (error) {
        this.$Message.error('编辑失败，请稍后重试')
      }
    },

    // 删除课件
    handleDelete(file) {
      this.$Modal.confirm({
        title: '确认删除',
        content: `确定要删除课件 "${file.title}" 吗？`,
        onOk: async () => {
          try {
            // TODO: 替换为实际的删除API调用
            // await this.$api.deleteFile(file.id)
            
            // 从本地数据中移除
            this.files = this.files.filter(f => f.id !== file.id)
            this.$Message.success('删除成功')
          } catch (error) {
            this.$Message.error('删除失败，请稍后重试')
          }
        }
      })
    },

    // 上传课件
    async handleUpload() {
      if (!this.uploadForm.title.trim()) {
        this.$Message.error('请输入课件名称')
        return
      }
      
      try {
        // TODO: 替换为实际的上传API调用
        // await this.$api.uploadFile(this.uploadForm)
        
        // 添加到本地数据
        const newFile = {
          id: Date.now(),
          title: this.uploadForm.title,
          uploadTime: new Date().toLocaleString()
        }
        this.files.unshift(newFile)
        
        this.$Message.success('上传成功')
        this.showUploadModal = false
        this.uploadForm = { title: '', file: null }
      } catch (error) {
        this.$Message.error('上传失败，请稍后重试')
      }
    },

    // 文件上传成功回调
    handleUploadSuccess(response, file) {
      this.uploadForm.file = file
      this.$Message.success('文件上传成功')
    },

    // 文件上传失败回调
    handleUploadError(error, file) {
      this.$Message.error('文件上传失败')
    },

    // 根据文件类型获取图标
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
      font-weight: 540;
      border-radius: 16px;
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
          transition: all 0.3s ease;
          background: rgba(0,122,255,0.6);
          border: none;
          padding: 5px 20px;
          font-size: 15px;
          font-weight: 540;
          border-radius: 16px;
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

          .action-btn {
            transition: all 0.3s ease;
            border-radius: 12px;
            padding: 4px 12px;
            font-size: 14px;
            border: none;

            &:hover {
              transform: translateY(-1px);
            }

            &.ivu-btn-primary {
              background: rgba(0,122,255,0.6);
              box-shadow: 0 2px 6px rgba(45, 140, 240, 0.2);

              &:hover {
                background: rgba(0,122,255,0.8);
                box-shadow: 0 4px 12px rgba(45, 140, 240, 0.3);
              }
            }

            &.ivu-btn-warning {
              background: rgba(255,153,0,0.6);
              box-shadow: 0 2px 6px rgba(255, 153, 0, 0.2);

              &:hover {
                background: rgba(255,153,0,0.8);
                box-shadow: 0 4px 12px rgba(255, 153, 0, 0.3);
              }
            }

            &.ivu-btn-error {
              background: rgba(237,64,20,0.6);
              box-shadow: 0 2px 6px rgba(237, 64, 20, 0.2);

              &:hover {
                background: rgba(237,64,20,0.8);
                box-shadow: 0 4px 12px rgba(237, 64, 20, 0.3);
              }
            }
          }
        }
      }
    }
  }
}
</style> 