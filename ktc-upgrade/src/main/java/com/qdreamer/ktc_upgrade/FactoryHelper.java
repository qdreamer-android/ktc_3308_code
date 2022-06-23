package com.qdreamer.ktc_upgrade;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.pwong.library.utils.LogUtil;
import com.qdreamer.qvoice.QEngine;
import com.qdreamer.qvoice.QEngine.FeedType;
import com.qdreamer.utils.FileUtils;
import com.qdreamer.utils.ZipUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @PackageName: com.qdreamer.qvoice
 * @Title: FactoryHelper
 * @Description:麦克风厂测算法辅助类
 * @data: 2021年8月19日
 */
public class FactoryHelper {
	private final String TAG = "qtk-" + FactoryHelper.class.getSimpleName();
	private static QdreamerArrayFactoryTestListener mFactoryTestListener = null;
	private static FactoryHelper mFactoryHelper = null;
	private final int START_STATE = 0;
	private final int STOP_STATE = 1;
	private boolean initSuccessed = false;

	private int STATE = 10;
	private QEngine mFactoryEngine;
	private Context mContext;
	private Handler mHandler;
	private String mPath;
	private byte[] mData = null;

	private FileOutputStream mWavFile;

	public String getStoragePath() {
		return mPath + "qvoice" + File.separator;
	}

	FactoryHelper(Context context) {
		this.mContext = context;
		mPath = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
		/* 创建bfio引擎实例 */
		mFactoryEngine = new QEngine();

		// 引擎消息处理handler
		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if (msg.obj != null) {
					mData = (byte[]) msg.obj;
				}
//				Log.d(TAG,"msg type:"+msg.what);
				switch (msg.what) {

				case QEngine.QENGINE_FACTORY_MIC_NULL://录音空
					if (mFactoryTestListener != null) {
						mFactoryTestListener.onMicNull(new String(mData));
					}
					break;
				case QEngine.QENGINE_FACTORY_MIC_EQUAL:// 录音相同
					if (mFactoryTestListener != null) {
						mFactoryTestListener.onMicEqual(new String(mData));
					}

					break;
				case QEngine.QENGINE_FACTORY_MIC_MAX:// 录音破音
					if (mFactoryTestListener != null) {
						mFactoryTestListener.onMicMax(new String(mData));
					}
	
					break;
				case QEngine.QENGINE_FACTORY_MIC_CORR:// 录音相关性差
					if (mFactoryTestListener != null) {
						mFactoryTestListener.onMicCorr(new String(mData));
					}

					break;
				case QEngine.QENGINE_FACTORY_MIC_ENERGY:// 录音能量差
					if (mFactoryTestListener != null) {
						mFactoryTestListener.onMicEnergy(new String(mData));
					}
					break;
				case QEngine.QENGINE_FACTORY_AEC_NULL:// 回采空
					if (mFactoryTestListener != null) {
						mFactoryTestListener.onAecNull(new String(mData));
					}
					break;
				case QEngine.QENGINE_FACTORY_AEC_EQUAL:// 回采相同
					if (mFactoryTestListener != null) {
						mFactoryTestListener.onAecEqual(new String(mData));
					}

					break;
				case QEngine.QENGINE_FACTORY_AEC_MAX:// 回采破音
					if (mFactoryTestListener != null) {
						mFactoryTestListener.onAecMax(new String(mData));
					}
	
					break;
				case QEngine.QENGINE_FACTORY_AEC_CORR:// 回采相关性差
					if (mFactoryTestListener != null) {
						mFactoryTestListener.onAecCorr(new String(mData));
					}

					break;
				case QEngine.QENGINE_FACTORY_AEC_ENERGY:// 回采能量差
					if (mFactoryTestListener != null) {
						mFactoryTestListener.onAecEnergy(new String(mData));
					}
					break;
				default:

					break;
				}
				super.handleMessage(msg);
			}
		};
	}



	public static FactoryHelper getInstance(Context context) {
		if (mFactoryHelper == null) {
			synchronized (FactoryHelper.class) {
				if (mFactoryHelper == null) {
					mFactoryHelper = new FactoryHelper(context);
				}
			}
		}
		return mFactoryHelper;
	}

	/* 算法回调函数 */
	public interface QdreamerArrayFactoryTestListener {

		void onMicNull(String msg);

		void onMicEqual(String msg);

		void onMicMax(String msg);

		void onMicCorr(String msg);

		void onMicEnergy(String msg);

		void onAecNull(String msg);

		void onAecEqual(String msg);

		void onAecMax(String msg);

		void onAecCorr(String msg);

		void onAecEnergy(String msg);


	}


	/* 设置回调函数 */
	public void setListener(QdreamerArrayFactoryTestListener arrayListener) {
		this.mFactoryTestListener = arrayListener;
	
	}

	public boolean init(long session, String mRes) {
		if (session == 0) {
			Log.e(TAG, "init session failed");
			return false;
		}
		boolean init = mFactoryEngine.init(session, mRes, mHandler);
		if (init) {
			initSuccessed = true;
			return true;
		} else {
			initSuccessed = false;
			Log.e(TAG, "factory engine init failed！");
			return false;
		}
	}

	public boolean start() {
		if (STATE == STOP_STATE || STATE == 10) {
			if (initSuccessed) {
				int start = mFactoryEngine.start();// 启动引擎
				if (start == 0) {
					STATE = START_STATE;
					return true;
				} else {
					Log.e(TAG, "factory engine start failed！");
					return false;
				}
			}
		}
		return false;
	}


	public boolean feedData(byte[] data) {
		if (STATE == START_STATE) {
			if (initSuccessed = true) {
				int feed = mFactoryEngine.feed(data, FeedType.QENGINE_FEED_DATA);
				return feed == 0 ? true : false;
			}
		}
		return false;
	}


	public boolean getResutAndStop() {
		if (STATE == START_STATE) {
			LogUtil.INSTANCE.logE(HomeActivity.SOCKET_TAG, "6666666666666666");
			mFactoryEngine.feed(new byte[0], FeedType.QENGINE_FEED_END);
			LogUtil.INSTANCE.logE(HomeActivity.SOCKET_TAG, "7777777777777777");
			int reset = mFactoryEngine.reset();
			STATE = STOP_STATE;
			return reset == 0;
		}
		return false;
	}

	public void delete() {
			mFactoryEngine.destory();// 销毁引擎
			mFactoryHelper = null;

	}

	public void copyRes(int resId) {
		String res = "qvoice";
		String fn;
		InputStream in;
		fn = mPath + res;
//		if (!FileUtils.existes(fn)) {
			try {
				fn = mPath + "qvoice.zip";
				in = mContext.getResources().openRawResource(resId);
				FileUtils.copyFile2(fn, in);
				in.close();
				ZipUtils.unZip(mPath, fn);
				FileUtils.rmFile(mPath + "qvoice.zip");
				Log.d(TAG,"copy res doned");
			} catch (IOException e) {
				e.printStackTrace();
			}
//		}
	}

	/* 创建保存音频文件 */
	public void createWavFile(String path) {
		File file = new File(path);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		try {
			mWavFile = new FileOutputStream(path+"mul.pcm");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/* 保存音频格式为.pcm */
	public void saveWavData(byte[] wavData) {
		try {
			mWavFile.write(wavData, 0, wavData.length);// 往文件里写音频数据
		} catch (IOException e) {
			e.printStackTrace();
		}
		// try {
		// WaveUtils.Pcm2Wave("/sdcard/qvoice/mic.wav",
		// "/sdcard/qvoice/mic.pcm", 16000, (short) 1);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}
