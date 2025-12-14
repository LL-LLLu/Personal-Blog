package com.luqi.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luqi.weblog.common.domain.dos.CommentDO;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @description: Comment Mapper
 **/
public interface CommentMapper extends BaseMapper<CommentDO> {

    /**
     * Query comments by router URL and status
     * @param routerUrl
     * @param status
     * @return
     */
    default List<CommentDO> selectByRouterUrlAndStatus(String routerUrl, Integer status) {
        return selectList(Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getRouterUrl, routerUrl) // Filter by router URL
                .eq(CommentDO::getStatus, status) // Filter by status
                .orderByDesc(CommentDO::getCreateTime) // Order by create time descending
        );
    }

    /**
     * Paginated query for comments
     * @param current
     * @param size
     * @param routerUrl
     * @param startDate
     * @param endDate
     * @param status
     * @return
     */
    default Page<CommentDO> selectPageList(Long current, Long size, String routerUrl,
                                           LocalDate startDate, LocalDate endDate, Integer status) {
        // Pagination object (which page, how many records per page)
        Page<CommentDO> page = new Page<>(current, size);

        // Build query conditions
        LambdaQueryWrapper<CommentDO> wrapper = Wrappers.<CommentDO>lambdaQuery()
                .like(StringUtils.isNotBlank(routerUrl), CommentDO::getRouterUrl, routerUrl) // like fuzzy query
                .eq(Objects.nonNull(status), CommentDO::getStatus, status) // Comment status
                .ge(Objects.nonNull(startDate), CommentDO::getCreateTime, startDate) // Greater than or equal to startDate
                .le(Objects.nonNull(endDate), CommentDO::getCreateTime, endDate)  // Less than or equal to endDate
                .orderByDesc(CommentDO::getCreateTime); // Order by create time descending

        return selectPage(page, wrapper);
    }

    /**
     * Query comments by reply comment ID
     * @param replyCommentId
     * @return
     */
    default List<CommentDO> selectByReplyCommentId(Long replyCommentId) {
        return selectList(Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getReplyCommentId, replyCommentId)
                .orderByDesc(CommentDO::getCreateTime)
        );
    }

    /**
     * Delete comments by parent comment ID
     * @param id
     * @return
     */
    default int deleteByParentCommentId(Long id) {
        return delete(Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getParentCommentId, id));
    }

}
