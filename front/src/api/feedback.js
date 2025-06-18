import { getRequest, postRequest, getNoAuthRequest } from '@/libs/axios';

// 添加课程评价
export const addFeedback = (params) => {
    return getRequest('/feedback/addOne', params);
};

// 获取单条课程评价
export const getFeedback = (params) => {
    return getNoAuthRequest('/feedback/getOne', params);
};

// 获取课程评价总数
export const getFeedbackCount = () => {
    return getNoAuthRequest('/feedback/count');
};

// 获取所有课程评价
export const getAllFeedbacks = () => {
    return getNoAuthRequest('/feedback/getAll');
};

// 分页获取课程评价
export const getFeedbacksByPage = (params) => {
    return getNoAuthRequest('/feedback/getByPage', params);
};

// 增改课程评价
export const saveOrUpdateFeedback = (params) => {
    return postRequest('/feedback/insertOrUpdate', params);
};

// 新增课程评价
export const insertFeedback = (params) => {
    return postRequest('/feedback/insert', params);
};

// 更新课程评价
export const updateFeedback = (params) => {
    return postRequest('/feedback/update', params);
};

// 删除课程评价
export const deleteFeedbacks = (params) => {
    return postRequest('/feedback/delByIds', params);
};