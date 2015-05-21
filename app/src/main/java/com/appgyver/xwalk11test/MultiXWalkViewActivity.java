package com.appgyver.xwalk11test;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class MultiXWalkViewActivity extends Activity {

    /**
     * Choose between XWalkView / platform WebView.
     *
     * N.B. If the platform WebView appears as white, you'll need to scroll it with finger.
     * Platform WebView doesn't zoom out by default like XWalkView does.
     */
    private final static boolean IS_XWALK = true;

    /**
     * Crosswalk uses either a SurfaceView or a TextureView.
     *
     * Using TextureView doesn't seem to affect crashing. However, without TextureView
     * eg. canvas performance seems to degrade. See the FPS results using the SimpleSpinner
     * URL.
     *
     * The main problem is that SurfaceView will display multiple XWalkViews in inverse order
     * and there is nothing an app can do about it (we have tried).
     * Therefore TextureView is the only option for multiple XWalkViews.
     * See https://crosswalk-project.org/jira/browse/XWALK-2012
     *
     */
    private final static boolean IS_TEXTURE_VIEW_ENABLED = true;

    /**
     * Measure FPS between SurfaceView and TextureView.
     */
    //    private final static String TARGET_URL = "https://www.scirra.com/labs/simplespinner/";

    /**
     * Load simple content to allows more XWalkViews. Even with simple content performance
     * is inferior to the platform WebView.
     */
    //    private final static String TARGET_URL = "file:///android_asset/simple.html";

    /**
     * Use a complex site to simulate the multi-XWalkView requirements of a
     * production-grade HTML5 hybrid app.
     */
    private final static String TARGET_URL = "http://intel.com";

    /**
     * Devices can manage about this many XWalkViews without crashing in http://intel.com.
     * Numbers are gathered without the USB cable being attached to the device.
     *
     * It is noticeable that devices running Android <= 4.3 are much more stable than
     * devices running on Android >= 4.4.
     *
     * Crosswalk 11
     * ============
     *
     * Samsung Galaxy Tab 4 (arm)   @4.4.2 (API 19) => 7
     * Asus MemoPad 8 (x86)         @4.4.x (API 19) => 7
     * LG Nexus 5 (arm)             @5.0.x (API 21) => 6
     * LG Nexus 4 (arm)             @4.4.x (API 19) => 6
     *
     * Samsung Galaxy Tab 3 (x86)   @4.2.2 (API 17) => 20
     * Samsung Nexus (arm)          @4.2.1 (API 17) => 12
     * Samsung S3 retail (arm)      @4.3   (API 18) => 12
     * Samsung S3 AT&T model (arm)  @4.1.1 (API 16) => 12
     *
     *
     * Using the Platform WebView, each of these devices can easily reach 50 WebViews in intel.com.
     * The performance gap between XWalkView is tenfold.
     *
     */
    private final static int HOW_MANY = 12;

    private final static int WIDTH = 200;
    private final static int HEIGHT = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, IS_TEXTURE_VIEW_ENABLED);

        RelativeLayout root = new RelativeLayout(this);

        if (IS_XWALK) {
            loadXWalkViews(root);
        } else {
            loadPlatformWebViews(root);
        }

        setContentView(root);
    }

    /**
     * With platform webviews you will need to scroll with finger inside the window
     * if the window appears as white (it doesn't zoom out by default like XWalkView does).
     */
    private void loadPlatformWebViews(RelativeLayout root) {
        WebViewClient webViewClient = new WebViewClient();

        for (int i = 0; i < HOW_MANY; i++) {
            WebView webView = new WebView(this);
            webView.setX(getX(i));
            webView.setY(getY(i));
            webView.setWebViewClient(webViewClient);
            webView.loadUrl(TARGET_URL);

            root.addView(webView, WIDTH, HEIGHT);
        }
    }

    private void loadXWalkViews(RelativeLayout root) {
        for (int i = 0; i < HOW_MANY; i++) {
            XWalkView xWalkView = new XWalkView(this, this);
            xWalkView.setX(getX(i));
            xWalkView.setY(getY(i));
            xWalkView.load(TARGET_URL, null);

            root.addView(xWalkView, WIDTH, HEIGHT);
        }
    }

    private int getY(int i) {
        return (i % 5) * 100;
    }

    private int getX(int i) {
        return i * 25;
    }
}
