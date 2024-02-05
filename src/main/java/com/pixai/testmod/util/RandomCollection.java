package com.pixai.testmod.util;

import net.minecraft.util.RandomSource;

import java.util.NavigableMap;
import java.util.TreeMap;

public class RandomCollection<T>{
    private final NavigableMap<Double, T> map = new TreeMap<Double, T>();
    private RandomSource random;
    private double total = 0;

    public RandomCollection() {
    }

    public void SetRandomSource(RandomSource source){
        this.random = source;
    }

    public RandomCollection<T> add(double weight, T result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public T next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}
