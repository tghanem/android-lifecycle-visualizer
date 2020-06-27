package impl;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Helper {
    public static String getExceptionInformation(Exception exception) {
        StringBuilder sb = new StringBuilder();

        sb.append(exception.getClass().toString() + ": " + exception.getMessage());
        sb.append(System.lineSeparator());

        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        sb.append(sw.toString());

        return sb.toString();
    }
}
