import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class sortTask extends RecursiveTask<int[]> {

    private final ArrayList<Integer> dataArray;

    public sortTask(ArrayList<Integer> dataArray) {
        this.dataArray = dataArray;
    }

    @Override
    protected int[] compute() {
        //create a new empty list
        ArrayList<Integer> sortedList;

        //convert the arraylist to an integer array
        int[] integers = dataArray.stream().mapToInt(Integer::intValue).toArray();

        //check if the array is bigger than 1
        if (integers.length > 1) {
            //split the array in 2
            int[] firstHalf = Arrays.copyOfRange(integers, 0, (integers.length / 2));
            int[] secondHalf = Arrays.copyOfRange(integers, (integers.length / 2), integers.length);

            //convert the arrays back into arraylists
            ArrayList<Integer> leftHalf = (ArrayList<Integer>) Arrays.stream(firstHalf).boxed().collect(Collectors.toList());
            ArrayList<Integer> rightHalf = (ArrayList<Integer>) Arrays.stream(secondHalf).boxed().collect(Collectors.toList());

            //create 2 new sorttasks that each get a half
            var leftTask = new sortTask(leftHalf);
            var rightTask = new sortTask(rightHalf);

            //let the tasks split themselves into more tasks
            leftTask.fork();
            rightTask.fork();

            //get the output of the tasks
            int[] leftOutput = leftTask.join();
            int[] rightOutput = rightTask.join();

            //merge the halves back together
            int[] sortedArray = merge(leftOutput, rightOutput);

            //convert the output back into an arraylist
            sortedList = (ArrayList<Integer>) Arrays.stream(sortedArray).boxed().collect(Collectors.toList());
        }else {
            //if the size of the array is smaller than one, the array is automatically sorted
            sortedList = dataArray;
        }

        //return the sorted arraylist
        return sortedList.stream().mapToInt(Integer::intValue).toArray();
    }

    public static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int leftPos = 0, rightPos= 0, mergedPos = 0;
        while(leftPos < left.length && rightPos < right.length) {
            if (left[leftPos] < right[rightPos]) {
                result[mergedPos++] = left[leftPos++];
            } else {
                result[mergedPos++] = right[rightPos++];
            }
        }
        while (leftPos < left.length) result[mergedPos++] = left[leftPos++];
        while (rightPos < right.length) result[mergedPos++] = right[rightPos++];
        return result;
    }

}