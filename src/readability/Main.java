package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Calculates the readability score according to several approaches:
 * - Automated readability index;
 * (<a href="https://en.wikipedia.org/wiki/Automated_readability_index">...</a>)
 * - Flesch–Kincaid
 * readability tests; (<a
 * href="https://en.wikipedia.org/wiki/Flesch%E2%80%93Kincaid_readability_tests">...</a>)
 * - Simple Measure of Gobbledygook index; (<a href="https://en.wikipedia.org/wiki/SMOG">...</a>)
 * - Coleman–Liau index. (<a href="http://en.wikipedia.org/wiki/Coleman%E2%80%93Liau_index">...</a>)
 */
public class Main {

  String datasetPath;
  String inputText;
  int sentencesNum;
  int wordsNum;
  int charsNum;
  int syllablesNum;
  int polysyllablesNum;

  /**
   * Starts the application.
   *
   * @param args The path to the text file must be specified in args[0].
   */
  public static void main(String[] args) {
    Main estimator = new Main();
    if (args.length > 0) {
      estimator.datasetPath = args[0];
    } else {
      System.out.println("No file path specified in args.");
    }

    estimator.start();
  }

  private void start() {
    getTextFromFile();
    printTextInfo();
    calculateParameters();
    printParametersInfo();
    printMenu();
    String input = getUserInput();
    handleCommand(input);
  }

  private void getTextFromFile() {
    inputText = readFileAsString(datasetPath);
  }

  private String readFileAsString(String datasetPath) {
    try {
      return new String(Files.readAllBytes(Paths.get(datasetPath)));
    } catch (IOException | NullPointerException e) {
      System.out.println("File not found: " + e);
      return "";
    }
  }

  private void printTextInfo() {
    System.out.println("The text is:");
    System.out.println(inputText);
    System.out.println();
  }

  private void printMenu() {
    System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
  }

  private String getUserInput() {
    String input;
    try (Scanner scanner = new Scanner(System.in)) {
      input = scanner.next();
    }
    return input;
  }

  private void calculateParameters() {
    sentencesNum = countSentences();
    wordsNum = countWords();
    charsNum = countChars();
    syllablesNum = countSyllables();
    polysyllablesNum = countPolysyllables();
  }

  private int countSentences() {
    String[] sentences = inputText.split("[.!?]\\s");
    return Arrays.equals(new String[]{""}, sentences) ? 0 : sentences.length;
  }

  private int countWords() {
    return getWords().length;
  }

  private String[] getWords() {
    String[] words = inputText.split("\\W*\\s");
    return Arrays.equals(new String[]{""}, words) ? new String[0] : words;
  }

  private int countChars() {
    return inputText.replaceAll("\\s", "").length();
  }

  private int countSyllables() {
    return Arrays.stream(getSyllablesProWord()).sum();
  }

  private int[] getSyllablesProWord() {
    String[] words = getWords();
    int[] syllablesProWord = new int[words.length];
    for (int i = 0; i < words.length; i++) {
      int countSyllables = 0;
      String tempWord = words[i];
      while (tempWord.matches(".*[aeiouyAEIOUY].*")) {
        tempWord = tempWord.replaceFirst("[aeiouyAEIOUY]+", "");
        countSyllables++;
      }
      if (!"".equals(words[i]) && words[i].substring(words[i].length() - 1).matches("e")) {
        countSyllables--;
      }
      countSyllables = countSyllables == 0 ? 1 : countSyllables;
      syllablesProWord[i] = countSyllables;
    }
    return syllablesProWord;
  }

  private int countPolysyllables() {
    int[] syllables = getSyllablesProWord();
    int polysyllables = 0;
    for (int syllablesProWort : syllables) {
      if (syllablesProWort > 2) {
        polysyllables++;
      }
    }
    return polysyllables;
  }


  private void printParametersInfo() {
    System.out.println("Words: " + wordsNum);
    System.out.println("Sentences: " + sentencesNum);
    System.out.println("Characters: " + charsNum);
    System.out.println("Syllables: " + syllablesNum);
    System.out.println("Polysyllables: " + polysyllablesNum);
  }

