package cn.edu.hit.artman.service;

import cn.edu.hit.artman.pojo.po.User;
import cn.edu.hit.artman.pojo.vo.UserLoginVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {

    boolean register(String username, String email, String password);

    UserLoginVO loginByName(String username, String password);

    UserLoginVO loginByEmail(String email, String password);

    boolean logout(Long userId);

    boolean updatePassword(Long userId, String oldPassword, String newPassword);
}
