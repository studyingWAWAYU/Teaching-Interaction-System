import { getRequest, postRequest, getNoAuthRequest } from '@/libs/axios';

// 获取单个作业答案
export const getAssignmentAnsOne = (params) => {
    return getNoAuthRequest('/assignment_ans/getOne', params);
};

// 获取作业答案总数
export const getAssignmentAnsCount = () => {
    return getNoAuthRequest('/assignment_ans/count');
};

// 获取所有作业答案
export const getAllAssignmentAns = () => {
    return getNoAuthRequest('/assignment_ans/getAll');
};

// 分页获取作业答案
export const getAssignmentAnsByPage = (params) => {
    return getNoAuthRequest('/assignment_ans/getByPage', params);
};

// 增改作业答案
export const saveOrUpdateAssignmentAns = (params) => {
    return postRequest('/assignment_ans/insertOrUpdate', params);
};

// 新增作业答案
export const addAssignmentAns = (params) => {
    return postRequest('/assignment_ans/insert', params);
};

// 更新作业答案
export const updateAssignmentAns = (params) => {
    return postRequest('/assignment_ans/update', params);
};

// 删除作业答案
export const deleteAssignmentAns = (params) => {
    return postRequest('/assignment_ans/delByIds', params);
};