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
//  Inclined towards using Lists
//  Should use Set instead because the files are all distinct
//  What happens if there are non-distinct files added?
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

    for (DocFile docFile : directory) {
      newDocDirectory.addFile(docFile.duplicate());
    }
    return newDocDirectory;
  }

  public boolean containsFile(String name) {
//    Is the way you loop through a list the same as that of arrays
//    Is there a one-liner for this?
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
//    Has to be a shorter way of writing this
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
//    Should've used object equality stuffs to make this work
//    Actually, I've already defined a method called containsFile
    if (containsFile(file.getName())) {
      throw new IllegalArgumentException();
    } else {
      directory.add(file);
    }
  }

  public boolean removeFile(String filename) {
//      I feel like I have to implement object equality right?
//      Can't use .remove() in Set because I don't know what the type of the file to be
//      removed is
//    Not sure if it returns false if nothing has been removed. Requires testing
    return directory.removeIf(docFile -> docFile.getName() == filename);
  }

  public DocFile getFile(String filename) {
    for (DocFile docFile : directory) {
      if (docFile.getName() == filename) {
        return docFile;
      }
    }
//    Why doesn't functional programming work here?
//    return directory.stream()
//            .filter(docFile -> docFile.getName() != filename)
//            .collect(Collectors.toList())
//            .get(0);
    return null;
  }

}
