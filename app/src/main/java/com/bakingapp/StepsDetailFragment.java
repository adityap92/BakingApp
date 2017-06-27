package com.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aditya on 6/26/17.
 */

public class StepsDetailFragment extends Fragment  implements ExoPlayer.EventListener{

    @BindView(R.id.exoView)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.tvShortDesc)
    TextView tvShortDesc;
    @BindView(R.id.bPrev1)
    Button bPrev1;
    @BindView(R.id.bNext1)
    Button bNext1;
    private SimpleExoPlayer mExoPlayer;
    private Context mContext;
    private final String TAG = StepsDetailFragment.class.getSimpleName();
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private ArrayList<Recipe.Step> currSteps;
    private int pos;

    public StepsDetailFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_steps_detail, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getActivity();

        //retreive data from Bundle
        Bundle bundle = this.getArguments();
        currSteps = (ArrayList<Recipe.Step>) bundle.getSerializable("detail");
        pos = bundle.getInt("pos",0);

        //setup ExoPlayer and media session
        initializeMediaSession();

        updateUI(pos);

        //set up button click listeners
        bPrev1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pos>0)
                    updateUI(--pos);
                else
                    Toast.makeText(mContext,"First Step",Toast.LENGTH_SHORT).show();
            }
        });
        bNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pos<currSteps.size()-2)
                    updateUI(++pos);
                else
                    Toast.makeText(mContext,"Last Step",Toast.LENGTH_SHORT).show();
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        return rootView;
    }

    private void updateUI(int pos){
        tvShortDesc.setText(currSteps.get(pos).getShortDesc());

        String vidUrl = currSteps.get(pos).getVideoUrl();
        String thumbUrl = currSteps.get(pos).getThumbnail();

        if(vidUrl.equals("")&&thumbUrl.equals("")) {
            mPlayerView.setVisibility(View.GONE);
            releasePlayer();
        }else if(!vidUrl.equals("")) {
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(currSteps.get(pos).getVideoUrl()));
        }else if(!thumbUrl.equals("")) {
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(currSteps.get(pos).getVideoUrl()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(mContext, "RecipeVideo");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    mContext, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(mContext, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    private void releasePlayer() {
        if(mExoPlayer!=null){
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }
}
