import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class Main {

    public static TimeChecker timeChecker = new TimeChecker();
    public static ArrayList<Integer> listToTest = new ArrayList<>();

    public static void main(String[] args) {
        String introText = "     ____.                     __________                     .__                          __    \n" +
                "    |    |____ ___  _______    \\______   \\ ____   ____   ____ |  |__   _____ _____ _______|  | __\n" +
                "    |    \\__  \\\\  \\/ /\\__  \\    |    |  _// __ \\ /    \\_/ ___\\|  |  \\ /     \\\\__  \\\\_  __ \\  |/ /\n" +
                "/\\__|    |/ __ \\\\   /  / __ \\_  |    |   \\  ___/|   |  \\  \\___|   Y  \\  Y Y  \\/ __ \\|  | \\/    < \n" +
                "\\________(____  /\\_/  (____  /  |______  /\\___  >___|  /\\___  >___|  /__|_|  (____  /__|  |__|_ \\\n" +
                "              \\/           \\/          \\/     \\/     \\/     \\/     \\/      \\/     \\/           \\/";
        String subText = " -----------------------------------> By M-TD Copyright 2023 <-----------------------------------\n";

        //show intro text
        System.out.println(introText);
        System.out.println(subText);

        //check if data is added into the args, if start the benchmark with default values
        if (args.length == 0){
            createListToTest(1000, 12);
        }else{
            //create a list with data and catch errors if necessary
            try {
                createListToTest(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                System.out.println("Created testing list with a size of: " + args[1] + " and a starting number of " + args[0]);
            } catch (Exception e) {
                System.err.println("Error within the arguments: " + e);
            }
        }

        //run the benchmark
        runBenchmark();
    }

    //function to create a list with values to test
    public static void createListToTest(int startingNumber, int numberOfLoops) {
        //add the default value
        listToTest.add(startingNumber);

        //create a temp variable
        int temp = startingNumber;
        int valueToCalculate;

        //double the last value and add into the array
        for (int i = 2; i < numberOfLoops; i++) {
            valueToCalculate = temp * 2;
            listToTest.add(valueToCalculate);
            temp = valueToCalculate;
        }

        //print the total list
        System.out.println("Generated a list with numbers to test:");
        System.out.println(listToTest);
    }

    //function to start the benchmark and print the time of how long it takes
    public static void runBenchmark() {
        long totalTime = timeChecker.checkCommandTime(()->{
            ArrayList<Long> result;

            //go through each number and test them 10 times
            for (int i : listToTest) {
                result = new ArrayList<>();
                //generate list with random numbers
                int[] ints = generateList(1000000, i);
                ArrayList<Integer> listWithInts = Arrays.stream(ints).boxed().collect(Collectors.toCollection(ArrayList::new));

                for (int j = 0; j < 10; j++) {

                    //sort the numbers and check how long it takes
                    long time = timeChecker.checkCommandTime(() -> {
                        try {
                            forkJoinSort(listWithInts);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                    //print the amount of sorted numbers and how long it took to sort
                    result.add(time);
                }

                //calculate and print the average time in ms
                System.out.println("time in ms of " + i + ": " + result);
            }
        });

        //print the total time
        System.out.println("Total time needed for executing benchmark: " + totalTime + " milliseconds");
    }

    //function to sort a list with numbers using the java forkjoin method
    public static void forkJoinSort(ArrayList<Integer> arrayOfNumbers) throws Exception {
        //create a forkjoinpool and assign a newly created task to it
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        sortTask newTask = new sortTask(arrayOfNumbers);

        //execute the assigned task
        forkJoinPool.execute(newTask);

        //get the sorted array
        int[] listToTest = newTask.get();
    }

    //function to create a list with random numbers
    public static int[] generateList(int maxNumber, int maxSize) {
        //create new empty list
        ArrayList<Integer> integers = new ArrayList<>();
        int number;

        //add random numbers to the list
        for (int i = 0; i < maxSize; i++) {
            number = (int) Math.floor(Math.random() * (maxNumber + 1));
            integers.add(number);
        }

        //return the list
        return Arrays.stream(integers.toArray(new Integer[0])).mapToInt(i -> i).toArray();
    }
}