package cn.edu.hit.artman.mapper;

import cn.edu.hit.artman.pojo.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface UserMapper extends BaseMapper<User> {

    User getUserByUsername(String username);

    User getUserByEmail(String email);
}




