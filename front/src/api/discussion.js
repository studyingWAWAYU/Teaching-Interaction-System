import { getRequest, postRequest, getNoAuthRequest } from '@/libs/axios';

// 获取单个主题
export const getTopics = (courseId,params) => {
    return getNoAuthRequest(`/course/${courseId}/topics/getOne`, params);
};

// 获取主题总数
export const getTopicsCount = (courseId) => {
    return getNoAuthRequest(`/course/${courseId}/topics/count`);
};

// 获取所有主题
export const getAllTopics = (courseId) => {
    return getNoAuthRequest(`/course/${courseId}/topics/getAll`);
};

// 根据likes降序排序获取所有主题
export const getAllTopicsSorted = (courseId) => {
    return getNoAuthRequest(`/course/${courseId}/topics/getAll/sorted_by_likes`,params);
};

// 增改主题
export const saveOrUpdateTopics = (courseId,params) => {
    return postRequest(`/course/${courseId}/topics/insertOrUpdate`, params);
};

// 新增主题
export const addTopics = (courseId,params) => {
    return postRequest(`/course/${courseId}/topics/insert`, params);
};

// 更新主题
export const updateTopics = (courseId,params) => {
    return postRequest(`/course/${courseId}/topics/update`, params);
};

// 删除课程
export const deleteTopics = (courseId,params) => {
    return postRequest(`/course/${courseId}/topics/delByIds`, params);
};