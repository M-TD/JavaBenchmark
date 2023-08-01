import java.time.Instant;

public class TimeChecker {
    public TimeChecker(){

    }

    public long checkCommandTime(Runnable runnable){
        long start = Instant.now().toEpochMilli();
        runnable.run();
        long end = Instant.now().toEpochMilli();
        return end - start;
    }
}
