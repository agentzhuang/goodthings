package com.goodthings.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.goodthings.common.BizException;
import com.goodthings.common.Result;
import com.goodthings.dto.LoginDTO;
import com.goodthings.dto.RegisterDTO;
import com.goodthings.entity.SysUser;
import com.goodthings.mapper.SysUserMapper;
import com.goodthings.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        // 检查用户名是否存在
        SysUser existUser = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, dto.getUsername())
        );
        if (existUser != null) {
            throw new BizException("用户名已存在");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setStatus(1);
        sysUserMapper.insert(user);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        return Result.success("注册成功", data);
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BizException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        return Result.success("登录成功", data);
    }

    @GetMapping("/me")
    public Result<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromHeader(authHeader);
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/me")
    public Result<?> updateCurrentUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SysUser updateData) {
        Long userId = getUserIdFromHeader(authHeader);
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (updateData.getNickname() != null) user.setNickname(updateData.getNickname());
        if (updateData.getAvatar() != null) user.setAvatar(updateData.getAvatar());
        if (updateData.getEmail() != null) user.setEmail(updateData.getEmail());
        sysUserMapper.updateById(user);
        return Result.success("更新成功");
    }

    private Long getUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }
        String token = authHeader.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }
}
