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

	boolean lightFlag=true;
	public MainFaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setEGLContextClientVersion(2);
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mRenderer = new SceneRenderer();
		setRenderer(mRenderer);
		//setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setZOrderOnTop(true);
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
            mRenderer.circle4.yAngle += dx * TOUCH_SCALE_FACTOR;//设置纹理矩形绕y轴旋转角度
            mRenderer.circle4.zAngle+= dy * TOUCH_SCALE_FACTOR;//设置第纹理矩形绕z轴旋转角度
            mRenderer.circle3.yAngle += dx * TOUCH_SCALE_FACTOR;//设置纹理矩形绕y轴旋转角度
            mRenderer.circle3.zAngle+= dy * TOUCH_SCALE_FACTOR;//设置第纹理矩形绕z轴旋转角度
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }
    
	private class SceneRenderer implements GLSurfaceView.Renderer {

		public int textureId4;
		public int textureId3;
		public int texId = -1;
		/* (non-Javadoc)
		 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
		 */
		Circle circle4, circle3;//纹理矩形
		TextRect tRect;
		//Triangle triangle;
		@Override
		public void onDrawFrame(GL10 arg0) {
			// TODO Auto-generated method stub
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
//			glEnable(GL_BLEND);
//        	glBlendFunc(GL_SRC_ALPHA_SATURATE, GL_ONE);
			
			
        	
			MatrixState.pushMatrix();
            tRect.drawSelf(texId);
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
			circle3.drawSelf(textureId3);
            MatrixState.popMatrix();
            
			MatrixState.pushMatrix();
			circle4.drawSelf(textureId4);
            MatrixState.popMatrix();			
			
		}

		/* (non-Javadoc)
		 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
		 */
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
			GLES20.glViewport(0, 0, width, height);
			float ratio = (float) width /height;
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1.0f, 10);
			MatrixState.setCamera(0,0,2.0f,0f,0f,0f,0f,1.0f,0.0f);
			//初始化光源
//	        MatrixState.setLightLocation(100 , 0 , 100);
//	                      
//	        //启动一个线程定时修改灯光的位置
//	        new Thread()
//	        {
//				public void run()
//				{
//					float redAngle = 0;
//					while(lightFlag)
//					{	
//						//根据角度计算灯光的位置
//						redAngle=(redAngle+5)%360;
//						float rx=(float) (15*Math.sin(Math.toRadians(redAngle)));
//						float rz=(float) (15*Math.cos(Math.toRadians(redAngle)));
//						MatrixState.setLightLocation(rx, 0, rz);
//						//Log.d("MainFace", rx + ".." + rz);
//						
//						try {
//								Thread.sleep(100);
//							} catch (InterruptedException e) {				  			
//								e.printStackTrace();
//							}
//					}
//				}
//	        }.start();
		}

		/* (non-Javadoc)
		 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
		 */
		@Override
		public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
			// TODO Auto-generated method stub
			GLES20.glClearColor(0f, 0f, 0f, 0f);
			//启用深度测试
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    		//设置为打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
			//初始化变换矩阵
            MatrixState.setInitStack();
            FontUtil.cIndex=(FontUtil.cIndex+1)%FontUtil.content.length;
        	FontUtil.updateRGB();
        	if (texId != -1) {
        		GLES20.glDeleteTextures(1, new int[]{texId}, 0);
			}
            Bitmap bm=FontUtil.generateWTF(FontUtil.getContent(FontUtil.cIndex, FontUtil.content), 512, 512);
            texId=initTexture(0,bm);
            
        	
            textureId4 = initTexture(R.drawable.circle4,null);
            textureId3 = initTexture(R.drawable.circle3,null);
			circle4 = new Circle(MainFaceView.this,1.0f,1.0f,100);
			circle3 = new Circle(MainFaceView.this,0.8f,1.0f,100);
            //triangle = new Triangle(MainFaceView.this);
			GLES20.glDisable(GLES20.GL_CULL_FACE);
		}
		
	}
	
	public int initTexture(int sourseId, Bitmap bitmap)//textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		int textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        
     
        //通过输入流加载图片===============begin===================
		Bitmap bitmapTmp;
		if (null != bitmap) {
			bitmapTmp = bitmap;
		}else{
			 InputStream is = this.getResources().openRawResource(sourseId);
		        
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
        return textureId;
	}



}