  private void handleCommand(String input) {
    System.out.println();
    switch (input) {
      case "ARI" -> processAutomatedReadabilityIndex();
      case "FK" -> processFleschKincaidReadabilityTests();
      case "SMOG" -> processSimpleMeasureOfGobbledygook();
      case "CL" -> processColemanLiauIndex();
      case "all" -> processAllIndexes();
      default -> System.out.println("Unknown command");
    }
  }

  private void processAutomatedReadabilityIndex() {
    double indexARI = getAutomatedReadabilityIndex();
    int ageARI = getAge((int) Math.ceil(indexARI));
    System.out.printf("Automated Readability Index: %.2f (about %d-year-olds).%n", indexARI,
        ageARI);
  }

  private void processFleschKincaidReadabilityTests() {
    double indexFK = getFleschKincaidReadabilityTests();
    int ageFK = getAge((int) Math.ceil(indexFK));
    System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d-year-olds).%n", indexFK,
        ageFK);
  }

  private void processSimpleMeasureOfGobbledygook() {
    double indexSMOG = getSimpleMeasureOfGobbledygook();
    int ageSMOG = getAge((int) Math.ceil(indexSMOG));
    System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d-year-olds).%n", indexSMOG,
        ageSMOG);
  }

  private void processColemanLiauIndex() {
    double indexCLI = getColemanLiauIndex();
    int ageCLI = getAge((int) Math.ceil(indexCLI));
    System.out.printf("Coleman–Liau index: %.2f (about %d-year-olds).", indexCLI, ageCLI);
  }

  private void processAllIndexes() {
    double indexARI = getAutomatedReadabilityIndex();
    double indexFK = getFleschKincaidReadabilityTests();
    double indexSMOG = getSimpleMeasureOfGobbledygook();
    double indexCLI = getColemanLiauIndex();
    int ageARI = getAge((int) Math.ceil(indexARI));
    int ageFK = getAge((int) Math.ceil(indexFK));
    int ageSMOG = getAge((int) Math.ceil(indexSMOG));
    int ageCLI = getAge((int) Math.ceil(indexCLI));

    System.out.printf("Automated Readability Index: %.2f (about %d-year-olds).%n", indexARI,
        ageARI);
    System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d-year-olds).%n", indexFK,
        ageFK);
    System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d-year-olds).%n", indexSMOG,
        ageSMOG);
    System.out.printf("Coleman–Liau index: %.2f (about %d-year-olds).%n", indexCLI, ageCLI);
    System.out.println();

    double averageAge = (ageARI + ageFK + ageSMOG + ageCLI) / 4.0;
    System.out.printf("This text should be understood in average by %.2f-year-olds.%n", averageAge);
  }

  private double getAutomatedReadabilityIndex() {
    return 4.71 * (charsNum * 1.0 / wordsNum) + 0.5 * (wordsNum * 1.0 / sentencesNum) - 21.43;
  }

  private double getFleschKincaidReadabilityTests() {
    return 0.39 * (wordsNum * 1.0 / sentencesNum) + 11.8 * (syllablesNum * 1.0 / wordsNum) - 15.59;
  }

  private double getSimpleMeasureOfGobbledygook() {
    return 1.043 * Math.sqrt(polysyllablesNum * (30.0 / sentencesNum)) + 3.1291;
  }

  private double getColemanLiauIndex() {
    double l = charsNum * 100.0 / wordsNum;
    double s = sentencesNum * 100.0 / wordsNum;
    return 0.0588 * l - 0.296 * s - 15.8;
  }

  private int getAge(int score) {
    if (score < 0) {
      System.out.println("Not correct score");
      return 0;
    }
    return switch (score) {
      case 1 -> 6;
      case 2 -> 7;
      case 3 -> 8;
      case 4 -> 9;
      case 5 -> 10;
      case 6 -> 11;
      case 7 -> 12;
      case 8 -> 13;
      case 9 -> 14;
      case 10 -> 15;
      case 11 -> 16;
      case 12 -> 17;
      case 13 -> 18;
      case 14 -> 22;
      default -> 23;
    };
  }
}
