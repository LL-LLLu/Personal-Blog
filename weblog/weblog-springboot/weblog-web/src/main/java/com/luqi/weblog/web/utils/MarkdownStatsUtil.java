package com.luqi.weblog.web.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownStatsUtil {

    // Words read per minute, assumed to be 300 words (adjust according to actual situation)
    private static final int WORDS_READ_PRE_MINUTE = 300;

    /**
     * Calculate markdown word count
     * @param markdown
     * @return
     */
    public static int calculateWordCount(String markdown) {
        // Replace consecutive whitespace characters (including spaces, tabs, newlines, etc.) with a single space
        String cleanText = markdown.replaceAll("\\s+", " ");
        // Use regex to match Chinese characters
        Pattern chinesePattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher chineseMatcher = chinesePattern.matcher(cleanText);

        // Calculate Chinese character count
        int chineseCount = 0;
        while (chineseMatcher.find()) {
            chineseCount++;
        }

        // Remove Chinese characters
        String textWithoutChinese = cleanText.replaceAll("[\\u4e00-\\u9fa5]", "");

        if (StringUtils.isBlank(textWithoutChinese)) return chineseCount;

        // Split text into words using spaces
        String[] words = textWithoutChinese.trim().split("\\s+");
        return chineseCount + words.length;
    }

    /**
     * Calculate reading time
     * @param wordCount
     * @return
     */
    public static String calculateReadingTime(int wordCount) {
        // Check if total word count is greater than one minute's reading word count
        if (wordCount >= WORDS_READ_PRE_MINUTE) {
            // Calculate minutes required
            int minutes = wordCount / WORDS_READ_PRE_MINUTE;
            return "About " + minutes + " min";
        }

        // If total word count is less than one minute's reading word count, calculate in seconds
        int seconds = (wordCount % WORDS_READ_PRE_MINUTE) * 60 / WORDS_READ_PRE_MINUTE;

        // Minimum reading time is 1 second
        if (seconds == 0) seconds = 1;

        return "About " + seconds + " sec";
    }
}