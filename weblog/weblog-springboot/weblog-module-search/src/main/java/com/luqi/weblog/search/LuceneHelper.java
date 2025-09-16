package com.luqi.weblog.search;

import com.google.common.collect.Lists;
import com.luqi.weblog.search.config.LuceneProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import com.luqi.weblog.search.analyzer.MultilingualAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j
public class LuceneHelper {

    @Autowired
    private LuceneProperties properties;

    /**
     * Create index
     * @param index Index name
     * @param documents Documents
     */
    public void createIndex(String index, List<Document> documents) {
        try {
            String indexDir = properties.getIndexDir() + File.separator + index;
            File dir = new File(indexDir);

            // Check if index directory exists
            if (dir.exists()) {
                // Clean directory content
                FileUtils.cleanDirectory(dir);
            } else {
                // Create directory if not exists
                FileUtils.forceMkdir(dir);
            }

            // Open index directory
            Directory directory = FSDirectory.open(Paths.get(indexDir));

            // Multilingual analyzer (supports Chinese and English)
            Analyzer analyzer = new MultilingualAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            // Create index
            IndexWriter writer = new IndexWriter(directory, config);

            // Add documents
            documents.forEach(document -> {
                try {
                    writer.addDocument(document);
                } catch (IOException e) {
                    log.error("Error adding Lucene document: ", e);
                }
            });

            // Commit
            writer.commit();
            writer.close();
        } catch (Exception e) {
            log.error("Failed to create Lucene index: ", e);
        }
    }

    /**
     * Keyword search, query total data amount
     * @param index Index name
     * @param word Query keyword
     * @param columns Fields to search
     * @return
     */
    public long searchTotal(String index, String word, String[] columns) {
        try {
            String indexDir = properties.getIndexDir() + File.separator + index;
            // Open index directory
            Directory directory = FSDirectory.open(Paths.get(indexDir));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            // Multilingual analyzer (supports Chinese and English)
            Analyzer analyzer = new MultilingualAnalyzer();
            // Query parser
            QueryParser parser = new MultiFieldQueryParser(columns, analyzer);
            // Parse query keywords
            Query query = parser.parse(word);

            // Search documents
            TopDocs totalDocs  = searcher.search(query, Integer.MAX_VALUE);
            // Return document count
            return totalDocs.totalHits.value;
        } catch (Exception e) {
            log.error("Lucene query error: ", e);
            return 0;
        }
    }

    /**
     * Keyword search
     * @param index Index name
     * @param word Query keyword
     * @param columns Fields to search
     * @param current Current page
     * @param size Page size
     * @return
     */
    public List<Document> search(String index, String word, String[] columns, int current, int size) {
        return search(index, word, columns, current, size, false, 2);
    }

    /**
     * Keyword search (supports fuzzy and wildcard search)
     * @param index Index name
     * @param word Query keyword
     * @param columns Fields to search
     * @param current Current page
     * @param size Page size
     * @param fuzzySearch Enable fuzzy search
     * @param maxEdits Max edit distance for fuzzy search (0-2)
     * @return
     */
    public List<Document> search(String index, String word, String[] columns, int current, int size, boolean fuzzySearch, int maxEdits) {
        return search(index, word, columns, current, size, fuzzySearch, maxEdits, false);
    }

