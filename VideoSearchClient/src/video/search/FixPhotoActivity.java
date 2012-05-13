package video.search;

import java.io.File;
import java.io.FileOutputStream;

import video.main.CommonOperation;
import video.main.FeatureCode;
import video.values.Const;
import video.values.SearchType;
import video.view.PhotoFixer;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FixPhotoActivity extends Activity implements OnClickListener {
	PhotoFixer photoFixer=null;
	private Button btnSelectAll=null;
	private Button btnTurnLeft=null;
	private Button btnTurnRight=null;
	private Button btnSave=null;
	private Button btnCancel=null;
	private Button btnAdvance=null;
	private Button btnCrop=null;
	
	String photopath=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fixphoto);
		LinearLayout ll= (LinearLayout)findViewById(R.id.fixerlayout);
		//得到照片
		photopath=getIntent().getStringExtra("bitmap");
		Bitmap photo=BitmapFactory.decodeFile(photopath);
		if(photo==null)
		{
			finish();
		}
		photoFixer=new PhotoFixer(this,photo);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		photoFixer.setLayoutParams(params);
		ll.addView(photoFixer);
		
		photo=null;
		//找到按钮
		btnSelectAll=(Button)findViewById(R.id.btnSelectAll);
		btnTurnLeft=(Button)findViewById(R.id.btnTurnLeft);
		btnTurnRight=(Button)findViewById(R.id.btnTurnRight);
		btnSave=(Button)findViewById(R.id.btnSave);
		btnCancel=(Button)findViewById(R.id.btnCancel);
		btnAdvance=(Button)findViewById(R.id.btnAdvance);
		btnCrop=(Button)findViewById(R.id.btnCrop);
		//设置事件
		btnSelectAll.setOnClickListener(this);
		btnTurnLeft.setOnClickListener(this);
		btnTurnRight.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnAdvance.setOnClickListener(this);
		btnCrop.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent searchIntent=new Intent(FixPhotoActivity.this,ResultActivity.class);
		//放置类型
		searchIntent.putExtra("type", SearchType.PHOTO);
		//放置图片
		byte[] photo =CommonOperation.bitmapToBytes( photoFixer.getFixedBitmap());
		searchIntent.putExtra("photo", photo);
		
		switch (v.getId()) {
		case R.id.btnSelectAll:
			photoFixer.selectAll();
			break;
		case R.id.btnCancel:
			finish();
			break;
		case R.id.btnTurnLeft:
			photoFixer.turnLeft();
			break;
		case R.id.btnTurnRight:
			photoFixer.turnRight();
			break;
		case R.id.btnSave:
			showSaveDialog();
			break;
		case R.id.btnAdvance:
			CommonOperation.showAdvanceDialog(FixPhotoActivity.this,searchIntent);
			break;
		case R.id.btnCrop:
			startPhotoZoom(Uri.fromFile(new File(photopath)));
		default:
			break;	
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data==null)
			return;
		Bundle extras = data.getExtras();         
		if (extras != null) {              
			Bitmap photo = extras.getParcelable("data");              
			this.photoFixer.setImageBitmap(photo);
			this.photoFixer.postInvalidate();
		}
	}
	private void showSaveDialog() {
		final Bitmap bm=photoFixer.getFixedBitmap();
		View saveDialog=getLayoutInflater().inflate(R.layout.savephoto, null);
		final EditText photoNameEditText=(EditText)saveDialog.findViewById(R.id.editText1);
		ImageView show=(ImageView)saveDialog.findViewById(R.id.imageView1);
		TextView tView=(TextView)saveDialog.findViewById(R.id.textView2);
		tView.setText("图片大小: "+bm.getWidth()+"(宽) * "+bm.getHeight()+"(高)");
		show.setImageBitmap(bm);
		Time time=new Time();
		time.setToNow();
		photoNameEditText.setText(String.valueOf(time.toMillis(false)));
		new AlertDialog.Builder(FixPhotoActivity.this).setView(saveDialog).setPositiveButton("保存", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				File file=new File(Const.APP_DIR_PHOTO,photoNameEditText.getText().toString()+".jpg");
				FileOutputStream outputStream=null;
				try{
					outputStream=new FileOutputStream(file);
					bm.compress(CompressFormat.PNG, 100, outputStream);
					Toast.makeText(FixPhotoActivity.this, "成功保存文件到 " +file.getPath(), 3000).show();
					outputStream.close();
				}
				catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(FixPhotoActivity.this, "保存文件失败，请检查权限。"+e.getMessage(), 3000).show();
				}
			}
		}).setNegativeButton("取消", null).show();
	}
	
	public void startPhotoZoom(Uri uri) {  
        /*           * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页           * yourself_sdk_path/docs/reference/android/content/Intent.html           * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,           * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么           * 制做的了...吼吼           */         
		Intent intent = new Intent("com.android.camera.action.CROP");          
		intent.setDataAndType(uri, "image/*");          //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪          
		intent.putExtra("crop", "true");          // aspectX aspectY 是宽高的比例          
	//	intent.putExtra("aspectX", 1);         
	//	intent.putExtra("aspectY", 1);          // outputX outputY 是裁剪图片宽高          
	//	intent.putExtra("outputX", 150);          
	//	intent.putExtra("outputY", 150);         
		intent.putExtra("return-data", true);          
		startActivityForResult(intent, 3);
	}  
	
}
