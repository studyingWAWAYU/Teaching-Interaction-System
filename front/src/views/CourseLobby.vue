<template>
  <div class="course-lobby">
    <div class="banner-section">
      <div class="banner-container">
        <Carousel autoplay loop :autoplay-speed="5000">
          <CarouselItem v-for="i in 4" :key="i">
            <img :src="require(`@/assets/banner${i}.jpg`)" class="banner-img" />
          </CarouselItem>
        </Carousel>
      </div>
    </div>

    <div class="content-section">
      <div class="content-container">
        <h2 class="section-title">Recommended Courses</h2>
        
        <div class="operation-area">
          <div class="search-box">
            <Input
              v-model="searchQuery"
              placeholder="Search Course Name..."
              clearable
              size="large"
              @on-change="handleSearch">
              <Icon type="ios-search" slot="prefix" />
            </Input>
          </div>
          <Select 
            v-model="courseStatus" 
            placeholder="Select Course Status" 
            class="status-select" 
            clearable
            transfer>
            <Option value="Upcoming" label="Upcoming" />
            <Option value="Ongoing" label="Ongoing" />
            <Option value="Closed" label="Closed" />
          </Select>
          <Button v-if="isTeacher" type="primary" class="create-course-btn" @click="showCreateCourseModal = true">Create New Course</Button>
        </div>

        <Modal v-model="showCreateCourseModal" title="Create New Course" ok-text="OK" cancel-text="Cancel" @on-ok="handleCreateCourse">
          <Form :model="createCourseForm" :label-width="180">
            <FormItem label="Course Name：">
              <Input v-model="createCourseForm.name"  />
            </FormItem>
            <FormItem label="Credits：">
              <InputNumber v-model="createCourseForm.credits" :min="1.00" :max="10.00" :step="0.50" :precision="2" style="width:100%;" />
            </FormItem>
            <FormItem label="Start Time：">
              <DatePicker v-model="createCourseForm.startTime" type="date" format="yyyy-MM-dd" style="width:100%" />
            </FormItem>
            <FormItem label="End Time：">
              <DatePicker v-model="createCourseForm.endTime" type="date" format="yyyy-MM-dd" style="width:100%" />
            </FormItem>
            <FormItem label="Image (optional)：">
              <Upload
                :action="uploadFileUrl"
                :headers="uploadHeaders"
                :show-upload-list="false"
                :before-upload="beforeImageUpload"
                :on-success="handleImageUploadSuccess"
                :on-error="handleImageUploadError"
                accept="image/*">
                <Button type="primary" v-if="!uploadFileName">Upload</Button>
                <Button type="dashed" v-else style="cursor:default;pointer-events:none;">{{ uploadFileName }}</Button>
              </Upload>
              <div v-if="createCourseForm.image" style="margin-top:10px;">
                <img :src="createCourseForm.image" alt="Course Image" style="max-width: 180px; max-height: 120px; border-radius: 8px; border:1px solid #eee;" />
              </div>
            </FormItem>
            <FormItem label="Introduction (optional)：">
              <Input v-model="createCourseForm.introduction" type="textarea" :rows="4" />
            </FormItem>
          </Form>
        </Modal>

        <div class="course-grid">
          <Row :gutter="20">
            <Col span="6" v-for="course in filteredCourses" :key="course.id">
              <Card class="course-card" @click.native="navigateToCourse(course.id)">
                <div class="course-img-wrapper">
                  <img :src="course.image" class="course-img" />
                </div>
                <div class="course-info">
                  <h3>{{ course.title }}</h3>
                  <div class="course-meta">
                    <p class="teacher">
                      <Icon type="ios-person" />
                      {{ course.teacherName || 'Unknown instructor' }}
                    </p>
                    <div class="course-status" :class="course.status">
                      {{ course.status }}
                    </div>
                  </div>
                </div>
              </Card>
            </Col>
          </Row>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAllCourses, addCourse, saveOrUpdateCourse } from '@/api/course';
import { getAllUsers } from '@/views/roster/user/api';
import { uploadFile } from '@/api/index';
import Cookies from 'js-cookie';
import { getStore } from '@/libs/storage';

