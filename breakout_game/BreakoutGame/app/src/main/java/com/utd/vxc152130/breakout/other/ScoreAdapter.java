package com.utd.vxc152130.breakout.other;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.utd.vxc152130.breakout.R;
import com.utd.vxc152130.breakout.models.Score;

import java.util.List;

/*************************************************************************************************************
 * ScoreAdapter class to load scores to View
 * @author Vincy Shrine
 * @created 11 /27/2017
 *************************************************************************************************************/
public class ScoreAdapter extends BaseAdapter {


    private Activity activity;
    private List<Score> scores = null;

    /**
     * Constructor to populate list view
     *
     * @param activity the activity
     * @param scores   the scores
     * @author Lopamudra Muduli
     */
    public ScoreAdapter(Activity activity, List<Score> scores) {
        this.activity = activity;
        this.scores = scores;
    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Object getItem(int i) {
        return scores.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * The type View holder.
     */
    public class ViewHolder {
        /**
         * The Id.
         */
        TextView id;
        /**
         * The Name.
         */
        TextView name;
        /**
         * The Score.
         */
        TextView score;
    }

    /**
     * Renders the view for an item
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     * @author Vincy Shrine
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            // setLayout
            convertView = activity.getLayoutInflater().inflate(R.layout.adapter_score, parent, false);
            viewHolder = new ViewHolder();
            // get view for name and phone number
            viewHolder.id = (TextView) convertView.findViewById(R.id.serialNo);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.score = (TextView) convertView.findViewById(R.id.scoreValue);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Score score = scores.get(position);
        viewHolder.id.setText(Integer.toString(position + 1) + ".");
        viewHolder.name.setText(score.getName());
        viewHolder.score.setText(Integer.toString(score.getScore()));
        return convertView;
    }
}
