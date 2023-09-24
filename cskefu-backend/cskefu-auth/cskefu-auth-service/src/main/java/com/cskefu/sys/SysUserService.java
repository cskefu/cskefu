package com.cskefu.sys;

import com.cskefu.security.LoginRequest;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

public interface SysUserService extends IService<SysUser> {
    void login(HttpServletResponse response, LoginRequest login);
}
