package cn.wl.data.controller;

import cn.wl.basics.parameter.CommonConstant;
import cn.wl.basics.redis.RedisTemplateHelper;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.utils.SecurityUtil;
import cn.wl.basics.baseVo.Result;
import cn.wl.data.entity.*;
import cn.wl.data.service.*;
import cn.wl.data.utils.VoUtil;
import cn.wl.data.utils.WlNullUtils;
import cn.wl.data.vo.MenuVo;
import cn.wl.data.vo.UserByPermissionVo;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "菜单管理接口")
@RequestMapping("/wl/permission")
@CacheConfig(cacheNames = "permission")
@Transactional
public class PermissionController {

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private IRolePermissionService iRolePermissionService;

    @Autowired
    private IPermissionService iPermissionService;

    @Autowired
    private RedisTemplateHelper redisTemplateHelper;

    @Autowired
    private IRoleService iRoleService;

    @Autowired
    private IUserService iUserService;

    @ApiOperation(value = "查询菜单权限拥有者")
    @GetMapping("/getUserByPermission")
    public Result<List<UserByPermissionVo>> getUserByPermission(@RequestParam String permissionId) {
        Permission permission = iPermissionService.getById(permissionId);
        if (permission == null) {
            return ResultUtil.error("该菜单已被删除");
        }
        List<UserByPermissionVo> ansList = new ArrayList<>();
        QueryWrapper<RolePermission> qw = new QueryWrapper<>();
        qw.eq("permission_id", permissionId);
        List<RolePermission> rolePermissionList = iRolePermissionService.list(qw);
        for (RolePermission rp : rolePermissionList) {
            Role role = iRoleService.getById(rp.getRoleId());
            if (role != null) {
                QueryWrapper<User> userQw = new QueryWrapper<>();
                userQw.eq("role_id", role.getId());
                List<User> userList = iUserService.list(userQw);
                for (User user : userList) {
                    if (user != null) {
                        boolean flag = false;
                        for (UserByPermissionVo vo : ansList) {
                            if (Objects.equals(vo.getUserId(), user.getId())) {
                                flag = true;
                                vo.setRoleStr(vo.getRoleStr() + role.getName() + "(" + role.getDescription() + ") ");
                                break;
                            }
                        }
                        if (!flag) {
                            UserByPermissionVo vo = new UserByPermissionVo();
                            vo.setUserId(user.getId());
                            vo.setUserName(user.getNickname());
                            vo.setRoleStr(role.getName());
                            vo.setCode(user.getUsername());
                            vo.setMobile(user.getMobile());
                            ansList.add(vo);
                        }
                    }
                }
            }
        }
        return new ResultUtil<List<UserByPermissionVo>>().setData(ansList);
    }

    @ApiOperation(value = "根据层级查询菜单")
    private List<Permission> getPermissionListByLevel(int level) {
        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.eq("level", level);
        qw.orderByAsc("sort_order");
        return iPermissionService.list(qw);
    }

    @ApiOperation(value = "根据父ID查询菜单")
    private List<Permission> getPermissionListByParentId(Integer parentId) {
        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.eq("parent_id", parentId);
        qw.orderByAsc("sort_order");
        return iPermissionService.list(qw);
    }

