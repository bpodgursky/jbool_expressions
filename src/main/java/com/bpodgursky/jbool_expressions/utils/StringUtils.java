package com.bpodgursky.jbool_expressions.utils;

import java.util.Iterator;
import java.util.Objects;

public class StringUtils {


  public static String join(Object[] array, String separator) {
    return array == null ? null : join(array, separator, 0, array.length);
  }

  public static String join(Object[] array, String separator, int startIndex, int endIndex) {
    if (array == null) {
      return null;
    } else {
      if (separator == null) {
        separator = "";
      }

      int noOfItems = endIndex - startIndex;
      if (noOfItems <= 0) {
        return "";
      } else {
        StringBuilder buf = new StringBuilder(noOfItems * 16);

        for(int i = startIndex; i < endIndex; ++i) {
          if (i > startIndex) {
            buf.append(separator);
          }

          if (array[i] != null) {
            buf.append(array[i]);
          }
        }

        return buf.toString();
      }
    }
  }

  public static String join(Iterable<?> iterable, String separator) {
    return iterable == null ? null : join(iterable.iterator(), separator);
  }

  public static String join(Iterator<?> iterator, String separator) {
    if (iterator == null) {
      return null;
    } else if (!iterator.hasNext()) {
      return "";
    } else {
      Object first = iterator.next();
      if (!iterator.hasNext()) {
        String result = Objects.toString(first, "");
        return result;
      } else {
        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
          buf.append(first);
        }

        while(iterator.hasNext()) {
          if (separator != null) {
            buf.append(separator);
          }

          Object obj = iterator.next();
          if (obj != null) {
            buf.append(obj);
          }
        }

        return buf.toString();
      }
    }
  }
}
