package cn.wl.data.vo;

import cn.wl.data.entity.User;
import lombok.Data;

@Data
public class RegistDTO extends User {

    private Integer identity;
}