    @ApiOperation(value = "查询菜单")
    @GetMapping("/getMenuList")
    public Result<List<MenuVo>> getMenuList() {
        List<MenuVo> menuList = new ArrayList<>();
        User currUser = securityUtil.getCurrUser();
        String keyInRedis = "permission::userMenuList:" + currUser.getId();
        String valueInRedis = redisTemplateHelper.get(keyInRedis);
        if (!WlNullUtils.isNull(valueInRedis)) {
            return new ResultUtil<List<MenuVo>>().setData(JSON.parseArray(valueInRedis, MenuVo.class));
        }

        List<Permission> list = getPermissionByUserId(currUser.getId());

        for (Permission permission : list) {
            if (CommonConstant.PERMISSION_NAV.equals(permission.getType()) && CommonConstant.LEVEL_ZERO.equals(permission.getLevel())) {
                menuList.add(VoUtil.permissionToMenuVo(permission));
            }
        }

        List<MenuVo> firstMenuList = new ArrayList<>();
        List<MenuVo> secondMenuList = new ArrayList<>();
        List<MenuVo> buttonPermissions = new ArrayList<>();

        for (Permission permission : list) {
            if (Objects.equals(CommonConstant.PERMISSION_PAGE, permission.getType())) {
                if (Objects.equals(CommonConstant.LEVEL_ONE, permission.getLevel())) {
                    firstMenuList.add(VoUtil.permissionToMenuVo(permission));
                } else if (Objects.equals(CommonConstant.LEVEL_TWO, permission.getLevel())) {
                    secondMenuList.add(VoUtil.permissionToMenuVo(permission));
                }
            } else if (Objects.equals(CommonConstant.PERMISSION_OPERATION, permission.getType()) && Objects.equals(CommonConstant.LEVEL_THREE, permission.getLevel())) {
                buttonPermissions.add(VoUtil.permissionToMenuVo(permission));
            }
        }

        for (MenuVo vo : secondMenuList) {
            List<String> permTypes = new ArrayList<>();
            for (MenuVo menuVo : buttonPermissions) {
                if (Objects.equals(vo.getId(), menuVo.getParentId())) {
                    permTypes.add(menuVo.getButtonType());
                }
            }
            vo.setPermTypes(permTypes);
        }

        for (MenuVo vo : firstMenuList) {
            List<MenuVo> secondMenu = new ArrayList<>();
            for (MenuVo menuVo : secondMenuList) {
                if (Objects.equals(vo.getId(), menuVo.getParentId())) {
                    secondMenu.add(menuVo);
                }
            }
            vo.setChildren(secondMenu);
        }

        for (MenuVo vo : menuList) {
            List<MenuVo> firstMenu = new ArrayList<>();
            for (MenuVo menuVo : firstMenuList) {
                if (Objects.equals(vo.getId(), menuVo.getParentId())) {
                    firstMenu.add(menuVo);
                }
            }
            vo.setChildren(firstMenu);
        }

        redisTemplateHelper.set(keyInRedis, JSONObject.toJSONString(menuList), 10L, TimeUnit.DAYS);
        return new ResultUtil<List<MenuVo>>().setData(menuList);
    }

    @ApiOperation(value = "搜索菜单")
    @GetMapping("/search")
    public Result<List<Permission>> searchPermissionList(@RequestParam String title) {
        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.like("title", title);
        qw.orderByAsc("sort_order");
        return new ResultUtil<List<Permission>>().setData(iPermissionService.list(qw));
    }

    @ApiOperation(value = "查询全部菜单")
    @GetMapping("/getAllList")
    @Cacheable(key = "'allList'")
    public Result<List<Permission>> getAllList() {
        List<Permission> list0 = getPermissionListByLevel(0);
        for (Permission p0 : list0) {
            List<Permission> list1 = getPermissionListByParentId(p0.getId());
            p0.setChildren(list1);
            for (Permission p1 : list1) {
                List<Permission> children1 = getPermissionListByParentId(p1.getId());
                p1.setChildren(children1);
                for (Permission p2 : children1) {
                    List<Permission> children2 = getPermissionListByParentId(p2.getId());
                    p2.setChildren(children2);
                }
            }
        }
        return new ResultUtil<List<Permission>>().setData(list0);
    }

