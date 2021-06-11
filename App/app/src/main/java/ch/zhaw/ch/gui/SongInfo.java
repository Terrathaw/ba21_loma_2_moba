package ch.zhaw.ch.gui;

import android.net.Uri;

/**
 * @author: lopezale
 * Helper class that is used to display a list of songs in the SongListView
 */
public class SongInfo {
    private Uri uri;
    private long id;
    private String title;
    private String artist;

    public SongInfo(Uri uri, String title, String artist) {
        this.uri = uri;
        this.title = title;
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public Uri getUri() {
        return uri;
    }
}
