package filesystems;

import java.util.Arrays;
import java.util.Objects;

public final class DocDataFile extends DocFile {

//  Almost forgot to specify the access modifiers and the final in these vars
  private final byte[] byteContents;
  private final int size;

  /**
   * Construct a file with the given name.
   *
   * @param name The name of the file.
   */

  public DocDataFile(String name, byte[] byteContents) {
    super(name);
    this.byteContents = byteContents;
    size = name.length() + byteContents.length;
  }

  @Override
  public int getSize() {
    return size;
  }

  @Override
  public boolean isDirectory() {
    return false;
  }

  @Override
  public boolean isDataFile() {
    return true;
  }

  @Override
  public DocDirectory asDirectory() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DocDataFile asDataFile() {
    return this;
  }

  @Override
  public DocFile duplicate() {
    DocFile newDocDataFile = new DocDataFile(getName(), byteContents);
    return newDocDataFile;
  }

  public boolean containsByte(byte b) {
//    Shit! I don't know how to check if an array contains something
    for (byte bc : byteContents) {
      if (bc == b) {
        return true;
      }
    }
    return false;
  }

//  * √: the same as sample answer


//  Revise this later
//  Learn how to use the IDE to simplify the object equality process
//  * Sample answer did this method
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DocDataFile that)) return false;
    return getSize() == that.getSize() && Arrays.equals(byteContents, that.byteContents);
  }


//  Must use hashcode to perform object equality
  @Override
  public int hashCode() {
    int result = Objects.hash(getSize());
    result = 31 * result + Arrays.hashCode(byteContents);
    return result;
  }
}
