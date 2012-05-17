package com.iBeiKe.InfoPortal.update;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 提供初始化的动画显示，提供新的线程，防止被主线程操作干扰动画显示。
 *
 */
public class LoadingView extends ImageView implements Runnable
{
	private boolean isStop = false;
	
	private int[] imageIds;
	private int index = 0;
	private int length = 1;
	
	public LoadingView(Context context)
	{
		this(context, null);
	}

	public LoadingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public void setImageIds(int[] imageId)
	{
		this.imageIds = imageId;
		if(imageIds != null && imageIds.length > 0)
		{
			length = imageIds.length;
		}
	}
	
		@Override
	protected void onDetachedFromWindow()
	{
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		isStop = true;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(imageIds != null && imageIds.length > 0)
		{
			this.setImageResource(imageIds[index]);
		}
	}

	public void run()
	{
		while(!isStop)
		{
			index = ++index % length;
			postInvalidate();
			try
			{
				Thread.sleep(400);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void startAnim()
	{
		new Thread(this).start();
	}

}
