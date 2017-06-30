package com.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

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
    public static SimpleExoPlayer mExoPlayer;
    public static MediaSessionCompat mMediaSession;
    private Context mContext;
    private final String TAG = StepsDetailFragment.class.getSimpleName();
    private PlaybackStateCompat.Builder mStateBuilder;
    private ArrayList<Recipe.Step> currSteps;
    private int pos;
    @BindView(R.id.ivThumbnail)
    ImageView thumbnail;

    public StepsDetailFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currSteps = MainActivity.currentRecipe.getSteps();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_steps_detail, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getActivity();

        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        if(!tablet){
            setHasOptionsMenu(true);
            ((AppCompatActivity) mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }else
            ((AppCompatActivity) mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //setup ExoPlayer and media session
        initializeMediaSession();
        updateUI();

        //set up button click listeners
        bPrev1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos=((MainActivity) mContext).getStepSelected();
                if(pos>0) {
                    ((MainActivity) mContext).onStepSelected(--pos);
                    updateUI();
                }else{
                    Snackbar.make(((AppCompatActivity) mContext)
                                    .findViewById(R.id.myCoordinatorLayout),
                            R.string.first_step,
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
        bNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos=((MainActivity) mContext).getStepSelected();
                if(pos<currSteps.size()-1) {
                    ((MainActivity) mContext).onStepSelected(++pos);
                    updateUI();
                }else{
                    Snackbar.make(((AppCompatActivity) mContext)
                                    .findViewById(R.id.myCoordinatorLayout),
                            R.string.last_step,
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        return rootView;
    }

    private void updateUI(){

        Recipe.Step  step = MainActivity.currentRecipe.getSteps().get(
                ((MainActivity) getActivity()).getStepSelected());

        tvShortDesc.setText(step.getShortDesc());

        String vidUrl = step.getVideoUrl();
        String thumbUrl = step.getThumbnail();
        releasePlayer();

        if(vidUrl.equals("")&&thumbUrl.equals("")) {
            mPlayerView.setVisibility(View.GONE);
            thumbnail.setVisibility(View.GONE);
        }else if(!vidUrl.equals("")) {
            thumbnail.setVisibility(View.GONE);
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer();
        }else if(!thumbUrl.equals("")) {
            mPlayerView.setVisibility(View.GONE);
            thumbnail.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(step.getThumbnail()).into(thumbnail);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializePlayer() {

        Recipe.Step  step = MainActivity.currentRecipe.getSteps().get(
                ((MainActivity) getActivity()).getStepSelected());

        Uri mediaUri = null;
        if(!step.getVideoUrl().equals(""))
             mediaUri= Uri.parse(step.getVideoUrl());

        if (mExoPlayer == null && mediaUri != null) {
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

    public static void releasePlayer() {
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
    public void onResume() {
        super.onResume();
        initializePlayer();
        mMediaSession.setActive(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
        mMediaSession.setActive(false);
    }
}
