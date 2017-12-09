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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

public class PartitionIterable<T> implements Iterable<List<T>>, Closeable {
    private final Iterator<T> backingIterable;
    private final int listSize;
    private InputStream inputStream;

    public PartitionIterable(final T[] arr, final int listSize) {
        this(asList(arr), listSize);
    }

    public PartitionIterable(final Iterable<T> backingIterable, final int listSize) {
        this(backingIterable.iterator(), listSize);
    }

    public PartitionIterable(final Iterator<T> backingIterator, final int listSize) {
        this.backingIterable = backingIterator;
        this.listSize = listSize;
    }

    public PartitionIterable(final InputStream is, final int listSize, Function<InputStream, Iterator<T>> function) {
        this(function.apply(is), listSize);
        inputStream = is;
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new PartitionIterator<>(backingIterable, listSize);
    }

    public Stream<List<T>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public Stream<List<T>> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }


    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