export default {
  name: 'CourseLobby',
  data() {
    return {
      searchQuery: '',
      courseStatus: '',
      courses: [],
      users: [],
      showCreateCourseModal: false,
      createCourseForm: {
        name: '',
        credits: '',
        startTime: '',
        endTime: '',
        introduction: '',
        image: ''
      },
      uploadFileName: '',
      uploadFileUrl: '/wl' + uploadFile,
      uploadHeaders: {
        accessToken: getStore('accessToken')
      },
      filteredDiscussions: [
        // 你的静态数据
      ]
    }
  },
  computed: {
    filteredCourses() {
      // 计算课程状态
      const now = new Date();
      return this.courses
        .map(course => {
          let status = 'Upcoming';
          if (course.startTime && course.endTime) {
            const start = new Date(course.startTime);
            const end = new Date(course.endTime);
            if (now < start) {
              status = 'Upcoming';
            } else if (now >= start && now <= end) {
              status = 'Ongoing';
            } else if (now > end) {
              status = 'Closed';
            }
          }
          return { ...course, status };
        })
        .filter(course => {
          const matchesSearch = course.title.toLowerCase().includes(this.searchQuery.toLowerCase())
          const matchesStatus = !this.courseStatus || course.status === this.courseStatus
          return matchesSearch && matchesStatus
        })
        .sort((a, b) => a.id - b.id)
    },
    isTeacher() {
      try {
        const userInfo = Cookies.get('userInfo')
        if (userInfo) {
          const user = JSON.parse(userInfo)
          if (user.role && user.role.name) {
            return user.role.name === 'ROLE_TEACHER' || user.role.name === 'ROLE_ADMIN'
          }
          if (user.roleName) {
            return user.roleName === 'ROLE_TEACHER' || user.roleName === 'ROLE_ADMIN'
          }
        }
        return false
      } catch (error) {
        return false
      }
    }
  },
  created() {
    this.loadData();
  },
  methods: {
    async loadData() {
      await this.loadUsers();
      await this.loadCourses();
    },
    
    async loadCourses() {
      try {
        const res = await getAllCourses();
        if (res.success) {
          this.courses = res.result.map(course => ({
            ...course,
            teacherName: this.getTeacherName(course.createBy),
            status: course.status || 'Normal',
            credits: course.credits !== undefined ? course.credits : 2
          }));
        } else {
        }
      } catch (error) {
      }
    },
    
    async loadUsers() {
      try {
        const res = await getAllUsers();
        if (res.success) {
          this.users = res.result;
        }
      } catch (error) {
        console.error('Failed to load user info :', error);
      }
    },
    
    getTeacherName(createBy) {
      if (!createBy) return 'Unknown teacher';
      if (!this.users || this.users.length === 0) {
        return `Instrutor ${createBy}`;
      }

      const createById = parseInt(createBy);
      const user = this.users.find(u => u.id === createById);
      
      if (user) {
        return user.username || user.nickname || `Instrutor ${createBy}`;
      }
      return `Instrutor ${createBy}`;
    },
    
    navigateToCourse(courseId) {
      this.$router.push({ name: 'course_manage', params: { id: courseId } });
    },

    beforeImageUpload(file) {
      const isImage = file.type.startsWith('image/');
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isImage) {
        this.$Message.error('Only image files are allowed.');
      }
      if (!isLt5M) {
        this.$Message.error('Image size must be less than 5MB.');
      }
      if (isImage && isLt5M) {
        this.uploadFileName = file.name;
      }
      return isImage && isLt5M;
    },
    handleImageUploadSuccess(res, file) {
      if (res.success && res.result && res.result.url) {
        this.createCourseForm.image = res.result.url;
        this.$Message.success('Image uploaded successfully!');
      }
    },
    async handleCreateCourse() {
      if (!this.createCourseForm.name || !this.createCourseForm.credits || !this.createCourseForm.startTime || !this.createCourseForm.endTime) {
        this.$Message.warning('Please fill in all required fields');
        return false;
      }
      // 获取当前登录用户username
      let username = '';
      try {
        const userInfo = JSON.parse(Cookies.get('userInfo'));
        username = userInfo.username || userInfo.nickname || 'Unknown';
      } catch (e) {
        username = 'Unknown';
      }

      const newCourse = {
        id: Date.now(),
        title: this.createCourseForm.name,
        credits: this.createCourseForm.credits,
        startTime: this.createCourseForm.startTime,
        endTime: this.createCourseForm.endTime,
        content: this.createCourseForm.introduction,
        image: this.createCourseForm.image,
        teacherName: username,
        status: 'open'
      };
      this.courses.push(newCourse);
      this.$Message.success('Course created successfully!');
      this.showCreateCourseModal = false;
      this.createCourseForm = { name: '', credits: '', startTime: '', endTime: '', introduction: '', image: '' };
      this.uploadFileName = '';
    },
    async saveEditInfo() {
      if (!this.editCourseInfo.name || !this.editCourseInfo.credits || !this.editCourseInfo.startTime || !this.editCourseInfo.endTime) {
        this.$Message.warning('Please fill in all fields');
        return false;
      }

      // 格式化时间（确保为字符串）
      const formatDate = dt => {
        if (!dt) return '';
        const d = new Date(dt);
        const pad = n => n < 10 ? '0' + n : n;
        return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}`;
      };

      // 组装参数
      const params = {
        id: this.courseInfo.id, // 课程ID必须传
        title: this.editCourseInfo.name,
        credits: this.editCourseInfo.credits ,
        startTime: formatDate(this.editCourseInfo.startTime),
        endTime: formatDate(this.editCourseInfo.endTime),
        content: this.courseInfo.introduction, // 简介可选
        image: this.courseInfo.image           // 图片可选
      };

      try {
        const res = await saveOrUpdateCourse(params);
        if (res.success) {
          this.$Message.success('Course info updated successfully!');
          this.showEditInfoModal = false;
          await this.getCourseInfo();
        } else {
        }
      } catch (error) {
      }
    },
    openEditInfoModal() {
      this.editCourseInfo = {
        id: this.courseInfo.id,
        name: this.courseInfo.name,
        credits: this.courseInfo.credits,
        startTime: this.courseInfo.startTime || '',
        endTime: this.courseInfo.endTime || '',
        introduction: this.courseInfo.introduction || '',
        image: this.courseInfo.image || ''
      };
      this.showEditInfoModal = true;
    }
  }
}
</script>

<style lang="less" scoped>
.course-lobby {
  padding: 30px;
  background: rgba(246,247,251, 0.8);
  border-radius: 20px;
}

.banner-section {
  margin-bottom: 40px;
}

.banner-container {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.banner-img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 16px;
}

.banner-section {
  :deep(.ivu-carousel) {
    width: 100%;
    max-width: 1500px;
    margin: 0 auto;
  }

  :deep(.ivu-carousel-item) {
    padding: 0 15px;
  }
}

.content-section {
  margin-top: 20px;
}

.content-container {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px 50px;
}

.section-title {
  text-align: center;
  margin-bottom: 35px;
  font-size: 30px;
  font-weight: 600;
  color: #515a6e;
}

.operation-area {
  display: flex;
  gap: 35px;
  margin-bottom: 35px;
  align-items: center;
}

.search-box {
  width: 300px;

  :deep(.ivu-input-wrapper) {
    .ivu-input {
      border-radius: 25px;
      padding-left: 40px;
      height: 40px;
      font-size: 16px;

      &:focus {
        box-shadow: 0 0 0 2px rgba(45, 140, 240, 0.2);
      }
    }

    .ivu-input-prefix {
      left: 12px;
      color: #808695;
      font-size: 16px;
      line-height: 40px;
    }
  }
}
.status-select {
  width: 200px;

  :deep(.ivu-select-selection) {
    border-radius: 25px;
    height: 40px;
    padding-left: 8px;
    border: 1px solid #dcdee2;
    display: flex;
    align-items: center;

    &:hover {
      border-color: #2d8cf0;
    }

    &:focus {
      border-color: #2d8cf0;
      box-shadow: 0 0 0 2px rgba(45, 140, 240, 0.2);
    }
  }

  :deep(.ivu-select-placeholder) {
    height: 40px;
    line-height: 40px;
    font-size: 16px;
  }
}

.course-grid {
  margin-top: 20px;
}

.course-card {
  cursor: pointer;
  margin-bottom: 20px;
  border-radius: 20px;
  overflow: hidden;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
}

.course-img-wrapper {
  position: relative;
  overflow: hidden;
  height: 180px;
  margin-bottom: 5px;
}

.course-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 16px;

  .course-card:hover & {
    transform: scale(1.05);
  }
}

.course-info {
  padding: 5px 14px;
  //border: 1px solid red;

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #515a6e;
    margin-bottom: 10px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 100%;
  }

  .course-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  .teacher {
    font-size: 14px;
    color: #808695;
    display: flex;
    align-items: center;
    gap: 6px;
    max-width: 60%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    .ivu-icon {
      font-size: 16px;
      color: #515a6e;
      flex-shrink: 0;
    }
  }
}

.course-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  text-transform: capitalize;
  color: #fff;
  background: rgba(0, 0, 0, 0.6);
  white-space: nowrap;
  flex-shrink: 0;

  &.Upcoming {
    background: #42b983;
  }

  &.Ongoing {
    background: #faad14;
  }

  &.Closed {
    background: #e55858;
  }
}

.create-course-btn {
  margin-left: auto;
  border-radius: 22px;
}
</style> 