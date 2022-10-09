package com.example.demo.utils;

import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

/**
 */
public class CollectorExt {

    public static <T> Collector<T, List<List<T>>, List<List<T>>> slice(Integer size) {
        return slice((list, v) -> list.size() >= size);
    }


    /**
     * @param shouldSlice (in1, in2 , out)
     *                    in1: current list ,
     *                    in2: current element,
     *                    out: slice if its true
     */
    public static <T> Collector<T, List<List<T>>, List<List<T>>> slice(BiFunction<List<T>, T, Boolean> shouldSlice) {
        return new CollectorAdapt<>(
                ArrayList::new,
                (list, v) -> {
                    if (list.isEmpty() || shouldSlice.apply(list.get(list.size() - 1), v)) {
                        list.add(new ArrayList<T>() {{
                            add(v);
                        }});
                    } else {
                        list.get(list.size() - 1).add(v);
                    }
                },
                (v0, v1) -> {
                    v0.addAll(v1);
                    return v0;
                },
                Function.identity()
        );
    }


    /**
     * @param distinct (in , out)
     */
    public static <T, R> Collector<T, Map<R, T>, List<T>> distinctBy(Function<T, R> distinct) {
        return new CollectorAdapt<>(
                HashMap::new,
                (map, t) -> map.put(distinct.apply(t), t),
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                },
                (map) -> new ArrayList<T>(map.values())
        );
    }


    @AllArgsConstructor
    static class CollectorAdapt<T, A, B> implements Collector<T, A, B> {
        private Supplier<A> supplier;
        private BiConsumer<A, T> accumulator;
        private BinaryOperator<A> combiner;
        private Function<A, B> finisher;

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, B> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

}
