<template>
  <div class="assignments-content">
    <!-- 教师：显示创建作业按钮 -->
    <div v-if="isTeacher" class="create-section" style="background:none;box-shadow:none;padding:0;margin-bottom:24px;display:flex;justify-content:flex-end;">
      <Button
          type="primary"
          icon="ios-add-circle-outline"
          @click="showCreateModal = true"
          class="create-btn"
          style="border-radius:22px;height:40px;padding:0 20px;font-size:15px;">
        Create Assignment
      </Button>
    </div>

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
          <div class="header-right">
            <!-- 学生：显示提交按钮 -->
            <div v-if="!isTeacher && getStatus(assignment) === 'open'">
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
            <!-- 教师：显示编辑按钮 -->
            <div v-if="isTeacher" class="teacher-actions">
              <Button
                  type="primary"
                  size="middle"
                  icon="ios-create-outline"
                  @click="handleEdit(assignment)"
                  class="edit-btn">
                Edit
              </Button>
              <Button
                  type="error"
                  size="middle"
                  icon="ios-trash-outline"
                  @click="handleDeleteAssignment(assignment)"
                  class="del-btn">
                Delete
              </Button>
            </div>
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
                  <!-- 教师：显示删除文件按钮 -->
                  <Button v-if="isTeacher"
                          type="error"
                          size="middle"
                          icon="md-trash"
                          @click="handleDeleteRequirementFile(file, assignment)"
                          class="delete-btn">
                    Delete
                  </Button>
                </div>
              </div>
            </div>
          </div>

          <!-- 学生：显示提交 -->
          <div class="file-section" v-if="!isTeacher">
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
                            @click="handleDelete(file, assignment)"
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

    <!-- 创建作业模态框 -->
    <Modal v-model="showCreateModal" title="Create Assignment" @on-ok="handleCreateAssignment">
      <Form :model="createForm" :label-width="120" :rules="createRules" ref="createForm">
        <FormItem label="Title" prop="title">
          <Input v-model="createForm.title" placeholder="Enter assignment title" />
        </FormItem>
        <FormItem label="Description" prop="description">
          <Input v-model="createForm.description" type="textarea" :rows="4" placeholder="Enter assignment description" />
        </FormItem>
        <FormItem label="Start Time" prop="startTime">
          <DatePicker
              v-model="createForm.startTime"
              type="datetime"
              placeholder="Select start time"
              format="yyyy-MM-dd HH:mm:ss"
              style="width: 100%" />
        </FormItem>
        <FormItem label="Due Time" prop="dueTime">
          <DatePicker
              v-model="createForm.dueTime"
              type="datetime"
              placeholder="Select due time"
              format="yyyy-MM-dd HH:mm:ss"
              style="width: 100%" />
        </FormItem>
        <FormItem label="Requirement Files (Optional)">
          <Upload
              ref="createUpload"
              :show-upload-list="true"
              :on-success="handleRequirementUploadSuccess"
              :on-error="handleUploadError"
              :format="['pdf', 'docx', 'doc', 'ppt', 'pptx', 'zip', 'rar']"
              :max-size="20480"
              action="/api/upload"
              :before-upload="handleBeforeUpload"
              multiple>
            <Button icon="ios-cloud-upload-outline">Choose Files</Button>
          </Upload>
        </FormItem>
      </Form>
    </Modal>

    <!-- 编辑作业模态框 -->
    <Modal v-model="showEditModal" title="Edit Assignment" @on-ok="handleEditAssignment">
      <Form :model="editForm" :label-width="120" :rules="editRules" ref="editForm">
        <FormItem label="Title" prop="title">
          <Input v-model="editForm.title" placeholder="Enter assignment title" />
        </FormItem>
        <FormItem label="Description" prop="description">
          <Input v-model="editForm.description" type="textarea" :rows="4" placeholder="Enter assignment description" />
        </FormItem>
        <FormItem label="Start Time" prop="startTime">
          <DatePicker
              v-model="editForm.startTime"
              type="datetime"
              placeholder="Select start time"
              format="yyyy-MM-dd HH:mm:ss"
              style="width: 100%" />
        </FormItem>
        <FormItem label="Due Time" prop="dueTime">
          <DatePicker
              v-model="editForm.dueTime"
              type="datetime"
              placeholder="Select due time"
              format="yyyy-MM-dd HH:mm:ss"
              style="width: 100%" />
        </FormItem>
        <FormItem label="Requirement Files (Optional)">
          <Upload
              ref="editUpload"
              :show-upload-list="true"
              :on-success="handleRequirementUploadSuccess"
              :on-error="handleUploadError"
              :format="['pdf', 'docx', 'doc', 'ppt', 'pptx', 'zip', 'rar']"
              :max-size="20480"
              action="/api/upload"
              :before-upload="handleBeforeUpload"
              multiple>
            <Button icon="ios-cloud-upload-outline">Choose Files</Button>
          </Upload>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<script>
