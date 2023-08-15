package com.maddiesmarket.springadlister.utils;

import java.util.ArrayList;
import java.util.List;

public class Lists {
    public static <T> List<T> findMissingElements(List<T> list1, List<T> list2) {
        List<T> missingElements = new ArrayList<>();

        for (T element : list1) {
            if (!list2.contains(element)) {
                missingElements.add(element);
            }
        }

        return missingElements;
    }

    public static <T> List<T> findNewElements(List<T> list1, List<T> list2) {
        List<T> newElements = new ArrayList<>();

        for (T element : list2) {
            if (!list1.contains(element)) {
                newElements.add(element);
            }
        }

        return newElements;
    }

}
