package com.example.mipatternrecognition;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity implements CvCameraViewListener2 {

	private static final String TAG = "OCVSample::Activity";
	private static final int VIEW_MODE_SURF_DETECTION = 0; // SURF (nonfree
															// module)
	private static final int VIEW_MODE_SIFT_DETECTION = 1; // SIFT (nonfree
															// module)
	private static final int VIEW_MODE_FAST_DETECTION = 2; // FastFeatureDetector
	private static final int VIEW_MODE_STAR_DETECTION = 3; // StarFeatureDetector
	private static final int VIEW_MODE_ORB_DETECTION = 4; // ORB
	private static final int VIEW_MODE_MSER_DETECTION = 5; // MSER
	private static final int VIEW_MODE_GFTT_DETECTION = 6; // GoodFeaturesToTrackDetector
	private static final int VIEW_MODE_HARRIS_DETECTION = 7; // GoodFeaturesToTrackDetector
																// with Harris
																// detector
																// enabled
	private static final int VIEW_MODE_NORMAL = 8;

	private static final int SURF_DETECTION = 0; // SURF (nonfree module)
	private static final int SIFT_DETECTION = 1; // SIFT (nonfree module)
	private static final int FAST_DETECTION = 2; // FastFeatureDetector
	private static final int STAR_DETECTION = 3; // StarFeatureDetector
	private static final int ORB_DETECTION = 4; // ORB
	private static final int MSER_DETECTION = 5; // MSER
	private static final int GFTT_DETECTION = 6; // GoodFeaturesToTrackDetector
	private static final int HARRIS_DETECTION = 7; // GoodFeaturesToTrackDetector
													// with Harris detector
													// enabled

	private CameraBridgeViewBase mOpenCvCameraView;
	private MenuItem mItemSurfFeaturesDetection = null;
	private MenuItem mItemSIFTFeaturesDetection = null;
	private MenuItem mItemFastFeaturesDetection = null;
	private MenuItem mItemStarFeaturesDetection = null;
	private MenuItem mItemORBFeaturesDetection = null;
	private MenuItem mItemMSERFeaturesDetection = null;
	private MenuItem mItemGFTTFeaturesDetection = null;
	private MenuItem mItemHarrisFeaturesDetection = null;
	private MenuItem mItemViewModeNormal = null;
	private boolean buscandoObjeto;
	private int mViewMode;
	private Mat mGray;
	private Mat mRgba;
	private Button buttonVistaNormal;
	private Button buttonCapturaObjeto;
	private Button buttonEncuentraObjeto;
	private boolean patronAdquirido = false;
	private boolean encontrado = false;
	private int DETECTION_TYPE = SURF_DETECTION; // CHOOOSEN KIND OF DETECTION

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");

				// Load native library after(!) OpenCV initialization
				System.loadLibrary("opencv_java");
				System.loadLibrary("nonfree");
				System.loadLibrary("mipattern_recognition");

				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");

		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);

		mViewMode = VIEW_MODE_SURF_DETECTION;

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surface_view);
		buttonCapturaObjeto = (Button) findViewById(R.id.buttonCapturaObjeto);
		buttonEncuentraObjeto = (Button) findViewById(R.id.buttonEncuentraObjeto);
		buttonVistaNormal = (Button) findViewById(R.id.buttonVistaNormal);

		mOpenCvCameraView.setCvCameraViewListener(this);

		/* Listener Captura Objeto Button */
		buttonCapturaObjeto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				patronAdquirido = true;
			}
		});

		/* Listener Encuentra Objeto Button */
		buttonEncuentraObjeto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buscandoObjeto = true;
			}
		});

		/* Listener Vista Normal Button */
		buttonVistaNormal.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buscandoObjeto = false;
				patronAdquirido = false;
				encontrado = false;
			}
		});

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		Log.i(TAG, "called onCreateOptionsMenu");
		mItemSurfFeaturesDetection = menu.add("Surf Feature Detecction");
		mItemSIFTFeaturesDetection = menu.add("SIFT Feature Detecction");
		mItemFastFeaturesDetection = menu.add("Fast Feature Detecction");
		mItemStarFeaturesDetection = menu.add("Star Feature Detecction");
		mItemORBFeaturesDetection = menu.add("ORB Feature Detecction");
		mItemMSERFeaturesDetection = menu.add("MSER Feature Detecction");
		mItemGFTTFeaturesDetection = menu.add("GFTT Feature Detecction");
		mItemHarrisFeaturesDetection = menu.add("Harris Feature Detecction");
		mItemViewModeNormal = menu.add("Normal View");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == mItemSurfFeaturesDetection) {
			mViewMode = VIEW_MODE_SURF_DETECTION;
			DETECTION_TYPE = SURF_DETECTION;
		} else if (item == mItemSIFTFeaturesDetection) {
			mViewMode = VIEW_MODE_SIFT_DETECTION;
			DETECTION_TYPE = SIFT_DETECTION;
		} else if (item == mItemFastFeaturesDetection) {
			mViewMode = VIEW_MODE_FAST_DETECTION;
			DETECTION_TYPE = FAST_DETECTION;
		} else if (item == mItemStarFeaturesDetection) {
			mViewMode = VIEW_MODE_STAR_DETECTION;
			DETECTION_TYPE = STAR_DETECTION;
		} else if (item == mItemORBFeaturesDetection) {
			mViewMode = VIEW_MODE_ORB_DETECTION;
			DETECTION_TYPE = ORB_DETECTION;
		} else if (item == mItemMSERFeaturesDetection) {
			mViewMode = VIEW_MODE_MSER_DETECTION;
			DETECTION_TYPE = MSER_DETECTION;
		} else if (item == mItemGFTTFeaturesDetection) {
			mViewMode = VIEW_MODE_GFTT_DETECTION;
			DETECTION_TYPE = GFTT_DETECTION;
		} else if (item == mItemHarrisFeaturesDetection) {
			mViewMode = VIEW_MODE_HARRIS_DETECTION;
			DETECTION_TYPE = HARRIS_DETECTION;
		}
		return true;
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if (patronAdquirido) {
			mRgba = inputFrame.rgba();
			mGray = inputFrame.gray();
			encontrado = FindFeatures(mGray.getNativeObjAddr(),
					mRgba.getNativeObjAddr(), DETECTION_TYPE);
			patronAdquirido = false;
		} else if (buscandoObjeto) {
			final int viewMode = mViewMode;
			switch (viewMode) {
			case VIEW_MODE_SURF_DETECTION:
				// input frame has RGBA format
				mRgba = inputFrame.rgba();
				mGray = inputFrame.gray();
				encontrado = FindFeatures(mGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr(), SURF_DETECTION);
				break;
			case VIEW_MODE_SIFT_DETECTION:
				// input frame has RGBA format
				mRgba = inputFrame.rgba();
				mGray = inputFrame.gray();
				encontrado = FindFeatures(mGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr(), SIFT_DETECTION);
				break;
			case VIEW_MODE_FAST_DETECTION:
				// input frame has RGBA format
				mRgba = inputFrame.rgba();
				mGray = inputFrame.gray();
				encontrado = FindFeatures(mGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr(), FAST_DETECTION);
				break;
			case VIEW_MODE_ORB_DETECTION:
				// input frame has RGBA format
				mRgba = inputFrame.rgba();
				mGray = inputFrame.gray();
				encontrado = FindFeatures(mGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr(), ORB_DETECTION);
				break;
			case VIEW_MODE_MSER_DETECTION:
				// input frame has RGBA format
				mRgba = inputFrame.rgba();
				mGray = inputFrame.gray();
				encontrado = FindFeatures(mGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr(), MSER_DETECTION);
				break;
			case VIEW_MODE_GFTT_DETECTION:
				// input frame has RGBA format
				mRgba = inputFrame.rgba();
				mGray = inputFrame.gray();
				encontrado = FindFeatures(mGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr(), GFTT_DETECTION);
				break;
			case VIEW_MODE_HARRIS_DETECTION:
				// input frame has RGBA format
				mRgba = inputFrame.rgba();
				mGray = inputFrame.gray();
				encontrado = FindFeatures(mGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr(), HARRIS_DETECTION);
				break;
			case VIEW_MODE_STAR_DETECTION:
				// input frame has RGBA format
				mRgba = inputFrame.rgba();
				mGray = inputFrame.gray();
				encontrado = FindFeatures(mGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr(), STAR_DETECTION);
				break;
			}
		} else
			mRgba = inputFrame.rgba();
		if (encontrado)
			buscandoObjeto = false;
		return mRgba;
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		mGray.release();
		mRgba.release();

	}

	public native boolean FindFeatures(long matAddrGr, long matAddrRgba,
			int typeDetection);
}
