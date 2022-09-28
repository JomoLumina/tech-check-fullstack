package com.ikhokha.techcheck;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommentAnalyzer implements ICommentAnalyzer {

  private final File file;
  private final Integer maxLength;

  public CommentAnalyzer(File file, Integer maxLength) {
    this.file = file;
    this.maxLength = maxLength;
  }

  public Map<String, Integer> analyze() {

    Map<String, Integer> resultsMap = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        getShorterThan(line, maxLength, resultsMap);
        checkMetrics(line, resultsMap);
        countQuestions(line, resultsMap);
        countSpamComments(line, resultsMap);
      }
    } catch (FileNotFoundException e) {
      System.out.printf("%s%s%n", Constants.NOT_FOUND, file.getAbsolutePath());
      e.printStackTrace();
    } catch (IOException e) {
      System.out.printf("%s%s%n", Constants.IO_ERROR, file.getAbsolutePath());
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println(Constants.GENERAL_ERROR);
      e.printStackTrace();
    }
    return resultsMap;
  }

  /**
   * This method checks for all the matrix indicators from the Keywords enum
   *
   * @param line       the line of comments to check on
   * @param resultsMap the map that keeps track of counts
   */
  public void checkMetrics(String line, Map<String, Integer> resultsMap) {
    for (Metrics metric : Metrics.values()) {
      long count = Arrays.stream(line.split(Constants.SPACE)).
          filter(str -> str.toUpperCase().contains(metric.toString())).count();
      if (count > 0) {
        incOccurrence(resultsMap, String.format(Constants.MATRIX_MENTIONS,
            metric.toString().toUpperCase()), (int) count);
      }
    }
  }

  /**
   * This method counts all the comments that are shorter than @Param length characters
   *
   * @param line     the line of comments to check on
   * @param length   the length to check against
   * @param countMap the map that keeps track of counts
   */
  public void getShorterThan(String line, Integer length, Map<String, Integer> countMap) {
    if (line.length() < length) {
      Integer count = 1;
      String key = String.format(Constants.SHORTER_THAN, length);
      incOccurrence(countMap, key, count);
    }
  }

  /**
   * This method counts how many of the comments are questions
   *
   * @param line     the line of comments to check on
   * @param countMap the map that keeps track of counts
   */
  public void countQuestions(String line, Map<String, Integer> countMap) {
    long count = line.chars().filter(ch -> ch == Constants.QUESTION_MARK).count();
    if (count > 0) {
      incOccurrence(countMap, Constants.QUESTIONS, (int) count);
    }
  }

  /**
   * This method counts how many of the comments are questions
   *
   * @param line     the line of comments to check on
   * @param countMap the map that keeps track of counts
   */
  public void countSpamComments(String line, Map<String, Integer> countMap) {

    long count = Arrays.stream(line.split(Constants.SPACE)).
        filter(ch -> ch.contains(Constants.PROTOCOL) ||
            ch.contains(Constants.SECURE_PROTOCOL)).count();
    if (count > 0) {
      incOccurrence(countMap, Constants.SPAM, (int) count);
    }
  }

  /**
   * This method increments a counter by 1 for a match type on the countMap. Uninitialized keys will be set to 1
   *
   * @param countMap the map that keeps track of counts
   * @param key      the key for the value to increment
   */
  public void incOccurrence(Map<String, Integer> countMap, String key, Integer count) {
    countMap.putIfAbsent(key, 0);
    countMap.put(key, countMap.get(key) + count);
  }

}
