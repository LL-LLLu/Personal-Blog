package com.luqi.weblog.web.markdown.renderer;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.Objects;
import java.util.Set;

public class LinkNodeRenderer implements NodeRenderer {

    private final HtmlWriter html;

    /**
     * Website domain (change to your own domain when going live)
     */
    private final static String DOMAIN = "www.luqi.com";


    public LinkNodeRenderer(HtmlNodeRendererContext context) {
        this.html = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        // Specify the node type to custom render, here we specify hyperlink Link
        return Sets.newHashSet(Link.class);
    }

    @Override
    public void render(Node node) {
        if (node instanceof Link) {
            Link link = (Link) node;

            // Link content
            String linkDescription = null;
            if (Objects.nonNull(link.getFirstChild())) {
                Text text = (Text) link.getFirstChild();
                linkDescription = text.getLiteral();
            }

            // Link URL
            String linkUrl = link.getDestination();
            // Link title
            String linkTitle = link.getTitle();

            // Build HTML structure
            StringBuilder sb = new StringBuilder("<a href=\"");
            sb.append(linkUrl);
            sb.append("\"");

            // Add title="link title" attribute
            if (StringUtils.isNotBlank(linkTitle)) {
                sb.append(String.format(" title=\"%s\"", linkTitle));
            }

            // If the link is not from our domain, add rel="nofollow" attribute
            if (!linkUrl.contains(DOMAIN)) {
                sb.append(" rel=\"nofollow\"");
            }

            // Add target="_blank" attribute
            sb.append(" target=\"_blank\">");
            // Hyperlink display content
            sb.append(StringUtils.isNotBlank(linkDescription) ? linkDescription : linkUrl);
            sb.append("</a>");

            // Add SVG icon
            String svg = "<span><svg xmlns=\"http://www.w3.org/2000/svg\" class=\"inline ml-1\" style=\"color: #aaa;\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\" class=\"icon outbound\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path> <polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg> <span class=\"sr-only\"></span></span>";
            sb.append(svg);

            // Set HTML content
            html.raw(sb.toString());
        }
    }
}