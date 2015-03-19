package ics466.timeit;

/**
 * Created by seikochan on 3/16/15.
 */
public class TimeItActivity {
    private String actName;
    private long timeStart;
    private long timeEnd;
    private long timeTotal;

    public TimeItActivity(){
        actName = "";
        timeStart= -1;
        timeEnd = -1;
        timeTotal = -1;
    }

    public TimeItActivity(String name, long time){
        actName = name;
        timeTotal = time;
    }

    public TimeItActivity(String name, long start, long end){
        //error checking for start before end?

    }

    public void setActName(String name){
        actName = name;
    }

    public String getActName(){
        return actName;
    }

    public void setTimeStart(long start){
        timeStart = start;
    }

    public void setTimeEnd(long end){
        timeEnd = end;
    }

    public long getTimeStart(){
        return timeStart;
    }

    public long getTimeEnd(){
        return timeEnd;
    }

    public void setTimeTotal(long total){
        timeTotal= total;
    }

    public long getTimeTotal(){
        return timeTotal;
    }

}
