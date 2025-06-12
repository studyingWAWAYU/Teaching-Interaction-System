<template>
  <div class="calendar-container">
    <div class="calendar-header">
      <div class="calendar-nav">
        <div class="nav-group">
          <Icon type="ios-arrow-dropleft-circle" @click="prevYear" class="nav-icon year-icon" />
          <Icon type="ios-arrow-back" @click="prevMonth" class="nav-icon" />
        </div>
        <span class="calendar-title">{{ formatDate }}</span>
        <div class="nav-group">
          <Icon type="ios-arrow-forward" @click="nextMonth" class="nav-icon" />
          <Icon type="ios-arrow-dropright-circle" @click="nextYear" class="nav-icon year-icon" />
        </div>
      </div>
    </div>
    <div class="calendar-body">
      <div class="calendar-week">
        <span v-for="day in ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']" :key="day">{{ day }}</span>
      </div>
      <div class="calendar-days">
        <span 
          v-for="(day, index) in calendarDays" 
          :key="index"
          :class="{
            'current-day': isCurrentDay(day),
            'other-month': !day.currentMonth,
            'empty': !day.date
          }"
        >
          {{ day.date }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Calendar",
  data() {
    return {
      currentYear: new Date().getFullYear(),
      currentMonth: new Date().getMonth(),
      currentDate: new Date().getDate()
    };
  },
  computed: {
    formatDate() {
      const months = ['January', 'February', 'March', 'April', 'May', 'June', 
                     'July', 'August', 'September', 'October', 'November', 'December'];
      return `${months[this.currentMonth]} ${this.currentYear}`;
    },
    calendarDays() {
      const year = this.currentYear;
      const month = this.currentMonth;
      
      // 获取当月第一天是星期几
      const firstDay = new Date(year, month, 1).getDay();
      // 获取当月天数
      const daysInMonth = new Date(year, month + 1, 0).getDate();
      // 获取上个月天数
      const daysInPrevMonth = new Date(year, month, 0).getDate();
      
      const days = [];
      
      // 添加上个月的日期
      for (let i = firstDay - 1; i >= 0; i--) {
        days.push({
          date: daysInPrevMonth - i,
          currentMonth: false
        });
      }
      
      // 添加当月的日期
      for (let i = 1; i <= daysInMonth; i++) {
        days.push({
          date: i,
          currentMonth: true
        });
      }
      
      // 添加下个月的日期
      const remainingDays = 42 - days.length; // 6行7列 = 42
      for (let i = 1; i <= remainingDays; i++) {
        days.push({
          date: i,
          currentMonth: false
        });
      }
      
      return days;
    }
  },
  methods: {
    prevYear() {
      this.currentYear--;
    },
    nextYear() {
      this.currentYear++;
    },
    prevMonth() {
      if (this.currentMonth === 0) {
        this.currentYear--;
        this.currentMonth = 11;
      } else {
        this.currentMonth--;
      }
    },
    nextMonth() {
      if (this.currentMonth === 11) {
        this.currentYear++;
        this.currentMonth = 0;
      } else {
        this.currentMonth++;
      }
    },
    isCurrentDay(day) {
      const today = new Date();
      return day.currentMonth && 
             day.date === today.getDate() && 
             this.currentMonth === today.getMonth() && 
             this.currentYear === today.getFullYear();
    }
  }
};
</script>

<style lang="less" scoped>
.calendar-container {
  width: 35%;
  padding: 20px;
  background-color: #fff;
  border-radius: 25px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.08);
  border: 1px solid rgba(0,0,0,.08);
}

.calendar-header {
  text-align: center;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.calendar-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0 10px;
}

.nav-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-icon {
  font-size: 24px;
  color: #3c7fb4;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.year-icon {
  font-size: 28px;
  color: #3c7fb4;
}

.nav-icon:hover {
  background-color: rgba(0,122,255,0.1);
}

.calendar-title {
  font-size: 22px;
  color: #515a6e;
  font-weight: 600;
  text-transform: capitalize;
  letter-spacing: 0.5px;
}

.calendar-week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  margin-bottom: 8px;
}

.calendar-week span {
  color: #3c7fb4;
  font-size: 15px;
  font-weight: 550;
  padding: 8px 0;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  text-align: center;
}

.calendar-days span {
  padding: 8px;
  color: #515a6e;
  font-size: 17px;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.2s ease;
  position: relative;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 2px auto;
}

.calendar-days span:not(.current-day):hover {
  background-color: rgba(0,122,255,0.1);
}

.calendar-days .current-day {
  background-color: #C4E2FF;
  color: #51586c;
  cursor: default;
  font-size: 19px;
  font-weight: 600;
}

.calendar-days span.other-month {
  color: #C7C7CC;
}

.calendar-days span.empty {
  visibility: hidden;
}

//点击效果
.calendar-days span:not(.current-day):active {
  transform: scale(0.95);
  background-color: rgba(0,122,255,0.2);
}
</style> 