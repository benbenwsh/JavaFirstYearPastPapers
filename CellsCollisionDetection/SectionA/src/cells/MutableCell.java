package cells;

// Not familiar with how to implement/extend generic interface/class
// * I realised you do not have to use T as the generic type, i.e.,
// * you do not need to follow the type parameter used in the interface/
// * parent class, it could be any generic type/type/etc.
// * However, the type parameter of the child class must match that of
// * the interface implemented
// * Exactly the same as model answer

public class MutableCell<U> implements Cell<U> {

  private U value;

//  No need to do <U> here
  public MutableCell() {
    this.value = null;
  }

  public MutableCell(U value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }

    this.value = value;
  }

  @Override
  public void set(U value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }

    this.value = value;
  }

  @Override
  public U get() {
    return this.value;
  }

  @Override
  public boolean isSet() {
    return this.value != null;
  }
}
