/*
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.subtitles.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for split.
 */
public final class SplitUtils {

    private SplitUtils() {
    }

    /**
     * Split input list in slices.
     * Slices in result will be not overlapped and will contains all contents of input items that cover it.
     *
     * @param input list of input sliced with specified content.
     * @param <T>   content type.
     * @return list of not overlapped slices.
     */
    public static <T> List<Slice<T>> split(List<Slice<T>> input) {
        List<Slice<T>> slices = new ArrayList<>();

        Queue<Slice<T>> tails = new PriorityQueue<>(input.size(), Comparator.comparingLong(Slice::getBegin));
        input.forEach(tails::offer);

        Queue<Slice<T>> heads = new PriorityQueue<>(input.size(), Comparator.comparingLong(Slice::getEnd));
        heads.offer(tails.poll());

        long slicepoint = heads.peek().begin;

        while (!heads.isEmpty() || !tails.isEmpty()) {
            Slice head = heads.peek();
            Slice tail = tails.peek();

            boolean headEnd = tail == null || tail.begin > head.end;

            long begin = Math.max(slicepoint, head.begin);
            long end = headEnd ? head.end : tail.begin;

            if (begin != end) {
                Slice<T> slice = new Slice<>(begin, end);

                heads.stream().sorted(Comparator.comparingInt(input::indexOf))
                        .flatMap(Slice::contentStream)
                        .forEach(slice.contents::add);

                slices.add(slice);

                slicepoint = slice.end;
            }

            if (headEnd) {
                heads.poll();
            }

            if (!headEnd || heads.isEmpty()) {
                if (tail != null) {
                    heads.offer(tails.poll());
                }
            }
        }
        return slices;
    }

    /**
     * Builder to create {@link Slice} wrapper for content.
     * For 'begin' and 'end' values computation use setBeginGetter and setEndGetter functions.
     *
     * @param <T> content type.
     */
    public static class SliceBuilder<T> {
        private ToLongFunction<T> beginGetter;
        private ToLongFunction<T> endGetter;

        public SliceBuilder<T> setBeginGetter(ToLongFunction<T> beginGetter) {
            this.beginGetter = beginGetter;
            return this;
        }

        public SliceBuilder<T> setEndGetter(ToLongFunction<T> endGetter) {
            this.endGetter = endGetter;
            return this;
        }

        public Slice<T> build(T t) {
            return new Slice<>(beginGetter.applyAsLong(t), endGetter.applyAsLong(t), t);
        }
    }

    /**
     * Sliced interval that contains merged contents, for all overlapped intervals.
     *
     * @param <T> content type.
     */
    public static class Slice<T> {
        long begin;
        long end;
        final List<T> contents = new ArrayList<>();

        public Slice(long begin, long end) {
            this.begin = begin;
            this.end = end;
        }

        public Slice(long begin, long end, T content) {
            this.begin = begin;
            this.end = end;
            this.contents.add(content);
        }

        public long getBegin() {
            return begin;
        }

        public long getEnd() {
            return end;
        }

        public List<T> getContents() {
            return contents;
        }

        Stream<T> contentStream() {
            return contents.stream();
        }

        @Override
        public String toString() {
            return String.format("[%d,%d] {%s}", begin, end,
                    contents.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")));
        }
    }
}
