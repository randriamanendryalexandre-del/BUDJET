package mg.budget;
import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
public class MainActivity extends Activity {
    private WebView web;
    class Bridge {
        @JavascriptInterface public void ask() {
            runOnUiThread(() -> { if (Build.VERSION.SDK_INT >= 33) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1); });
        }
        @JavascriptInterface public void show(String t, String m) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= 26) nm.createNotificationChannel(new NotificationChannel("budget", "Alertes budget", NotificationManager.IMPORTANCE_DEFAULT));
            Notification.Builder nb = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(MainActivity.this, "budget") : new Notification.Builder(MainActivity.this);
            nb.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(t).setContentText(m).setAutoCancel(true);
            nm.notify((int) (System.currentTimeMillis() / 1000), nb.build());
        }
    }
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        web.addJavascriptInterface(new Bridge(), "Android");
        setContentView(web);
        if (b != null) web.restoreState(b); else web.loadUrl("file:///android_asset/index.html");
    }
    @Override protected void onSaveInstanceState(Bundle o) { super.onSaveInstanceState(o); web.saveState(o); }
    @Override public void onBackPressed() { if (web.canGoBack()) web.goBack(); else super.onBackPressed(); }
}
