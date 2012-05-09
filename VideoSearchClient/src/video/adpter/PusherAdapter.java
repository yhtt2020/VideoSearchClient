package video.adpter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import video.module.LoadingThread;
import video.module.PusherEntity;
import video.module.SanInputStream;
import video.values.Const;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PusherAdapter extends BaseAdapter {
	private Context mContext;
	private ImageView[] imgItems;
	private int selResId;
	private PusherEntity[] pusherEntities;
	
	public PusherAdapter (Context mContext,PusherEntity[] pusherEntities,int width,int height,int selResId) {
		super();
		this.mContext=mContext;
		this.selResId=selResId;
		this.pusherEntities=pusherEntities;
		imgItems=new ImageView[pusherEntities.length];
		 for(int i=0;i<pusherEntities.length;i++)  
	        {  
	            imgItems[i] = new ImageView(mContext);   
	           // imgItems[i].setLayoutParams(new Gallery.LayoutParams(310, 310));//设置ImageView宽高   
	            //imgItems[i].setAdjustViewBounds(false);   
	            imgItems[i].setScaleType(ImageView.ScaleType.CENTER_CROP);   
	            imgItems[i].setPadding(10, 10, 10, 10);   	
	        }  
	}

	@Override
	public int getCount() {
		return imgItems.length;
	}

	@Override
	public Object getItem(int position) {
		return position;

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

/*
	public void SetFocus(int index)    
    {    
        for(int i=0;i<imgItems.length;i++)    
        {    
            if(i!=index)    
            {    
                imgItems[i].setBackgroundResource(0);//恢复未选中的样式  
            }    
        }    
        imgItems[index].setBackgroundResource(selResId);//设置选中的样式  
    }    
	*/
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;   


        if (convertView == null) {   
            imageView=imgItems[position];  
        } else {   
            imageView = (ImageView) convertView;   
        }   
        
        String filePath=Const.APP_DIR_TEMP+String.valueOf(pusherEntities[position].getId())+".jpg";
		File file=new File(filePath);
		if( (file.exists()) )
		{
			imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
		}
		else {
			loadImageOnLoadingThread(imageView,pusherEntities[position].imageURL,file );
		}
		
        return imageView;   
	}
	private static void loadImageOnLoadingThread(final ImageView view, final String url,final File file){
		LoadingThread.run(new Runnable(){
			@Override
			public void run() {
				try{
					InputStream in = new java.net.URL(url).openStream();
					Bitmap image = BitmapFactory.decodeStream(new SanInputStream(in));
					FileOutputStream outputStream=new FileOutputStream(file);
					image.compress(CompressFormat.JPEG, 100, outputStream);
					outputStream.close();
					((ImageView)view).setImageBitmap(image);
					view.postInvalidate();
				}
				catch(Exception e){
				}
			}});
	}
}
