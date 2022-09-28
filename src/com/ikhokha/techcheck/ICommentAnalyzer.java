package com.ikhokha.techcheck;

import java.util.Map;

public interface ICommentAnalyzer {

  Map<String, Integer> analyze();
  void checkMetrics(String line, Map<String, Integer> resultsMap);
  void getShorterThan(String line, Integer length, Map<String, Integer> countMap);
  void countQuestions(String line, Map<String, Integer> countMap);
  void countSpamComments(String line, Map<String, Integer> countMap);
  void incOccurrence(Map<String, Integer> countMap, String key, Integer count);
}
