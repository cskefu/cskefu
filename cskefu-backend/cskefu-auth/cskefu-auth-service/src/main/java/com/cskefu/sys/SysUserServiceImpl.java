package com.cskefu.sys;

import com.cskefu.mvc.HttpResponseUtils;
import com.cskefu.security.JWTTokenUtils;
import com.cskefu.security.LoginRequest;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public void login(HttpServletResponse response, LoginRequest login) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(login.getUsername());
            if (userDetails != null && passwordEncoder.matches(login.getPassword(), userDetails.getPassword())) {
                response.setHeader(HttpHeaders.AUTHORIZATION, JWTTokenUtils.generateToken(userDetails));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                HttpResponseUtils.unauthorizedResponse(response, "用户名或密码错误！");
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            try {
                HttpResponseUtils.unauthorizedResponse(response, "登录失败！");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}