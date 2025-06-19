import menuConfig from '@/libs/menuConfig.js';

// 模拟菜单API接口
export const getMenuList = () => {
    return new Promise((resolve) => {
        // 模拟API响应格式
        resolve({
            success: true,
            result: menuConfig,
            message: '获取菜单成功'
        });
    });
};

// 获取所有菜单列表（用于菜单管理页面）
export const getAllMenuList = () => {
    return new Promise((resolve) => {
        resolve({
            success: true,
            result: menuConfig,
            message: '获取所有菜单成功'
        });
    });
};

// 搜索菜单
export const searchMenu = (params) => {
    return new Promise((resolve) => {
        const { title } = params;
        let result = menuConfig;
        
        if (title) {
            // 简单的搜索逻辑
            result = searchMenuRecursive(menuConfig, title);
        }
        
        resolve({
            success: true,
            result: result,
            message: '搜索菜单成功'
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