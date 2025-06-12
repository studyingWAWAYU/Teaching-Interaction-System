<template>
  <div class="lessons-container">
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
            :reviews="reviews"/>
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

export default {
  name: 'Lessons',
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
        name: 'Java Program Design',
        instructor: 'Ken',
        Time: 'Spring 2024 Semester',
        credits: 4,
        image: require('@/assets/course1.png'),
        introduction: 'This course mainly introduces the basic knowledge and practical application of Java programming, including object-oriented programming, collection framework, multithreading and other contents.',
        objectives: '1. 掌握Java语言的基本语法和编程规范\n2. 深入理解面向对象编程的核心概念\n3. 熟练运用Java集合框架进行数据处理\n4. 掌握多线程编程的基本原理和应用'
      },
      reviews: [
        {
          id: 1,
          name: 'Ben',
          rating: 5,
          content: '老师讲课很生动，课程内容很实用。',
          time: '2024-03-15'
        },
        {
          id: 2,
          name: 'Amy',
          rating: 4,
          content: '课程安排合理，实践机会多。',
          time: '2024-03-14'
        },
        {
          id: 3,
          name: 'Emma',
          rating: 0,
          content: '不喜欢。',
          time: '2024-03-14'
        }

      ],
      discussions: [
        {
          id: 1,
          title: '关于Java多线程的疑问',
          author: 'Ken',
          time: '2024-03-15 14:30',
          content: '在实现多线程时，如何避免死锁问题？有没有一些最佳实践可以分享？',
          replyCount: 3,
          showReplies: false,
          replies: [
            {
              id: 1,
              author: 'Ben',
              time: '2024-03-15 15:00',
              content: '建议使用tryLock()方法，并设置超时时间，这样可以避免死锁。'
            },
            {
              id: 2,
              author: 'Amy',
              time: '2024-03-15 15:30',
              content: '也可以使用ReentrantLock，它提供了更灵活的锁机制。'
            }
          ]
        },
        {
          id: 2,
          title: '课程项目建议',
          author: 'Amy',
          time: '2024-03-14 10:20',
          content: '建议在课程项目中加入更多实际案例，这样可以帮助我们更好地理解知识点。',
          replyCount: 2,
          showReplies: false,
          replies: [
            {
              id: 3,
              author: 'Ken',
              time: '2024-03-14 11:00',
              content: '很好的建议，我会在下一节课加入更多实际案例。'
            }
          ]
        }
      ],
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
    this.getCourseInfo();  // 获取课程信息
    this.getCourseReviews();  // 获取课程评价
    this.getDiscussions();  // 获取讨论列表
  },
  methods: {
    async getCourseInfo() {
      try {
        // const response = await this.$api.course.getCourseInfo(this.$route.params.id);
        // this.courseInfo = response.data;
        // 暂时使用模拟数据
      } catch (error) {
        this.$Message.error('Failed to get course information');
      }
    },
    
    async getCourseReviews() {
      try {
        // const response = await this.$api.course.getCourseReviews(this.$route.params.id);
        // this.reviews = response.data;
        // 暂时使用模拟数据
      } catch (error) {
        this.$Message.error('Failed to get course reviews');
      }
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
        if (this.isEnrolled) {
          // await this.$api.course.dropCourse(this.$route.params.id);
          this.$Message.success('Drop Successfully');
        } else {
          // await this.$api.course.enrollCourse(this.$route.params.id);
          this.$Message.success('Add Successfully');
        }
        this.isEnrolled = !this.isEnrolled;
      } catch (error) {
        this.$Message.error('Operation failed');
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
        // const response = await this.$api.course.createTopic({
        //   courseId: this.$route.params.id,
        //   ...this.newTopic
        // });
        // this.discussions.unshift(response.data);
        // 暂时使用模拟数据
        const newTopic = {
          id: Date.now(), // 临时ID
          title: this.newTopic.title,
          author: 'Current User',
          time: new Date().toLocaleString(),
          content: this.newTopic.content,
          replyCount: 0,
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
        // const response = await this.$api.course.createReply({
        //   courseId: this.$route.params.id,
        //   topicId: topic.id,
        //   content: this.newReply.content
        // });
        // topic.replies.push(response.data);
        // 暂时使用模拟数据
        topic.replies.push({
          id: Date.now(), // 临时ID
          author: 'Current User',
          time: new Date().toLocaleString(),
          content: this.newReply.content
        });
        topic.replyCount++;
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
.lessons-container {
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

    .empty-content {
      padding: 40px 0;
      text-align: center;
      color: #808695;
    }
  }
}
</style> 