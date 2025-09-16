package com.luqi.weblog.web.markdown;

import com.luqi.weblog.web.markdown.renderer.ImageNodeRenderer;
import com.luqi.weblog.web.markdown.renderer.LinkNodeRenderer;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Arrays;
import java.util.List;

/**
 * @author: luqi
 * @url: www.luqi.com
 * @date: 2023/10/31 21:16
 * @description: Markdown Converter
 **/
public class MarkdownHelper {

    /**
     * Markdown Parser
     */
    private final static Parser PARSER;
    /**
     * HTML Renderer
     */
    private final static HtmlRenderer HTML_RENDERER;

    /**
     * Initialization
     */
    static {
        // Markdown Extensions
        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(), // Table Extension
                HeadingAnchorExtension.create(), // Heading Anchor Extension
                ImageAttributesExtension.create(), // Image Attributes
                TaskListItemsExtension.create() // Task List Extension
        );

        PARSER = Parser.builder().extensions(extensions).build();
        HTML_RENDERER = HtmlRenderer.builder()
                .extensions(extensions)
                .nodeRendererFactory(context -> new ImageNodeRenderer(context)) // Custom image rendering
                .nodeRendererFactory(context -> new LinkNodeRenderer(context)) // Custom link rendering
                .build();
    }


    /**
     * Convert Markdown to HTML
     * @param markdown
     * @return
     */
    public static String convertMarkdown2Html(String markdown) {
        Node document = PARSER.parse(markdown);
        return HTML_RENDERER.render(document);
    }

    public static void main(String[] args) {
        String markdown = "[http://www.luqi.com1](http://www.luqi.com \"http://www.luqi.com2\")";
        System.out.println(MarkdownHelper.convertMarkdown2Html(markdown));
    }

}
