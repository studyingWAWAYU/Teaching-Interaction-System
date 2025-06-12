<template>
  <div class="assignments-content">
    <div class="assignments-list">
      <div class="assignment-card" v-for="assignment in assignments" :key="assignment.id">
        <div class="assignment-header">
          <div class="assignment-title">
            <Icon type="ios-paper" class="title-icon" />
            <span>{{ assignment.title }}</span>
            <Tag :color="getStatusColor(assignment.status)" class="status-tag">
              {{ getStatusText(assignment.status) }}
            </Tag>
          </div>
          <div class="assignment-time">
            <span class="time-item">
              <Icon type="ios-time-outline" />
              开始时间：{{ assignment.startTime }}
            </span>
            <span class="time-item">
              <Icon type="ios-time" />
              截止时间：{{ assignment.dueTime }}
            </span>
          </div>
        </div>

        <div class="assignment-description">
          {{ assignment.description }}
        </div>

        <div class="assignment-files">
          <div class="file-section">
            <div class="section-title">作业文件</div>
            <div class="file-list">
              <div class="file-item" v-for="file in assignment.files" :key="file.id">
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

          <div class="file-section" v-if="assignment.submission">
            <div class="section-title">我的提交</div>
            <div class="file-list">
              <div class="file-item" v-for="file in assignment.submission.files" :key="file.id">
                <div class="file-info">
                  <Icon :type="getFileIcon(file.title)" class="file-icon" />
                  <span class="file-name">{{ file.title }}</span>
                </div>
                <div class="file-meta">
                  <span class="upload-time">
                    <Icon type="ios-time-outline" />
                    {{ file.uploadTime }}
                  </span>
                  <Button type="error"
                    size="middle"
                    icon="ios-trash-outline"
                    @click="handleDelete(file)"
                    class="delete-btn">
                    Delete
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="assignment-actions" v-if="assignment.status === 'open'">
          <Upload
            action="/api/upload"
            :before-upload="handleBeforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :max-size="20480"
            :format="['pdf', 'doc', 'docx', 'zip', 'rar']"
            multiple
          >
            <Button type="primary" icon="ios-cloud-upload-outline">
              提交作业
            </Button>
          </Upload>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// 模拟数据，后续可替换为API调用
const mockAssignments = [
  {
    id: 1,
    title: 'Java基础语法作业',
    description: '完成教材第三章的练习题，包括：1. 基本数据类型的使用 2. 控制流程语句 3. 数组操作',
    status: 'open',
    startTime: '2024-03-15 14:30',
    dueTime: '2024-03-22 23:59',
    files: [
      {
        id: 101,
        title: '作业要求.pdf',
        uploadTime: '2024-03-15 14:30'
      }
    ],
    submission: {
      files: [
        {
          id: 201,
          title: '我的作业.pdf',
          uploadTime: '2024-03-18 16:45'
        }
      ]
    }
  },
  {
    id: 2,
    title: '面向对象编程实践',
    description: '设计并实现一个简单的学生管理系统，要求使用面向对象的思想，包含类的继承、多态等特性。',
    status: 'unreleased',
    startTime: '2024-03-25 00:00',
    dueTime: '2024-04-01 23:59',
    files: []
  }
]

export default {
  name: 'CourseAssignments',
  data() {
    return {
      assignments: [],
      loading: false,
      error: null
    }
  },
  created() {
    this.fetchAssignments()
  },
  methods: {
    // 获取作业列表
    async fetchAssignments() {
      try {
        this.loading = true
        // TODO: 替换为实际的API调用
        // const response = await this.$api.getAssignments(this.$route.params.courseId)
        // this.assignments = response.data
        
        // 临时使用模拟数据
        this.assignments = mockAssignments
      } catch (error) {
        this.error = error.message
        this.$Message.error('获取作业列表失败')
      } finally {
        this.loading = false
      }
    },

    // 下载文件
    async handleDownload(file) {
      try {
        // TODO: 替换为实际的下载API调用
        // const response = await this.$api.downloadFile(file.id)
        // 处理文件下载逻辑
        
        this.$Message.success(`开始下载: ${file.title}`)
      } catch (error) {
        this.$Message.error('下载失败，请稍后重试')
      }
    },

    // 删除提交的文件
    async handleDelete(file) {
      try {
        // TODO: 替换为实际的删除API调用
        // await this.$api.deleteSubmissionFile(file.id)
        
        this.$Message.success('文件删除成功')
      } catch (error) {
        this.$Message.error('删除失败，请稍后重试')
      }
    },

    // 上传前检查
    handleBeforeUpload(file) {
      const isValidFormat = ['pdf', 'doc', 'docx', 'zip', 'rar'].includes(file.name.split('.').pop().toLowerCase())
      if (!isValidFormat) {
        this.$Message.error('只支持 PDF、Word、ZIP、RAR 格式的文件')
        return false
      }
      return true
    },

    // 上传成功
    handleUploadSuccess(response, file) {
      this.$Message.success('文件上传成功')
      // TODO: 更新作业提交状态
    },

    // 上传失败
    handleUploadError(error) {
      this.$Message.error('文件上传失败，请重试')
    },

    // 获取文件图标
    getFileIcon(fileName) {
      const extension = fileName.split('.').pop().toLowerCase()
      const iconMap = {
        'pdf': 'ios-document',
        'docx': 'ios-document-text',
        'doc': 'ios-document-text',
        'zip': 'ios-archive',
        'rar': 'ios-archive'
      }
      return iconMap[extension] || 'ios-document'
    },

    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'unreleased': 'default',
        'open': 'success',
        'submitted': 'warning',
        'closed': 'error'
      }
      return colorMap[status] || 'default'
    },

    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        'unreleased': '未发布',
        'open': '进行中',
        'submitted': '已提交',
        'closed': '已截止'
      }
      return textMap[status] || status
    }
  }
}
</script>

