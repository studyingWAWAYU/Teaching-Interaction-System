package cn.wl.basics.security;

import cn.wl.basics.parameter.CommonConstant;
import cn.wl.data.entity.User;
import cn.wl.data.utils.WlNullUtils;
import cn.wl.data.vo.PermissionDTO;
import cn.wl.data.vo.RoleDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@ApiOperation(value = "查询用户的角色和菜单权限")
public class SecurityUserDetails extends User implements UserDetails {

    private static final long serialVersionUID = 1L;

    //private List<RoleDTO> roles;
    private RoleDTO role;
    private List<PermissionDTO> permissions;

    @Override
    @ApiOperation(value = "查询用户的角色和菜单权限")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        // 菜单权限
        if(permissions!=null && !permissions.isEmpty()){
            for (PermissionDTO dto : permissions) {
                if(!WlNullUtils.isNull(dto.getTitle()) && !WlNullUtils.isNull(dto.getPath())) {
                    grantedAuthorityList.add(new SimpleGrantedAuthority(dto.getTitle()));
                }
            }
        }
        // 角色
        if (role != null && !WlNullUtils.isNull(role.getName())) {
            grantedAuthorityList.add(new SimpleGrantedAuthority(role.getName()));
        }
        /*
        if(role != null && roles.size() > 0){
            roles.forEach(role -> {
                if(!WlNullUtils.isNull(role.getName())){
                    grantedAuthorityList.add(new SimpleGrantedAuthority(role.getName()));
                }
            });
        }

         */
        return grantedAuthorityList;
    }

    @Override
    @ApiOperation(value = "账号是否启用")
    public boolean isEnabled() {
        //return Objects.equals(CommonConstant.USER_STATUS_NORMAL,this.getStatus());
        return true;
    }

    @ApiOperation(value = "账号是否过期")
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @ApiOperation(value = "账号密码是否过期")
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @ApiOperation(value = "账号是否禁用")
    public boolean isAccountNonLocked() {
        //return !Objects.equals(CommonConstant.USER_STATUS_LOCK, this.getStatus());
        return true;
    }

    /**
     * 自定义类构造器
     * @param user 系统账户
     */
    public SecurityUserDetails(User user) {
        if(user != null) {
            //this.setUsername(user.getUsername());
            //this.setPassword(user.getPassword());
            this.permissions  = user.getPermissions();
            //this.roles = user.getRoles();
            this.setId(user.getId());
            this.setUsername(user.getUsername());
            this.setPassword(user.getPassword());
            this.setEmail(user.getEmail());
            this.setMobile(user.getMobile());
            this.setRoleId(user.getRoleId());
            this.setRole(user.getRole());
            this.setPermissions(user.getPermissions());
            this.setNumber(user.getNumber());
            this.setSex(user.getSex());
            this.setNickname(user.getNickname());
            this.setAvatar(user.getAvatar());
            this.setDepartmentId(user.getDepartmentId());
            this.setMyDoor(user.getMyDoor());
            this.setCreate_time(user.getCreate_time());
        }
    }
}