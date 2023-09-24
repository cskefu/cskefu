package com.cskefu.security;

import com.cskefu.sys.SysUser;
import com.cskefu.sys.SysUserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 登录
 */
@RestController
@RequestMapping(value = "/auth")
public class LoginController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * 登录
     */
    @PostMapping(value = "/login", name = "登录")
    public void login(HttpServletResponse response, @Valid @RequestBody @Validated LoginRequest request) {
        sysUserService.login(response, request);
    }

    /**
     * 测试
     */
    @GetMapping(value = "/test", name = "测试")
    public UserDetails test() {
        return SysUser.builder().username("xxx").password("xxx").id(new BigDecimal("123")).build();
    }

    @GetMapping(value = {"", ""}, name = "")
    public List<SysUser> index() {
        return sysUserService.list();
    }
}
