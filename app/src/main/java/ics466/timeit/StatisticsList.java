package ics466.timeit;
        import android.app.Activity;
        import android.app.ListActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.ListAdapter;
        import android.widget.ListView;
        import android.widget.TextView;

        import com.alamkanak.weekview.WeekViewEvent;

        import org.w3c.dom.Text;

        import java.math.BigInteger;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Timer;
        import java.util.Iterator;
        import java.util.Map;
        import java.util.concurrent.TimeUnit;

public class StatisticsList extends ListActivity {
    List<TimeItActivity> activityArrList = new ArrayList<TimeItActivity>();
    Button btnStatsClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_popup_main);

        // add button listener
        btnStatsClose = (Button) findViewById(R.id.btn_stats_close);
        btnStatsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        populateStatisticsList();
        populateListView();
    }


    private void populateStatisticsList() {

        Iterator iter = MainActivity.getActivityMap().entrySet().iterator();
        while (iter.hasNext()) {
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
    }

    private void populateListView() {

        ListAdapter adapter = new ArrayAdapter<TimeItActivity>(
                StatisticsList.this, R.layout.stats_item, activityArrList) {

            public View getView(int position, View convertView, ViewGroup parent) {
                //View itemView = convertView;
                final ViewHolder mHolder;

                TextView nameView;
                TextView statView;
                TextView totTimeView;

                //Ensure we have a view to work with
                if (convertView == null) {
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
                int stat = (int) ((currAct.getTimeTotal() / msDay) * 100);
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
        };

        ListView list = getListView();
        list.setAdapter(adapter);
    }
    //helper class and cache mechanism that stores Views
    private  class ViewHolder {
        private TextView mText;
        private TextView mStat;

    }
}
