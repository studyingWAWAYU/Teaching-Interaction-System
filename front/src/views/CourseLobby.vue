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
            <Option value="open" label="Open" />
            <Option value="ongoing" label="Ongoing" />
            <Option value="closed" label="Closed" />
          </Select>
        </div>

        <div class="course-grid">
          <Row :gutter="20">
            <Col span="6" v-for="course in filteredCourses" :key="course.id">
              <Card class="course-card" @click.native="navigateToCourse(course.id)">
                <div class="course-img-wrapper">
                  <img :src="course.img" class="course-img" />
                </div>
                <div class="course-info">
                  <h3>{{ course.title }}</h3>
                  <div class="course-meta">
                    <p class="teacher">
                      <Icon type="ios-person" />
                      {{ course.teacher }}
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
export default {
  name: 'CourseLobby',
  data() {
    return {
      searchQuery: '',
      courseStatus: '',
      courses: [
        {
          id: 1,
          title: 'Java Program Design',
          teacher: 'Ken',
          img: require('@/assets/course1.png'),
          status: 'open'
        },
        {
          id: 2,
          title: 'Python Program Design 2025',
          teacher: 'Jenny',
          img: require('@/assets/course2.png'),
          status: 'ongoing'
        },
        {
          id: 3,
          title: '课程3',
          teacher: '王老师',
          img: require('@/assets/course3.jpg'),
          status: 'closed'
        },
        {
          id: 4,
          title: '课程4',
          teacher: '李老师',
          img: require('@/assets/course2.png'),
          status: 'ongoing'
        },
        {
          id: 5,
          title: '课程5',
          teacher: '王老师',
          img: require('@/assets/course3.jpg'),
          status: 'closed'
        }
      ]
    }
  },
  computed: {
    filteredCourses() {
      return this.courses
        .filter(course => {
          const matchesSearch = course.title.toLowerCase().includes(this.searchQuery.toLowerCase())
          const matchesStatus = !this.courseStatus || course.status === this.courseStatus
          return matchesSearch && matchesStatus
        })
        .sort((a, b) => a.id - b.id)
    }
  },
  methods: {
    navigateToCourse(courseId) {
      this.$router.push(`/course/${courseId}`)
    },
    handleSearch() {
      // 可以在这里添加搜索逻辑
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
    max-width: 1350px;
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

  &.open {
    background: rgba(66,185,131);
  }

  &.ongoing {
    background: rgba(250, 173, 20);
  }

  &.closed {
    background: rgb(229, 88, 88);
  }
}
</style> 