package video.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import video.module.LoadingThread;
import video.module.SanInputStream;
import video.search.FixPhotoActivity;
import video.search.MapPosActivity;
import video.search.PrevVideoActivity;
import video.search.R;
import video.search.ResultActivity;
import video.search.VideoPlayerActivity;
import video.values.Const;
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
import android.net.Uri;
import android.opengl.Visibility;
import android.text.format.Time;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
	
	/*
	 View root = ResultActivity.this.getLayoutInflater().inflate(
				R.layout.itemmenu, null);
	   popContextMenu = new PopupWindow(root,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		Button btnDetail = (Button) root.findViewById(R.id.btnDetail);
		Button btnPlay = (Button) root.findViewById(R.id.btnPlay);
		Button btnLead = (Button) root.findViewById(R.id.btnLead);
		Button btnAgain = (Button) root.findViewById(R.id.btnAgain);
		class ContextMenuOnClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btnPlay:
					Intent intent = new Intent(ResultActivity.this,
							VideoPlayerActivity.class);
					intent.putExtra("name", good.getName());
					intent.putExtra("url",
							ResultActivity.VIDEO_URL + good.getId() + ".3gp");
					startActivity(intent);
					break;
				case R.id.btnLead:
					if (good.getExactPosition().isEmpty()) {
						CommonOperation.toast(ResultActivity.this,
								"该商品无位置信息，暂时无法定位。");
						break;
					}
					Intent mapintent = new Intent(ResultActivity.this,
							MapPosActivity.class);
					mapintent.putExtra("pos", good.getExactPosition());
					startActivityForResult(mapintent, REQUEST_MapPos);
					break;
				case R.id.btnDetail:
					Intent browserIntent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(good.getUrl()));
					startActivity(browserIntent);
					break;
				case R.id.btnAgain:
					File file = new File(Const.APP_DIR_TEMP
							+ String.valueOf(good.getId()) + ".jpg");
					if (file.exists()) {
						Intent intent2 = new Intent(ResultActivity.this,
								FixPhotoActivity.class);
						// intent.putExtra("bitmap", photo);
						intent2.putExtra("bitmap", CommonOperation
								.bitmapToBytes(BitmapFactory
										.decodeFile(Const.APP_DIR_TEMP
												+ String.valueOf(good.getId())
												+ ".jpg")));
						startActivity(intent2);
						finish();
					} else {
						{
							Toast.makeText(ResultActivity.this,
									"该商品暂无缩略图，无法搜索。", Toast.LENGTH_LONG).show();
						}
					}
				default:
					break;
				}
			}
		}
		btnDetail.setOnClickListener(new ContextMenuOnClickListener());
		btnLead.setOnClickListener(new ContextMenuOnClickListener());
		btnPlay.setOnClickListener(new ContextMenuOnClickListener());
		btnAgain.setOnClickListener(new ContextMenuOnClickListener());
		popContextMenu.setAnimationStyle(android.R.style.Animation_Translucent);
		popContextMenu.showAtLocation(root, Gravity.BOTTOM, 0, -400);
		popContextMenu.setOutsideTouchable(true);
		popContextMenu.setFocusable(false);
		popContextMenu.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_OUTSIDE)
					{
						popContextMenu.dismiss();
						popContextMenu=null;
						return true;
					}
				return false;
			}
		});
	}
	  */
	public static PopupWindow initPopWindow(Context context,View root)
	{
		PopupWindow popupWindow=null;
		popupWindow = new PopupWindow(root,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
		return popupWindow;
	}
	
	public static void showAdvanceDialog(final Context context, final Intent intent){
		View root=((Activity) context).getLayoutInflater().inflate(R.layout.searchdetail, null);
		
		final PopupWindow popupWindow=initPopWindow(context,root);
		final Spinner spAlpha= (Spinner)root.findViewById(R.id.spAlpha);
		final Spinner spSameDegree=(Spinner)root.findViewById(R.id.spSameDegree);
		final Spinner spKind=(Spinner)root.findViewById(R.id.spKind);
		Button btnSearch=(Button)root.findViewById(R.id.btnSearch);
		Button btnCancel=(Button)root.findViewById(R.id.btnCancel);
		final ImageView imgPreview= (ImageView) root.findViewById(R.id.imgPreview);
		if(context.getClass()==PrevVideoActivity.class)
		{
			((PrevVideoActivity)context).stopPlayBack();
			imgPreview.setVisibility(View.GONE);
		}
		else {
			imgPreview.setImageBitmap(BitmapFactory.decodeByteArray(intent.getByteArrayExtra("photo"), 0,intent.getByteArrayExtra("photo").length));
		}
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String alpha=spAlpha.getSelectedItem().toString();
				String kind=spKind.getSelectedItem().toString();
				String sameDegree=spSameDegree.getSelectedItem().toString();
				//放入三个参数
				double al= (Math.sqrt(Double.parseDouble(alpha))) ;
				double sa= (Math.sqrt(Double.parseDouble(sameDegree))) ;
				intent.putExtra("alpha", String.valueOf(al));
				intent.putExtra("kind", kind);
				intent.putExtra("samedegree",String.valueOf(sameDegree));
				//启动搜索结果活动		
				context.startActivity(intent);
				
				((Activity) context).finish();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
		popupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
	}
	public static void startWebBrowser(Context context,String url){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(url));
		context.startActivity(browserIntent);
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
	public static String getTimeString()
	{
		Time time = new Time();
		time.setToNow();
		String result = String.valueOf(time.toMillis(false));
		return result;
	}
}
