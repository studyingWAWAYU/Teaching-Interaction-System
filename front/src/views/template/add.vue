<style lang="less">
</style>
<template>
<div>
    <Card>
        <p slot="title">Add</p>
        <Form ref="form" :model="form" :label-width="90" :rules="formValidate">
            <FormItem label="Name" prop="name">
                <Input v-model="form.name" style="width: 400px" />
            </FormItem>
            <Form-item class="br">
                <Button @click="handleSubmit" :loading="submitLoading" type="primary">Submit and Save</Button>
                <Button @click="handleReset">Reset</Button>
            </Form-item>
        </Form>
    </Card>
</div>
</template>

<script>
export default {
    name: "add",
    data() {
        return {
            loading: true, // Form loading status
            submitLoading: false, // Form submission status
            form: {
                id: "",
                name: ""
            },
            // Form validation rules
            formValidate: {
                name: [{
                    required: true,
                    message: "Cannot be empty",
                    trigger: "blur"
                }]
            },
            backRoute: ""
        };
    },
    methods: {
        init() {
            this.backRoute = this.$route.query.backRoute;
        },
        handleReset() {
            this.$refs.form.resetFields();
        },
        handleSubmit() {
            this.$refs.form.validate(valid => {
                if (valid) {
                    // Simulate success
                    this.submitLoading = false;
                    this.$Message.success("Added successfully");
                    this.closeCurrentPage();
                }
            });
        },
        // Close current page
        closeCurrentPage() {
            this.$store.commit("removeTag", "add");
            localStorage.pageOpenedList = JSON.stringify(
                this.$store.state.app.pageOpenedList
            );
            this.$router.push({
                name: this.backRoute
            });
        }
    },
    watch: {
        $route(to, from) {
            if (to.name == "add") {
                this.handleReset();
            }
        }
    },
    mounted() {
        this.init();
    }
};
</script>