package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.api.mapper.UserMapper;
import com.movie.api.model.dto.ForgotResetDto;
import com.movie.api.model.dto.ForgotSendCodeDto;
import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.User;
import com.movie.api.model.vo.ForgotSendCodeVO;
import com.movie.api.model.vo.PageResult;
import com.movie.api.service.ForgotPasswordService;
import com.movie.api.service.UserService;
import com.movie.api.utils.DataTimeUtil;
import com.movie.api.utils.ValidationUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private ForgotPasswordService forgotPasswordService;

    // 用户登录验证（校验用户名和密码）
    @Override
    public User login(LoginDto dto) throws Exception {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("username", dto.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) throw new Exception("用户名或密码错误");
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) throw new Exception("用户名或密码错误");
        return user;
    }

    // 查询所有用户
    @Override
    public List<User> findAll() {
        return userMapper.selectList(null);
    }

    // 分页查询用户，支持按用户名/昵称模糊搜索
    @Override
    public PageResult<User> findByPage(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword));
        }
        wrapper.orderByDesc(User::getCreateAt);
        Page<User> pageParam = new Page<>(page, size);
        Page<User> userPage = userMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(
                userPage.getTotal(),
                (int) userPage.getCurrent(),
                (int) userPage.getSize(),
                userPage.getRecords()
        );
    }

    // 根据ID查询用户
    @Override
    public User findById(String id) {
        return userMapper.selectById(id);
    }

    // 更新用户信息
    @Override
    public User update(User user) throws Exception {
        ValidationUtil.requireValidMobileCNIfPresent(user.getPhone(), "手机号");
        User existing = userMapper.selectById(user.getId());
        if (existing == null) {
            throw new Exception("用户不存在");
        }
        if (StringUtils.hasText(user.getPassword()) && !user.getPassword().startsWith("$2")) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }
        user.setUpdateAt(DataTimeUtil.getNowTimeString());
        userMapper.updateById(user);
        return userMapper.selectById(user.getId());
    }

    // 注册新用户
    @Override
    public User save(User user) throws Exception {
        ValidationUtil.requireValidMobileCN(user.getPhone(), "手机号");
        if (findByUsername(user.getUsername()) != null) {
            throw new Exception("用户名已注册");
        }
        String now = DataTimeUtil.getNowTimeString();
        user.setId(UUID.randomUUID().toString());
        user.setCreateAt(now);
        user.setUpdateAt(now);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        return user;
    }

    // 根据用户名查询用户
    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    // 根据ID删除用户
    @Override
    public User findByPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone.trim());
        return userMapper.selectOne(wrapper);
    }

    @Override
    public ForgotSendCodeVO sendForgotCode(ForgotSendCodeDto dto) throws Exception {
        User user = findByPhone(dto.getPhone());
        return forgotPasswordService.sendCode("user", dto, user != null);
    }

    @Override
    public void verifyForgotSms(ForgotResetDto dto) throws Exception {
        ValidationUtil.requireValidMobileCN(dto.getPhone(), "手机号");
        if (ValidationUtil.isBlank(dto.getSmsCode())) {
            throw new Exception("请输入短信验证码");
        }
        User user = findByPhone(dto.getPhone());
        if (user == null) {
            throw new Exception("该手机号未绑定账号");
        }
        if (!forgotPasswordService.verifySmsCode("user", dto.getPhone(), dto.getSmsCode())) {
            throw new Exception("短信验证码错误或已过期");
        }
    }

    @Override
    public void resetPasswordByPhone(ForgotResetDto dto) throws Exception {
        forgotPasswordService.validateResetForm(dto);
        User user = findByPhone(dto.getPhone());
        if (user == null) {
            throw new Exception("该手机号未绑定账号");
        }
        if (!forgotPasswordService.verifySmsCode("user", dto.getPhone(), dto.getSmsCode())) {
            throw new Exception("短信验证码错误或已过期");
        }
        user.setPassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
        user.setUpdateAt(DataTimeUtil.getNowTimeString());
        userMapper.updateById(user);
        forgotPasswordService.clearSmsCode("user", dto.getPhone());
    }

    @Override
    public void deleteById(String id) {
        userMapper.deleteById(id);
    }

}