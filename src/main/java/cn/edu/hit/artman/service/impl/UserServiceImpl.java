package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.common.exception.ArtManException;
import cn.edu.hit.artman.pojo.vo.UserLoginVO;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.hit.artman.pojo.po.User;
import cn.edu.hit.artman.service.UserService;
import cn.edu.hit.artman.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.net.HttpURLConnection.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private final UserMapper userMapper;

    @Override
    public boolean register(String username, String email, String password) {

        // 检查用户名是否已存在
        User existingUserByUsername = userMapper.getUserByUsername(username);
        if (existingUserByUsername != null) {
            throw new ArtManException(HTTP_BAD_REQUEST, "用户名已存在");
        }

        // 检查邮箱是否已存在
        User existingUserByEmail = userMapper.getUserByEmail(email);
        if (existingUserByEmail != null) {
            throw new ArtManException(HTTP_BAD_REQUEST, "邮箱已存在");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(password); // 密码先不加密

        return userMapper.insert(newUser) > 0;
    }

    @Override
    public UserLoginVO loginByName(String username, String password) {
        User user = userMapper.getUserByUsername(username);
        return login(user, password);
    }

    @Override
    public UserLoginVO loginByEmail(String email, String password) {
        User user = userMapper.getUserByEmail(email);
        return login(user, password);
    }

    @Override
    public boolean logout(Long userId) {
        // 需要销毁用户的刷新令牌
        // 这里暂时不处理token，直接返回成功
        return true;
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {

        // 查找用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "用户不存在");
        }

        // 验证旧密码是否正确
        if (!user.getPasswordHash().equals(oldPassword)) {
            throw new ArtManException(HTTP_BAD_REQUEST, "旧密码错误");
        }

        // 比较新旧密码是否相同
        if (oldPassword.equals(newPassword)) {
            throw new ArtManException(HTTP_BAD_REQUEST, "新密码不能与旧密码相同");
        }

        // 更新密码
        user.setPasswordHash(newPassword); // 密码先不加密

        return userMapper.updateById(user) > 0;
    }

    private UserLoginVO login(User user, String password) {
        // 验证用户是否注册
        if (user == null) {
            throw new ArtManException(HTTP_BAD_REQUEST, "用户未注册");
        }

        // 验证密码是否正确
        if (!user.getPasswordHash().equals(password)) {
            throw new ArtManException(HTTP_BAD_REQUEST, "密码错误");
        }

        UserLoginVO userLoginVO = BeanUtil.copyProperties(user, UserLoginVO.class);

        // 生成token（这里可以使用JWT或其他方式）
        // 这里暂时不处理token，直接返回用户信息
        userLoginVO.setAccessToken("未实现的访问令牌");
        userLoginVO.setRefreshToken("未实现的刷新令牌");

        return userLoginVO;
    }
}




