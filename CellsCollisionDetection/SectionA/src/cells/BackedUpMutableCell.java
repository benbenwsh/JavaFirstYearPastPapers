package cells;

import java.util.ArrayList;
import java.util.List;

public class BackedUpMutableCell<T> extends MutableCell<T> implements BackedUpCell<T> {

// * Model answer uses a variable size to eliminate the use of .size()
// * However, I do not care.
  private final List<T> previousValues;
  private final String mode;
  private final int limit;

  public BackedUpMutableCell() {
    super();
    this.previousValues = new ArrayList<>();
    this.mode = "unbounded backup";
    this.limit = Integer.MAX_VALUE;
  }

//  Is this a good practice? (calling this() and overwriting mode instead of initialising the attributes
//  one by one
//  Turns out, I can't use this because of declaring my attributes as final
  public BackedUpMutableCell(int limit) {
    super();
    if (limit < 0) {
      throw new IllegalArgumentException();
    }
    this.previousValues = new ArrayList<>();
    this.mode = "bounded backup";
    this.limit = limit;
  }

  @Override
  public void set(T value) {
    if (isSet()) {
      previousValues.add(get());

      if (previousValues.size() > limit) {
        previousValues.remove(0);
      }
    }
    super.set(value);
  }

  @Override
  public boolean hasBackup() {
    return !previousValues.isEmpty();
  }

//  Stuck here for a while: did not put super and did not put remove
//  (because the overridden set method puts the value back into the list
//  So revertToPrevious would do nothing if we use the current set method
  @Override
  public void revertToPrevious() {
    if (!hasBackup()) {
      throw new UnsupportedOperationException();
    }

    super.set(previousValues.remove(previousValues.size()-1));
  }
}
