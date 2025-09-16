package com.luqi.weblog.common.enums;

import com.luqi.weblog.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: lu qi
 * @url: www.luqi.com
 * @date: TODO
 * @description: Response to exception code
 **/
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "Something is wrong, we are trying to fix it..."),
    PARAM_NOT_VALID("10001", "parameter invalid"),

    // ----------- 业务异常状态码 -----------
    PRODUCT_NOT_FOUND("20000", "This product does not exist..."),
    LOGIN_FAIL("20000", "Login Failure"),
    USERNAME_OR_PWD_ERROR("20001", "Username or Password Incorrect"),
    UNAUTHORIZED("20002", "NO Access Authentication, Please login FIRST!"),
    FORBIDDEN("20004", "Demo Account only allow for searching operation"),
    USERNAME_NOT_FOUND("20003", "This User Does not Exist"),
    CATEGORY_NAME_IS_EXISTED("20005", "Category already existed, please do not add replicas"),
    TAG_CANT_DUPLICATE("20006", "Please do not add existing tags!"),
    TAG_NOT_EXISTED("20007", "Tag does not exist!"),
    FILE_UPLOAD_FAILED("20008", "File Upload Failed!"),
    CATEGORY_NOT_EXISTED("20009", " Category does not Exist!"),
    ARTICLE_NOT_FOUND("20010", "Article does not exist!"),
    CATEGORY_CAN_NOT_DELETE("20011", "This category contains articles. Please delete the corresponding articles before deleting the category!"),
    TAG_CAN_NOT_DELETE("20012", "This tag contains articles. Please delete the corresponding articles before deleting the tag!"),
    ;

    // Exception code for errors
    private String errorCode;
    // 错误信息
    private String errorMessage;

}
