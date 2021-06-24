package org.gobiiproject.gobiimodel.utils;

import org.apache.commons.lang.StringUtils;

public class URLUtils {

    public static final String pathSeparator = "/";

    public static String stripStartAndEndPathSeparator(String url) {
        return StringUtils.stripEnd(StringUtils.stripStart(url, pathSeparator), pathSeparator);
    }
}
