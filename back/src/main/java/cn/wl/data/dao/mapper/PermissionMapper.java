package cn.wl.data.dao.mapper;

import cn.wl.data.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据用户ID查询该用户角色所拥有的所有权限（按钮权限）
     */
    @Select("SELECT DISTINCT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN user u ON u.role_id = rp.role_id " +
            "WHERE u.id = #{userId} AND p.status = 1 AND p.button_type = 3")
    List<Permission> getUserPermissions(@Param("userId") String userId);
}
