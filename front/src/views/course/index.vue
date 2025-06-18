<template>
  <div class="course-container">
    <Card class="course-info">
      <Row :gutter="20">
        <Col span="8">
          <div class="course-img">
            <img :src="courseInfo.image" :alt="courseInfo.name">
          </div>
        </Col>

        <Col span="16">
          <div class="info">
            <h1 class="course-title">{{ courseInfo.name }}</h1>
            <div class="course-meta">
              <div class="meta-item">
                <Icon type="ios-person" />
                <span>Instructor:&nbsp; {{ courseInfo.instructor }}</span>
              </div>
              <div class="meta-item">
                <Icon type="ios-school" />
                <span>Credits:&nbsp; {{ courseInfo.credits }}</span>
              </div>
              <div class="meta-item">
                <Icon type="ios-time" />
                <span>Course Time:&nbsp; {{ courseInfo.Time }}</span>
              </div>
            </div>
            <div class="choice">
              <Button 
                :type="isEnrolled ? 'error' : 'primary'" 
                size="large" 
                @click="handleEnroll"
                class="course-btn">
                {{ isEnrolled ? 'Drop Course' : 'Add Course' }}
              </Button>
            </div>
          </div>
        </Col>
      </Row>
    </Card>

    <Card class="course-content">
      <Tabs class="pages">
        <TabPane label="Details">
          <CourseDetails 
            :courseInfo="courseInfo"
            :reviews="reviews"
            @refresh-reviews="refreshReviews"/>
        </TabPane>

        <TabPane label="Resources">
          <CourseResources />
        </TabPane>

        <TabPane label="Assignments">
          <CourseAssignments />
        </TabPane>

        <TabPane label="Discussion">
          <Discussion 
            :discussions="discussions"
            @show-create-modal="showCreateModal"
            @show-reply-modal="showReplyModal"/>
        </TabPane>
      </Tabs>
    </Card>

    <Modal v-model="createModalVisible" title="Create Topic" @on-ok="handleCreateTopic">
      <Form :model="newTopic" :label-width="80">
        <FormItem label="Title">
          <Input v-model="newTopic.title" placeholder="Enter topic title" />
        </FormItem>
        <FormItem label="Content">
          <Input
            v-model="newTopic.content"
            type="textarea"
            :rows="4"
            placeholder="Enter topic content" />
        </FormItem>
      </Form>
    </Modal>

    <Modal v-model="replyModalVisible" title="Reply to Topic" @on-ok="handleReply">
      <Form :model="newReply" :label-width="80">
        <FormItem label="Content">
          <Input
            v-model="newReply.content"
            type="textarea"
            :rows="4"
            placeholder="Enter your reply" />
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<script>
import Discussion from './components/discussion.vue'
import CourseDetails from './components/details.vue'
import CourseResources from './components/resources.vue'
import CourseAssignments from './components/assignments.vue'
import { getCourse, updateCourse } from '@/api/course'
import { getAllFeedbacks } from '@/api/feedback'
import { getAllUsers } from '@/views/roster/user/api'
import Cookies from 'js-cookie'

