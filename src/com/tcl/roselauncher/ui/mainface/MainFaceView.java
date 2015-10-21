/**
 * 
 */
package com.tcl.roselauncher.ui.mainface;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;




import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

/**
 * @Project MainFace	
 * @author houxb
 * @Date 2015-10-21
 * 
 */
public class MainFaceView extends GLSurfaceView {

	/**
	 * @param context
	 */
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
	private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
	private SceneRenderer mRenderer; 
	private int textureId;
	public MainFaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setEGLContextClientVersion(2);
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mRenderer = new SceneRenderer();
		setRenderer(mRenderer);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);//设置透明
        this.setZOrderOnTop(true);//设置置顶
	}
	
	  //触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            mRenderer.texRect.yAngle += dx * TOUCH_SCALE_FACTOR;//设置纹理矩形绕y轴旋转角度
            mRenderer.texRect.zAngle+= dy * TOUCH_SCALE_FACTOR;//设置第纹理矩形绕z轴旋转角度
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }
    
	private class SceneRenderer implements GLSurfaceView.Renderer {

		/* (non-Javadoc)
		 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
		 */
		Triangle texRect;//纹理矩形
		
		@Override
		public void onDrawFrame(GL10 arg0) {
			// TODO Auto-generated method stub
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
			texRect.drawSelf(textureId);
			
		}

		/* (non-Javadoc)
		 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
		 */
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
			GLES20.glViewport(0, 0, width, height);
			float ratio = (float) width /height;
			MatrixState.setProject(-ratio, ratio, -1, 1, 1, 10);
			MatrixState.setCamera(0,0,3,0f,0f,0f,0f,1.0f,0.0f);
		}

		/* (non-Javadoc)
		 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
		 */
		@Override
		public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
			// TODO Auto-generated method stub
			GLES20.glClearColor(0f, 0f, 0f, 0f);
			texRect = new Triangle(MainFaceView.this);
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			initTexture();
			GLES20.glDisable(GLES20.GL_CULL_FACE);
		}
		
	}
	public void initTexture()//textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        
     
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(R.drawable.wall);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================  
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
	}



}
