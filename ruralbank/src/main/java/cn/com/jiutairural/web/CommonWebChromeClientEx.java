package cn.com.jiutairural.web;

import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
/**
*
*@author Mr.kang
*@date 2019-07-02
*
*/
public class CommonWebChromeClientEx extends WebChromeClient {

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		try {
			if (view instanceof CommonWebView) {
			    CommonWebView webview = (CommonWebView) view;
				webview.injectJavascriptInterfaces(view);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onProgressChanged(view, newProgress);
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
		try {
			if (view instanceof CommonWebView) {
			    CommonWebView webview = (CommonWebView) view;
				if (webview.handleJsInterface(view, url, message, defaultValue, result)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onJsPrompt(view, url, message, defaultValue, result);
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		try {
			if (view instanceof CommonWebView) {
			    CommonWebView webview = (CommonWebView) view;
				webview.injectJavascriptInterfaces(view);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
