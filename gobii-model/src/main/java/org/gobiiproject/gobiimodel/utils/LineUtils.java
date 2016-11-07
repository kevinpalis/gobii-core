package org.gobiiproject.gobiimodel.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Phil on 4/4/2016.
 */
public class LineUtils {

    public static String wrapLine(String message) {

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        return "[" + now + "] " + message;
    }

    public static boolean isNullOrEmpty(String value) {

        return (null == value || value.isEmpty());
    }

    public static String terminateDirectoryPath(String path, char terminationCharacter) {

        String returnVal = path;

        if (path != null) {
            if (path.charAt(path.length() - 1) != terminationCharacter) {
                returnVal += terminationCharacter;
            }
        }

        return returnVal;
    }

}
