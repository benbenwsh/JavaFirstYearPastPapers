package filesystems;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DocDirectory extends DocFile {

  /**
   * Construct a file with the given name.
   *
   * @param name The name of the file.
   */
  private final Set<DocFile> directory;
  private final int size;

//  * Wrongly used List instead of Set at the start because forgot about the keyword 'distinct'
  public DocDirectory(String name) {
    super(name);
    directory = new HashSet<>();
    size = name.length();
  }

  @Override
  public int getSize() {
    return size;
  }

  @Override
  public boolean isDirectory() {
    return true;
  }

  @Override
  public boolean isDataFile() {
    return false;
  }

  @Override
  public DocDirectory asDirectory() {
    return this;
  }

  @Override
  public DocDataFile asDataFile() {
    throw new UnsupportedOperationException();
  }

//  Should I overload the constructor so that I can pass in directory var
//  I think I neeed to duplicate the directory var, otherwise changes made in the duplicate
//  will affect the contents of the original
  @Override
  public DocFile duplicate() {
    DocDirectory newDocDirectory = new DocDirectory(getName());

//    * Model answer uses stream, but for loop is fine
//    * Not familiar with forEach in stream. Does it have an effect on the stream?
//    * My attempt:
//    * directory.stream().forEach(x -> newDocDirectory.addFile(x.duplicate()));
//    * Model answer did newDocDirectory.directory.addAll(...)
//    * Although directory is private it can access it because we're in the class right now
    for (DocFile docFile : directory) {
      newDocDirectory.addFile(docFile.duplicate());
    }
    return newDocDirectory;
  }

  public boolean containsFile(String name) {
//    * Model answer uses anyMatch
//    * Weakness: methods in stream
//    return directory.stream().anyMatch(x -> x.getName() == name);
    for (DocFile file : directory) {
      if (file.getName() == name) {
        return true;
      }
    }
    return false;
  }

  public Set<DocFile> getAllFiles() {
    return directory;
  }

  public Set<DocDirectory> getDirectories() {
//    * X: Model answer uses method isDirectory and asDirectory
//    * Use methods defined
    return directory.stream()
                    .filter(docFile -> docFile instanceof DocDirectory)
                    .map(docFile -> (DocDirectory) docFile).collect(Collectors.toSet());
  }

  public Set<DocDataFile> getDataFiles() {
    return directory.stream()
            .filter(docFile -> docFile instanceof DocDataFile)
            .map(docFile -> (DocDataFile) docFile).collect(Collectors.toSet());
  }

  public void addFile(DocFile file) {
//    Wanted to implement object equality, but realised that I have containsFile()
    if (containsFile(file.getName())) {
      throw new IllegalArgumentException();
    } else {
//      * Don't use else because the program terminates after throwing an exception
      directory.add(file);
    }
  }

  public boolean removeFile(String filename) {
//    * Better than model answer: good job using methods of Set to make it more readable
    return directory.removeIf(docFile -> docFile.getName() == filename);
  }

  public DocFile getFile(String filename) {
//    * Turns out I made the stupid mistake of writing != instead of ==
    return directory.stream()
            .filter(docFile -> docFile.getName() == filename)
            .collect(Collectors.toList())
            .get(0);
//    * Better to do findFirst, which returns an optional, and then get()
  }
}