<style lang="less" scoped>
.assignments-content {
  padding: 25px;

  .assignments-list {
    padding: 30px;
    
    .assignment-card {
      background: rgba(255, 255, 255, 0.8);
      backdrop-filter: blur(8px);
      border-radius: 16px;
      padding: 30px;
      margin-bottom: 28px;
      transition: all 0.3s ease;
      box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.08);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 2px 6px 16px rgba(0, 0, 0, 0.08);
        background: rgba(255, 255, 255, 0.9);
      }

      .assignment-header {
        margin-bottom: 20px;

        .assignment-title {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 12px;

          .title-icon {
            font-size: 24px;
            color: #2d8cf0;
          }

          span {
            font-size: 20px;
            font-weight: 550;
            color: #515a6e;
          }

          .status-tag {
            margin-left: 12px;
          }
        }

        .assignment-time {
          display: flex;
          gap: 24px;
          color: #808695;
          font-size: 15px;

          .time-item {
            display: flex;
            align-items: center;

            .ivu-icon {
              margin-right: 6px;
              font-size: 18px;
            }
          }
        }
      }

      .assignment-description {
        color: #515a6e;
        font-size: 16px;
        line-height: 1.6;
        margin-bottom: 24px;
        padding: 16px;
        background: rgba(0, 0, 0, 0.02);
        border-radius: 12px;
      }

      .assignment-files {
        .file-section {
          margin-bottom: 24px;

          &:last-child {
            margin-bottom: 0;
          }

          .section-title {
            font-size: 16px;
            font-weight: 500;
            color: #515a6e;
            margin-bottom: 16px;
          }

          .file-list {
            .file-item {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 16px 20px;
              background: rgba(0, 0, 0, 0.02);
              border-radius: 12px;
              margin-bottom: 12px;

              &:last-child {
                margin-bottom: 0;
              }

              .file-info {
                display: flex;
                align-items: center;
                gap: 12px;

                .file-icon {
                  font-size: 20px;
                  color: #2d8cf0;
                }

                .file-name {
                  color: #515a6e;
                  font-size: 16px;
                }
              }

              .file-meta {
                display: flex;
                align-items: center;
                gap: 20px;

                .upload-time {
                  color: #808695;
                  font-size: 14px;
                  display: flex;
                  align-items: center;

                  .ivu-icon {
                    margin-right: 6px;
                    font-size: 16px;
                  }
                }

                .download-btn, .delete-btn {
                  opacity: 0.8;
                  transform: translateX(5px);
                  transition: all 0.3s ease;
                  border: none;
                  padding: 6px 16px;
                  font-size: 14px;
                  font-weight: 500;
                  border-radius: 16px;
                  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);

                  &:hover {
                    opacity: 1;
                    transform: translateY(-1px);
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                  }

                  .ivu-icon {
                    margin-right: 4px;
                  }
                }

                .download-btn {
                  background: #2d8cf0;
                  color: #fff;

                  &:hover {
                    background: #2b85e4;
                  }
                }

                .delete-btn {
                  background: #ed4014;
                  color: #fff;

                  &:hover {
                    background: #e54d17;
                  }
                }
              }
            }
          }
        }
      }

      .assignment-actions {
        margin-top: 24px;
        text-align: right;
      }
    }
  }
}
</style> 