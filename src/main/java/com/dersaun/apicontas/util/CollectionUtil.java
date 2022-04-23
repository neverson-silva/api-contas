package com.dersaun.apicontas.util;

import com.google.common.base.Joiner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CollectionUtil {

    @SuppressWarnings("unchecked")
    public static void orderByKey(Map<? extends Comparable, ?> valores) {
        valores.entrySet()
                .stream()
                .sorted( Map.Entry.comparingByKey() )
                .collect(Collectors.toMap(
                        (Function<Map.Entry, Object>) Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public static IntStream range(int start, int size) {
        return IntStream.rangeClosed(start, size);
    }

    @SuppressWarnings("unchecked")
    public static <K extends Integer, V> void forEach(List<V> valores,
                                                     BiConsumer<K , V> biConsumer) {

        for (Integer i = 0; i <= valores.size(); i++ ) {
            V current = valores.get(i);
            biConsumer.accept((K) i, current);
        }
    }

    public static String join(List<?> valores, String joiner) {
        return Joiner.on(joiner).join(valores);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> groupBy(List<V> valores, Function<? super V, ? extends K> classifier) {
        return (Map<K, V>) valores.stream().collect(Collectors.groupingBy(classifier));
    }

//    public static Map<> orderByKeyReverse() {
//
//    }
//
//    public static Map<> order() {
//
//    }
//
//    public static Map<> orderReverse() {
//
//    }
//
//    public static List<> sort() {
//
//    }
//
//    public static List<> sortReverse() {
//
//    }
}
