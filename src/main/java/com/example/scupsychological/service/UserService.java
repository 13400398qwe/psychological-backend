package com.example.scupsychological.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.ChangePasswordDTO;
import com.example.scupsychological.pojo.dto.LoginDTO;
import com.example.scupsychological.pojo.dto.UserCreateRequestDto;
import com.example.scupsychological.pojo.dto.UserUpdateRequestDto;
import com.example.scupsychological.pojo.vo.LoginResponseVO;
import com.example.scupsychological.pojo.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author tpj
 * @since 2025-06-21
 */
public interface UserService {

    LoginResponseVO login(LoginDTO loginDTO);
    void logout(String token);
    //添加用户
    UserVO createUser(UserCreateRequestDto userCreateDto);

    void deleteUser(Long id);

    UserVO updateUser(Long id, UserUpdateRequestDto  updateDto);

    List<UserVO> listUsers();

    Page<UserVO> listUsersByPage(long page, long size, String username);

    @Transactional
    String updateAvatar(Long userId, MultipartFile file);

    void changePassword(Long userId, @Valid ChangePasswordDTO changePasswordDTO);

    UserVO getUserById(Long id);
}
