/**
 * 
 */
package com.tcl.roselauncher.ui.mainface;


import static com.tcl.roselauncher.ui.mainface.Constant.*;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



import android.R.integer;
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
	float lightOffset=-4;//灯光的位置或方向的偏移量
	public Ball ball;
	public static int angleTemp = 0;
	boolean lightFlag=true;
	int textureIdEarth;//系统分配的地球纹理id
    int textureIdEarthNight;//系统分配的地球夜晚纹理id
    float yAngle=0;//太阳灯光绕y轴旋转的角度
    float xAngle=0;//摄像机绕X轴旋转的角度
    
    float eAngle=0;//地球自转角度    
    float cAngle=0;//天球自转的角度
	public MainFaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setEGLContextClientVersion(2);
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mRenderer = new SceneRenderer();
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setZOrderOnTop(true);
	}
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	//触控横向位移太阳绕y轴旋转
            float dx = x - mPreviousX;//计算触控笔X位移 
            yAngle += dx * TOUCH_SCALE_FACTOR;//设置太阳绕y轴旋转的角度
            float sunx=(float)(Math.cos(Math.toRadians(yAngle))*100);
            float sunz=-(float)(Math.sin(Math.toRadians(yAngle))*100);
            MatrixState.setLightLocationSun(sunx,5,sunz);  
            
            //触控纵向位移摄像机绕x轴旋转 -90〜+90
            float dy = y - mPreviousY;//计算触控笔Y位移 
            xAngle += dy * TOUCH_SCALE_FACTOR;//设置太阳绕y轴旋转的角度
            if(xAngle>90)
            {
            	xAngle=90;
            }
            else if(xAngle<-90)
            {
            	xAngle=-90;
            }
            float cy=(float) (7.2*Math.sin(Math.toRadians(xAngle)));
            float cz=(float) (7.2*Math.cos(Math.toRadians(xAngle)));
            float upy=(float) Math.cos(Math.toRadians(xAngle));
            float upz=-(float) Math.sin(Math.toRadians(xAngle));
            MatrixState.setCamera(0, cy, cz, 0, 0, 0, 0, upy, upz);           
        }
        mPreviousX = x;//记录触控笔位置
        mPreviousY = y;
        return true; 
    } 
    
	private class SceneRenderer implements GLSurfaceView.Renderer {


		Launchegg launchegg;
		//Triangle triangle;
		@Override
		public void onDrawFrame(GL10 arg0) {
			// TODO Auto-generated method stub
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
//			glEnable(GL_BLEND);
//        	glBlendFunc(GL_SRC_ALPHA_SATURATE, GL_ONE);
//			MatrixState.setLightLocation(lightOffset, 0, 1.5f);
			MatrixState.setLightLocationSun(0 , 0 , 100);
			
//			MatrixState.pushMatrix();
//            tRect.drawSelf(texId);
//            MatrixState.popMatrix();
//            
//            MatrixState.pushMatrix();
//			circle3.drawSelf(textureId3);
//            MatrixState.popMatrix();
//            
//			MatrixState.pushMatrix();
//			circle4.drawSelf(textureId4);
//            MatrixState.popMatrix();	
            
//            MatrixState.pushMatrix();			
//			MatrixState.rotate(angleTemp, 1, 0, 0);
//			MatrixState.translate(2.5f, 0, 0);
//			ball.drawSelf();
//            MatrixState.popMatrix();
//            angleTemp ++;
			MatrixState.pushMatrix();
            //地球自转
//            MatrixState.rotate(eAngle, 0, 1, 0);
//        	//绘制纹理圆球
//			launchegg.drawSelf(textureIdEarth,textureIdEarthNight);   
            //推坐标系到月球位置   
			MatrixState.rotate(eAngle, 0, 3, 1);
            MatrixState.translate(5.5f, 0, 0);  
            //月球自转     
            
            launchegg.drawSelf(textureIdEarth,textureIdEarthNight);  
            //恢复现场
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
	        
	      //启动一个线程定时旋转地球、月球
            new Thread()
            {
            	public void run()
            	{
            		while(threadFlag)
            		{
            			//地球自转角度
            			eAngle=(eAngle+2)%360;
            			//天球自转角度
            			cAngle=(cAngle+0.2f)%360;
            			try {
							Thread.sleep(100);
						} catch (InterruptedException e) {				  			
							e.printStackTrace();
						}
            		}
            	}
            }.start();
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
            launchegg = new Launchegg(MainFaceView.this, 2.0f);
			textureIdEarth = initTexture(R.drawable.earth);
			textureIdEarthNight = initTexture(R.drawable.earthn);
			//初始化变换矩阵
            MatrixState.setInitStack();
//            
//            Bitmap bm=FontUtil.generateWLT(FontUtil.getContent(1, FontUtil.content), 256, 256);
//	        texId=initTextureBmp(bm);
//	        tRect = new TextRect(MainFaceView.this);

//			GLES20.glDisable(GLES20.GL_CULL_FACE);
		}
	}
	
	public int initTexture(int sourseId)//textureId
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
	
	public int initTextureBmp(Bitmap bitmap)
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
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmap, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmap.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}



}
