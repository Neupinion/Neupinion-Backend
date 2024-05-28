package com.neupinion.neupinion.article.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TagInfo {
    private final String tag;
    private final String className;
    private final String idName;

    public static TagInfo ofByClass(String tag, String className) {
        return new TagInfo(tag, className, null);
    }

    public static TagInfo ofById(final String tag, final String idName) {
        return new TagInfo(tag, null, idName);
    }

    public boolean isClass() {
        return className != null;
    }

    public boolean isId() {
        return idName != null;
    }
}
