package com.coreoz.openapi;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ListMerger {
	/**
	 * This method compares two iterable collections (left and right) element by element using the provided Comparator<T>. It handles the elements based on their presence in the iterables as follows:<br>
     * - Left-only elements: Elements that appear only in the left iterable are processed using the leftOnly consumer.<br>
     * - Right-only elements: Elements that appear only in the right iterable are processed using the rightOnly consumer.<br>
     * - Common elements: Elements that are present in both iterables are processed using the both bi-consumer, which receives corresponding elements from both left and right.<br>
     * <br>
     * This utility method is useful for performing operations such as finding differences between two collections, synchronizing data, or merging collections based on custom logic defined by the consumers.
	 */
	public static <T, L extends T, R extends T> void compare(
			final Iterable<L> left,
			final Iterable<R> right,
			final Comparator<T> compareItems,
			Consumer<L> leftOnly,
			Consumer<R> rightOnly,
			BiConsumer<L, R> both
		) {
		List<L> la = Lists.newArrayList(left);
		List<R> lb = Lists.newArrayList(right);
		la.sort(compareItems);
		lb.sort(compareItems);
		Iterator<L> itas = la.iterator();
		Iterator<R> itbs = lb.iterator();

		L a = Iterators.getNext(itas, null);
		R b = Iterators.getNext(itbs, null);
		while (a != null || b != null) {
			Integer compare = a != null && b != null ? compareItems.compare(a, b) : null;

			if (compare != null && compare == 0) {
				both.accept(a, b);
				a = Iterators.getNext(itas, null);
				b = Iterators.getNext(itbs, null);
			} else if (b == null || (compare != null && compare < 0)) {
				leftOnly.accept(a);
				a = Iterators.getNext(itas, null);
			} else {
				rightOnly.accept(b);
				b = Iterators.getNext(itbs, null);
			}
		}
	}
}
