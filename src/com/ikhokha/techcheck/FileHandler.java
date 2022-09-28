package com.ikhokha.techcheck;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class FileHandler implements IFileHandler {

  Integer maxLength = Constants.DEFAULT_MAX_LENGTH;
  public  FileHandler(){
  }

  /**
   * This method uses a ExecutorService for threading to work through the files using parallel stream
   */
  public void FetchAndProcessCommentFiles() throws FileNotFoundException {
    String rootPath = Objects.requireNonNull(Thread.currentThread().
        getContextClassLoader().getResource(Constants.EMPTY_STRING)).getPath();
    try {
      Properties props = new Properties();
      String configFile = String.format("%s%s%s", rootPath, Constants.PATH, Constants.CONFIG_FILE);
      props.load(new FileInputStream(configFile));
      maxLength = Integer.parseInt(props.getOrDefault(Constants.MAX_LENGTH_KEY, maxLength).toString());

      Map<String, Integer> totalResults = new HashMap<>();
      WalkThroughFiles(totalResults, maxLength);

      System.out.println(Constants.RESULTS_HEADER);
      totalResults.forEach((k, v) -> System.out.println(k + " : " + v));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method uses a ExecutorService for threading to work through the files using parallel stream
   *
   * @param totalResults the target map to store results
   * @param maxLength the length to check for the shorter than length comments
   */
  private void WalkThroughFiles(Map<String, Integer> totalResults, Integer maxLength) throws IOException {
    Files.walk(Paths.get(Constants.FOLDER_NAME))
        .collect(Collectors.toList())
        .parallelStream()
        .filter(Files::isRegularFile)
        .filter(p -> p.getFileName().toString().endsWith(Constants.EXTENSION))
        .map(Path::toFile)
        .forEach(commentFile -> {
          CommentAnalyzer commentAnalyzer = new CommentAnalyzer(commentFile, maxLength);
          Map<String, Integer> fileResults = commentAnalyzer.analyze();
          addReportResults(fileResults, totalResults);
        });
  }

  /**
   * This method adds the result counts from a source map to the target map
   *
   * @param source the source map
   * @param target the target map
   */
  private void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {
    for (Map.Entry<String, Integer> entry : source.entrySet()) {
      target.putIfAbsent(entry.getKey(), 0);
      target.put(entry.getKey(), target.get(entry.getKey()) + entry.getValue());
    }
  }

}
