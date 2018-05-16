package lv.dong.playservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
        OnItemClickListener, AnimationListener {

    public static ImageView btnPlay, btnForward, btnBackward, btnNext,
            btnPrevious, listSongBtn;
    public static ImageButton btnShuffle, btnRepeat;
    public static SeekBar songProgressBar;

    // Songs list
    public static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private ListAdapter adapter;
    private ListView listSongLv;
    private LinearLayout playerScreen, listSongScreen;
    private Button backBtn;
    public static TextView songTitle, songCurrentDurationLabel,
            songTotalDurationLabel;

    public Intent playerService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

        SongsProvider plm = new SongsProvider();
        // get all songs from sdcard. If there no directory contains
        // musing-->throw exception
        try {
            songsList = plm.getPlayList();
            // looping through playlist
            Log.e("size>>>", songsListData.size()+"");
            for (int i = 0; i < songsList.size(); i++) {
                // creating new HashMap
                HashMap<String, String> song = songsList.get(i);
                // adding HashList to ArrayList
                songsListData.add(song);
            }
            // Adding song Items to ListView Adapter
            adapter = new SimpleAdapter(this, songsListData,
                    R.layout.listsong_item, new String[] { "songTitle" },
                    new int[] { R.id.songTitle });
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        listSongLv = (ListView) findViewById(R.id.listsong_listview);
        // ----start service.
        playerService = new Intent(this, PlayerService.class);
        playerService.putExtra("songIndex", PlayerService.currentSongIndex);
        startService(playerService);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (!PlayerService.mp.isPlaying()) {
            stopService(playerService);
            cancelNotification();
        }
    }

    // -- Cancel Notification
    public void cancelNotification() {
        String notificationServiceStr = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(notificationServiceStr);
        mNotificationManager.cancel(PlayerService.NOTIFICATION_ID);
    }

    /**
     * Initiaze Views
     */
    private void initViews() {

        playerScreen = (LinearLayout) findViewById(R.id.playerScreen);
        listSongScreen = (LinearLayout) findViewById(R.id.list_song_layout);
        listSongScreen.setVisibility(View.INVISIBLE);

        btnPlay = (ImageView) findViewById(R.id.btn_play_imageview);
        btnForward = (ImageView) findViewById(R.id.btn_forward_imageview);
        btnBackward = (ImageView) findViewById(R.id.btn_backward_imagview);
        btnNext = (ImageView) findViewById(R.id.btn_next_imageview);
        btnPrevious = (ImageView) findViewById(R.id.btn_previous_imageview);
        listSongBtn = (ImageView) findViewById(R.id.listsong_btn);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);

        songProgressBar = (SeekBar) findViewById(R.id.song_playing_progressbar);

        songTitle = (TextView) findViewById(R.id.song_title_txt);
        songCurrentDurationLabel = (TextView) findViewById(R.id.current_time_txt);
        songTotalDurationLabel = (TextView) findViewById(R.id.total_time_txt);
        backBtn = (Button) findViewById(R.id.back_btn);

        btnPlay.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnBackward.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        listSongBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * View OnclickListener Implement
     *
     * @param v
     */
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.listsong_btn:
                showListSongScreen();
                listSongLv.setAdapter(adapter);
                listSongLv.setOnItemClickListener(this);

                break;
            case R.id.back_btn:
                showListSongScreen();
                break;

        }
    }

    /**
     * onItemClick Listener Implement.
     */
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        switch (parent.getId()) {
            case R.id.listsong_listview:
                playerService = new Intent(this, PlayerService.class);
                playerService.putExtra("songIndex", position);
                startService(playerService);
                showListSongScreen();

                break;
        }

    }

    /**
     * Show Listsong Screen with Sliding Animation
     */
    private boolean isVisible = false;

    private void showListSongScreen() {
        Animation anim;
        if (isVisible == false) {
            listSongScreen.setVisibility(View.VISIBLE);
            anim = AnimationUtils.loadAnimation(this, R.anim.push_down_in);
            isVisible = true;
        } else {
            anim = AnimationUtils.loadAnimation(this, R.anim.push_down_out);
            isVisible = false;
            playerScreen.setVisibility(View.VISIBLE);

        }
        anim.setAnimationListener(this);
        listSongScreen.startAnimation(anim);

    }

    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub
        if (isVisible == true)
            playerScreen.setVisibility(View.INVISIBLE);
        else
            listSongScreen.setVisibility(View.INVISIBLE);
    }

    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }
}
