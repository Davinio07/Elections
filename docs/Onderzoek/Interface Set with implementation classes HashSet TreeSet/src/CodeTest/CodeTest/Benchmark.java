package CodeTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * tests for HashSet vs. TreeSet for 'add', 'contains', and 'remove' operations.
 */
public class Benchmark {

    // 10 times repeated
    private static final int NUMBER_OF_RUNS = 10;

    // the N sizes I tested
    private static final int[] SIZES_TO_TEST = { 10_000, 100_000, 1_000_000 };

    // Random object (imported)
    // seed 123 produces the exact same "random" numbers every time, making our test fair.
    private static final Random random = new Random(123);


    /**
     * Main Function of the code. The styling is done with the prints.
     */
    public static void main(String[] args) {
        System.out.println("Set Performance Benchmark (Average over " + NUMBER_OF_RUNS + " runs)");
        System.out.println("---------------------------------------------------------------------------------");

        // Print the header for our table
        System.out.printf("%-10s | %-12s | %-15s | %-15s | %-15s%n",
                "Set Type", "N (Size)", "Add (ms)", "Contains (ms)", "Remove (ms)");
        System.out.println("---------------------------------------------------------------------------------");

        // Loop 1: Go through each size (10k, 100k, 1M)
        for (int N : SIZES_TO_TEST) {

            // These variables will hold the SUM of all 10 runs
            double hashSetAddTotal = 0, hashSetContainsTotal = 0, hashSetRemoveTotal = 0;
            double treeSetAddTotal = 0, treeSetContainsTotal = 0, treeSetRemoveTotal = 0;

            // Generate the "Random" test data  for this size (N)
            // This makes the test fair.
            List<Integer> testData = generateRandomData(N);

            // Loop 2: Run the test 10 (N) times (to make it reliable)
            for (int i = 0; i < NUMBER_OF_RUNS; i++) {

                // 1. HashSet Test
                Set<Integer> hashSet = new HashSet<>();
                hashSetAddTotal += testAdd(hashSet, testData);
                hashSetContainsTotal += testContains(hashSet, testData);

                // For remove, we must re-fill the set, so we test 'add' again
                Set<Integer> hashSetForRemove = new HashSet<>();
                testAdd(hashSetForRemove, testData);
                hashSetRemoveTotal += testRemove(hashSetForRemove, testData);




                // 2. TreeSet Test
                Set<Integer> treeSet = new TreeSet<>();
                treeSetAddTotal += testAdd(treeSet, testData);
                treeSetContainsTotal += testContains(treeSet, testData);

                // Re-fill for the remove test for reliability again
                Set<Integer> treeSetForRemove = new TreeSet<>();
                testAdd(treeSetForRemove, testData);
                treeSetRemoveTotal += testRemove(treeSetForRemove, testData);
            }

            // Now that the 10 runs are done, calculate the average
            System.out.printf("%-10s | %-12d | %-15.3f | %-15.3f | %-15.3f%n",
                    "HashSet", N,
                    (hashSetAddTotal / NUMBER_OF_RUNS),
                    (hashSetContainsTotal / NUMBER_OF_RUNS),
                    (hashSetRemoveTotal / NUMBER_OF_RUNS));

            System.out.printf("%-10s | %-12d | %-15.3f | %-15.3f | %-15.3f%n",
                    "TreeSet", N,
                    (treeSetAddTotal / NUMBER_OF_RUNS),
                    (treeSetContainsTotal / NUMBER_OF_RUNS),
                    (treeSetRemoveTotal / NUMBER_OF_RUNS));
        }

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Benchmark finished.");
    }

    /**
     * [ANSWERS SQ4: Measured 'add' performance]
     *
     * Tests the time to add all elements from a list to a set.
     * We pass in the 'set' (e.g., new HashSet<>()) and the 'data' to add.
     * It returns the time taken in milliseconds (ms).
     */
    private static double testAdd(Set<Integer> set, List<Integer> data) {
        // Get the time before the test starts.
        long startTime = System.nanoTime();

        // The test loops through all data and add it
        for (Integer item : data) {
            set.add(item);
        }

        // Get the time after the test
        long endTime = System.nanoTime();

        // Return the difference, converted from nanoseconds to milliseconds
        return (endTime - startTime) / 1_000_000.0;
    }

    /**
     * [ANSWERS SQ4: Measured 'contains' performance]
     *
     * Tests the time to check if a set contains every item from a list.
     * The set should already be full for this test.
     */
    private static double testContains(Set<Integer> set, List<Integer> data) {
        long startTime = System.nanoTime();

        // The test: loop through all data and check 'contains'
        for (Integer item : data) {
            set.contains(item);
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000.0;
    }

    /**
     * [ANSWERS SQ4: Measured 'remove' performance]
     *
     * Tests the time to remove every item in a list from a set.
     * The set should be full for this test.
     */
    private static double testRemove(Set<Integer> set, List<Integer> data) {
        long startTime = System.nanoTime();

        // The test: loop through all data and 'remove' it
        for (Integer item : data) {
            set.remove(item);
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000.0;
    }

    /**
     * Creates a list of N random integers.
     * We do this because we want both loops to have the smame randomized numbers ofcourse, just like before.
     */
    private static List<Integer> generateRandomData(int N) {
        List<Integer> data = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            data.add(random.nextInt());
        }
        return data;
    }
}