import Cookies from 'js-cookie'
import { getAllAssignmentReq, saveOrUpdateAssignmentReq, addAssignmentReq, deleteAssignmentReq } from '@/api/assignmentReq';
import { getAllAssignmentAns, saveOrUpdateAssignmentAns, addAssignmentAns, deleteAssignmentAns } from '@/api/assignmentAns';
import { downloadFile } from '@/api/file';

export default {
  name: 'CourseAssignments',
  data() {
    return {
      assignments: [],
      loading: false,
      error: null,
      courseId: null,
      currentUserId: null,
      showCreateModal: false,
      showEditModal: false,
      createForm: {
        title: '',
        description: '',
        startTime: null,
        dueTime: null,
        files: []
      },
      editForm: {
        id: null,
        title: '',
        description: '',
        startTime: null,
        dueTime: null,
        files: []
      },
      createRules: {
        title: [
          { required: true, message: 'Please enter assignment title', trigger: 'blur' }
        ],
        description: [
          { required: true, message: 'Please enter assignment description', trigger: 'blur' }
        ],
        startTime: [
          { required: true, type: 'date', message: 'Please select start time', trigger: 'change' }
        ],
        dueTime: [
          { required: true, type: 'date', message: 'Please select due time', trigger: 'change' }
        ]
      },
      editRules: {
        title: [
          { required: true, message: 'Please enter assignment title', trigger: 'blur' }
        ],
        description: [
          { required: true, message: 'Please enter assignment description', trigger: 'blur' }
        ],
        startTime: [
          { required: true, type: 'date', message: 'Please select start time', trigger: 'change' }
        ],
        dueTime: [
          { required: true, type: 'date', message: 'Please select due time', trigger: 'change' }
        ]
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
        console.error('User Info error:', error)
        return false
      }
    }
  },
  created() {
    this.courseId = this.$route.params.id
    this.getCurrentUserId()
    this.fetchAssignments()
  },
  methods: {
    // 获取当前用户ID
    getCurrentUserId() {
      try {
        const userInfo = Cookies.get('userInfo')
        if (userInfo) {
          const user = JSON.parse(userInfo)
          this.currentUserId = user.id
        }
      } catch (error) {
        console.error('User Info error:', error)
      }
    },

    async fetchAssignments() {
      try {
        this.loading = true
        // 获取作业要求
        const reqResponse = await getAllAssignmentReq()
        const assignments = reqResponse && reqResponse.data ? reqResponse.data : []

        // 获取作业答案（学生提交的文件）
        const ansResponse = await getAllAssignmentAns()
        const submissions = ansResponse && ansResponse.data ? ansResponse.data : []

        // 合并数据
        this.assignments = assignments.map(assignment => {
          // 找到对应的提交文件
          const submission = submissions.find(sub => sub.assignmentId === assignment.id && sub.userId === this.currentUserId)
          return {
            ...assignment,
            submission: submission ? { files: submission.files || [] } : null
          }
        })
      } catch (error) {
        this.error = error.message
        this.$Message.error('Failed to fetch assignments')
      } finally {
        this.loading = false
      }
    },

    // 下载文件
    async handleDownload(file) {
      try {
        this.$Message.success(`Start Download: ${file.title}`)
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

    // 创建作业
    async handleCreateAssignment() {
      try {
        this.$refs.createForm.validate(async (valid) => {
          if (valid) {
            const params = {
              title: this.createForm.title,
              description: this.createForm.description,
              startTime: this.createForm.startTime,
              dueTime: this.createForm.dueTime,
              files: this.createForm.files,
              courseId: this.courseId
            }

            await addAssignmentReq(params)
            this.$Message.success('Assignment created successfully')
            this.showCreateModal = false
            this.resetCreateForm()
            this.fetchAssignments()
          }
        })
      } catch (error) {
        this.$Message.error('Failed to create assignment')
      }
    },

    // 编辑作业
    handleEdit(assignment) {
      this.editForm = {
        id: assignment.id,
        title: assignment.title,
        description: assignment.description,
        startTime: new Date(assignment.startTime),
        dueTime: new Date(assignment.dueTime),
        files: assignment.files || []
      }
      this.showEditModal = true
    },

    // 提交编辑
    async handleEditAssignment() {
      try {
        this.$refs.editForm.validate(async (valid) => {
          if (valid) {
            const params = {
              id: this.editForm.id,
              title: this.editForm.title,
              description: this.editForm.description,
              startTime: this.editForm.startTime,
              dueTime: this.editForm.dueTime,
              files: this.editForm.files,
              courseId: this.courseId
            }

            await saveOrUpdateAssignmentReq(params)
            this.$Message.success('Assignment updated successfully')
            this.showEditModal = false
            this.fetchAssignments()
          }
        })
      } catch (error) {
        this.$Message.error('Failed to update assignment')
      }
    },

    // 删除作业
    handleDeleteAssignment(assignment) {
      this.$Modal.confirm({
        title: 'Confirm deletion',
        content: `Are you sure you want to delete "${assignment.title}"? This will also delete all student submissions.`,
        okText: 'Delete',
        cancelText: 'Cancel',
        onOk: async () => {
          try {
            const params = {
              ids: [assignment.id]
            }
            await deleteAssignmentReq(params)
            this.$Message.success('Assignment deleted successfully')
            this.fetchAssignments()
          } catch (error) {
            this.$Message.error('Failed to delete assignment')
          }
        }
      })
    },

    // 删除作业要求文件
    handleDeleteRequirementFile(file, assignment) {
      this.$Modal.confirm({
        title: 'Confirm deletion',
        content: `Are you sure you want to delete "${file.title}"?`,
        okText: 'Delete',
        cancelText: 'Cancel',
        onOk: async () => {
          try {
            assignment.files = assignment.files.filter(f => f.id !== file.id)
            const params = {
              id: assignment.id,
              files: assignment.files
            }
            await saveOrUpdateAssignmentReq(params)
            this.$Message.success('File deleted successfully')
          } catch (error) {
            this.$Message.error('Failed to delete file')
          }
        }
      })
    },

    resetCreateForm() {
      this.createForm = {
        title: '',
        description: '',
        startTime: null,
        dueTime: null,
        files: []
      }
      this.$refs.createForm.resetFields()
    },

    // 作业要求文件上传成功
    handleRequirementUploadSuccess(response, file) {
      const newFile = {
        id: Date.now(),
        title: file.name,
        uploadTime: new Date().toISOString()
      }

      if (this.showCreateModal) {
        this.createForm.files.push(newFile)
      } else if (this.showEditModal) {
        this.editForm.files.push(newFile)
      }

      this.$Message.success('File uploaded successfully')
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

    // 判断作业时间
    isAssignmentStarted(assignment) {
      return new Date() >= new Date(assignment.startTime)
    },
    isAssignmentDue(assignment) {
      return new Date() > new Date(assignment.dueTime)
    },

    // 计算作业状态
    getStatus(assignment) {
      if (!this.isAssignmentStarted(assignment)) {
        return 'unreleased'
      }
      if (this.isAssignmentDue(assignment)) {
        return 'closed'
      }
      if (this.isAssignmentInValidPeriod(assignment)) {
        // 已提交
        if (this.isAssignmentSubmitted(assignment)) {
          return 'submitted'
        }
        return 'open'
      }
      return 'closed'
    },

    getSubmissionStatusText(status) {
      const textMap = {
        'unreleased': 'Cannot submit.',
        'open': 'Not submitted yet',
        'submitted': 'Not submitted yet',
        'closed': 'Not submitted yet'
      }
      return textMap[status] || 'Not submitted yet'
    },

    // 状态颜色
    getStatusColor(status) {
      const colorMap = {
        'unreleased': 'default',
        'open': 'success',
        'submitted': 'warning',
        'closed': 'error'
      }
      return colorMap[status] || 'default'
    },

    // 文件图标
    getFileIcon(fileName) {
      const extension = fileName.split('.').pop().toLowerCase()
      const iconMap = {
        'pdf': 'ios-document',
        'docx': 'ios-paper',
        'doc': 'ios-paper',
        'zip': 'ios-archive',
        'rar': 'ios-archive'
      }
      return iconMap[extension] || 'ios-document'
    },

    // 删除提交的文件
    async handleDelete(file, assignment) {
      this.$Modal.confirm({
        title: 'Confirm deletion',
        content: `Are you sure you want to delete "${file.title}"?`,
        okText: 'Delete',
        cancelText: 'Cancel',
        onOk: async () => {
          try {
            assignment.submission.files = assignment.submission.files.filter(f => f.id !== file.id)
            if (assignment.submission.files.length === 0) {
              // 删除作业答案记录
              const params = {
                ids: [assignment.submission.id]
              }
              await deleteAssignmentAns(params)
              delete assignment.submission
            } else {
              // 更新作业答案记录
              const params = {
                id: assignment.submission.id,
                files: assignment.submission.files
              }
              await saveOrUpdateAssignmentAns(params)
            }
            this.$Message.success('File deleted successfully.')
          } catch (error) {
            this.$Message.error('Delete failed')
          }
        }
      })
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

    async handleUploadSuccess(response, file) {
      try {
        const assignment = this.assignments.find(a => this.getStatus(a) === 'open')
        if (assignment) {
          if (!assignment.submission) {
            assignment.submission = { files: [] }
          }
          const newFile = {
            id: Date.now(),
            title: file.name,
            uploadTime: new Date().toISOString()
          }
          assignment.submission.files.push(newFile)

          const params = {
            assignmentId: assignment.id,
            userId: this.currentUserId,
            files: assignment.submission.files
          }

          if (assignment.submission.id) {
            params.id = assignment.submission.id
            await saveOrUpdateAssignmentAns(params)
          } else {
            // 创建新记录
            const result = await addAssignmentAns(params)
            if (result && result.data) {
              assignment.submission.id = result.data.id
            }
          }
        }
        this.$Message.success('File uploaded successfully.')
      } catch (error) {
        this.$Message.error('Upload failed')
      }
    },

    // 上传错误处理
    handleUploadError(error) {
      this.$Message.error('File upload failed')
    }
  }
}
</script>

<style lang="less" scoped>
.assignments-content {
  padding: 20px 30px;

  .create-section {
    margin-bottom: 20px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 16px;
    box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.08);

    .create-btn {
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
          display: flex;
          gap: 12px;

          .teacher-actions {
            display: flex;
            gap: 8px;

            .edit-btn {
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

            .del-btn {
              opacity: 0.7;
              border-radius: 16px;
              padding: 6px 16px;
              font-size: 14px;
              border: none;

              &:hover {
                opacity: 1.2;
                transform: translateY(-1px);
              }
            }
          }
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

