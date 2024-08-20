package com.coreoz.openapi;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ListMergerTest {
    @Test
    public void testCompare_RightOnly() {
        List<Integer> left = Arrays.asList(1, 2);
        List<Integer> right = Arrays.asList(3, 4);

        List<Integer> leftOnlyResults = new ArrayList<>();
        List<Integer> rightOnlyResults = new ArrayList<>();
        List<Integer> bothResultsLeft = new ArrayList<>();
        List<Integer> bothResultsRight = new ArrayList<>();

        Consumer<Integer> leftOnly = leftOnlyResults::add;
        Consumer<Integer> rightOnly = rightOnlyResults::add;
        BiConsumer<Integer, Integer> both = (l, r) -> {
            bothResultsLeft.add(l);
            bothResultsRight.add(r);
        };

        ListMerger.compare(left, right, Integer::compareTo, leftOnly, rightOnly, both);

        assertThat(leftOnlyResults).containsExactly(1, 2);
        assertThat(rightOnlyResults).containsExactly(3, 4);
        assertThat(bothResultsLeft).isEmpty();
        assertThat(bothResultsRight).isEmpty();
    }

    @Test
    public void testCompare_Both() {
        List<Integer> left = Arrays.asList(1, 2, 3, 4);
        List<Integer> right = Arrays.asList(3, 4, 5, 6);

        List<Integer> leftOnlyResults = new ArrayList<>();
        List<Integer> rightOnlyResults = new ArrayList<>();
        List<Integer> bothResultsLeft = new ArrayList<>();
        List<Integer> bothResultsRight = new ArrayList<>();

        Consumer<Integer> leftOnly = leftOnlyResults::add;
        Consumer<Integer> rightOnly = rightOnlyResults::add;
        BiConsumer<Integer, Integer> both = (l, r) -> {
            bothResultsLeft.add(l);
            bothResultsRight.add(r);
        };

        ListMerger.compare(left, right, Integer::compareTo, leftOnly, rightOnly, both);

        assertThat(leftOnlyResults).containsExactly(1, 2);
        assertThat(rightOnlyResults).containsExactly(5, 6);
        assertThat(bothResultsLeft).containsExactly(3, 4);
        assertThat(bothResultsRight).containsExactly(3, 4);
    }

    @Test
    public void testCompare_Empty() {
        List<Integer> left = Arrays.asList();
        List<Integer> right = Arrays.asList();

        List<Integer> leftOnlyResults = new ArrayList<>();
        List<Integer> rightOnlyResults = new ArrayList<>();
        List<Integer> bothResultsLeft = new ArrayList<>();
        List<Integer> bothResultsRight = new ArrayList<>();

        Consumer<Integer> leftOnly = leftOnlyResults::add;
        Consumer<Integer> rightOnly = rightOnlyResults::add;
        BiConsumer<Integer, Integer> both = (l, r) -> {
            bothResultsLeft.add(l);
            bothResultsRight.add(r);
        };

        ListMerger.compare(left, right, Integer::compareTo, leftOnly, rightOnly, both);

        assertThat(leftOnlyResults).isEmpty();
        assertThat(rightOnlyResults).isEmpty();
        assertThat(bothResultsLeft).isEmpty();
        assertThat(bothResultsRight).isEmpty();
    }
}
