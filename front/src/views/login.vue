<template>
  <div class="login">
    <Row justify="center" align="middle" @keydown.enter.native="submitLogin" style="height:100%">
      <div class="loginUp">
        <div class="loginLeft">
          <img src="../assets/login/logo.png" alt="" srcset="">
          <span class="line"></span>
          <span class="title">Teaching interaction system</span>
        </div>
      </div>
      <div class="loginMiddle">
        <div class="login-background">
          <div class="loginBg"></div>
          <div class="loginRight">
            <Row class="loginRow">
              <Tabs v-model="tabName" @on-click="changeTabName" class="loginTab">
                <TabPane label="-" disabled name="mobile">
                </TabPane>
                <TabPane label="Login" name="userAndPassword">
                  <Form ref="usernameLoginForm" :model="form" :rules="usernameLoginFormRules" class="form">
                    <FormItem prop="username" class="loginInput">
                      <Row>
                        <Input v-model="form.username" size="large" clearable placeholder="Log in to your account" autocomplete="off">
                          <Icon class="iconfont icon-yonghu" slot="prefix" style="line-height:50px" />
                        </Input>
                      </Row>
                    </FormItem>
                    <FormItem prop="password">
                      <Input style="height:50px;line-height:50px" type="password" v-model="form.password" size="large" placeholder="Please enter your login password" password autocomplete="off">
                        <Icon class="iconfont icon-mima1" slot="prefix" style="line-height:50px" />
                      </Input>
                    </FormItem>

                  </Form>
                  <Row type="flex" justify="space-between" align="middle">
                    <Checkbox v-model="saveLogin" size="large">Automatic Login</Checkbox>
                    <router-link to="/regist">
                      <a class="forget-pass">Click here to register</a>
                    </router-link>
                  </Row>
                  <Row>
                    <Button class="login-btn" type="primary" size="large" :loading="loading" @click="submitLogin" long>
                      <span v-if="!loading" style=" font-weight:bold">Login</span>
                      <span v-else>Logging in... Please wait}</span>
                    </Button>
                  </Row>
                </TabPane>
                <TabPane label="-" disabled name="mobile">
                </TabPane>
              </Tabs>

            </Row>
            <p class="loginBottom">
              Ask. Answer. Achieve. Together.
            </p>
          </div>
        </div>
      </div>
      <div class="loginDown">
        <div class="footer-container">
          <div class="footer-item">
            <img src="../assets/login/logo2.png" alt="Activity Hub Logo" class="footer-logo" />
            <span>WisdomLink</span>
          </div>
          <div class="footer-item">
            <Icon type="md-pin" class="footer-icon" />
            <span>Foshan, Guanddong, China</span>
          </div>
          <div class="footer-item">
            <Icon type="md-call" class="footer-icon" />
            <span>123-456-7890</span>
          </div>
          <div class="footer-item">
            <Icon type="md-mail" class="footer-icon" />
            <span>cams@ntesmail.com</span>
          </div>
          <div class="footer-item">
            <Icon type="md-time" class="footer-icon" />
            <span>© 2025 WisdomLink. All rights reserved.</span>
          </div>
        </div>
      </div>
    </Row>
  </div>
</template>

<script>
import {
  login,
  userInfo,
} from "@/api/index";
import Cookies from "js-cookie";
import util from "@/libs/util.js";
export default {
  components: {
  },
  data() {
    return {
      tabName: "userAndPassword",
      saveLogin: true,
      loading: false,
      form: {
        username: "",
        password: "",
      },
      usernameLoginFormRules: {
        username: [{
          required: true,
          message: "The account cannot be empty",
          trigger: "blur"
        }],
        password: [{
          required: true,
          message: "The password cannot be empty",
          trigger: "blur"
        }],
      }
    };
  },
  methods: {
    afterLogin(res) {
      let accessToken = res.result;
      this.setStore("accessToken", accessToken);
      userInfo().then((res) => {
        if (res.success) {
          delete res.result.permissions;
          let roles = [];
          res.result.roles.forEach((e) => {
            roles.push(e.name);
          });
          delete res.result.roles;
          this.setStore("roles", roles);
          this.setStore("saveLogin", this.saveLogin);
          if (this.saveLogin) {
            Cookies.set("userInfo", JSON.stringify(res.result), {
              expires: 100,
            });
          } else {
            Cookies.set("userInfo", JSON.stringify(res.result));
          }
          this.setStore("userInfo", res.result);
          this.$store.commit("setAvatarPath", res.result.avatar);
          util.initRouter(this);
          this.$router.push({
            name: "home_index",
          });
        } else {
          this.loading = false;
        }
      });
    },
    submitLogin() {
      this.$refs.usernameLoginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          login({
            username: this.form.username,
            password: this.form.password,
            saveLogin: this.saveLogin
          }).then(res => {
            if (res.success) {
              this.afterLogin(res);
            } else {
              this.loading = false;
            }
          });
        }
      });
    }
  },
};
</script>

