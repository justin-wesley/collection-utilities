/*
 * Copyright 2017 Justin Wesley
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.wesleyhome.collection;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

import static java.util.Collections.unmodifiableList;

public class PartitionIterator<T> implements Iterator<List<T>> {

    private final List<T> backingList;
    private final Iterator<T> backingIterator;
    private final int listSize;
    private int index;
    private final int size;

    public PartitionIterator(Iterable<T> iterable, int listSize) {
        if (iterable instanceof List) {
            List<T> list = (List<T>) iterable;
            this.backingList = unmodifiableList(list);
        } else if (iterable instanceof Collection) {
            this.backingList = ((Collection<T>) iterable).stream().collect(toImmutableList());
        } else {
            this.backingList = StreamSupport.stream(iterable.spliterator(), false).collect(toImmutableList());
        }
        this.size = this.backingList.size();
        this.listSize = listSize;
        index = 0;
        backingIterator = null;
    }

    public PartitionIterator(Iterator<T> iterator, int listSize) {
        backingList = null;
        backingIterator = iterator;
        this.listSize = listSize;
        size = -1;
    }

    private <t> Collector<t, List<t>, List<t>> toImmutableList() {
        return Collector.of(ArrayList::new, List::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, Collections::unmodifiableList);
    }

    @Override
    public boolean hasNext() {
        return backingIterator != null ? backingIterator.hasNext() : index < size;
    }

    @Override
    public List<T> next() {
        if (backingIterator != null) {
            List<T> list = new ArrayList<>();
            do {
                list.add(backingIterator.next());
            } while (backingIterator.hasNext() && list.size() < this.listSize);
            return unmodifiableList(list);
        } else {
            int start = index;
            int endIndex = start + listSize;
            if (endIndex > size) {
                endIndex = size;
            }
            index = endIndex;
            return unmodifiableList(this.backingList.subList(start, endIndex));
        }
    }
}