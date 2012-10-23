package com.example.cobra;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


@SuppressLint("NewApi")
public class MainActivity extends Activity implements SurfaceHolder.Callback{

    /* 【SurfaceHolder.Callback 回调函数】 */
    public void surfaceCreated(SurfaceHolder holder)
    // SurfaceView启动时/初次实例化，预览界面被创建时，该方法被调用。
    {
        // TODO Auto-generated method stub
    	int defaultCameraId = 0;
    	//Find the total number of cameras avaiable
    	int numberOfCameras = Camera.getNumberOfCameras();
    	//Find the id of the default camera
    	CameraInfo cameraInfo = new CameraInfo();
    	for(int i = 0; i<numberOfCameras;i++)
    	{
    	Camera.getCameraInfo(i, cameraInfo);
    	if(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT)//获取前置摄像头的id
    	{
    	defaultCameraId = i;
    	}
    	}
    	mCamera = Camera.open(defaultCameraId);//打开前置的摄像头
        try {
            Log.i(TAG, "SurfaceHolder.Callback：surface Created");
            
            mCamera.setPreviewDisplay(mSurfaceHolder);// set the surface to be
                                                        // used for live preview

        } catch (Exception ex) {
            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
            Log.i(TAG, "initCamera" + ex.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    // 当SurfaceView/预览界面的格式和大小发生改变时，该方法被调用
    {
        // TODO Auto-generated method stub
        Log.i(TAG, "SurfaceHolder.Callback：Surface Changed");
        // mPreviewHeight = height;
        // mPreviewWidth = width;
        initCamera();
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    // SurfaceView销毁时，该方法被调用
    {
        // TODO Auto-generated method stub
        Log.i(TAG, "SurfaceHolder.Callback：Surface Destroyed");
        if (null != mCamera) {
            mCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
            mCamera.stopPreview();
            bIfPreview = false;
            mCamera.release();
            mCamera = null;
        }
    }
     
    /* 【2】【相机预览】 */
    private void initCamera()// surfaceChanged中调用
    {
        Log.i(TAG, "going into initCamera");
        if (bIfPreview) {
            mCamera.stopPreview();// stopCamera();
        }

        if (null != mCamera) {
            try {
                /* Camera Service settings */
                Camera.Parameters parameters = mCamera.getParameters();
                // parameters.setFlashMode("off"); // 无闪光灯
                parameters.setPictureFormat(PixelFormat.JPEG); // Sets the image
                                                                // format for
                                                                // picture
                                                                // 设定相片格式为JPEG，默认为NV21
                parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP); // Sets
                                                                        // the
                                                                        // image
                                                                        // format
                                                                        // for
                                                                        // preview
                                                                        // picture，默认为NV21
                /*
                 * 【ImageFormat】JPEG/NV16(YCrCb format，used for
                 * Video)/NV21(YCrCb format，used for Image)/RGB_565/YUY2/YU12
                 */

                // 【调试】获取caera支持的PictrueSize，看看能否设置？？
                List<Size> pictureSizes = mCamera.getParameters()
                        .getSupportedPictureSizes();
                List<Size> previewSizes = mCamera.getParameters()
                        .getSupportedPreviewSizes();
                List<Integer> previewFormats = mCamera.getParameters()
                        .getSupportedPreviewFormats();
                List<Integer> previewFrameRates = mCamera.getParameters()
                        .getSupportedPreviewFrameRates();
                Log.i(TAG, "initCamera cyy support parameters is ");
                Size psize = null;
                for (int i = 0; i < pictureSizes.size(); i++) {
                    psize = pictureSizes.get(i);
                    Log.i(TAG, "initCamera  PictrueSize,width: "
                            + psize.width + " height" + psize.height);
                }
                for (int i = 0; i < previewSizes.size(); i++) {
                    psize = previewSizes.get(i);
                    Log.i(TAG, "initCamera PreviewSize,width: "
                            + psize.width + " height" + psize.height);
                }
                Integer pf = null;
                for (int i = 0; i < previewFormats.size(); i++) {
                    pf = previewFormats.get(i);
                    Log.i(TAG, "initCamera previewformates:" + pf);
                }

                
                // 设置拍照和预览图片大小
                parameters.setPictureSize(640, 480);//(640, 480); // 指定拍照图片的大小
                parameters.setPreviewSize(mPreviewWidth, mPreviewHeight); // 指定preview的大小
                // 这两个属性 如果这两个属性设置的和真实手机的不一样时，就会报错

                // 横竖屏镜头自动调整
                if (this.getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    parameters.set("orientation", "portrait"); //
                    parameters.set("rotation", 90); // 镜头角度转90度（默认摄像头是横拍）
                    mCamera.setDisplayOrientation(90); // 在2.2以上可以使用
                } else// 如果是横屏
                {
                    parameters.set("orientation", "landscape"); //
                    mCamera.setDisplayOrientation(0); // 在2.2以上可以使用
                }

                /* 视频流编码处理 */
                // 添加对视频流处理函数
                mCamera.setPreviewCallback(mJpegPreviewCallback);

                // 设定配置参数并开启预览
                mCamera.setParameters(parameters); // 将Camera.Parameters设定予Camera
                mCamera.startPreview(); // 打开预览画面
                
                bIfPreview = true;

                // 【调试】设置后的图片大小和预览大小以及帧率
                Camera.Size csize = mCamera.getParameters().getPreviewSize();
                mPreviewHeight = csize.height; //
                mPreviewWidth = csize.width;
                Log.i(TAG, "initCamera after setting, previewSize:width: "
                        + csize.width + " height: " + csize.height);
                csize = mCamera.getParameters().getPictureSize();
                Log.i(TAG, "initCamera after setting, pictruesize:width: "
                        + csize.width + " height: " + csize.height);
                Log.i(TAG, "initCamera after setting, previewformate is "
                        + mCamera.getParameters().getPreviewFormat());
                Log.i(TAG, "initCamera after setting, previewFrameRate is "
                        + mCamera.getParameters().getPreviewFrameRate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
     
     
    // 【获取视频预览帧的接口】
    PreviewCallback mJpegPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // 传递进来的data,默认是YUV420SP的
            // TODO Auto-generated method stub
            try {
                Log.i(TAG, "going into onPreviewFrame");
                
                // get data length
                int YUVIMGLEN = data.length;
                byte[] rgbBuf = new byte[3 * mPreviewWidth * mPreviewHeight];
                decodeYUV420SP(rgbBuf,data,mPreviewWidth,mPreviewHeight);
                //printHexString(rgbBuf);
                //Bitmap bmp = BitmapFactory.decodeByteArray(rgbBuf, 0, rgbBuf.length);
               
                Log.i(TAG+"RGB:::::", rgbBuf.length+"");
                
                

                /*
                 * //shadowed by chengning 2012-4-25 //mYUV420sp = data; //
                 * 获取原生的YUV420SP数据 YUVIMGLEN = data.length;
                 * 
                 * // 拷贝原生yuv420sp数据 mYuvBufferlock.acquire();
                 * System.arraycopy(data, 0, mYUV420SPSendBuffer, 0,
                 * data.length); //System.arraycopy(data, 0, mWrtieBuffer, 0,
                 * data.length); mYuvBufferlock.release();
                 * 
                 * // 开启编码线程，如开启PEG编码方式线程 mSendThread1.start();
                 */
            } catch (Exception e) {
                Log.v("System.out", e.toString());
            }// endtry

        }// endonPriview

    };
    
    // print byte[] for test only
    public static void printHexString( byte[] b) { 
    	for (int i = 0; i < b.length; i++) { 
    	String hex = Integer.toHexString(b[i] & 0xFF); 
    	if (hex.length() == 1) { 
    	hex = '0' + hex; 
    	} 
    	Log.i("HEX::",hex.toUpperCase());
    	//System.out.print(hex.toUpperCase() ); 
    	} 

    	} 

    
    // Change YUV420SP to RGB
    static public void decodeYUV420SP(byte[] rgbBuf, byte[] yuv420sp, int width, int height) {
    	final int frameSize = width * height;
		if (rgbBuf == null)
			throw new NullPointerException("buffer 'rgbBuf' is null");
		if (rgbBuf.length < frameSize * 3)
			throw new IllegalArgumentException("buffer 'rgbBuf' size "
					+ rgbBuf.length + " < minimum " + frameSize * 3);

		if (yuv420sp == null)
			throw new NullPointerException("buffer 'yuv420sp' is null");

		if (yuv420sp.length < frameSize * 3 / 2)
			throw new IllegalArgumentException("buffer 'yuv420sp' size " + yuv420sp.length
					+ " < minimum " + frameSize * 3 / 2);
    	
    	int i = 0, y = 0;
    	int uvp = 0, u = 0, v = 0;
    	int y1192 = 0, r = 0, g = 0, b = 0;
    	
    	for (int j = 0, yp = 0; j < height; j++) {
    		uvp = frameSize + (j >> 1) * width;
    		u = 0;
    		v = 0;
    		for (i = 0; i < width; i++, yp++) {
    			y = (0xff & ((int) yuv420sp[yp])) - 16;
    			if (y < 0) y = 0;
    			if ((i & 1) == 0) {
    				v = (0xff & yuv420sp[uvp++]) - 128;
    				u = (0xff & yuv420sp[uvp++]) - 128;
    			}
    			
    			y1192 = 1192 * y;
    			r = (y1192 + 1634 * v);
    			g = (y1192 - 833 * v - 400 * u);
    			b = (y1192 + 2066 * u);
    			
    			if (r < 0) r = 0; else if (r > 262143) r = 262143;
    			if (g < 0) g = 0; else if (g > 262143) g = 262143;
    			if (b < 0) b = 0; else if (b > 262143) b = 262143;
    			
    			rgbBuf[yp * 3] = (byte)(r >> 10);
    			rgbBuf[yp * 3 + 1] = (byte)(g >> 10);
    			rgbBuf[yp * 3 + 2] = (byte)(b >> 10);
    		}
    	}
    }
    
    
 // InitSurfaceView
    private void initSurfaceView() {
        mSurfaceview = (SurfaceView) this.findViewById(R.id.Surfaceview);
        mSurfaceHolder = mSurfaceview.getHolder(); // 绑定SurfaceView，取得SurfaceHolder对象
        mSurfaceHolder.addCallback(MainActivity.this); // SurfaceHolder加入回调接口
        // mSurfaceHolder.setFixedSize(176, 144); // 预览大小設置
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 設置顯示器類型，setType必须设置
    }
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initSurfaceView();
    }
    
    
 // 定义对象
    private SurfaceView mSurfaceview = null;  // SurfaceView对象：(视图组件)视频显示 
    private SurfaceHolder mSurfaceHolder = null;  // SurfaceHolder对象：(抽象接口)SurfaceView支持类 
    private Camera mCamera =null;     // Camera对象，相机预览
    
    public static String TAG = "cn";
    boolean bIfPreview = false;
    int mPreviewHeight = 480;
    int mPreviewWidth = 640;
    
}