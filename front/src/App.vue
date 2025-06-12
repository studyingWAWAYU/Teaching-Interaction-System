<template>
<div id="main" class="app-main">
    <router-view></router-view>
</div>
</template>

<script>
export default {
    data() {
        return {};
    },
    mounted() {},
    beforeDestroy() {},
    methods: {}
};
</script>

<style lang="less">
html,
body {
    width: 100%;
    height: 100%;
    background: #f0f0f0;
}

.app-main {
    width: 100%;
    height: 100%;
}

.br button {
    margin-right: 8px;
}

.operation button {
    margin-right: 8px;
}

.operation .brr button {
    margin-right: 0 !important;
}

.ivu-btn-text:focus {
    box-shadow: none !important;
}

.ivu-tag {
    cursor: pointer;
}

.block-tool .ivu-tooltip,
.block-tool .ivu-tooltip-rel {
    display: block;
}

.block-pop .ivu-poptip,
.block-pop .ivu-poptip-rel {
    display: block;
}

.form-wangEditor,
.form-quill {
    .ivu-form-item-content {
        line-height: unset !important;
    }
}

// 导航栏下拉菜单
.ivu-dropdown-menu {
    width: 150px !important;
    padding: 4px 4px !important;

    .ivu-dropdown-item {
        font-size: 18px !important;
    }
    .ivu-dropdown-item:hover{
        background-color: rgba(0,122,255,0.1);
    }
}
</style>
