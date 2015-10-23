package com.tcl.roselauncher.ui.mainface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;

public class TextRect
{
	static final float UNIT_SIZE=0.6f;
	int mProgram;//�Զ�����Ⱦ���߳���id
    int muMVPMatrixHandle;//�ܱ任��������id
    int maPositionHandle; //����λ����������id  
    int maTexCoorHandle; //��������������������id  
    
	FloatBuffer   mVertexBuffer;//�����������ݻ���
	FloatBuffer   mTexCoorBuffer;//���������������ݻ���
    int vCount=0;   
    public TextRect(MainFaceView mv)
    {
    	initVertexData();
    	initShader(mv);
    }
    //��ʼ���������ݵķ���
    public void initVertexData()
    {
    	vCount=6;
        float vertices[]=new float[]
        {
        	-UNIT_SIZE,-UNIT_SIZE,0,
            UNIT_SIZE,-UNIT_SIZE,0,
            UNIT_SIZE,UNIT_SIZE,0,
            
            UNIT_SIZE,UNIT_SIZE,0,
            -UNIT_SIZE,UNIT_SIZE,0,
            -UNIT_SIZE,-UNIT_SIZE,0,
        };
        //���������������ݻ���
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        
        float[] texcoor=new float[]
        {
        	0,1,   1,1,   1,0,
        	1,0,   0,0,   0,1
        };
        ByteBuffer tbb = ByteBuffer.allocateDirect(texcoor.length*4);
        tbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mTexCoorBuffer = tbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mTexCoorBuffer.put(texcoor);//�򻺳����з��붥����������
        mTexCoorBuffer.position(0);//���û�������ʼλ��        
    }
    //��ʼ��shader
    public void initShader(MainFaceView mv)
    {
    	//���ض�����ɫ���Ľű�����
        String mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        String mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�������������������id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    //�����Ʒ���
    public void drawSelf(int texId)    
    {
    	//ָ��ʹ��ĳ��shader����
   	 	GLES20.glUseProgram(mProgram); 
        //�����ձ任������shader����
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //���Ͷ���λ������
        GLES20.glVertexAttribPointer  
        (
        	maPositionHandle,   
        	3, 
        	GLES20.GL_FLOAT, 
        	false,
            3*4,   
            mVertexBuffer
        );       
        //���Ͷ���������������
        GLES20.glVertexAttribPointer  
        (
       		maTexCoorHandle, 
        	2, 
        	GLES20.GL_FLOAT, 
        	false,
            2*4,   
            mTexCoorBuffer
        );   
        //������λ�á�����������������
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        
        //������
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        
        //�����������
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
}