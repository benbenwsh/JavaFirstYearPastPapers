package huffman;

import java.util.*;

public class HuffmanEncoder {

  final HuffmanNode root;
  final Map<String, String> word2bitsequence;

  private HuffmanEncoder(HuffmanNode root,
      Map<String, String> word2bitSequence) {
    this.root = root;
    this.word2bitsequence = word2bitSequence;
  }

  public static HuffmanEncoder buildEncoder(Map<String, Integer> wordCounts) {
    //TODO: complete the implementation of this method (Q1)

    if (wordCounts == null) {
      throw new HuffmanEncoderException("wordCounts cannot be null");
    }
    if (wordCounts.size() < 2) {
      throw new HuffmanEncoderException("This encoder requires at least two different words");
    }

    // fixing the order in which words will be processed: this determines the execution and makes
    // tests reproducible.
    // Not sure where to actually use sortedWords
    TreeMap<String, Integer> sortedWords = new TreeMap<String,Integer>(wordCounts);
    PriorityQueue<HuffmanNode> queue = new PriorityQueue<>(sortedWords.size());

    //YOUR IMPLEMENTATION HERE...
//    Stuck here for a while
//    I'm such an idiot: I did while loop for a for-each loop
    for (Map.Entry<String, Integer> entry : sortedWords.entrySet()) {
      queue.offer(new HuffmanLeaf(entry.getValue(), entry.getKey()));
    }

    while (queue.size() > 1) {
      HuffmanNode left = queue.poll();
      HuffmanNode right = queue.poll();

      queue.offer(new HuffmanInternalNode(left, right));
    }


    HuffmanNode root = queue.poll();
    Map<String, String> word2bitSequence = new HashMap<>();
    dfs(root, "", word2bitSequence);

    return new HuffmanEncoder(root, word2bitSequence);
  }


//  Not sure if it is good practice to use private static method as a helper method to a public static method
  private static void dfs(HuffmanNode root, String encoding, Map<String, String> result) {
    if (root instanceof HuffmanLeaf leaf) {
      result.put(leaf.getWord(), encoding);
    } else if (root instanceof HuffmanInternalNode iNode) {
      dfs(iNode.getLeft(), encoding + "0", result);
      dfs(iNode.getRight(), encoding + "1", result);
    } else {
      throw new HuffmanEncoderException("HuffmanNode is neither a leaf or an internal node");
    }
  }


  public String compress(List<String> text) {
    assert text != null && text.size() > 0;

    String encoding = "";
    //TODO: implement this method (Q2)
    for (String word : text) {
      if (!word2bitsequence.containsKey(word)) {
        throw new HuffmanEncoderException("The input word is not in the map");
      } else {
        encoding += word2bitsequence.get(word);
      }
    }

    return encoding;
  }


//  Disgusting code here, review!!! Must become faster
  public List<String> decompress(String compressedText) {
    assert compressedText != null && compressedText.length() > 0;

    // What happens if there is no leaf node? or some other issues?

    //TODO: implement this method (Q3)
    List<String> result = new ArrayList<>();
    HuffmanNode currNode = root;
//    Why is it taking me so long to implement this
//    Should I use loop or recursion here?
    while (compressedText.length() > 0) {
      if (currNode instanceof HuffmanLeaf leaf) {
//        Forgot to add currNode = root, leading to OutOfMemoryError
        currNode = root;
        result.add(leaf.word);
      } else if (currNode instanceof HuffmanInternalNode iNode) {
//      Didn't know how to access the first element of a string
//      I used .indexOf, which gives the index of the char, which is the opp of what I want to do
        if (compressedText.charAt(0) == '0') {
          currNode = iNode.getLeft();
        } else if (compressedText.charAt(0) == '1') {
          currNode = iNode.getRight();
        } else {
          throw new HuffmanEncoderException("Compressed encoding contains characters that are not 0 or 1");
        }
//        Not familiar with string methods: not sure how to get the tail of a string
//        It returns an empty string if the beginIndex is geq length()
        compressedText = compressedText.substring(1);
      } else {
        throw new HuffmanEncoderException("HuffmanNode is neither a leaf nor an internal node");
      }
    }

    if (currNode instanceof HuffmanLeaf leaf) {
      result.add(leaf.word);
    } else {
      throw new HuffmanEncoderException("Invalid compressed encoding");
    }

    return result;
  }

  // Below the classes representing the tree's nodes. There should be no need to modify them, but
  // feel free to do it if you see it fit

  private static abstract class HuffmanNode implements Comparable<HuffmanNode> {

    private final int count;

    public HuffmanNode(int count) {
      this.count = count;
    }

    @Override
    public int compareTo(HuffmanNode otherNode) {
      return count - otherNode.count;
    }
  }


  private static class HuffmanLeaf extends HuffmanNode {

    private final String word;

    public HuffmanLeaf(int frequency, String word) {
      super(frequency);
      this.word = word;
    }

    public String getWord() {
      return word;
    }
  }


  private static class HuffmanInternalNode extends HuffmanNode {

    private final HuffmanNode left;
    private final HuffmanNode right;

    public HuffmanInternalNode(HuffmanNode left, HuffmanNode right) {
      super(left.count + right.count);
      this.left = left;
      this.right = right;
    }

    public HuffmanNode getLeft() {
      return left;
    }

    public HuffmanNode getRight() {
      return right;
    }
  }
}
