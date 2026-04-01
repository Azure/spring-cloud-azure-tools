package com.azure.spring.reference.intellij.links;

public class MsDocsLink implements Link {

    private final String path;

    public MsDocsLink(String path) {
        this.path = path;
    }

        @Override
    public String getName() {
        return "msdocs";
    }

    @Override
    public String getPath() {
        return path;
    }
}
