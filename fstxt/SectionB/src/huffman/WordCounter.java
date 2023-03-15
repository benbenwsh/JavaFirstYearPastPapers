package huffman;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCounter extends Thread {

  Map<String, AtomicInteger> result;
  List<String> words;

  public WordCounter(Map<String, AtomicInteger> result, List<String> words) {
    this.result = result;
    this.words = words;
  }

  @Override
  public void run() {
//    I feel like this implementation is extremely inefficient. Because it takes O(n) for containsKey
//    Maybe add all distinct words with value 0, taking O(n)
//    Is it just the same as adding the words with the count with it?
//    The sequential implementation is also O(n)
//    I don't think efficiency matters here because concurrent operations is faster even with the same complexity
//    I also think the most important thing is that it words without race conditions
    for (String word : words) {
//      What happens if map does not contain the key, but you get it
//      Should I use AtomicInteger here. The +1 operation reminds me of +1
//      I can put a lock around this if-else block, but then they are just adding keys sequentially
//      Maybe do a fine-grained solution, where we lock a word when we want to access it
//      I don't know how to lock a String, so forget about it
//      Maybe use optimistic locking, which uses atomic primitives and functions?
//      How do I do optimistic locking? AtomicIntegers, check at the end
//      More familiar with operations of AtomicInteger, I'm going through the suggestions provided by IDEA

//      if (result.containsKey(word)) {
//        Is this the same as writing them in one line, does the compiler still do two separate operations?
//        If so, if a thread increments cnt, then another thread does result.put, it overrides the result
//        I feel like AtomicInteger has to be a critical resource, otherwise what is the point of the atomic operation
//        Should I make a Map<String, AtomicInteger> instead
//        There is something called AtomicFieldUpdater. Should I give that a go?
      result.get(word).getAndIncrement();
//          When do we use compareAndSet, because how do you get the expected value without calculating it non-atomically?
//          Oh, nvm, I misunderstood the meaning of expectedValue: it means the expected current value
//          In that case, I think I don't need compareAndSet because getAndIncrement is already atomic.
//        while (count.compareAndSet());

//        By the way I rmb that result.put(..., cnt + 1) is not atomic, but it doesn't matter cuz cnt is local and not a
//        critical resource

//      } else {

//        What if someone did this right after another thread checks whether it containsKey
//        I know that this is not thread-safe yet becuase it still hasn't solved the issue above, but I'm just going to test it
//        Error indeed occured, where there is a field with count usually one less than the original
//        This is difficult to get it lock free
//        One way is to initialise the map with AtomicIntegers of 1s sequentially, but that is not concurrent though
//        The splitting the lists for threads to operate is not that concurrent as well!!
//        Final way is to write my own data structure for result, which locks at the start of put() method
//        result.put(word, new AtomicInteger(1));
    }
  }
}
