package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.constant.MessageConstant;
import com.example.scupsychological.common.exception.*;
import com.example.scupsychological.mapper.UsersMapper;
import com.example.scupsychological.pojo.FileStorageService;
import com.example.scupsychological.pojo.dto.ChangePasswordDTO;
import com.example.scupsychological.pojo.dto.LoginDTO;
import com.example.scupsychological.pojo.dto.UserCreateRequestDto;
import com.example.scupsychological.pojo.dto.UserUpdateRequestDto;
import com.example.scupsychological.pojo.entity.Users;
import com.example.scupsychological.pojo.vo.LoginResponseVO;
import com.example.scupsychological.pojo.vo.UserVO;
import com.example.scupsychological.service.UserService;
import com.example.scupsychological.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final  UsersMapper usersMapper;
    private final JwtUtil jwtUtil;
    private final RedisTemplate redisTemplate;
    private final FileStorageService fileStorageService;
    @Override
    public LoginResponseVO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("用户登录：{}", loginDTO);

        Users user = usersMapper.selectOne(new QueryWrapper<Users>().eq("username", username));
        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if (!user.getPassword().equals(password)) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        if (user.getIsDeleted()) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_DELETED);
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        LoginResponseVO loginResponseVO = getLoginResponseVO(token, user);
        log.info("用户{}登录成功，生成token：{}", username, token);
        return loginResponseVO;
    }

    private static LoginResponseVO getLoginResponseVO(String token, Users user) {
        LoginResponseVO loginResponseVO = new LoginResponseVO();
        loginResponseVO.setToken(token);
        LoginResponseVO.UserInfoVO userInfoVO = new LoginResponseVO.UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setName(user.getName());
        userInfoVO.setRole(user.getRole());
        userInfoVO.setGender(user.getGender());
        userInfoVO.setPhone(user.getPhone());
        userInfoVO.setAvatar(user.getAvatar());
        loginResponseVO.setUserInfo(userInfoVO);//设置用户信息
        return loginResponseVO;
    }
    @Override
    public void logout(String token) {
        // 1. 解析 token
        Claims claims = jwtUtil.validateAndParseToken(token);
        if (claims != null) {
            // 2. 获取 token 的唯一标识和过期时间
            Date expiration = claims.getExpiration();
            long ttl = expiration.getTime() - System.currentTimeMillis();
            redisTemplate.opsForValue().set("jwt:blacklist:" + token, 1, ttl, TimeUnit.MILLISECONDS);
        }
    }
    @Override
    @Transactional // 建议加上事务注解
    public UserVO createUser(UserCreateRequestDto createDto) {
        // 1. 检查用户名是否已存在
        if (usersMapper.exists(new QueryWrapper<Users>().eq("username", createDto.getUsername()))) {
            throw new UserNameExitsException();
        }
        Users newUser = new Users();
        if(usersMapper.selectByUsernameXml(createDto.getUsername()))
        {
            BeanUtils.copyProperties(createDto, newUser);
            newUser.setIsDeleted(false);
            newUser.setPassword(DigestUtils.md5DigestAsHex(createDto.getPassword().getBytes()));
            usersMapper.updateByUsernamexml(newUser);
        }
        else {      // 2. 创建 User 实体对象
            BeanUtils.copyProperties(createDto, newUser);
            newUser.setIsDeleted(false);// 未删除
            // 3. 对密码进行 BCrypt 加密
            newUser.setPassword(DigestUtils.md5DigestAsHex(createDto.getPassword().getBytes()));
            // 4. 将用户数据存入数据库
            usersMapper.insert(newUser);
        }
        // 5. 将新创建的 User 对象转换为 UserVO (不含密码) 并返回
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(newUser, userVO); // 使用 Spring 的工具类进行属性复制
        return userVO;
    }
    @Override
    @Transactional
    public void deleteUser(Long id) {
        usersMapper.deleteById(id);
    }
    @Override
    @Transactional
    public UserVO updateUser(Long id, UserUpdateRequestDto userUpdateDto) {
        Users user = usersMapper.selectById(id);
        if (user == null) {
            throw new AccountNotFoundException();
        }
        BeanUtils.copyProperties(userUpdateDto, user);
        if(userUpdateDto.getPassword() != null)
            user.setPassword(DigestUtils.md5DigestAsHex(userUpdateDto.getPassword().getBytes()));
        usersMapper.updateById(user);
        UserVO userVo =new UserVO();
        BeanUtils.copyProperties(user, userVo);
        userVo.setId(id);
        return userVo;
    }
    @Override
    @Transactional
    public List<UserVO> listUsers() {
        List<Users> usersList = usersMapper.selectList(null);
        List<UserVO> userVOList = new ArrayList<>();
        for (Users user : usersList) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userVOList.add(userVO);
        }
        return userVOList;
    }
    @Override
    @Transactional
    public Page<UserVO> listUsersByPage(long pageNum, long pageSize, String username) {
        // 1. 创建 MyBatis-Plus 的分页对象
        Page<Users> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like("username", username);
        }
        wrapper.orderByDesc("created_at"); // 按创建时间降序排序

        // 3. 执行分页查询
        // MP 会自动将查询结果和分页信息填充到 page 对象中
        usersMapper.selectPage(page, wrapper);

        // 4. 将 Page<User> 转换为 Page<UserVO>，以避免泄露密码等敏感信息
        Page<UserVO> userVoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<UserVO> userVoList = page.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
        userVoPage.setRecords(userVoList);

        return userVoPage;
    }


    @Transactional
    @Override
    public String updateAvatar(Long userId, MultipartFile file) {
        // 1. 文件校验 (大小、类型等)
        if (file.isEmpty() || file.getSize() > 5 * 1024 * 1024) { // 限制5MB
            throw new BaseException("文件为空或过大");
        }
        try {
            // 2. 调用文件服务上传文件，获取 URL
            String avatarUrl = fileStorageService.uploadAvatar(file, userId);
            // 3. 更新数据库中用户的 avatar 字段
            Users user = usersMapper.selectById(userId);
            if (user == null) {
                throw new BaseException("用户不存在");
            }
            user.setAvatar(avatarUrl);
            usersMapper.updateById(user);
            // 4. 返回新的头像 URL
            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("头像上传失败", e);
        }
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        // 1. 验证旧密码
        Users user = usersMapper.selectById(userId);
        if (!DigestUtils.md5DigestAsHex(changePasswordDTO.getOldPassword().getBytes()).equals(user.getPassword())) {
            throw new BaseException("旧密码错误");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(changePasswordDTO.getNewPassword().getBytes()));
       usersMapper.updateById( user);
    }

    @Override
    public UserVO getUserById(Long id) {
        Users user = usersMapper.selectById(id);
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
