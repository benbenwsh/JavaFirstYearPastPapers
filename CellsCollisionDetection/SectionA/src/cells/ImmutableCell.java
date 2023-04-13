package cells;

import java.util.Objects;

public class ImmutableCell<T> implements Cell<T> {

  private final T value;

  public ImmutableCell(T value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }

    this.value = value;
  }

  @Override
  public void set(T value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public T get() {
    return this.value;
  }

  @Override
  public boolean isSet() {
    return this.value != null;
  }

//  Why can't I do obj instanceof ImmutableCell<T> instead of using wildcard ?
  @Override
  public boolean equals(Object o) {
//    * No need null check because null instanceof ... returns false
//    * Should be instanceof Cell (without <?>) to check for instanceof
//    * a generic type
//    * Could use && to make this one line
    if (o == null) return false;
    if (!(o instanceof ImmutableCell<?> that)) return false;
    return value.equals(that.value);
  }

//  Is it necessary to write hashCode?
//  * yes
  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
