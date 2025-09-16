package com.luqi.weblog.jwt.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author: lu qi
 * @url: www.luqi.com
 * @date: TODO
 * @description: User Login
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRspVO {

    /**
     * Token 值
     */
    private String token;

}