<style lang="less">
html,body{
  background: #ffffff !important;
  font-family: Microsoft YaHei;
  font-weight: 400;;
}
a{
  font-family: Microsoft YaHei;

  color:#77c8c6;
}
input::-webkit-input-placeholder {
  font-size: 14px;
}
.ivu-checkbox-wrapper.ivu-checkbox-large{
  font-size: 14px;
}
a:hover{
  font-family: Microsoft YaHei;
  color: #77C8C6;
}
.login {
  height: 100%;
  background-color: #EAF3FA;

  .ivu-tabs-nav-container {
    line-height: 2;
    font-size: 17px;
    box-sizing: border-box;
    white-space: nowrap;
    overflow: hidden;
    position: relative;
    zoom: 1;
  }
  .loginUp{
    width: 100%;
    min-height: 80px;
    background-color: #b7d5f0;
    margin-top:-5px;
    overflow: hidden;
  }
  .loginLeft{
    margin-top: 15px;
    margin-left: 80px;
    height: 50px;
    display: flex;
  }
  .line{
    display: inline-block;
    width: 2px;
    height: 25px;
    background: url(../assets/login/line.png);
    margin: 0px 10px;
    margin-top: 15px;
  }
  .title{
    line-height: 58px;
    font-size: 18px;
    font-family: Microsoft YaHei;
    font-weight: 500;
    color:#3c7fb4;
    font-weight:bold;
  }
  .loginMiddle{
    width: 100%;
    height: 680px;
    margin: 0 auto;
    background-color: #EAF3FA;
    overflow: hidden;
    // background: linear-gradient(45deg, rgba(2, 173, 168, 0.17), rgba(0, 221, 215, 0.17));
  }
  .login-background{
    width: 1200px;
    height: 700px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
  }
  .loginBg{
    width: 650px;
    height: 400px;
    margin-top: 100px;
    background-image: url(../assets/login/back.png);
    background-repeat: no-repeat;
    background-position: center;
  }
  .loginRight{
    width: 450px;
    height: 480px;
    background-color: #ffffff;
    border: 1px solid #E6E6E6;
    box-shadow: 0px 2px 15px 1px rgba(0, 0, 0, 0.1);
    border-radius: 30px;
    margin-top: 115px;
    position: relative;
  }
  .loginRow{
    padding: 0px 30px;
  }
  .loginDown {
    height:55px;
    width: 1300px;
    border-radius:20px;
    margin-top:-35px;
    background-color: #99c2ed;
    box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.21);
  }
  .footer-container {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    padding: 10px 0;
  }
  .footer-item {
    display: flex;
    align-items: center;
    margin: 0 15px;
    font-size: 15px;
    color: #333;
  }
  .footer-logo {
    width: 24px;
    height: 24px;
    margin-right: 5px;
  }
  .footer-icon {
    margin-right: 5px;
    font-size: 16px; /* 图标大小，适配视觉效果  */
  }
  .loginTab{
    margin-top: 20px;
  }
  .ivu-tabs-tab{
    color: #333333;
    font-size: 18px;
    font-family: Microsoft YaHei;
    font-weight: bold;
  }
  /* 禁用标签页滚动箭头 */
  .ivu-tabs-nav-next, .ivu-tabs-nav-prev {
    display: none !important;
  }
  
  /* 禁用标签页滚动行为 */
  .ivu-tabs-nav-wrap {
    overflow: hidden !important;
    padding-right: 0 !important;
  }
  .ivu-tabs-nav .ivu-tabs-tab{
    padding: 8px !important;
    margin-right:100px !important;
  }
  .ivu-tabs-ink-bar{
    height: 4px;
    width: 86px !important;
    border-radius: 2px;
    margin: 0px 70px;
    background-color: #C4E2FF;
  }
  .ivu-tabs-nav .ivu-tabs-tab-active,.ivu-tabs-nav .ivu-tabs-tab:hover{
    color: #333333;
  }
  .loginInput{
    font-size: 18px;
    font-family: Microsoft YaHei;
    font-weight: bold;
    color: #333333;
  }
  .ivu-tabs-bar{
    border-bottom: 0px;
  }
  .login-btn{
    width: 390px;
    height: 50px;
    background: radial-gradient(circle, #77C8C6, #50C7C4);
    border: 2px solid #61C8C5;
    box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.21);
    border-radius: 35px;
  }
  .login .login-btn, .login .other-login{
    margin-top: 15px;
  }
  .loginBottom{
    width: 448px;
    height: 65px;
    background:#99c2ed;
    border-radius: 0px 0px 30px 30px;
    padding: 0px;
    position: absolute;
    bottom: 0px;
    font-size: 18px;
    font-weight: bold;
    color: #ffffff;
    text-align: center;
    line-height: 58px;
  }
  .loginDown p{
    text-align: center;
    font-size: 17px;
    font-family: Microsoft YaHei;
    color: #777777;
    line-height: 20px;
  }
  .ivu-checkbox-checked .ivu-checkbox-inner{
    background-color: #C4E2FF;
    border-color: #C4E2FF;
  }
  .ivu-form-item{
    margin-bottom: 24px;
  }
  .ivu-input-wrapper-large .ivu-input-icon{
    line-height: 50px;
  }
  .loginInput input:nth-of-type(1){
    height: 50px;
    font-size: 18px;
    font-weight: bold;
    font-family: Microsoft YaHei;
    color: #333333;
    line-height: 50px;
  }
  .ivu-input-large{
    height: 50px;
    color:#CFCFCF;
    line-height: 50px;
  }
  .ivu-input-large{
    font-size: 14px;
  }
  .ivu-btn-large{
    height: 50px;
  }
  .form {
    padding-top: 2vh;

    .input-verify {
      width: 67%;
    }
  }

  .forget-pass,
  .other-way {
    font-size: 14px;
  }

  .login-btn,
  .other-login {
    margin-top: 40px;
  }

  .icons {
    display: flex;
    align-items: center;
  }

  .other-icon {
    cursor: pointer;
    margin-left: 10px;
    display: flex;
    align-items: center;
    color: rgba(0, 0, 0, .2);

    :hover {
      color: #2d8cf0;
    }
  }

  .layout {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    width: 368px;
    height: 100%;
  }
}
</style>