    @ApiOperation(value = "删除菜单")
    @PostMapping("/delByIds")
    @CacheEvict(key = "'menuList'")
    public Result<Object> delByIds(@RequestParam String[] ids) {
        for (String id : ids) {
            QueryWrapper<RolePermission> qw = new QueryWrapper<>();
            qw.eq("permission_id", id);
            long count = iRolePermissionService.count(qw);
            if (count > 0L) {
                Permission permission = iPermissionService.getById(id);
                if (permission == null) {
                    return ResultUtil.error("菜单不存在");
                }
                return ResultUtil.error(permission.getTitle() + "菜单正在被角色使用，不能删除");
            }
        }
        for (String id : ids) {
            iPermissionService.removeById(id);
        }
        redisTemplateHelper.delete("permission::allList");
        return ResultUtil.success();
    }

    @ApiOperation(value = "添加菜单")
    @PostMapping("/add")
    @CacheEvict(key = "'menuList'")
    public Result<Permission> add(Permission permission) {
        if (Objects.equals(CommonConstant.PERMISSION_OPERATION, permission.getType())) {
            QueryWrapper<Permission> qw = new QueryWrapper<>();
            qw.eq("title", permission.getTitle());
            if (iPermissionService.count(qw) > 0L) {
                return new ResultUtil<Permission>().setErrorMsg("名称已存在");
            }
        }
        if (Objects.equals(CommonConstant.PERMISSION_NAV, permission.getType())) {
            permission.setParentId(0);
            if (WlNullUtils.isNull(permission.getPath())) {
                permission.setPath(permission.getName());
            }
            permission.setDescription("");
            permission.setComponent("");
        }
        iPermissionService.saveOrUpdate(permission);
        redisTemplateHelper.delete("permission::allList");
        return new ResultUtil<Permission>().setData(permission);
    }

    @ApiOperation(value = "编辑菜单")
    @PostMapping("/edit")
    public Result<Permission> edit(Permission permission) {
        if (Objects.equals(CommonConstant.PERMISSION_OPERATION, permission.getType())) {
            Permission p = iPermissionService.getById(permission.getId());
            if (!Objects.equals(p.getTitle(), permission.getTitle())) {
                QueryWrapper<Permission> qw = new QueryWrapper<>();
                qw.eq("title", permission.getTitle());
                if (iPermissionService.count(qw) > 0L) {
                    return new ResultUtil<Permission>().setErrorMsg("名称已存在");
                }
            }
        }
        iPermissionService.saveOrUpdate(permission);
        redisTemplateHelper.delete(redisTemplateHelper.keys("user:*"));
        redisTemplateHelper.delete(redisTemplateHelper.keys("permission::userMenuList:*"));
        redisTemplateHelper.delete("permission::allList");
        return new ResultUtil<Permission>().setData(permission);
    }

    private List<Permission> getPermissionByUserId(Integer userId) {
        User user = iUserService.getById(userId);
        if (user == null || user.getRoleId() == null) {
            return new ArrayList<>();
        }
        QueryWrapper<RolePermission> rpQw = new QueryWrapper<>();
        rpQw.eq("role_id", user.getRoleId());
        List<RolePermission> rolePermissionList = iRolePermissionService.list(rpQw);
        List<Permission> permissionList = new ArrayList<>();
        for (RolePermission rolePermission : rolePermissionList) {
            Permission p = iPermissionService.getById(rolePermission.getPermissionId());
            if (p != null && permissionList.stream().noneMatch(pp -> pp.getId().equals(p.getId()))) {
                permissionList.add(p);
            }
        }
        return permissionList;
    }

    @ApiOperation(value = "获取当前用户权限标识列表")
    @GetMapping("/getUserPermissionCodes")
    public Result<List<String>> getUserPermissionCodes() {
        User currUser = securityUtil.getCurrUser();
        List<Permission> permissionList = getPermissionByUserId(currUser.getId());

        List<String> permissionCodes = new ArrayList<>();
        for (Permission p : permissionList) {
            if (!WlNullUtils.isNull(p.getName())) {
                permissionCodes.add(p.getName());
            }
        }

        return new ResultUtil<List<String>>().setData(permissionCodes);
    }
}

