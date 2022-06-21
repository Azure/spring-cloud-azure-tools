package com.azure.spring.dev.tools.dependency.support;

import com.azure.spring.dev.tools.dependency.configuration.DependencyProperties;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;


public class SpringBootReleaseNotesReader {

    private final DependencyProperties dependencyProperties;

    public SpringBootReleaseNotesReader(DependencyProperties dependencyProperties) {
        this.dependencyProperties = dependencyProperties;
    }

    public String getReleaseNotes(String version) {
        String url = dependencyProperties.getRelease().getReleaseNotesUrl() + version;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20,TimeUnit.SECONDS)
            .build();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        String htmlContents = null;
        try {
            Response response = call.execute();
            htmlContents = response.body().string();
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                throw new IllegalStateException("Timed out");
            }
            if (e instanceof ConnectException) {
                throw new IllegalStateException("Failed to connection");
            }
        }
        Document doc = Jsoup.parse(htmlContents);
        return doc.getElementsByClass("markdown-body my-3").toString();
    }

}
