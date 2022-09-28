package com.ikhokha.techcheck;

import java.io.FileNotFoundException;

public class Main {
  public static void main(String[] args) {
    try {
      FileHandler fh = new FileHandler();
      fh.FetchAndProcessCommentFiles();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
