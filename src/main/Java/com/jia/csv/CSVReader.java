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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * Assumptions made:
 * - Field names in the first line are unique
 * - Column order is not important
 * - Row order is not important
 * - Quoted strings are double-quoted, not single-quoted
 * - Quoted strings, either for field names or values, are stored in the map with quotes stripped while leading and trailing spaces are preserved
 * 
 * Tested with 
 * - wacky.csv: contains some wacky cases (quotes, commas and leading/trailing spaces in field names and values, empty lines, missing cells)
 * - HMEQ.csv: supplied by assigner
 * 
 * Time spent on solution: 3/18/2024 7-9 pm; 3/19/2024 from 9-11 pm.
 * 
 * 
 * Overview: This is an exercise to explore your competency in the following areas:
 *   • modern java language features like Streams, Predicates, Function objects etc.
 *   • algorithmic thinking and modular design in terms of classes and methods.
 *   • general awareness of constraints when processing large files of data.
 *
 * Requirements:
 *   • Write a java program in the form of a java Stream pipeline to read a file in CSV (comma separated values) format (example attached).
 *     The first line of the file contains the names of the fields.
 *     The other lines contain values corresponding to the names in the first line.
 *
 *   • The program should read each of the other lines into a Map object and print its contents. Please ensure all lines are read even when some lines have errors.
 *
 *   • Every entry in the Map should contain:
 *   • Key - a string containing the field name (from the first row)
 *   • Value - the corresponding value (from the current row)
 *
 *   • A Value could be a String (sequence of characters), Double (floating point number) or Integer (no decimal point) and should be read as an Object of the appropriate type.
 *   • Null values are possible as are empty strings.
 *   • String values may be quoted or unquoted and may contain commas.
 *   • Please ignore leading and trailing spaces for numerical values and unquoted string values.
 *   • Leading and trailing spaces for quoted string values are significant and must not be ignored.
 *
 * Acceptance criteria:
 *   • Must read the lines of the file as a Stream. The input file may have millions of lines of data.
 *   
 *   • Use of explicit loops like while, for etc. are not permitted. Please use Stream functions like foreach, filter, map, join etc. to iterate and transform.
 *     This will be used to evaluate the competency in the modern java language features.
 *
 *   • There are many ways to parse data in java. Please choose an appropriate mechanism and describe why you chose it.
 *
 *   • If you want to make improvements or extend your code but do not have time, leave a //TODO comment in your code along with a description of what you would have done given time, and why.
 *
 * Bonus points:
 *   • Provide a solution that makes use of parallelism in processing the rows.
 *
 * Please limit the time you work on your solution to four hours.
 * Please commit the solution to a public git repository of your choice (eg, github), and let us know the location of the repository (preferably one day prior to your scheduled interview).
 *
 * Be prepared to:
 *   • Present your solution and walk through how you designed, implemented, and tested the program. During the technical interview, we may ask you to share your screen and show the program running.
 *   • Discuss any assumptions you made and challenges you encountered.
 */
public class CSVReader {
    private static final String COMMA_DELIMITER = ",";
    private static final String COMMA_IGNORE_QUOTED = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final int SERIAL_READ = 0;
    private static final int PARALLEL_READ = 1;
    
    private String[] names;

    public void read(String filePath, int method) throws IOException {
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("File " + filePath + " can not be found.");
            throw e;
        }
        
        try {
            // read the header line
            String nameLine = reader.readLine();
            names = split(nameLine);
            names = Arrays.stream(names).map(n->parseString(n)).toArray(size -> new String[size]);
            
            // read and print the value lines
            switch (method) {
            case SERIAL_READ:
                serialRead(reader);
                break;
            case PARALLEL_READ:
                parallelRead(reader);
                break;
            default:
                break;
            }
        } catch (IOException e) {
            System.out.println("Problem encountered reading file " + filePath);
            throw e;
        } finally {
            if ( reader != null ) reader.close();
        }        
    }

    // No need to return anything because we are done printing
    private void serialRead(BufferedReader reader)
    {
        reader.lines().map(lineToMap).forEach(System.out::println);
    }
    
    // No need to return anything because we are done printing
    private void parallelRead(BufferedReader reader)
    {
        reader.lines().parallel().map(lineToMap).forEach(System.out::println);
    }
    
    // Continue despite error
    private Function<String, Map<String, Object>> lineToMap = (line) -> {
        try {
            String[] values = split(line);

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

    // Only pass through regex if there's a double quote (") in the string because regex matching is expensive
    private String[] split(String line) {
        return line.contains("\"") ? line.split(COMMA_IGNORE_QUOTED) : line.split(COMMA_DELIMITER); 
    }
    
    private Object parseValue(String s) {
        if (s == null )
            return "";
        s = s.trim();
        if ( s == "" )
            return "";
        Object o;
        if ((o = parseInteger(s)) != null)
            return o;
        if ((o = parseDouble(s)) != null)
            return o;
        return parseString(s);
    }

    private Integer parseInteger(String s) {
        try {
            return Integer.valueOf(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String s) {
        try {
            return Double.valueOf(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String parseString(String s) {
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
