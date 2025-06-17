package cn.wl.data.controller;

import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.utils.SecurityUtil;
import cn.wl.data.entity.Permission;
import cn.wl.data.entity.User;
import cn.wl.data.service.IPermissionService;
import cn.wl.data.service.IUserService;
import cn.wl.data.utils.WlNullUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@RestController
@Api(tags = "个人门户接口")
@RequestMapping("/wl/myDoor")
@Transactional
public class MyDoorController {

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private IPermissionService iPermissionService;

    @Autowired
    private IUserService iUserService;

    @ApiOperation(value = "查询个人门户菜单A")
    @RequestMapping(value = "/getMyDoorList", method = RequestMethod.POST)
    public Result<List<MyDoorMenuClass>> getMyDoorList(){
        User user = securityUtil.getCurrUser();
        user = iUserService.getById(user.getId());
        List<MyDoorMenuClass> ans = new ArrayList<>();
        String myDoor = user.getMyDoor();
        if(WlNullUtils.isNull(myDoor)) {
            return new ResultUtil().setData(ans);
        }
        String[] WLs = myDoor.split("WL");
        List<Permission> all = iPermissionService.list();
        for (String wl : WLs) {
            for (Permission permission : all) {
                if(Objects.equals(permission.getName(),wl)) {
                    MyDoorMenuClass menu = new MyDoorMenuClass();
                    menu.setName(permission.getName());
                    menu.setTitle(permission.getTitle());
                    ans.add(menu);
                    break;
                }
            }
        }
        return new ResultUtil().setData(ans);
    }

    @ApiOperation(value = "获取个人门户菜单B")
    @RequestMapping(value = "/getMyDoorList6", method = RequestMethod.POST)
    public Result<List<MyDoorMenuClass>> getMyDoorList6(){
        User currentUser = securityUtil.getCurrUser();
        Integer userId = currentUser.getId();
        User user = iUserService.getById(userId);

        List<MyDoorMenuClass> ans = new ArrayList<>();
        String myDoor = user.getMyDoor();
        if(WlNullUtils.isNull(myDoor)) {
            ans.add(getNullMenu());ans.add(getNullMenu());ans.add(getNullMenu());
            ans.add(getNullMenu());ans.add(getNullMenu());ans.add(getNullMenu());
            return new ResultUtil().setData(ans);
        }
        String[] WLs = myDoor.split("WL");
        List<Permission> all = iPermissionService.list();
        for (String wl : WLs) {
            for (Permission permission : all) {
                if(Objects.equals(permission.getName(),wl)) {
                    MyDoorMenuClass menu = new MyDoorMenuClass();
                    menu.setName(permission.getName());
                    menu.setTitle(permission.getTitle());
                    ans.add(menu);
                    break;
                }
            }
        }
        int size = ans.size();
        if(size < 6) {
            int time = 6 - size;
            for(int i = 0 ; i < time; i ++) {
                ans.add(getNullMenu());
            }
        }
        return new ResultUtil().setData(ans);
    }

    @ApiOperation(value = "修改个人门户菜单")
    @RequestMapping(value = "/setMyDoorList", method = RequestMethod.POST)
    public Result<Object> setMyDoorList(@RequestParam String str){
        User user = securityUtil.getCurrUser();
        user = iUserService.getById(user.getId());
        if(user != null) {
            if(WlNullUtils.isNull(str)) {
                user.setMyDoor("");
                iUserService.saveOrUpdate(user);
            } else {
                user.setMyDoor(str);
                iUserService.saveOrUpdate(user);
            }
            return ResultUtil.success("OK");
        }
        return ResultUtil.error("ROSTER IS NULL");
    }

    private MyDoorMenuClass getNullMenu() {
        MyDoorMenuClass menu = new MyDoorMenuClass();
        menu.setName("null");
        menu.setTitle("empty");
        return menu;
    }

    @Data
    private class MyDoorMenuClass {
        private String name;
        private String title;
    }
}
