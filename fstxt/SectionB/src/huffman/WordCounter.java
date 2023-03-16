package huffman;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCounter extends Thread {

  private final Map<String, AtomicInteger> result;
  private final List<String> words;
  private final int from;
  private final int to;

  public WordCounter(Map<String, AtomicInteger> result, List<String> words, int from, int to) {
    this.result = result;
    this.words = words;
    this.from = from;
    this.to = to;
  }

  @Override
  public void run() {
//    * My first implementation uses an if else block in order to create a new AtomicInteger if the map does
//    * not contains that key yet, which is extremely inefficient as containsKey takes O(n)
//    * The solution is to add all distinct words in the map with value 0
    for (int i=from; i<to; i++) {
//      * Weakness: Concurrency (Fine-grained, optimistic, lazy removal, methods in AtomicInteger)
//      * Weakness: Methods in AtomicInteger (Does AtomicInteger have to be a critical resource?)
//      * Weakness: Which operations are atomic (Writing everything in one-line does not mean the operation is atomic)

//      * Should be O(1) as it is an ArrayList in the test cases
      result.get(words.get(i)).getAndIncrement();
    }
  }
}
