import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class Main {

    public static TimeChecker timeChecker = new TimeChecker();
    public static ArrayList<Integer> listToTest = new ArrayList<>();

    public static void main(String[] args) {

        //create a list with data
        try {
            createListToTest(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
            System.out.println("Created testing list with a size of: " + args[1] + " and a starting number of " + args[0]);
        }catch (Exception e){
            System.err.println("Error within the arguments: " + e);
        }

        //run the benchmark
        runBenchmark();

    }

    public static void createListToTest(int startingNumber, int numberOfLoops){
        listToTest.add(startingNumber);

        for (int i = 1; i < numberOfLoops; i++) {
            listToTest.add(startingNumber*i*2);
        }

        System.out.println(listToTest);
    }

    public static void runBenchmark() {
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
    }

    public static void forkJoinSort(ArrayList<Integer> arrayOfNumbers) throws Exception {
        //create a forkjoinpool and assign a newly created task to it
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        sortTask newTask = new sortTask(arrayOfNumbers);

        //execute the assigned task
        forkJoinPool.execute(newTask);

        //get the sorted array
        int[] listToTest = newTask.get();
    }

    public static int[] generateList(int maxNumber, int maxSize){
        ArrayList<Integer> integers = new ArrayList<>();
        int number;

        for (int i = 0; i < maxSize; i++) {
            number = (int) Math.floor(Math.random()*(maxNumber+1));
            integers.add(number);
        }

        return Arrays.stream(integers.toArray(new Integer[0])).mapToInt(i->i).toArray();
    }


}



