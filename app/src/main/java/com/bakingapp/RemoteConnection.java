package com.bakingapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by aditya on 6/20/17.
 */

public class RemoteConnection {

    private static RemoteConnection mInstance;
    private RequestQueue mRequestQueue;
    private static Context thisContext;

    public RemoteConnection(Context c){
        this.thisContext = c;
        mRequestQueue = getRequestQueue();

    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(thisContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static synchronized RemoteConnection getInstance(Context context){
        if(mInstance == null){
            mInstance = new RemoteConnection(context);
        }
        return mInstance;
    }
}
