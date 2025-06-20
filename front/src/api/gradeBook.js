import { getRequest, postRequest, getNoAuthRequest } from '@/libs/axios';

// 获取单个成绩
export const getGradeBook = (params) => {
    return getNoAuthRequest('/gradeBook/getOne', params);
};

// 获取成绩总数
export const getGradeBookCount = () => {
    return getNoAuthRequest('/gradeBook/count');
};

// 获取所有成绩
export const getAllGradeBooks = () => {
    return getNoAuthRequest('/gradeBook/getAll');
};

// 分页获取成绩
export const getGradeBooksByPage = (params) => {
    return getNoAuthRequest('/gradeBook/getByPage', params);
};

// 增改成绩
export const saveOrUpdateGradeBook = (params) => {
    return postRequest('/gradeBook/insertOrUpdate', params);
};

// 新增成绩
export const addGradeBook = (params) => {
    return postRequest('/gradeBook/insert', params);
};

// 更新成绩
export const updateGradeBook = (params) => {
    return postRequest('/gradeBook/update', params);
};

// 删除成绩
export const deleteGradeBooks = (params) => {
    return postRequest('/gradeBook/delByIds', params);
}; 