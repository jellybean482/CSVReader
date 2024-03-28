package com.jia.csv.test;

import static org.junit.Assert.assertTrue;

import com.jia.csv.CSVReader;

import org.junit.Assert;
import org.junit.Test;

public class CSVReaderUnitTest {

    @Test
    public void splitRegular() {
        String[] expected = new String[] {"regular", "string"};
        StringBuffer sb = new StringBuffer(String.join(",", expected));
        String[] actual = CSVReader.split(sb.toString());
        Assert.assertArrayEquals(expected, actual);
    }
    
    @Test
    public void splitIgnoreQuotedComma() {
        String[] expected = new String[] {"string", "\",\"with comma"};
        StringBuffer sb = new StringBuffer(String.join(",", expected));
        String[] actual = CSVReader.split(sb.toString());
        Assert.assertArrayEquals(expected, actual);
    }
    
    @Test
    public void splitContainQuotes() {
        String[] expected = new String[] {"\" quoted \"", "string"};
        StringBuffer sb = new StringBuffer(String.join(",", expected));
        String[] actual = CSVReader.split(sb.toString());
        Assert.assertArrayEquals(expected, actual);
    }
    
    @Test
    public void splitContainUnmatchedQuote() {
        String[] expected = new String[] {"\" quoted ", "string"};
        StringBuffer sb = new StringBuffer(String.join(",", expected));
        String[] actual = CSVReader.split(sb.toString());
        Assert.assertArrayEquals(expected, actual);
    }
    
    @Test
    public void parseValue() {
        String expected = "string";
        Object actual = CSVReader.parseValue(expected);
        assertTrue(actual instanceof String);
        Assert.assertEquals(expected, (String)actual);
    }
    
    @Test
    public void parseValueStringLeadingSpaces() {
        String expected = "string";
        StringBuffer sb = new StringBuffer("   ");
        sb.append(expected);
        Object actual = CSVReader.parseValue(sb.toString());
        assertTrue(actual instanceof String);
        Assert.assertEquals(expected, (String)actual);
    }
    
    @Test
    public void parseValueStringTrailingSpaces() {
        String expected = "string";
        StringBuffer sb = new StringBuffer(expected);
        sb.append("   ");
        Object actual = CSVReader.parseValue(sb.toString());
        assertTrue(actual instanceof String);
        Assert.assertEquals(expected, (String)actual);
    }
    
    @Test
    public void parseValueInteger() {
        int expected = 123;
        Object actual = CSVReader.parseValue(String.valueOf(expected));
        assertTrue(actual instanceof Integer);
        Assert.assertEquals(expected, ((Integer)actual).intValue());
    }
    
    @Test
    public void parseValueDouble() {
        double expected = 123.456;
        Object actual = CSVReader.parseValue(String.valueOf(expected));
        assertTrue(actual instanceof Double);
        Assert.assertEquals(expected, ((Double)actual).doubleValue(), 0.001);
    }
    
    @Test
    public void parseInteger() {
        int expected = 123;
        Integer actual = CSVReader.parseInteger(String.valueOf(expected));
        Assert.assertEquals(expected, actual.intValue());
    }
    
    @Test
    public void parseDouble() {
        double expected = 123.456;
        Double actual = CSVReader.parseDouble(String.valueOf(expected));
        Assert.assertEquals(expected, actual.doubleValue(), 0.001);
    }
    
    @Test
    public void parseStringLeadingSpaces() {
        String expected = "string";
        StringBuffer sb = new StringBuffer("   ");
        sb.append(expected);
        Object actual = CSVReader.parseString(sb.toString());
        assertTrue(actual instanceof String);
        Assert.assertEquals(expected, (String)actual);
    }
}