export default {
  name: 'CourseManage',
  components: {
    Discussion,
    CourseDetails,
    CourseResources,
    CourseAssignments
  },
  data() {
    return {
      isEnrolled: false,
      courseInfo: {
        id: null,
        name: '',
        instructor: '',
        Time: '',
        credits: 0,
        image: '',
        introduction: '',
      },
      reviews: [],
      discussions: [],
      users: [], // 存储用户信息
      createModalVisible: false,
      replyModalVisible: false,
      newTopic: {
        title: '',
        content: ''
      },
      newReply: {
        content: '',
        topicIndex: -1
      }
    }
  },
  created() {
    this.loadData();
  },
  methods: {
    async loadData() {
      await this.loadUsers();
      await this.getCourseInfo();
      await this.getCourseReviews();
      await this.getDiscussions();
    },
    
    async loadUsers() {
      try {
        const res = await getAllUsers();
        if (res.success) {
          this.users = res.result;
        }
      } catch (error) {
        console.error('加载用户信息失败:', error);
      }
    },
    
    // 获取课程信息
    async getCourseInfo() {
      try {
        const res = await getCourse({ id: this.$route.params.id });
        if (res.success) {
          console.log('完整课程数据:', res.result);

          // 转换数据格式，确保字段匹配
          this.courseInfo = {
            id: res.result.id,
            name: res.result.title || res.result.name,
            instructor: this.getTeacherName(res.result.createBy),
            Time: this.formatTimeRange(res.result.startTime, res.result.endTime),
            credits: 3, // 默认学分
            image: res.result.image,
            introduction: res.result.content,
            objectives: res.result.content,
            status: res.result.status || 'Normal'
          };
          this.isEnrolled = res.result.status === 'enrolled';
        } else {
          this.$Message.error('获取课程信息失败');
        }
      } catch (error) {
        this.$Message.error('获取课程信息失败');
      }
    },
    
    getTeacherName(createBy) {
      if (!createBy) return 'Unknown teacher';
      const createById = parseInt(createBy);
      const user = this.users.find(u => u.id === createById);
      if (user) {
        return user.username || user.nickname ||  `教师${createBy}`;
      }
      return `教师${createBy}`;
    },
    
    formatTimeRange(startTime, endTime) {
      if (!startTime || !endTime) {
        return 'Time to be comfirmed';
      }
      
      try {
        const start = new Date(startTime);
        const end = new Date(endTime);
        if (isNaN(start.getTime()) || isNaN(end.getTime())) {
          return '时间格式错误';
        }
        // 只显示年月日，格式：YYYY-MM-DD
        const startStr = start.toISOString().split('T')[0];
        const endStr = end.toISOString().split('T')[0];
        return `${startStr} - ${endStr}`;
      } catch (error) {
        return '时间格式错误';
      }
    },
    
    async getCourseReviews() {
      try {
        const res = await getAllFeedbacks();
        if (res.success) {
          // 过滤出当前课程的评价
          const courseFeedbacks = res.result.filter(feedback => 
            feedback.courseId === parseInt(this.$route.params.id)
          );
          
          // 为每个评价添加用户信息
          this.reviews = courseFeedbacks.map(feedback => {
            const user = this.users.find(u => u.id === feedback.createBy);
            return {
              id: feedback.id,
              content: feedback.content,
              rating: feedback.rating || 0,
              createTime: feedback.createTime,
              reviewerName: user ? (user.nickname || user.username) : `User${feedback.createBy}`
            };
          });
        } else {
          this.$Message.error('获取课程评价失败');
        }
      } catch (error) {
        this.$Message.error('获取课程评价失败');
      }
    },

    async refreshReviews() {
      await this.getCourseReviews();
    },
    
    async getDiscussions() {
      try {
        // const response = await this.$api.course.getDiscussions(this.$route.params.id);
        // this.discussions = response.data;
        // 暂时使用模拟数据
      } catch (error) {
        this.$Message.error('Failed to get discussions');
      }
    },
    // 选课/退课
    async handleEnroll() {
      try {
        const courseData = {
          ...this.courseInfo,
          status: this.isEnrolled ? 'unenrolled' : 'enrolled'
        };
        const res = await updateCourse(courseData);
        if (res.success) {
          this.isEnrolled = !this.isEnrolled;
          this.$Message.success(this.isEnrolled ? '选课成功' : '退课成功');
          // 更新课程信息
          this.getCourseInfo();
        } else {
          this.$Message.error(this.isEnrolled ? '退课失败' : '选课失败');
        }
      } catch (error) {
        this.$Message.error(this.isEnrolled ? '退课失败' : '选课失败');
      }
    },
    
    // 显示创建讨论主题的模态框
    showCreateModal() {
      this.createModalVisible = true;
      this.newTopic = {
        title: '',
        content: ''
      };
    },

    // 处理创建讨论主题
    async handleCreateTopic() {
      if (!this.newTopic.title || !this.newTopic.content) {
        this.$Message.warning('Please fill in all fields');
        return;
      }
      try {
        const userInfo = JSON.parse(Cookies.get('userInfo'));
        const newTopic = {
          id: Date.now(),
          title: this.newTopic.title,
          author: userInfo.nickname,
          time: new Date().toLocaleString(),
          content: this.newTopic.content,
          replyCount: 0,
          likes: 0,
          showReplies: false,
          replies: []
        };
        this.discussions.unshift(newTopic);
        this.$Message.success('Topic created successfully');
      } catch (error) {
        this.$Message.error('Failed to create topic');
      }
    },

    // 显示回复模态框
    showReplyModal(topic) {
      this.replyModalVisible = true;
      this.newReply = {
        content: '',
        topicIndex: this.discussions.indexOf(topic)
      };
    },

    // 处理回复
    async handleReply() {
      if (!this.newReply.content) {
        this.$Message.warning('Please enter your reply');
        return;
      }
      try {
        const topic = this.discussions[this.newReply.topicIndex];
        const userInfo = JSON.parse(Cookies.get('userInfo'));
        topic.replies.push({
          id: Date.now(), // 临时ID
          author: userInfo.nickname,
          time: new Date().toLocaleString(),
          content: this.newReply.content,
          likes: 0
        });
        topic.replyCount = topic.replies.length;
        topic.showReplies = true;
        this.$Message.success('Reply posted successfully');
      } catch (error) {
        this.$Message.error('Failed to post reply');
      }
    }
  }
}
</script>

