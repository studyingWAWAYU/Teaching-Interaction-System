import { getRequest, postRequest, putRequest, postBodyRequest, getNoAuthRequest, postNoAuthRequest } from '@/libs/axios';
import { getAllMenuList, searchMenu } from '@/api/menuApi.js';

// 获取所有菜单列表
export const getAllPermissionList = () => {
    return getAllMenuList();
}

// 搜索权限
export const searchPermission = (params) => {
    return searchMenu(params);
}

// 添加权限（模拟，实际不操作数据库）
export const addPermission = (params) => {
    return new Promise((resolve) => {
        resolve({
            success: true,
            message: '菜单已添加（静态配置模式）'
        });
    });
}

// 编辑权限（模拟，实际不操作数据库）
export const editPermission = (params) => {
    return new Promise((resolve) => {
        resolve({
            success: true,
            message: '菜单已编辑（静态配置模式）'
        });
    });
}

// 删除权限（模拟，实际不操作数据库）
export const deletePermission = (params) => {
    return new Promise((resolve) => {
        resolve({
            success: true,
            message: '菜单已删除（静态配置模式）'
        });
    });
}

export const getDictDataByType = (type, params) => {
    return getRequest(`/dictData/getByType/${type}`, params)
}

export const getUserByPermission = (params) => {
    return new Promise((resolve) => {
        resolve({
            success: true,
            result: [],
            message: '静态配置模式，无用户权限数据'
        });
    });
}