package com.luqi.weblog.jwt.handler;

import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.jwt.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: lu qi
 * @url: www.luqi.com
 * @date: TODO
 * @description: Login successfully 登录成功访问收保护的资源，但是权限不够
 **/
@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("Login successful to access protected resource, but do not have sufficient permission", accessDeniedException);
        // 预留，后面引入多角色时会用到
        // for now, there is only ADMIN, this role as access to everything. Later can expand on more roles.
        ResultUtil.fail(response, Response.fail(ResponseCodeEnum.FORBIDDEN));
    }
}