<style lang="less" scoped>
.course-container {
  padding: 30px;
  background: rgba(246,247,251, 0.8);
  border-radius: 20px;

  .course-info {
    margin-bottom: 15px;
    padding: 12px 25px;
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    }

    .course-img {
      width: 100%;
      height: 250px;
      overflow: hidden;
      border-radius: 20px;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s ease;

        &:hover {
          transform: scale(1.05);
        }
      }
    }

    .info {
      padding: 25px 35px;
      background-color: rgba(246,247,251);
      border-radius: 20px;
      margin-left: 18px;

      .course-title {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 20px;
        color: #515a6e;
        letter-spacing: -0.5px;
      }

      .course-meta {
        margin-bottom: 25px;

        .meta-item {
          display: flex;
          align-items: center;
          margin-bottom: 12px;
          font-size: 16px;
          color: #515a6e;

          .ivu-icon {
            margin-right: 10px;
            font-size: 18px;
            color: rgba(81,90,110,0.8);
          }
        }
      }

      .choice {
        .course-btn {
          width: 120px;
          height: 38px;
          border-radius: 19px;
          font-size: 15px;
          transition: all 0.3s ease;
          opacity: 0.8;
          box-shadow: 4px 4px 12px rgba(0,0,0, 0.2);

          &:hover {
            transform: translateY(-2px);
          }
        }
      }
    }
  }

  .course-content {
    border-radius: 20px;
    overflow: hidden;
    text-overflow: ellipsis;
    word-break: keep-all;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

    .pages {
      padding: 5px 30px;

      :deep(.ivu-tabs-nav) {
        padding: -10px 25px;
        background: rgba(255, 255, 255, 0.8);
      }

      :deep(.ivu-tabs-bar) {
        border-bottom: none;
      }

      :deep(.ivu-tabs-tab) {
        padding: 12px 30px;
        font-size: 18px;
        font-weight: bold;
        transition: all 0.3s ease;

        &:hover {
          color: #2d8cf0;
        }
      }

      :deep(.ivu-tabs-ink-bar) {
        height: 3px;
        border-radius: 5px;
        background-color: #2d8cf0;
      }
    }
  }
}

</style> 