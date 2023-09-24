package com.cskefu.security;

import com.cskefu.sys.SysUser;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String tokenHeader = "Authorization";
    public static final String headerPrefix = "Bearer ";
    @Resource
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //1:获取请求头中的token
        String bearerToken = request.getHeader(tokenHeader);
        //2：判断token是否存在
        if (StringUtils.hasText(bearerToken)) {
            //根据token获取用户名
            String token = bearerToken;
            if (bearerToken.startsWith(headerPrefix)) {
                token = bearerToken.substring(7);
            }
            SysUser sysUser = JWTTokenUtils.userInfo(token);
            //3：token存在但是security里面没有登录信息，代表有token但是没登录
            if (null == SecurityContextHolder.getContext().getAuthentication()) {
                //没有登录信息，直接登录
                UserDetails userDetails = userDetailsService.loadUserByUsername(sysUser.getUsername());
                //判断token是否有效,token没有过期，并且和userdetail中的username一样，那么就将security中的登录信息进行刷新
                if (sysUser.getUsername().equals(userDetails.getUsername())) {
                    //刷新security中的用户信息
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        chain.doFilter(request, response);
    }
}