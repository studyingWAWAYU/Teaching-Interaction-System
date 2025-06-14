<template>
  <div class="assignments-content">
    <div class="assignments-list">
      <div class="assignment-card" v-for="assignment in assignments" :key="assignment.id">
        <div class="assignment-header">
          <div class="header-left">
            <div class="assignment-title">
              <span>{{ assignment.title }}</span>
              <Tag :color="getStatusColor(getStatus(assignment))" class="status-tag">
                {{ getStatus(assignment) }}
              </Tag>
            </div>
            <div class="assignment-time">
              <span class="time-item">
                <Icon type="ios-time-outline" />
                Begin：{{ assignment.startTime }}
              </span>
              <span class="time-item">
                <Icon type="ios-time" />
                Deadline：{{ assignment.dueTime }}
              </span>
            </div>
          </div>
          <div class="header-right" v-if="getStatus(assignment) === 'open'">
            <Upload
                action="/api/upload"
                :before-upload="handleBeforeUpload"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :max-size="20480"
                :format="['pdf', 'doc', 'docx', 'zip', 'rar']"
                multiple>
              <Button type="primary" icon="ios-cloud-upload-outline" class="submit-btn">
                Submit
              </Button>
            </Upload>
          </div>
        </div>

        <div class="assignment-description">
          {{ assignment.description }}
        </div>

        <div class="assignment-files">
          <div class="file-section" v-if="assignment.files && assignment.files.length > 0">
            <div class="section-title">Assignment requirements</div>
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

          <div class="file-section">
            <div class="section-title">My submission</div>
            <div class="file-list">
              <template v-if="assignment.submission && assignment.submission.files && assignment.submission.files.length > 0">
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
                            icon="md-trash"
                            @click="handleDelete(file)"
                            class="delete-btn"
                            v-if="getStatus(assignment) !== 'closed'">
                      Delete
                    </Button>
                  </div>
                </div>
              </template>
              <div class="file-item" v-else>
                <div class="file-info">
                  <Icon type="ios-information-circle-outline" class="file-icon" />
                  <span class="file-name">
                    {{ getSubmissionStatusText(getStatus(assignment)) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
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
    title: '面向对象编程实践',
    description: '设计并实现一个简单的学生管理系统，要求使用面向对象的思想，包含类的继承、多态等特性。',
    startTime: '2025-06-05 00:00',
    dueTime: '2025-06-13 23:59',
    files: []
  },
  {
    id: 2,
    title: 'Test Assignment',
    description: '完成教材第三章的练习题，基本数据类型的使用 ',
    startTime: '2025-06-12 14:30',
    dueTime: '2025-06-22 23:59',
    files: [
      {
        id: 1,
        title: '作业要求.pdf',
        uploadTime: '2025-06-15 14:30'
      }
    ],
    submission: {
      files: [
        {
          id: 2,
          title: '我的作业.pdf',
          uploadTime: '2025-06-18 16:45'
        }
      ]
    }
  }

]

