package com.luqi.weblog.web.service.impl;

import com.google.common.collect.Lists;
import com.luqi.weblog.common.utils.PageResponse;
import com.luqi.weblog.common.utils.Response;
import com.luqi.weblog.search.LuceneHelper;
import com.luqi.weblog.search.config.LuceneProperties;
import com.luqi.weblog.search.index.ArticleIndex;
import com.luqi.weblog.web.model.vo.search.SearchArticlePageListReqVO;
import com.luqi.weblog.web.model.vo.search.SearchArticlePageListRspVO;
import com.luqi.weblog.web.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import com.luqi.weblog.search.analyzer.MultilingualAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.StringReader;
import java.util.List;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private LuceneProperties luceneProperties;
    @Autowired
    private LuceneHelper luceneHelper;

    @Override
    public Response searchArticlePageList(SearchArticlePageListReqVO searchArticlePageListReqVO) {
        int current = Math.toIntExact(searchArticlePageListReqVO.getCurrent());
        int size = Math.toIntExact(searchArticlePageListReqVO.getSize());

        // Query keyword
        String word = searchArticlePageListReqVO.getWord();

        // Fields to search (specifies searching article title and summary, any field containing the keyword will be found)
        String[] columns = {ArticleIndex.COLUMN_TITLE, ArticleIndex.COLUMN_SUMMARY};

        // Get search parameters
        Boolean fuzzySearch = searchArticlePageListReqVO.getFuzzySearch();
        Integer maxEdits = searchArticlePageListReqVO.getMaxEdits();
        Boolean wildcardSearch = searchArticlePageListReqVO.getWildcardSearch();
        
        // Use default values if not specified
        if (fuzzySearch == null) {
            fuzzySearch = false;
        }
        if (maxEdits == null) {
            maxEdits = 2;
        }
        if (wildcardSearch == null) {
            wildcardSearch = false;
        }

        // Query total record count
        long total;
        if (wildcardSearch) {
            total = luceneHelper.searchWildcardTotal(ArticleIndex.NAME, word, columns);
        } else if (fuzzySearch) {
            total = luceneHelper.searchFuzzyTotal(ArticleIndex.NAME, word, columns, maxEdits);
        } else {
            total = luceneHelper.searchTotal(ArticleIndex.NAME, word, columns);
        }

        // Execute search (paginated query)
        List<Document> documents = luceneHelper.search(ArticleIndex.NAME, word, columns, current, size, fuzzySearch, maxEdits, wildcardSearch);

        // If no relevant documents found, return directly
        if (CollectionUtils.isEmpty(documents)) {
            return PageResponse.success(total, current, size, null);
        }

        // ======================== Start keyword highlighting ========================
        // Multilingual analyzer (supports Chinese and English)
        MultilingualAnalyzer multilingualAnalyzer = new MultilingualAnalyzer();
        // Select appropriate analyzer for highlighting based on search term
        Analyzer analyzer = multilingualAnalyzer.selectAnalyzer(word);
        QueryParser parser = new QueryParser(ArticleIndex.COLUMN_TITLE, analyzer);
        Query query = null;
        try {
            query = parser.parse(word);
        } catch (ParseException e) {
            log.error("Error parsing keywords:", e);
        }

        // Create highlighter
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"color: #f73131\">", "</span>");
        Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));

        // Response VOs
        List<SearchArticlePageListRspVO> vos = Lists.newArrayList();
        // Iterate through found documents for keyword highlighting
        documents.forEach(document -> {
            try {
                // Article title
                String title = document.get(ArticleIndex.COLUMN_TITLE);

                // Get highlighted fragment
                TokenStream tokenStream = analyzer.tokenStream(ArticleIndex.COLUMN_TITLE, new StringReader(title));
                String titleFragment = highlighter.getBestFragment(tokenStream, title);

                // If no keyword matched, return original text
                String highlightedTitle = StringUtils.isNoneBlank(titleFragment) ? titleFragment : title;

                String id = document.get(ArticleIndex.COLUMN_ID);
                String cover = document.get(ArticleIndex.COLUMN_COVER);
                String createTime = document.get(ArticleIndex.COLUMN_CREATE_TIME);
                String summary = document.get(ArticleIndex.COLUMN_SUMMARY);

                // Assemble VO
                SearchArticlePageListRspVO vo = SearchArticlePageListRspVO.builder()
                        .id(Long.valueOf(id))
                        .title(highlightedTitle)
                        .summary(summary)
                        .cover(cover)
                        .createDate(createTime)
                        .build();

                vos.add(vo);
            } catch (Exception e) {
                log.error("Document conversion error: ", e);
            }
        });

        // Close analyzer resources
        try {
            analyzer.close();
            multilingualAnalyzer.close();
        } catch (Exception e) {
            log.warn("Exception occurred when closing analyzer resources: ", e);
        }

        return PageResponse.success(total, current, size, vos);
    }
}