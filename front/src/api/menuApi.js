import { menuConfig, filterMenuByRole } from '@/libs/menuConfig.js';

function getRoleId() {
    let role_id = null;
    try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'));
        role_id = userInfo && userInfo.role_id !== undefined ? userInfo.role_id : null;
    } catch (e) {
        role_id = null;
    }
    if (role_id === null) {
        role_id = Number(localStorage.getItem('role_id'));
    }
    return role_id;
}

export const getMenuList = () => {
    return new Promise((resolve) => {
        const role_id = getRoleId();
        const filteredMenu = filterMenuByRole(menuConfig, role_id);
        resolve({
            success: true,
            result: filteredMenu,
            message: 'Load menu successfully'
        });
    });
};

export const getAllMenuList = () => {
    return new Promise((resolve) => {
        resolve({
            success: true,
            result: menuConfig,
            message: 'Load menu successfully'
        });
    });
};

// 搜索菜单
export const searchMenu = (params) => {
    return new Promise((resolve) => {
        const { title } = params;
        let result = menuConfig;
        
        if (title) {
            result = searchMenuRecursive(menuConfig, title);
        }
        
        resolve({
            success: true,
            result: result,
            message: 'Get menu successfully'
        });
    });
};

// 递归搜索菜单
function searchMenuRecursive(menus, keyword) {
    let result = [];
    
    menus.forEach(menu => {
        if (menu.title && menu.title.includes(keyword)) {
            result.push(menu);
        }
        if (menu.children && menu.children.length > 0) {
            const childResult = searchMenuRecursive(menu.children, keyword);
            result = result.concat(childResult);
        }
    });
    
    return result;
}

export default {
    getMenuList,
    getAllMenuList,
    searchMenu
}; 