export default {
  name: 'CourseAssignments',
  data() {
    return {
      assignments: [],
      loading: false,
      error: null,
      courseId: null // 课程ID，从路由参数获取
    }
  },
  created() {
    this.courseId = this.$route.params.courseId
    this.fetchAssignments()
  },
  methods: {
    // API调用方法
    async fetchAssignments() {
      try {
        this.loading = true
        // const response = await this.$api.getAssignments(this.courseId)
        // this.assignments = response.data
        this.assignments = mockAssignments // 临时使用模拟数据
      } catch (error) {
        this.error = error.message
        this.$Message.error('Failed to fetch the assignment list.')
      } finally {
        this.loading = false
      }
    },

    // 下载文件
    async handleDownload(file) {
      try {
        // const response = await this.$api.downloadFile(file.id)
        // 处理文件下载逻辑

        this.$Message.success(`Start Downlaod: ${file.title}`)
      } catch (error) {
        this.$Message.error('Download failed, please try again later.')
      }
    },

    handleUploadError(error) {
      this.$Message.error('Upload failed, please try again later.')
    },

    // 判断作业是否已提交
    isAssignmentSubmitted(assignment) {
      return assignment.submission &&
          assignment.submission.files &&
          assignment.submission.files.length > 0
    },

    // 获取最新的提交时间
    getLatestSubmissionTime(assignment) {
      if (!this.isAssignmentSubmitted(assignment)) {
        return null
      }
      return Math.max(...assignment.submission.files.map(file => new Date(file.uploadTime).getTime()))
    },

    // 判断作业是否在有效期内
    isAssignmentInValidPeriod(assignment) {
      const now = new Date()
      const startTime = new Date(assignment.startTime)
      const dueTime = new Date(assignment.dueTime)
      return now >= startTime && now <= dueTime
    },

    // 判断作业是否已开始
    isAssignmentStarted(assignment) {
      return new Date() >= new Date(assignment.startTime)
    },

    // 判断作业是否已截止
    isAssignmentDue(assignment) {
      return new Date() > new Date(assignment.dueTime)
    },

    // 计算作业状态
    getStatus(assignment) {
      // 如果作业未开始
      if (!this.isAssignmentStarted(assignment)) {
        return 'unreleased'
      }

      // 如果作业已截止
      if (this.isAssignmentDue(assignment)) {
        return 'closed'
      }

      // 如果作业在有效期内
      if (this.isAssignmentInValidPeriod(assignment)) {
        // 如果已提交作业
        if (this.isAssignmentSubmitted(assignment)) {
          return 'submitted'
        }
        // 如果未提交作业
        return 'open'
      }

      return 'closed'
    },

    // 获取提交状态文本
    getSubmissionStatusText(status) {
      const textMap = {
        'unreleased': 'Cannot submit.',
        'open': 'Not submitted yet',
        'submitted': 'Not submitted yet',
        'closed': 'Not submitted yet'
      }
      return textMap[status] || 'Not submitted yet'
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

    // 删除提交的文件
    async handleDelete(file) {
      try {
        // await this.$api.deleteSubmissionFile(file.id)

        // 删除后更新本地状态
        const assignment = this.assignments.find(a => a.submission && a.submission.files.includes(file))
        if (assignment) {
          assignment.submission.files = assignment.submission.files.filter(f => f.id !== file.id)
          if (assignment.submission.files.length === 0) {
            delete assignment.submission
          }
        }

        this.$Message.success('File deleted successfully.')
      } catch (error) {
        this.$Message.error('Deletion failed, please try again later.')
      }
    },

    // 上传前检查
    handleBeforeUpload(file) {
      const isValidFormat = ['pdf', 'doc', 'docx', 'zip', 'rar'].includes(file.name.split('.').pop().toLowerCase())
      if (!isValidFormat) {
        this.$Message.error('Only PDF, Word, ZIP, and RAR formats are supported.')
        return false
      }
      return true
    },

    // 上传成功
    handleUploadSuccess(response, file) {
      try {
        // 更新本地状态
        const assignment = this.assignments.find(a => this.getStatus(a) === 'open')
        if (assignment) {
          if (!assignment.submission) {
            assignment.submission = { files: [] }
          }
          assignment.submission.files.push({
            id: Date.now(), // 临时ID，实际应该使用后端返回的ID
            title: file.name,
            uploadTime: new Date().toISOString()
          })
        }

        this.$Message.success('File uploaded successfully.')
      } catch (error) {
        this.$Message.error('Upload failed, please try again later.')
      }
    }
  }
}
</script>

<style lang="less" scoped>
.assignments-content {
  padding: 20px 30px;

  .assignments-list {
    padding: 25px;

    .assignment-card {
      background: rgba(255, 255, 255, 0.8);
      backdrop-filter: blur(8px);
      border-radius: 16px;
      padding: 25px 45px;
      margin-bottom: 32px;
      box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.08);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 2px 6px 16px rgba(0, 0, 0, 0.08);
        background: rgba(255, 255, 255, 0.9);
      }

      .assignment-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 20px;

        .header-left {
          flex: 1;
        }

        .header-right {
          margin-left: 20px;
        }

        .assignment-title {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 12px;

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

        .submit-btn {
          opacity: 0.8;
          border-radius: 16px;
          font-weight: 550;
          border: none;
          padding: 6px 16px;
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);

          &:hover {
            opacity: 1;
            background: #2b85e4;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
                  color: #515a6e;
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
    }
  }
}

</style> 