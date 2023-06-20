package com.zenyte.database;

import lombok.val;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DatabaseUtil {

    private static final Logger log = LogManager.getLogger(DatabaseUtil.class);

    public static String buildBatch(final String query, final int rows, final int columns) {
        val column = buildColumnStructure(columns);
        val sb = new StringBuilder((column.length() + 2) * (rows - 1) + query.length());

        sb.append(query);

        for(int index=1; index < rows; index++)
			sb.append(", " + column);

        return sb.toString();
    }

    public static String buildColumnStructure(final int columns) {
        val sb = new StringBuilder(3 * (columns - 1) + 1);

        for(int index=1; index <= columns; index++)
			sb.append(index == columns ? "?" : "?, ");

        return "( "+sb.toString()+" )";
    }

}
