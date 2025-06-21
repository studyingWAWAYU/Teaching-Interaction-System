import Main from '@/views/Main.vue';

export const loginRouter = {
    path: '/login',
    name: 'login',
    meta: {
        title: '登录 - 教学资源共享平台'
    },
    component: () => import('@/views/login.vue')
};

export const registRouter = {
    path: '/regist',
    name: 'regist',
    meta: {
        title: '注册 - 教学资源共享平台'
    },
    component: () => import('@/views/regist.vue')
};

export const page404 = {
    path: '/*',
    name: 'error-404',
    meta: {
        title: '404-页面不存在'
    },
    component: () => import('@/views/template/404.vue')
};

export const page403 = {
    path: '/403',
    meta: {
        title: '403-权限不足'
    },
    name: 'error-403',
    component: () => import('@/views/template/403.vue')
};

export const page500 = {
    path: '/500',
    meta: {
        title: '500-服务端错误'
    },
    name: 'error-500',
    component: () => import('@/views/template/500.vue')
};

export const otherRouter = {
    path: '/',
    name: 'otherRouter',
    redirect: '/home',
    component: Main,
    children: [
        { path: 'home', title: '主页', name: 'home_index', component: () => import('@/views/home/home.vue') },
        { path: 'myHome', title: '个人中心', name: 'my_home_index', component: () => import('@/views/home/myHome.vue') },
        { path: 'password', title: '修改密码', name: 'password', component: () => import('@/views/password/password.vue') },
        { path: 'add', title: '添加', name: 'add', component: () => import('@/views/template/add.vue') },
        { path: 'edit', title: '编辑', name: 'edit', component: () => import('@/views/template/edit.vue')},
        { path: 'course-lobby', title: 'Course Lobby', name: 'course_lobby', component: () => import('@/views/CourseLobby.vue') },
        { path: 'course/:id', title: 'Course Content', name: 'course_manage', component: () => import('@/views/course/index.vue') }

    ]
};

export const routers = [
    loginRouter,
    registRouter,
    otherRouter,
    page500,
    page403
];
