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

//  * Model answer does this
//    1. Uses for loop/stream to create, start, and join threads
//    2. Does not split the words, but assign from and to pointers
//    3. Each thread (Counter) saves the result in a private attribute
//    4. Stream is used to aggregate all the results of each thread and return it
//  * Model answer is more than 2x as fast as my solution
//  * But it is as fast as mine with 2 threads
//  * I like that it has a constant for the no of threads
//  * I like that it does not split words, as it takes O(n) each time
//  * I don't like the for loop and stream at the end as the time complexity is insane
  public static Map<String, Integer> countWords(List<String> words) {
    //TODO replace the current sequential implementation with a concurrent one (Q4)
//    * Weakness: Collectors.toMap(...)
//    * Weakness: Integer division
    int THREAD_NUM = (int) Math.ceilDiv(words.size(), 10);
    WordCounter[] wordCounters = new WordCounter[THREAD_NUM];
//    I was thinking about what the critical resource is and then came up with the
//    idea of making a map a critical resource and the threads can add to that
    Map<String, AtomicInteger> aiResult = new HashMap<>();

    for (String word : words) {
      aiResult.put(word, new AtomicInteger(0));
    }

//    What if there is only one element?
    for (int i=0; i<THREAD_NUM; i++) {
      wordCounters[i] = new WordCounter(aiResult, words, Math.ceilDiv(words.size(), THREAD_NUM) * i,
              (int) Math.min(words.size(), Math.ceilDiv(words.size(), THREAD_NUM) * (i+1)));
    }

//    * Weakness: Threads (How to start and stop threads)
    Arrays.stream(wordCounters).forEach(cnt -> cnt.start());

    Arrays.stream(wordCounters).forEach(cnt -> {
      try {
        cnt.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    Map<String, Integer> result = new HashMap<>();

//    * O(n): More efficient than the model answer (which is O(n^2))
    for (Map.Entry<String, AtomicInteger> wordCountPair : aiResult.entrySet()) {
      result.put(wordCountPair.getKey(), wordCountPair.getValue().get());
    }

    return result;
  }
}
