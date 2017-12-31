package net.coderodde;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.coderodde.util.MergesortInversionCounter;
import net.coderodde.util.NaturalMergesortInversionCounter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * This class implements benchmark for inversion counter algorithms.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 31, 2017)
 */
public class MyBenchmark {
    
    private static final int ARRAY_LENGTH = 1_000_000;
    private static final int MINIMUM_INTEGER_VALUE = -100_000;
    private static final int MAXIMUM_INTEGER_VALUE = +100_000;
    private static final int RUN_LENGTH_IN_PRESORTED_ARRAY = 2000;
    
    @State(Scope.Thread)
    public static class MyRandomState {
        
        private final Random random = new Random();
        Integer[] array;
        
        @Setup(Level.Trial)
        public void createRandomArray() {
            array = createRandomIntegerArray(ARRAY_LENGTH,
                                             MINIMUM_INTEGER_VALUE,
                                             MAXIMUM_INTEGER_VALUE, 
                                             random);
        }
    }
    
    @State(Scope.Thread)
    public static class MyPresortedState {
        private final Random random = new Random();
        Integer[] array;
        
        @Setup(Level.Trial)
        public void createPresortedArray() {
            array = createPresortedIntegerArray(ARRAY_LENGTH,
                                                MINIMUM_INTEGER_VALUE,
                                                MAXIMUM_INTEGER_VALUE,
                                                random);
        }
    }
    
    @Benchmark 
    @BenchmarkMode(Mode.AverageTime) 
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testMergesortOnRandomArray(MyRandomState state) {
        MergesortInversionCounter.count(state.array);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.AverageTime) 
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testMergesortOnPresortedArray(MyPresortedState state) {
        MergesortInversionCounter.count(state.array);
    }
    
    @Benchmark 
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testNaturalMergesortOnRandomArray(MyRandomState state) {
        NaturalMergesortInversionCounter.count(state.array);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testNaturalMergesortOnPresortedArray(MyPresortedState state) {
        NaturalMergesortInversionCounter.count(state.array);
    }
    
    private static Integer[] createRandomIntegerArray(int length,
                                                      int minimumIntegerValue,
                                                      int maximumIntegerValue,
                                                      Random random) {
        Integer[] array = new Integer[length];
        
        for (int i = 0; i < length; ++i) {
            array[i] = getRandomIntegerValue(minimumIntegerValue,
                                             maximumIntegerValue,
                                             random);
        }
        
        return array;
    }
    
    private static Integer[] 
        createPresortedIntegerArray(int length,
                                    int minimumIntegerValue,
                                    int maximumIntegerValue,
                                    Random random) {
        Integer[] randomArray = createRandomIntegerArray(length,
                                                         minimumIntegerValue,
                                                         maximumIntegerValue,
                                                         random);
        for (int i = 0; i < length; i += RUN_LENGTH_IN_PRESORTED_ARRAY) {
            Arrays.sort(randomArray,
                        i, 
                        Math.min(length, i + RUN_LENGTH_IN_PRESORTED_ARRAY));
        }
        
        return randomArray;
    }
    
    private static Integer getRandomIntegerValue(int minimumIntegerValue,
                                                 int maximumIntegerValue,
                                                 Random random) {
        return minimumIntegerValue + random.nextInt(maximumIntegerValue -
                                                    minimumIntegerValue + 1);
    }
    
    public static void main(String[] args) 
    throws RunnerException {
        Options options = new OptionsBuilder()
                          .include(MyBenchmark.class.getSimpleName())
                          .warmupIterations(5)
                          .measurementIterations(10)
                          .forks(1)
                          .build();
        new Runner(options).run();
    }
}
