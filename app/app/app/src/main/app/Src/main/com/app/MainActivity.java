package com.carpetsurfer.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {
    private WebView webView;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen, no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.activity_main);
        
        // Check and request permissions
        if (!checkPermissions()) {
            requestPermissions();
        }
        
        webView = findViewById(R.id.webview);
        setupWebView();
    }

    private boolean checkPermissions() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
        
        webView.setWebViewClient(new WebViewClient());
        
        // Add JavaScript bridge
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        
        // Load the app
        webView.loadUrl("file:///android_asset/www/index.html");
    }

    public class WebAppInterface {
        Context mContext;
        
        WebAppInterface(Context c) {
            mContext = c;
        }
        
        @android.webkit.JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
        
        @android.webkit.JavascriptInterface
        public void saveImage(String dataUrl) {
            try {
                String base64Data = dataUrl.substring(dataUrl.indexOf(",") + 1);
                byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
                
                File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File appDir = new File(picturesDir, "CarpetSurfer");
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                
                String fileName = "dope-scope-" + System.currentTimeMillis() + ".png";
                File file = new File(appDir, fileName);
                
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();
                
                runOnUiThread(() -> {
                    Toast.makeText(mContext, "Saved to Pictures/CarpetSurfer/", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(mContext, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }
        
        @android.webkit.JavascriptInterface
        public void exitApp() {
            finish();
        }
        
        @android.webkit.JavascriptInterface
        public void keepScreenOn() {
            runOnUiThread(() -> {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            });
        }
    }

    @Override
    public void onBackPressed() {
        webView.evaluateJavascript(
            "(function() { " +
            "  if (document.getElementById('uv-screen').classList.contains('active')) { " +
            "    document.querySelector('.exit-btn').click(); return false; " +
            "  } else if (document.getElementById('microscope-screen').classList.contains('active')) { " +
            "    document.querySelector('#microscope-screen .exit-btn').click(); return false; " +
            "  } else if (document.getElementById('home-screen').classList.contains('active')) { " +
            "    return true; " +
            "  } " +
            "  return false; " +
            "})()", 
            value -> {
                if ("true".equals(value)) {
                    finish();
                }
            }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
