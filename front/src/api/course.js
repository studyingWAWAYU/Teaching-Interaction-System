import { getRequest, postRequest } from '@/libs/axios';

// 获取单个课程
export const getCourse = (params) => {
    return getRequest('/course/getOne', params);
};

// 获取课程总数
export const getCourseCount = () => {
    return getRequest('/course/count');
};

// 获取所有课程
export const getAllCourses = () => {
    return getRequest('/course/getAll');
};

// 分页获取课程
export const getCoursesByPage = (params) => {
    return getRequest('/course/getByPage', params);
};

// 增改课程
export const saveOrUpdateCourse = (params) => {
    return postRequest('/course/insertOrUpdate', params);
};

// 新增课程
export const addCourse = (params) => {
    return postRequest('/course/insert', params);
};

// 更新课程
export const updateCourse = (params) => {
    return postRequest('/course/update', params);
};

// 删除课程
export const deleteCourses = (params) => {
    return postRequest('/course/delByIds', params);
}; 