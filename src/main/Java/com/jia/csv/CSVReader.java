package com.jia.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * Assumptions:
 * - Field names in the first line are unique
 * - Column order is not important
 * - Row order is not important
 * - Quoted strings are double-quoted, not single-quoted
 * - Quoted strings, either for field names or values, are stored in the map with quotes stripped while leading and trailing spaces are preserved
 * 
 * Tested with 
 * - CVSReaderUnitTest.java
 * - wacky.csv: contains some wacky cases (quotes, commas and leading/trailing spaces in field names and values, empty lines, missing cells)
 * - HMEQ.csv: supplied by assigner
 * 
 * Time spent on solution: 3/18/2024 7-9 pm; 3/19/2024 from 9-11 pm.
 * 
 */
final public class CSVReader {
    private static final String COMMA_DELIMITER = ",";
    private static final Pattern COMMA_PATTERN = Pattern.compile(COMMA_DELIMITER); // precompile for repeated use
    private static final String COMMA_IGNORE_QUOTED = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final Pattern COMMA_IGNORE_QUOTED_PATTERN = Pattern.compile(COMMA_IGNORE_QUOTED); // precompile for repeated use
    private static final int SERIAL_READ = 0;
    private static final int PARALLEL_READ = 1;

    private String[] names;

    public void read(String filePath, int method) throws IOException {
        File file = new File(filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            // read the header line
            String nameLine = reader.readLine();
            names = split(nameLine);
            names = Arrays.stream(names).map(n -> parseString(n)).toArray(size -> new String[size]);

            // read and print the value lines
            // No need to return anything because we are done printing
            switch (method) {
            case SERIAL_READ:
                reader.lines().map(lineToMap).forEach(System.out::println);
                break;
            case PARALLEL_READ:
                reader.lines().parallel().map(lineToMap).forEach(System.out::println);
                break;
            default:
                break;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + filePath + " can not be found.");
            throw e;
        } catch (IOException e) {
            System.out.println("Problem encountered reading file " + filePath);
            throw e;
        } 
        
    }

    // Continue despite error
    private Function<String, Map<String, Object>> lineToMap = (line) -> {
        try {
            String[] values = split(line);

            if (values.length > names.length)
                values = Arrays.copyOf(values, names.length);

            Object[] valueObjects = Arrays.stream(values).map(v -> parseValue(v)).toArray(size -> new Object[size]);

            Map<String, Object> row = IntStream.range(0, valueObjects.length)
                    .mapToObj(index -> new SimpleEntry<String, Object>(names[index], valueObjects[index]))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return row;
        } catch (Exception e) {
            System.out.println("Error storing row in a map.");
            e.printStackTrace();
            return null; // continue intentionally despite error
        }
    };

    // Only pass through COMMA_IGNORE_QUOTED if there's a double quote (") in the string because regex matching is expensive
    public static String[] split(String line) {
        return line.contains("\"") ? COMMA_IGNORE_QUOTED_PATTERN.split(line) : COMMA_PATTERN.split(line);
    }

    public static Object parseValue(String s) {
        if (s == null)
            return "";
        s = s.trim();
        if (s == "")
            return "";
        Object o;
        if ((o = parseInteger(s)) != null)
            return o;
        if ((o = parseDouble(s)) != null)
            return o;
        return parseString(s);
    }

    public static Integer parseInteger(String s) {
        try {
            return Integer.valueOf(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double parseDouble(String s) {
        try {
            return Double.valueOf(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String parseString(String s) {
        if (s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"')
            s = s.substring(1, s.length() - 1);
        return s;
    }

    public static void main(String[] args) {
        CSVReader reader = new CSVReader();

        try {
            // Serial read
            System.out.println("=================Serial read");
            long serialStart = System.currentTimeMillis();
            reader.read(args[0], SERIAL_READ);
            long serialEnd = System.currentTimeMillis();

            System.out.println("\n=================Parallel read");
            long parallelStart = System.currentTimeMillis();
            reader.read(args[0], PARALLEL_READ);
            long parallelEnd = System.currentTimeMillis();

            System.out.println("\nPerformance measurements:");
            System.out.println("Serial read execusion time used: " + (serialEnd - serialStart) + " (ms)");
            System.out.println("Parallel read execusion time used: " + (parallelEnd - parallelStart) + " (ms)");
        } catch (IOException e) {
            System.out.println("Error reading");
            e.printStackTrace();
        }
    }
}
