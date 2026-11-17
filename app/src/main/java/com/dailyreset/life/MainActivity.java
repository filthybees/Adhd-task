package com.dailyreset.life;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
public class MainActivity extends Activity {
 private WebView web;
 private ValueCallback<Uri[]> chooser;
 private String backup;
 private static final int IMPORT=2, EXPORT=3;
 @Override public void onCreate(Bundle state) {
  super.onCreate(state);
  getWindow().setStatusBarColor(0xff101b23);
  getWindow().setNavigationBarColor(0xff101e26);
  web=new WebView(this);
  setContentView(web);
  WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(true);
  web.setWebViewClient(new WebViewClient());
  web.addJavascriptInterface(new Object(){
   @JavascriptInterface public void saveBackup(String data){runOnUiThread(()->{
    backup=data;
    Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/json");i.putExtra(Intent.EXTRA_TITLE,"daily-reset-backup.json");startActivityForResult(i,EXPORT);
   });}
  },"Native");
  web.setWebChromeClient(new WebChromeClient(){
   @Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> callback, FileChooserParams params){
    chooser=callback;Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/json");startActivityForResult(i,IMPORT);return true;
   }
  });
  web.loadUrl("file:///android_asset/index.html");
 }
 @Override protected void onActivityResult(int code,int result,Intent intent){
  super.onActivityResult(code,result,intent);
  if(code==IMPORT && chooser!=null){chooser.onReceiveValue(result==RESULT_OK && intent!=null ? new Uri[]{intent.getData()}:null);chooser=null;}
  if(code==EXPORT && result==RESULT_OK && intent!=null && backup!=null){try(OutputStream out=getContentResolver().openOutputStream(intent.getData())){if(out!=null)out.write(backup.getBytes(StandardCharsets.UTF_8));}catch(Exception ignored){} backup=null;}
 }
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}