    /**
     * Keyword search (supports fuzzy and wildcard search)
     * @param index Index name
     * @param word Query keyword
     * @param columns Fields to search
     * @param current Current page
     * @param size Page size
     * @param fuzzySearch Enable fuzzy search
     * @param maxEdits Max edit distance for fuzzy search (0-2)
     * @param wildcardSearch Enable wildcard search
     * @return
     */
    public List<Document> search(String index, String word, String[] columns, int current, int size, boolean fuzzySearch, int maxEdits, boolean wildcardSearch) {
        try {
            String indexDir = properties.getIndexDir() + File.separator + index;
            // Open index directory
            Directory directory = FSDirectory.open(Paths.get(indexDir));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            Query query;
            if (wildcardSearch) {
                // Build wildcard query
                query = buildWildcardQuery(word, columns);
            } else if (fuzzySearch) {
                // Build fuzzy query
                query = buildFuzzyQuery(word, columns, maxEdits);
            } else {
                // Multilingual analyzer (supports Chinese and English)
                Analyzer analyzer = new MultilingualAnalyzer();
                // Query parser
                QueryParser parser = new MultiFieldQueryParser(columns, analyzer);
                // Parse query keywords
                query = parser.parse(word);
            }

            // Execute search, get first limit matching results
            int limit = current * size;
            TopDocs topDocs = searcher.search(query, limit); // Search first limit results

            // Array of matching documents
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            // Calculate pagination start - end position
            int start = (current - 1) * size;
            int end = Math.min(start + size, scoreDocs.length);

            // Return documents for specified page
            List<Document> documents = Lists.newArrayList();
            for (int i = start; i < end; i++) {
                Document doc = searcher.doc(scoreDocs[i].doc);
                documents.add(doc);
            }

            // Release resources
            reader.close();
            return documents;
        } catch (Exception e) {
            log.error("Lucene query error: ", e);
            return null;
        }
    }

    /**
     * Fuzzy query total data amount
     * @param index Index name
     * @param word Query keyword
     * @param columns Fields to search
     * @param maxEdits Max edit distance
     * @return
     */
    public long searchFuzzyTotal(String index, String word, String[] columns, int maxEdits) {
        try {
            String indexDir = properties.getIndexDir() + File.separator + index;
            // Open index directory
            Directory directory = FSDirectory.open(Paths.get(indexDir));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            // Build fuzzy query
            Query query = buildFuzzyQuery(word, columns, maxEdits);

            // Search documents
            TopDocs totalDocs = searcher.search(query, Integer.MAX_VALUE);
            // Release resources
            reader.close();
            // Return document count
            return totalDocs.totalHits.value;
        } catch (Exception e) {
            log.error("Fuzzy query Lucene error: ", e);
            return 0;
        }
    }

    /**
     * Wildcard query total data amount
     * @param index Index name
     * @param word Query keyword
     * @param columns Fields to search
     * @return
     */
    public long searchWildcardTotal(String index, String word, String[] columns) {
        try {
            String indexDir = properties.getIndexDir() + File.separator + index;
            // Open index directory
            Directory directory = FSDirectory.open(Paths.get(indexDir));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            // Build wildcard query
            Query query = buildWildcardQuery(word, columns);

            // Search documents
            TopDocs totalDocs = searcher.search(query, Integer.MAX_VALUE);
            // Release resources
            reader.close();
            // Return document count
            return totalDocs.totalHits.value;
        } catch (Exception e) {
            log.error("Wildcard query Lucene error: ", e);
            return 0;
        }
    }

    /**
     * Build fuzzy query
     * @param word Query keyword
     * @param columns Search fields
     * @param maxEdits Maximum edit distance
     * @return
     */
    private Query buildFuzzyQuery(String word, String[] columns, int maxEdits) {
        // Limit edit distance between 0-2
        maxEdits = Math.max(0, Math.min(2, maxEdits));
        
        // Split query terms
        String[] terms = word.trim().split("\\s+");
        
        if (terms.length == 1) {
            // Fuzzy query for single term
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            for (String column : columns) {
                FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(column, terms[0]), maxEdits);
                builder.add(fuzzyQuery, BooleanClause.Occur.SHOULD);
            }
            return builder.build();
        } else {
            // Combined query for multiple terms
            BooleanQuery.Builder mainBuilder = new BooleanQuery.Builder();
            
            for (String term : terms) {
                BooleanQuery.Builder termBuilder = new BooleanQuery.Builder();
                for (String column : columns) {
                    // Each term can match in any field
                    FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(column, term), maxEdits);
                    termBuilder.add(fuzzyQuery, BooleanClause.Occur.SHOULD);
                }
                // All terms must match
                mainBuilder.add(termBuilder.build(), BooleanClause.Occur.MUST);
            }
            
