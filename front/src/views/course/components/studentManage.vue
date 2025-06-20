<template>
  <div class="student-manage-content">
    <div class="header">
      <div class="search-box">
        <Input
            v-model="searchKeyword"
            placeholder="Search by name or student ID"
            @on-change="handleSearch"
            clearable
            size="large">
          <Icon type="ios-search" slot="prefix" />
        </Input>
      </div>
      <div class="btn-group">
        <Button type="primary" @click="showAddModal = true">Add Student</Button>
        <Button type="success" @click="toggleScoreMode">{{ scoreMode ? 'Save' : 'Score' }}</Button>
      </div>
    </div>

    <!--    学生列表-->
    <div class="student-list">
      <Table
          :columns="columns"
          :data="filteredStudents"
          :loading="loading"
          stripe
          size="large">
        <template slot-scope="{ row, index }" slot="action">
          <Button
              type="error"
              size="small"
              @click="handleDeleteStudent(row)">
            Delete
          </Button>
        </template>
        <template slot-scope="{ row, index }" slot="score">
          <div v-if="scoreMode">
            <InputNumber v-model="row.score" :min="0" :max="100" size="small" style="width:90px" />
          </div>
          <div v-else>
            <span>{{ row.score === null || row.score === undefined ? '' : row.score }}</span>
          </div>
        </template>
      </Table>
    </div>
    <Modal v-model="showAddModal" title="Add Student" @on-ok="handleAddStudent" ok-text="Add" cancel-text="Cancel">
      <Form :model="addForm" :label-width="120" :rules="addFormRules" ref="addFormRef">
        <FormItem label="Select Student" prop="selectedUserId">
          <Select
              v-model="addForm.selectedUserId"
              placeholder="Choose a student to add"
              filterable
              clearable
              style="width: 100%">
            <Option
                v-for="user in availableUsers"
                :value="user.id"
                :key="user.id"
                :label="`${user.username} (${user.nickname || 'No Name'})`">
              {{ user.username }} - {{ user.nickname || 'No Name' }}
            </Option>
          </Select>
        </FormItem>
      </Form>
    </Modal>
    <Modal v-model="showDeleteModal" title="Confirm Delete" >
      <p>Are you sure you want to delete student "{{ deleteStudent.name }}"?</p>
      <div slot="footer">
        <Button @click="showDeleteModal = false">Cancel</Button>
        <Button type="error" @click="confirmDeleteStudent">Delete</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
import { getAllGradeBooks, addGradeBook, saveOrUpdateGradeBook, deleteGradeBooks } from '@/api/gradeBook'
import { getAllUsers } from '@/views/roster/user/api'

