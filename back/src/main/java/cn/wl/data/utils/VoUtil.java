package cn.wl.data.utils;

import cn.wl.data.entity.Permission;
import cn.wl.data.vo.MenuVo;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 
@Api(tags = "菜单转换VO类")
public class VoUtil {

    @ApiOperation(value = "菜单转换VO类转换")
    public static MenuVo permissionToMenuVo(Permission permission){
        MenuVo vo = new MenuVo();
        BeanUtil.copyProperties(permission, vo);
        return vo;
    }
}