            return mainBuilder.build();
        }
    }

    /**
     * Build wildcard query
     * @param word Query keyword
     * @param columns Search fields
     * @return
     */
    private Query buildWildcardQuery(String word, String[] columns) {
        // Split query terms
        String[] terms = word.trim().split("\\s+");
        
        if (terms.length == 1) {
            // Wildcard query for single term
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            String wildcardTerm = preprocessWildcardTerm(terms[0]);
            
            for (String column : columns) {
                WildcardQuery wildcardQuery = new WildcardQuery(new Term(column, wildcardTerm));
                builder.add(wildcardQuery, BooleanClause.Occur.SHOULD);
            }
            return builder.build();
        } else {
            // Combined wildcard query for multiple terms
            BooleanQuery.Builder mainBuilder = new BooleanQuery.Builder();
            
            for (String term : terms) {
                BooleanQuery.Builder termBuilder = new BooleanQuery.Builder();
                String wildcardTerm = preprocessWildcardTerm(term);
                
                for (String column : columns) {
                    // Each term can match in any field
                    WildcardQuery wildcardQuery = new WildcardQuery(new Term(column, wildcardTerm));
                    termBuilder.add(wildcardQuery, BooleanClause.Occur.SHOULD);
                }
                // All terms must match
                mainBuilder.add(termBuilder.build(), BooleanClause.Occur.MUST);
            }
            
            return mainBuilder.build();
        }
    }

    /**
     * Preprocess wildcard query term
     * @param term Original query term
     * @return Processed wildcard query term
     */
    private String preprocessWildcardTerm(String term) {
        // If user didn't input wildcards, automatically add prefix and suffix wildcards
        if (!term.contains("*") && !term.contains("?")) {
            return "*" + term.toLowerCase() + "*";
        }
        
        // If wildcards are already included, return directly (convert to lowercase to improve match rate)
        return term.toLowerCase();
    }

    /**
     * Add document
     * @param index Index name
     * @param document New document
     * @return Number of documents affected
     */
    public long addDocument(String index, Document document) {
        try {
            String indexDir = properties.getIndexDir() + File.separator + index;

            // Open index storage directory
            Directory dir = FSDirectory.open(Paths.get(indexDir));

            // Configure IndexWriter
            Analyzer analyzer = new MultilingualAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(dir, config);

            // Add document
            long count = writer.addDocument(document);

            writer.commit();
            writer.close();

            return count;
        } catch (Exception e) {
            log.error("Failed to add Lucene document: ", e);
            return 0;
        }
    }

    /**
     * Delete document
     * @param index Index name
     * @param condition Delete condition
     * @return Number of documents affected
     */
    public long deleteDocument(String index, Term condition) {
        try {
            String indexDir = properties.getIndexDir() + File.separator + index;

            // Open index directory
            Directory directory = FSDirectory.open(Paths.get(indexDir));

            // Configure IndexWriter
            IndexWriterConfig config = new IndexWriterConfig(new MultilingualAnalyzer());
            IndexWriter writer = new IndexWriter(directory, config);

            // Delete documents
            long count = writer.deleteDocuments(condition);

            writer.commit();
            writer.close();

            return count;
        } catch (Exception e) {
            log.error("Error deleting Lucene document: ", e);
            return 0;
        }
    }

    /**
     * Update document
     * @param index Index name
     * @param document Document
     * @param condition Condition
     * @return Number of documents affected
     */
    public long updateDocument(String index, Document document, Term condition) {
        try {
            String indexDir = properties.getIndexDir() + File.separator + index;

            // Open index directory
            Directory directory = FSDirectory.open(Paths.get(indexDir));

            // Configure IndexWriter
            MultilingualAnalyzer analyzer = new MultilingualAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(directory, config);

            // Update document
            long count = writer.updateDocument(condition, document);

            // Commit changes
            writer.commit();
            writer.close();

            return count;
        } catch (Exception e) {
            log.error("Error updating Lucene document: ", e);
            return 0;
        }
    }
}