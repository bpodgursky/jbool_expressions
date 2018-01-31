package com.bpodgursky.jbool_expressions.utils;


public abstract class Optional<T> {

  public static <T> Optional<T> empty() {
    return Empty.INSTANCE;
  }

  public static <T> Optional<T> of(T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return new Present<T>(reference);
  }

  private Optional() {
  }

  public abstract boolean isPresent();

  public abstract T get();

  private static final class Empty<T> extends Optional<T> {
    private static final Empty INSTANCE = new Empty();

    private Empty() {
    }

    @Override
    public boolean isPresent() {
      return false;
    }

    @Override
    public T get() {
      throw new IllegalStateException("Optional.get() cannot be called on an empty value");
    }

    @Override
    public boolean equals(Object object) {
      return object == this;
    }

    @Override
    public int hashCode() {
      return 1502476572;
    }

    @Override
    public String toString() {
      return "Optional.empty()";
    }

  }

  private static final class Present<T> extends Optional<T> {
    private final T reference;

    private Present(T reference) {
      this.reference = reference;
    }

    @Override
    public boolean isPresent() {
      return true;
    }

    @Override
    public T get() {
      return this.reference;
    }

    @Override
    public boolean equals(Object object) {
      if (object instanceof Present) {
        Present<?> other = (Present)object;
        return this.reference.equals(other.reference);
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return 1502476572 + this.reference.hashCode();
    }

    @Override
    public String toString() {
      return "Optional.of(" + this.reference + ")";
    }
  }

}
