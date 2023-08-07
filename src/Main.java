import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class Main {
    public static TimeChecker timeChecker = new TimeChecker();
    public static BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
    public static FileWriter fileWriter;

    public static void main(String[] args) throws IOException {
        boolean keepRunning = true;

        String introText = "     ____.                     __________                     .__                          __    \n" +
                           "    |    |____ ___  _______    \\______   \\ ____   ____   ____ |  |__   _____ _____ _______|  | __\n" +
                           "    |    \\__  \\\\  \\/ /\\__  \\    |    |  _// __ \\ /    \\_/ ___\\|  |  \\ /     \\\\__  \\\\_  __ \\  |/ /\n" +
                           "/\\__|    |/ __ \\\\   /  / __ \\_  |    |   \\  ___/|   |  \\  \\___|   Y  \\  Y Y  \\/ __ \\|  | \\/    < \n" +
                           "\\________(____  /\\_/  (____  /  |______  /\\___  >___|  /\\___  >___|  /__|_|  (____  /__|  |__|_ \\\n" +
                           "              \\/           \\/          \\/     \\/     \\/     \\/     \\/      \\/     \\/           \\/";

        String subText = " -----------------------------------> By M-TD Copyright 2023 <-----------------------------------\n";

        String menuOptions = "Menu Options:\n" +
                             " - R: Run the benchmark\n" +
                             " - C: Run a custom benchmark\n" +
                             " - H: Show this help menu\n" +
                             " - A: Show info about this program\n" +
                             " - Q: Quit the program\n";

        String exitMessage = "Thank you for using this program!";

        //show intro text
        System.out.println(introText);
        System.out.println(subText);
        System.out.println(menuOptions);

        //check if data is added into the args, if start the benchmark with default values
        if (args.length != 0){
            //create a list with data and catch errors if necessary
            try {
                //create a new log file and writer
                File logFile = createNewFile();
                fileWriter = new FileWriter(logFile);
                runBenchmark(createListToTest(Integer.parseInt(args[0]), Integer.parseInt(args[1]),fileWriter),fileWriter);
                System.out.println("Created testing list with a size of: " + args[1] + " and a starting number of " + args[0]);
            } catch (Exception e) {
                System.err.println("Error within the arguments: " + e);
            }
        }

        while (keepRunning){
            //ask input
            System.out.println("Please enter a command: ");

            //get input
            String input;
            try {
                input = consoleReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //error checking
            if (input == null){
                throw new NullPointerException("Input is null");
            }

            //do something with the input
            switch (input.toLowerCase()) {
                case "q" -> {
                    //exit the program
                    System.out.println("Exiting program...");
                    System.out.println(exitMessage);
                    keepRunning = false;
                }
                case "r" -> {
                    //create a new log file and writer
                    File logFile = createNewFile();
                    try {
                        fileWriter = new FileWriter(logFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //run the benchmark
                    System.out.println("Running Benchmark...");

                    //create new list and run benchmark
                    runBenchmark(createListToTest(1000, 12,fileWriter),fileWriter);

                    //exit filewriter
                    fileWriter.close();
                }
                case "a" -> {
                    //show about
                    System.out.println("Program made by M TD, for use in Java supported systems.");
                    System.out.println("M-_-TD on GitHub: https://github.com/M-TD");
                    System.out.println("Consider supporting me: https://www.buymeacoffee.com/mteda");
                    System.out.println("Copyright M-_-TD 2023\n");
                }
                case "c" -> {
                    try {
                        //create a new log file and writer
                        File logFile = createNewFile();
                        try {
                            fileWriter = new FileWriter(logFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        //run custom program
                        System.out.println("What starting number do you want to use?");
                        String startingNumber = consoleReader.readLine();
                        System.out.println("How many times does the starting number need to double in the list?");
                        String doublingNumber = consoleReader.readLine();

                        //create new list and run benchmark
                        runBenchmark(createListToTest(Integer.parseInt(startingNumber), Integer.parseInt(doublingNumber),fileWriter),fileWriter);

                        //exit filewriter
                        fileWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "h" -> {
                    //show help menu
                    System.out.println(menuOptions);
                }
                default -> System.out.println("Unrecognized input given!");
            }
        }
    }

    //function to create a list with values to test
    public static ArrayList<Integer> createListToTest(int startingNumber, int numberOfLoops, FileWriter fileWriter) throws IOException {
        //create new list
        ArrayList<Integer> listToTest = new ArrayList<>();

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

        //write to file
        fileWriter.write("Generated a list with numbers to test:\n");
        fileWriter.write(listToTest + "\n");

        return listToTest;
    }

    //function to start the benchmark and print the time of how long it takes
    public static void runBenchmark(ArrayList<Integer> listToTest, FileWriter fileWriter) throws IOException {
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

                //write to logfile
                try {
                    fileWriter.write("time in ms of " + i + ": " + result+"\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //print the total time
        System.out.println("Total time needed for executing benchmark: " + totalTime + " milliseconds");

        //write to logfile
        fileWriter.write("Total time needed for executing benchmark: " + totalTime + " milliseconds");
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

    //function to create a file with the current date and time as the name
    public static File createNewFile(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");
        LocalDateTime now = LocalDateTime.now();
        String fileName = "log-"+dtf.format(now)+".txt";
        return new File(fileName);
    }
}