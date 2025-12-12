package com.luqi.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.luqi.weblog.common.domain.dos.WikiDO;
import com.luqi.weblog.common.domain.mapper.WikiMapper;
import com.luqi.weblog.common.enums.ResponseCodeEnum;
import com.luqi.weblog.common.exception.BizException;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.web.model.vo.wiki.FindWikiDetailReqVO;
import com.luqi.weblog.web.model.vo.wiki.FindWikiDetailRspVO;
import com.luqi.weblog.web.service.WikiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class WikiServiceImpl implements WikiService {

    @Autowired
    private WikiMapper wikiMapper;

    @Override
    public Response findAllWiki() {
        // Find all published wikis
        List<WikiDO> wikiDOS = wikiMapper.selectList(new LambdaQueryWrapper<WikiDO>()
                .eq(WikiDO::getIsPublish, true)
                .orderByDesc(WikiDO::getWeight)
                .orderByDesc(WikiDO::getCreateTime));
        return Response.success(wikiDOS);
    }

    @Override
    public Response findWikiDetail(FindWikiDetailReqVO findWikiDetailReqVO) {
        Long id = findWikiDetailReqVO.getId();
        WikiDO wikiDO = wikiMapper.selectById(id);

        if (Objects.isNull(wikiDO) || !wikiDO.getIsPublish()) {
            throw new BizException(ResponseCodeEnum.WIKI_NOT_FOUND);
        }

        FindWikiDetailRspVO vo = FindWikiDetailRspVO.builder()
                .id(wikiDO.getId())
                .title(wikiDO.getTitle())
                .cover(wikiDO.getCover())
                .summary(wikiDO.getSummary())
                .build();
        return Response.success(vo);
    }
}