package com.telit.zhkt_three.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.xwalk.core.XWalkActivityDelegate;
import org.xwalk.core.XWalkDialogManager;

public abstract class XwalkFragment extends Fragment {
    private XWalkActivityDelegate mActivityDelegate;

    public XwalkFragment() {
    }

    protected abstract void onXWalkReady();

    protected void onXWalkFailed() {

    }

    protected XWalkDialogManager getDialogManager() {
        return this.mActivityDelegate.getDialogManager();
    }

    public boolean isXWalkReady() {
        return this.mActivityDelegate.isXWalkReady();
    }

    public boolean isSharedMode() {
        return this.mActivityDelegate.isSharedMode();
    }

    public boolean isDownloadMode() {
        return this.mActivityDelegate.isDownloadMode();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Runnable cancelCommand = new Runnable() {
            public void run() {
                XwalkFragment.this.onXWalkFailed();
            }
        };
        Runnable completeCommand = new Runnable() {
            public void run() {
                XwalkFragment.this.onXWalkReady();
            }
        };
        this.mActivityDelegate = new XWalkActivityDelegate(getActivity(), cancelCommand, completeCommand);
    }

    public void onResume() {
        super.onResume();
        this.mActivityDelegate.onResume();
    }
}
