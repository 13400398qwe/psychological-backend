package com.example.scupsychological.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scupsychological.pojo.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author tpj
 * @since 2025-06-21
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    boolean selectByUsernameXml(@Param("username") String  username);

    void updateByUsernamexml(Users user);
}
