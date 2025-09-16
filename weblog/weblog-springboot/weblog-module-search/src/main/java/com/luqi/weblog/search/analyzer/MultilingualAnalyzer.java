package com.luqi.weblog.search.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 多语言分析器，支持中英文混合搜索
 * 自动检测文本语言并使用相应的分析器
 */
public class MultilingualAnalyzer extends Analyzer {
    
    private final SmartChineseAnalyzer chineseAnalyzer;
    private final StandardAnalyzer englishAnalyzer;
    private final Pattern chinesePattern = Pattern.compile("[\\u4e00-\\u9fa5]");
    
    public MultilingualAnalyzer() {
        this.chineseAnalyzer = new SmartChineseAnalyzer();
        this.englishAnalyzer = new StandardAnalyzer();
    }
    
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        // 使用标准分词器作为基础
        StandardTokenizer tokenizer = new StandardTokenizer();
        
        // 添加小写过滤器和使用英文分析器的停用词
        TokenStream tokenStream = new LowerCaseFilter(tokenizer);
        tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
    
    /**
     * 检测文本是否包含中文字符
     * @param text 待检测文本
     * @return true表示包含中文，false表示不包含
     */
    public boolean containsChinese(String text) {
        if (text == null) {
            return false;
        }
        return chinesePattern.matcher(text).find();
    }
    
    /**
     * 根据文本内容选择合适的分析器
     * @param text 待分析文本
     * @return 合适的分析器
     */
    public Analyzer selectAnalyzer(String text) {
        if (containsChinese(text)) {
            return chineseAnalyzer;
        } else {
            return englishAnalyzer;
        }
    }
    
    /**
     * 创建用于不同字段的分析器包装器
     * 可以为不同字段指定不同的分析策略
     * @return PerFieldAnalyzerWrapper
     */
    public static PerFieldAnalyzerWrapper createPerFieldAnalyzer() {
        Map<String, Analyzer> analyzerMap = new HashMap<>();
        
        // 默认使用多语言分析器
        Analyzer defaultAnalyzer = new MultilingualAnalyzer();
        
        // 可以为特定字段指定特殊的分析器
        // analyzerMap.put("title", new SmartChineseAnalyzer());
        // analyzerMap.put("summary", new StandardAnalyzer());
        
        return new PerFieldAnalyzerWrapper(defaultAnalyzer, analyzerMap);
    }
    
    @Override
    public void close() {
        super.close();
        chineseAnalyzer.close();
        englishAnalyzer.close();
    }
}