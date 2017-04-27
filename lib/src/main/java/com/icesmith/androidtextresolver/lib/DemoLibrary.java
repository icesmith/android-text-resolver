package com.icesmith.androidtextresolver.lib;

import android.content.Context;

public class DemoLibrary {
    private final Context mContext;

    public DemoLibrary(Context context) {
        mContext = context;
    }

    public String getDescription() {
        return mContext.getString(R.string.description);
    }
}
