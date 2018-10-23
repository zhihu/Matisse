package com.zhihu.matisse.internal.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

/**
 * @author 工藤
 * @email gougou@16fan.com
 * com.fan16.cn.util
 * create at 2018年10月23日12:17:59
 * description:媒体扫描
 */
public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

	private MediaScannerConnection mMs;
	private String path;
	private ScanListener listener;

	public interface ScanListener {

		/**
		 * scan finish
		 */
		void onScanFinish();
	}

	public SingleMediaScanner(Context context, String path, ScanListener l) {
		this.path = path;
		this.listener = l;
		this.mMs = new MediaScannerConnection(context, this);
		this.mMs.connect();
	}

	@Override public void onMediaScannerConnected() {
		mMs.scanFile(path, null);
	}

	@Override public void onScanCompleted(String path, Uri uri) {
		mMs.disconnect();
		if (listener != null) {
			listener.onScanFinish();
		}
	}
}