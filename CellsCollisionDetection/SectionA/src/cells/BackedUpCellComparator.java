package cells;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BackedUpCellComparator<U> implements Comparator<BackedUpCell<U>> {

  private final Comparator<U> valueComparator;

  public BackedUpCellComparator(Comparator<U> valueComparator) {
      this.valueComparator = valueComparator;
  }

  @Override
  public int compare(BackedUpCell<U> o1, BackedUpCell<U> o2) {
    if (!o1.isSet() && o2.isSet()) return -1;
    if (o1.isSet() && !o2.isSet()) return 1;
    if (!o1.isSet() && !o2.isSet()) return 0;
//    * while loop is better here because it causes redundant checking
//    * the first 3 statements
//    * However, you need a Deque (doubled-ended queue, where elements can
//    * be added to or removed at both ends, can be used as a stack)
//    * to keep track of previous values
//    * And pop all of it at the end of the compare method before return
    if (valueComparator.compare(o1.get(), o2.get()) == 0) {
      if (o1.hasBackup() && o2.hasBackup()) {
        U o1Val = o1.get();
        U o2Val = o2.get();
        o1.revertToPrevious();
        o2.revertToPrevious();
        int result = compare(o1, o2);
        o1.set(o1Val);
        o2.set(o2Val);
        return result;
      }
      if (!o1.hasBackup() && o2.hasBackup()) return -1;
      if (o1.hasBackup() && !o2.hasBackup()) return 1;
    }
    return valueComparator.compare(o1.get(), o2.get());
  }
}

// Finished Section A in 2:09