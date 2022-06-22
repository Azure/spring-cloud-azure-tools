package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.configuration.DependencyProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.client.RestTemplate;


public class SpringBootReleaseNotesReader {

    private final RestTemplate restTemplate;
    private final DependencyProperties dependencyProperties;
    static final String CONTRIBUTORS_INFO = "<h2> <g-emoji class=\"g-emoji\" alias=\"heart\" "
        + "fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/2764.png\">  ❤️ </g-emoji> "
        + "Contributors.*";

    public SpringBootReleaseNotesReader(RestTemplate restTemplate,
                                        DependencyProperties dependencyProperties) {
        this.restTemplate = restTemplate;
        this.dependencyProperties = dependencyProperties;
    }

    public String getUrl(String version) {
        return dependencyProperties.getRelease().getReleaseNotesUrl() + version;
    }

    public String getReleaseNotes(String version) {
        String url = getUrl(version);
        String htmlContents = restTemplate.getForObject(url, String.class);
        Document doc = Jsoup.parse(htmlContents);
        String releaseNotes = doc.getElementsByClass("markdown-body my-3").html();
        releaseNotes = releaseNotes.replaceAll("\n", "")
                                   .replaceAll(CONTRIBUTORS_INFO, "")
                                   .replaceAll("<h2>", "<h4>")
                                   .replaceAll("</h2>", "</h4>");
        String finalContents = String.format("<details><summary>Release notes</summary><p><em>Sourced from <a "
            + "href='%s'>spring-boot releases</a>.</em></p>", url) + releaseNotes + "</details>";
        return finalContents;
    }

}
