package ics466.timeit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.RectF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.ViewGroup;
import android.app.*;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends ActionBarActivity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener{

    private final Context CONTEXT = this;

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    private Button btnStatsClose;
    private Button btnStatsOpen;
    private PopupWindow pwStats;
    int i = 1;

    private List<Event> eventsArrList;
    private static HashMap<Integer, List<WeekViewEvent>> wEventMapByMonth;
    private static HashMap<String, List<Event>> activityMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    System.out.println("CREATED MAIN!!!!!!!!!");


        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        wEventMapByMonth = new HashMap<>();
        activityMap = new HashMap<>();
        eventsArrList = new ArrayList<>();

            //setStatsDialogBox();


    }

    public void setStatsDialogBox(){

        btnStatsOpen = (Button) findViewById(R.id.btnStatsOpen);

        // add button listener
        btnStatsOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder((CONTEXT));
                final ArrayList<TimeItActivity> activityArrList = new ArrayList<TimeItActivity>();

                // create a close button
                builder.setNegativeButton(R.string.close,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // create a list adapter for Events
                //populate Statistics List
                Iterator iter = MainActivity.getActivityMap().entrySet().iterator();
                while(iter.hasNext()) {
                    Map.Entry pair = (Map.Entry) iter.next();
                    System.out.print("FOUND: " + pair.getKey() + "-> ");
                    List<Event> eventList = (List<Event>) pair.getValue();


                    long totTime = 0;
                    for (int i = 0; i < eventList.size(); i++) {
                        System.out.print(eventList.get(i).getId() + " - ");
                        long eventTime = eventList.get(i).getEndTime() - eventList.get(i).getStartTime();
                        totTime = totTime + eventTime;
                    }
                    TimeItActivity act = new TimeItActivity((String) pair.getKey(), totTime);
                    activityArrList.add(act);
                    System.out.println("!!!!!!!!!!!!!!!!!!!");
                }

                // populate ListView
                ListAdapter adapter = new ArrayAdapter<TimeItActivity>(
                        MainActivity.this, R.layout.stats_item, activityArrList ) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        //View itemView = convertView;
                        final ViewHolder mHolder;

                        TextView nameView;
                        TextView statView;
                        TextView totTimeView;

                        //Ensure we have a view to work with
                        if(convertView == null) {
                            convertView = getLayoutInflater().inflate(R.layout.stats_item, parent, false);

////                            //TODO prevent multiple instances of same item in list
//                            mHolder = new ViewHolder();
//
//                            mHolder.mText = (TextView) convertView.findViewById(R.id.actName);
//                            mHolder.mStat = (TextView) convertView.findViewById(R.id.intStatVal);

//                            convertView.setTag(mHolder);
                        }
//                        else{
//                            mHolder = (ViewHolder) convertView.getTag();
//                        }


                        //TODO switch on day, week, month, etc

                        //Populate list
                        //Find activity to work with
                        TimeItActivity currAct = activityArrList.get(position);

                        //Fill the view
                        nameView = (TextView) convertView.findViewById(R.id.actName);
                        nameView.setText(currAct.getActName());
                        //mHolder.mText.setText(currAct.getActName());

                        statView = (TextView) convertView.findViewById(R.id.intStatVal);
                        //calculate statistics
                        //right now based on 24hrs (86400000 ms)
                        double msDay = 86400000;
                        int stat = (int)((currAct.getTimeTotal()/msDay)*100);
                        statView.setText(Integer.toString(stat) + "%");
//                        mHolder.mStat.setText(Integer.toString(stat) + "%");

                        totTimeView = (TextView) convertView.findViewById(R.id.totTimeVal);
                        long millis = currAct.getTimeTotal();
                        String prettyTime = String.format("%d hr, %d min, %d sec",
                                TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                        );
                        totTimeView.setText(prettyTime);

                        //convertView.setClickable(false);
                        //TODO make the listView items unclickable (this no work :( )

                        return (convertView);
                    }

                    //helper class and cache mechanism that stores Views
                    class ViewHolder {
                        private TextView mText;
                        private TextView mStat;

                    }
                };

                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });

                AlertDialog alertDialog = builder.create();

                alertDialog.setTitle("Statistics:");

                alertDialog.show();
            }



        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.add_act:
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setTitle("Add Activity");
                dialog.setContentView(R.layout.customdialog);
                dialog.show();

                final EditText editText =(EditText)dialog.findViewById(R.id.shour);
                final EditText editText2 =(EditText)dialog.findViewById(R.id.ehour);
                final EditText editText3 =(EditText)dialog.findViewById(R.id.sminute);
                final EditText editText4 =(EditText)dialog.findViewById(R.id.eminute);
                final EditText editText5 =(EditText)dialog.findViewById(R.id.day);
                final EditText editText6 =(EditText)dialog.findViewById(R.id.smonth);
                final EditText editText7 =(EditText)dialog.findViewById(R.id.syear);
                final EditText editText8 =(EditText)dialog.findViewById(R.id.actname);
                final EditText editText9 =(EditText)dialog.findViewById(R.id.color);

                Button addButton = (Button)dialog.findViewById(R.id.addbutton);
                final Button cancelButton = (Button)dialog.findViewById(R.id.cancelbutton);

                addButton.setOnClickListener(new View.OnClickListener(){

                    public void onClick(View v){
                        String[] act = new String[11];
                        act[0] = editText.getText().toString();
                        act[1] = editText2.getText().toString();
                        act[2] = editText3.getText().toString();
                        act[3] = editText4.getText().toString();
                        act[4] = editText5.getText().toString();
                        act[5] = editText6.getText().toString();
                        act[6] = editText7.getText().toString();
                        act[7] = editText8.getText().toString();
                        act[8] = editText9.getText().toString();

                        getUserInput(act);
                        //Toast.makeText(getApplicationContext(), "activity added!",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }

                });
                //when cancel button is click dialog box will disappearrr
                cancelButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        dialog.cancel();
                    }
                });

                return true;
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
//            case R.id.statistics:
//                System.out.println("STATISTICS CLICKED!!!!!!!!!");
//                getPopupWindow();
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(MainActivity.this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(MainActivity.this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }


    //method to add activities
    private void getUserInput(String [] act){


    try {
        Event event1 = new Event(i, act[7], R.color.event_color_03,
                Integer.parseInt(act[6]), Integer.parseInt(act[5]), Integer.parseInt(act[4]), Integer.parseInt(act[0]), Integer.parseInt(act[2]),
                Integer.parseInt(act[6]), Integer.parseInt(act[5]), Integer.parseInt(act[4]), Integer.parseInt(act[1]), Integer.parseInt(act[3]));
        event1.getwEvent().setColor(getResources().getColor(event1.getEventColor()));
        if (wEventMapByMonth.containsKey(event1.getStartMonth())) {
            wEventMapByMonth.get(event1.getStartMonth()).add(event1.getwEvent());
        } else {
            List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
            events.add(event1.getwEvent());
            wEventMapByMonth.put(event1.getStartMonth(), events);
        }
        eventsArrList.add(event1);
        if (activityMap.containsKey(event1.getEventName())) {
            activityMap.get(event1.getEventName()).add(event1);
        } else {
            List<Event> events = new ArrayList<Event>();
            events.add(event1);
            activityMap.put(event1.getEventName(), events);
        }
    }catch(NumberFormatException e){

        Toast.makeText(MainActivity.this, "Error Numbers Only for Number Field Please!", Toast.LENGTH_SHORT).show();


    }
           i++;

    }


    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Populate the week view with some events.
        List<WeekViewEvent> allEvents = new ArrayList<WeekViewEvent>();
    System.out.println("onMonthChange(" + newYear + "," + newMonth +") was called ---------------");

        // add the events in this month to the weekview calendar
        if(wEventMapByMonth.get(newMonth) != null){
            System.out.println("FOUND: events in month" + newMonth);
            allEvents.addAll(wEventMapByMonth.get(newMonth));
        }

        return allEvents;
    }

    private String getEventTitle(Calendar time) {
        return String.format("ics466.timeit.Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    protected static HashMap<Integer,List<WeekViewEvent>> getwEventMapByMonth(){
        return wEventMapByMonth;
    }

    protected static HashMap<String,List<Event>> getActivityMap(){
        return activityMap;
    }



// THIS IS TO START A NEW ACTIVITY, NOT THE SAME AS A POPUP WINDOW
    /**
     * Called when user presses "Statistics" button
     * @param view
     */
    public void openStatsWindow(View view) {
        Intent intent = new Intent(this, StatisticsList.class);
        startActivity(intent);
    }
}
