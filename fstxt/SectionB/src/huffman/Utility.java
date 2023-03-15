package huffman;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utility {

  public static List<String> getWords(String filePath) {
    List<String> words = null;
    try (Stream<String> linesStream = Files.lines(Paths.get(filePath))) {
      words = linesStream.flatMap(line -> Arrays.stream(line.split(" "))).map(word -> word.trim())
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return words;
  }

  public static String sequenceOfBitsAsNumber(String binaryEncoding) {
    final String binaryEncodingWithHeading1 =
        "1" + binaryEncoding; // Prepending 1 not to lose heading zeroes
    BigInteger result = new BigInteger(binaryEncodingWithHeading1, 2);
    return result.toString();
  }

  public static String numberAsSequenceOfBits(String numberRepresentation) {
    BigInteger number = new BigInteger(numberRepresentation);
    String binaryRepresentation = number.toString(2);
    return binaryRepresentation.substring(1); // Removing previously prepended 1
  }

  public static long totalLength(List<String> words) {
    long length = words.size() - 1; // White spaces
    length += words.stream().mapToLong(w -> w.length()).sum();
    return length;
  }

  public static Map<String, Integer> countWords(List<String> words) {
    //TODO replace the current sequential implementation with a concurrent one (Q4)
//    Actually forgot what toMap does, and the meaning of the stuffs in bracket
//    How many threads? 2
    int size = words.size();
//    I was thinking about what the critical resource is and stuff and thought about making
//    a critical resource of a map and the threads can add to that
    Map<String, AtomicInteger> aiResult = new HashMap<>();

    for (String word : words) {
      aiResult.put(word, new AtomicInteger(0));
    }

//    What if there is only one element?
    Thread u = new WordCounter(aiResult, words.subList(0, size / 2));
    Thread v = new WordCounter(aiResult,words.subList(size / 2, size));

//    Not familiar with how to start and stop threads
    u.start();
    v.start();

    try {
      u.join();
      v.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    Map<String, Integer> result = new HashMap<>();

//    Efficient?
    for (Map.Entry<String, AtomicInteger> wordCountPair : aiResult.entrySet()) {
      result.put(wordCountPair.getKey(), wordCountPair.getValue().get());
    }

    return result;
  }
}
