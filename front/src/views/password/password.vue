<template>
  <div>
    <Card class="change-password">
      <p slot="title">
        <Icon type="key"></Icon>Change Password
      </p>
      <div>
        <Form ref="editPasswordForm" :model="editPasswordForm" :label-width="100" label-position="right" :rules="passwordValidate" style="width:450px">
          <FormItem label="Original Password" prop="oldPass">
            <Input type="password" v-model="editPasswordForm.oldPass" placeholder="Please enter your current password"></Input>
          </FormItem>
          <FormItem label="New Password" prop="newPass">
            <SetPassword v-model="editPasswordForm.newPass" @on-change="changeInputPass" />
          </FormItem>
          <FormItem label="Confirm New Password" prop="rePass">
            <Input type="password" v-model="editPasswordForm.rePass" placeholder="Please re-enter the new password"></Input>
          </FormItem>
          <FormItem>
            <Button type="primary" style="width: 100px;margin-right:5px" :loading="savePassLoading" @click="saveEditPass">Save</Button>
            <Button @click="cancelEditPass">Cancel</Button>
          </FormItem>
        </Form>
      </div>
    </Card>
  </div>
</template>
<script>
import {
    changePass
} from "./api.js";
import SetPassword from "@/views/template/set-password";
export default {
    name: "change_pass",
    components: {
        SetPassword
    },
    data() {
        const valideRePassword = (rule, value, callback) => {
            if (value !== this.editPasswordForm.newPass) {
                callback(new Error("两次输入密码不一致"));
            } else {
                callback();
            }
        };
        return {
            savePassLoading: false,
            editPasswordForm: {
                oldPass: "",
                newPass: "",
                rePass: ""
            },
            strength: "",
            passwordValidate: {
                oldPass: [{
                    required: true,
                    message: "请输入原密码",
                    trigger: "blur"
                }],
                newPass: [{
                        required: true,
                        message: "请输入新密码",
                        trigger: "blur"
                    },
                    {
                        min: 6,
                        message: "请至少输入6个字符",
                        trigger: "blur"
                    },
                    {
                        max: 32,
                        message: "最多输入32个字符",
                        trigger: "blur"
                    }
                ],
                rePass: [{
                        required: true,
                        message: "请再次输入新密码",
                        trigger: "blur"
                    },
                    {
                        validator: valideRePassword,
                        trigger: "blur"
                    }
                ]
            }
        };
    },
    methods: {
        changeInputPass(v, grade, strength) {
            this.strength = strength;
        },
        saveEditPass() {
            let params = {
                password: this.editPasswordForm.oldPass,
                newPass: this.editPasswordForm.newPass,
                passStrength: this.strength
            };
            this.$refs["editPasswordForm"].validate(valid => {
                if (valid) {
                    this.savePassLoading = true;
                    changePass(params).then(res => {
                        this.savePassLoading = false;
                        if (res.success) {
                            this.$Modal.success({
                                title: "修改密码成功",
                                content: "修改密码成功，需重新登录",
                                onOk: () => {
                                    this.$store.commit("logout", this);
                                    this.$store.commit("clearOpenedSubmenu");
                                    this.$router.push({
                                        name: "login"
                                    });
                                }
                            });
                        }
                    });
                }
            });
        },
        cancelEditPass() {
            this.$store.commit("removeTag", "password");
            localStorage.pageOpenedList = JSON.stringify(
                this.$store.state.app.pageOpenedList
            );
            let lastPageName = "";
            let length = this.$store.state.app.pageOpenedList.length;
            if (length > 1) {
                lastPageName = this.$store.state.app.pageOpenedList[length - 1].name;
            } else {
                lastPageName = this.$store.state.app.pageOpenedList[0].name;
            }
            this.$router.push({
                name: lastPageName
            });
        }
    },
    mounted() {}
};
</script>
<style lang="less">
.change-pass {
    &-btn-box {
        margin-bottom: 10px;

        button {
            padding-left: 0;

            span {
                color: #2D8CF0;
                transition: all .2s;
            }

            span:hover {
                color: #0C25F1;
                transition: all .2s;
            }
        }
    }
}
</style>
