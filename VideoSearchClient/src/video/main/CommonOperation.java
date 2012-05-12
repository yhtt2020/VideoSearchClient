package video.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import video.module.LoadingThread;
import video.module.SanInputStream;
import video.search.PrevVideoActivity;
import video.search.R;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class CommonOperation { 
	
	public static Boolean createPath(String path) {
	     File file = new File(path);
	     if(file.exists())
	     {
	    	 return true;
	     }
	     else
	     {
	          if(file.mkdir())
	        	  {
	        	  	return true;
	        	  }
	     }
	     return false;
	 }
	
	public static void toast(Context context,CharSequence text,int flag) {
		Toast.makeText(context, text, flag).show();
	}
	public static void toast(Context context, CharSequence  text) {
		Toast.makeText(context, text,Toast.LENGTH_SHORT).show();
	}
	public static void showAdvanceDialog(final Context context, final Intent intent){
		View saveDialog=((Activity) context).getLayoutInflater().inflate(R.layout.searchdetail, null);
		final Spinner spAlpha= (Spinner)saveDialog.findViewById(R.id.spAlpha);
		final Spinner spSameDegree=(Spinner)saveDialog.findViewById(R.id.spSameDegree);
		final Spinner spKind=(Spinner)saveDialog.findViewById(R.id.spKind);
		new AlertDialog.Builder(context).setView(saveDialog).setPositiveButton("搜索", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String alpha=spAlpha.getSelectedItem().toString();
				String kind=spKind.getSelectedItem().toString();
				String sameDegree=spSameDegree.getSelectedItem().toString();
				//放入三个参数
				intent.putExtra("alpha", alpha);
				intent.putExtra("kind", kind);
				intent.putExtra("samedegree",sameDegree);
				//启动搜索结果活动		
				context.startActivity(intent);
				if(context.getClass()==PrevVideoActivity.class)
				{
					((PrevVideoActivity)context).stopPlayBack();
				}
				((Activity) context).finish();
				

			}
		}).setNegativeButton("取消", null).show();
		
	}
	
	public static byte[] bitmapToBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
		return os.toByteArray();
		//return bitmap
	}

	public static void loadImageOnLoadingThread(final ImageView view, final String url,final File file){
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
