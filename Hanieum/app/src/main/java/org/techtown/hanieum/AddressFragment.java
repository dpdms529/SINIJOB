package org.techtown.hanieum;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressFragment extends Fragment implements View.OnClickListener {

    TextView title;
    EditText addressText;
    Button nextBtn;

    WebView webView;

    Handler handler;

    Context context;

    public AddressFragment() {
    }

    public static AddressFragment newInstance() {
        AddressFragment fragment = new AddressFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        context = getContext();
        title = view.findViewById(R.id.title);
        addressText = view.findViewById(R.id.addressText);
        nextBtn = view.findViewById(R.id.nextBtn);
        webView = view.findViewById(R.id.webview);

        nextBtn.setOnClickListener(this);
        addressText.setOnClickListener(this);

        handler = new Handler();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == nextBtn) {
            ((InfoGetActivity)getActivity()).replaceFragment(NameFragment.newInstance());
        } else if(v == addressText){
            init_webView();
        }
    }

    public void init_webView(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.addJavascriptInterface(new AndroidBridge(),"hanium");
        webView.setWebViewClient(client);
        webView.setWebChromeClient(chromeClient);
        webView.loadUrl(getResources().getString(R.string.serverIP)+"/address_api.php");
    }

    private class AndroidBridge{
        @JavascriptInterface
        public void getAddress(String sigunguCode, String roadnameCode, String roadAddress, String buildingName){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(buildingName.equals("")){
                        addressText.setText(roadAddress);
                    }else{
                        addressText.setText(roadAddress+", "+buildingName);
                    }
                    Log.d("TAG", "run: "+sigunguCode + roadnameCode);
                }
            });
        }
    }

    WebViewClient client = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    };

    WebChromeClient chromeClient = new WebChromeClient(){
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(context);
            newWebView.getSettings().setJavaScriptEnabled(true);
            Dialog dialog = new Dialog(context);
            dialog.setContentView(newWebView);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(params);
            dialog.show();

            newWebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    super.onJsAlert(view, url, message, result);
                    return true;
                }

                @Override
                public void onCloseWindow(WebView window) {
                    dialog.dismiss();
                }
            });
            ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
            resultMsg.sendToTarget();

            return true;
        }
    };


}