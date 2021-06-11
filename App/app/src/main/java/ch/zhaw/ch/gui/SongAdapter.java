package ch.zhaw.ch.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ch.zhaw.ch.R;

/**
 * @author: lopezale
 * Adapter used by Views to create a list of songs.
 */
public class SongAdapter extends BaseAdapter {

    private static final String TAG = "SongAdapter";

    private ArrayList<SongInfo> songInfoList;
    private LayoutInflater songViewInflater;

    public SongAdapter(Context c, ArrayList<SongInfo> songInfoList) {
        this.songInfoList = songInfoList;
        songViewInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return songInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = songViewInflater.inflate(R.layout.song, parent, false);

        TextView titleView = convertView.findViewById(R.id.songTitle);
        TextView artistView = convertView.findViewById(R.id.songArtist);
        SongInfo songInfo = songInfoList.get(position);
        titleView.setText(songInfo.getTitle());
        artistView.setText(songInfo.getArtist());
        convertView.setTag(position);

        return convertView;
    }
}
