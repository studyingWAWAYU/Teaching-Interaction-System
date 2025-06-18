<template>
  <div class="resource-content">
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
          <Button type="primary"
            size="middle"
            icon="ios-download-outline"
            @click="handleDownload(file)"
            class="download-btn">
            Download
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
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
      error: null
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
        
        this.$Message.success(`Start Downlaod: ${file.title}`)
      } catch (error) {
        this.$Message.error('Download failed, please try again later.')
      }
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
      }
    }
  }
}
</style> 