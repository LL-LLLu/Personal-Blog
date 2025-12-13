package com.luqi.weblog.admin.controller;

import com.luqi.weblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.luqi.weblog.admin.model.vo.comment.FindCommentPageListReqVO;
import com.luqi.weblog.admin.service.AdminCommentService;
import com.luqi.weblog.common.aspect.ApiOperationLog;
import com.luqi.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: Admin Comment Module Controller
 **/
@RestController
@RequestMapping("/admin/comment")
@Api(tags = "Admin Comment Module")
public class AdminCommentController {

    @Autowired
    private AdminCommentService commentService;

    @PostMapping("/list")
    @ApiOperation(value = "Query comment page list")
    @ApiOperationLog(description = "Query comment page list")
    public Response findCommentPageList(@RequestBody @Validated FindCommentPageListReqVO findCommentPageListReqVO) {
        return commentService.findCommentPageList(findCommentPageListReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "Delete comment")
    @ApiOperationLog(description = "Delete comment")
    public Response deleteComment(@RequestBody @Validated DeleteCommentReqVO deleteCommentReqVO) {
        return commentService.deleteComment(deleteCommentReqVO);
    }

}