export default {
  name: 'StudentManage',
  props: {
    courseId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      loading: false,
      students: [],
      allUsers: [],
      searchKeyword: '',
      showAddModal: false,
      showDeleteModal: false,
      deleteStudent: {},
      addForm: {
        selectedUserId: null
      },
      addFormRules: {
        selectedUserId: [
          { required: true, message: 'Please select a student', trigger: 'change' }
        ]
      },
      columns: [
        { title: 'Name', key: 'name', minWidth: 160 },
        { title: 'Student ID', key: 'studentId', minWidth: 130 },
        { title: 'Department', key: 'department', minWidth: 180 },
        { title: 'Email', key: 'email', minWidth: 250 },
        { title: 'Score', key: 'score', slot: 'score', minWidth: 110, align: 'center' },
        { title: 'Action', slot: 'action', minWidth: 120, align: 'center' }
      ],
      scoreMode: false
    }
  },
  computed: {
    filteredStudents() {
      if (!this.searchKeyword) {
        return this.students
      }
      const keyword = this.searchKeyword.toLowerCase()
      return this.students.filter(student =>
          student.name.toLowerCase().includes(keyword) ||
          student.studentId.toLowerCase().includes(keyword) ||
          student.email.toLowerCase().includes(keyword)
      )
    },
    // 过滤出还没有加入当前课程的用户
    availableUsers() {
      const enrolledUserIds = this.students.map(student => student.userId)
      return this.allUsers.filter(user => !enrolledUserIds.includes(user.id))
    }
  },
  created() {
    this.fetchStudents()
    this.fetchAllUsers()
  },
  methods: {
    async fetchStudents() {
      try {
        this.loading = true
        const response = await getAllGradeBooks()
        if (response && response.data) {
          // 过滤出当前课程的学生
          this.students = response.data.filter(student => student.courseId === this.courseId)
        }
      } catch (error) {
        console.error('Failed to fetch students:', error)
        this.$Message.error('Failed to load students')
      } finally {
        this.loading = false
      }
    },

    async fetchAllUsers() {
      try {
        const response = await getAllUsers()
        if (response && response.success) {
          this.allUsers = response.result || []
        }
      } catch (error) {
        console.error('Failed to fetch users:', error)
        this.$Message.error('Failed to load users')
      }
    },

    async handleAddStudent() {
      this.$refs.addFormRef.validate(async (valid) => {
        if (valid) {
          try {
            const selectedUser = this.allUsers.find(user => user.id === this.addForm.selectedUserId)
            if (!selectedUser) {
              this.$Message.error('Selected user not found')
              return
            }

            const params = {
              courseId: this.courseId,
              userId: selectedUser.id,
              name: selectedUser.nickname || selectedUser.username,
              studentId: selectedUser.username,
              email: selectedUser.email || '',
              department: selectedUser.departmentName || '',
              score: null
            }

            await addGradeBook(params)
            this.$Message.success('Student added successfully')
            this.showAddModal = false
            this.resetAddForm()
            this.fetchStudents() // 重新获取学生列表
          } catch (error) {
            console.error('Add student error:', error)
            this.$Message.error('Failed to add student')
          }
        }
      })
    },

    resetAddForm() {
      this.addForm = { selectedUserId: null }
      this.$refs.addFormRef.resetFields()
    },

    handleDeleteStudent(student) {
      this.deleteStudent = student
      this.showDeleteModal = true
    },

    async confirmDeleteStudent() {
      try {
        const params = {
          ids: [this.deleteStudent.id]
        }
        await deleteGradeBooks(params)
        this.$Message.success('Student deleted successfully')
        this.showDeleteModal = false
        this.deleteStudent = {}
        this.fetchStudents()
      } catch (error) {
        console.error('Delete student error:', error)
        this.$Message.error('Failed to delete student')
      }
    },

  // 保存所有修改的成绩
    async toggleScoreMode() {
      if (this.scoreMode) {
        try {
          const updatePromises = this.students.map(student => {
            if (student.score !== null && student.score !== undefined) {
              return saveOrUpdateGradeBook({
                id: student.id,
                courseId: student.courseId,
                userId: student.userId,
                name: student.name,
                studentId: student.studentId,
                email: student.email,
                department: student.department,
                score: student.score
              })
            }
            return Promise.resolve()
          })

          await Promise.all(updatePromises)
          this.$Message.success('Scores saved successfully')
        } catch (error) {
          console.error('Save scores error:', error)
          this.$Message.error('Failed to save scores')
        }
      }
      this.scoreMode = !this.scoreMode
    }
  }
}
</script>

<style lang="less" scoped>
.student-manage-content {
  padding: 30px 40px 30px 40px;
  max-width: 1200px;
  margin: 0 auto;
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    .search-box {
      width: 340px;
      :deep(.ivu-input-wrapper) {
        .ivu-input {
          border-radius: 25px;
          padding-left: 40px;
          height: 44px;
          font-size: 17px;
        }
        .ivu-input-prefix {
          left: 12px;
          color: #808695;
          font-size: 17px;
          line-height: 44px;
        }
      }
    }
    .btn-group {
      display: flex;
      gap: 16px;
      .ivu-btn {
        border-radius: 22px;
        height: 44px;
        padding: 0 28px;
        font-size: 16px;
      }
    }
  }
  .student-list {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 18px;
    box-shadow: 2px 4px 18px rgba(0, 0, 0, 0.10);
    overflow: hidden;
    padding: 24px 18px 18px 18px;
    .ivu-table {
      font-size: 16px;
      .ivu-table-header {
        background: rgba(45, 140, 240, 0.08);
        th {
          background: transparent;
          border-bottom: 1.5px solid #e8eaec;
          font-weight: 600;
          color: #515a6e;
          font-size: 16px;
        }
      }
      .ivu-table-tbody {
        tr {
          transition: all 0.3s ease;
          &:hover {
            background: rgba(45, 140, 240, 0.07);
          }
          td {
            border-bottom: 1px solid #f0f0f0;
            padding: 14px 10px;
          }
        }
      }
    }
    .ivu-btn-error {
      background: rgba(237, 64, 20, 0.6);
      border: none;
      border-radius: 6px;
      padding: 4px 8px;
      font-size: 13px;
      transition: all 0.3s ease;
      &:hover {
        background: rgba(237, 64, 20, 0.8);
        transform: translateY(-1px);
        box-shadow: 0 2px 8px rgba(237, 64, 20, 0.3);
      }
    }
  }
  .ivu-modal {
    .ivu-modal-header {
      background: rgba(45, 140, 240, 0.08);
      border-bottom: 1px solid #e8eaec;
    }
    .ivu-modal-body {
      padding: 28px;
    }
    .ivu-form-item {
      margin-bottom: 22px;
      .ivu-input {
        border-radius: 6px;
        &:focus {
          border-color: #2d8cf0;
          box-shadow: 0 0 0 2px rgba(45, 140, 240, 0.2);
        }
      }
    }
  }
}
</style>





