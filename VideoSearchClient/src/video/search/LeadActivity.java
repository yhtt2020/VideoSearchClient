package video.search;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.ksoap2.serialization.SoapObject;

import video.ad.AdBanner;
import video.main.CommonOperation;
import video.values.Const;
import video.values.Global;
import video.values.HanderMessage;
import video.values.SearchType;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.FlagToString;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LeadActivity extends Activity implements OnClickListener {

	private static final int RECODE_TAKEPHOTO = 0x001;
	private static final int RECODE_CHOOSEPHOTO = 0x002;
	private static final int RECODE_TAKEVIDEO = 0x003;
	private static final int RECODE_CHOOSEVIDEO = 0x004;
	private static final int NOYIFICATION_ID = 0x1123;
	private Button btnPhoto = null;
	private Button btnVideo = null;
	private Button btnCPhoto = null;
	private Button btnCVideo = null;
	private Button btnKeyWords = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		(new loadMain()).start();
	}
	
	private void showAd(){
		View v = findViewById(R.id.llAd);
		LinearLayout ll = (LinearLayout)v;
		AdBanner.create(this, ll);
	}

	private void init() {
		showAd();
		btnPhoto = (Button) findViewById(R.id.btnsphoto);
		btnVideo = (Button) findViewById(R.id.btnsvideo);
		btnCPhoto = (Button) findViewById(R.id.btnscphoto);
		btnCVideo = (Button) findViewById(R.id.btnscvideo);
		btnKeyWords = (Button) findViewById(R.id.btnskeywords);

		btnPhoto.setOnClickListener(this);
		btnVideo.setOnClickListener(this);
		btnCPhoto.setOnClickListener(this);
		btnCVideo.setOnClickListener(this);
		btnKeyWords.setOnClickListener(this);
		
		SharedPreferences preferences;
		SharedPreferences.Editor editor;
		preferences=PreferenceManager.getDefaultSharedPreferences(this);
		//preferences=getSharedPreferences("coolsou", MODE_PRIVATE);
		editor=preferences.edit();
		//������״����б�����
		if(preferences.getBoolean(Const.SETTING_AUTOLOGIN, false)==true)
		{
			Global.userid=preferences.getString(Const.SETTING_USERID, "0");
			Global.userName=preferences.getString(Const.SETTING_USERNAME, "��");
		}
		if(preferences.getBoolean(Const.SETTING_ISFIRSTTIME, true)==true)
		{
			//ֻҪ�κ�һ��Ŀ¼����ʧ�ܾ��Զ��˳�
			if(!CommonOperation.createPath(Const.APP_DIR) || !CommonOperation.createPath(Const.APP_DIR_PHOTO) 
				|| !CommonOperation.createPath(Const.APP_DIR_TEMP)	) 
			{
				Toast.makeText(this, "�ǳ���Ǹ��������������Ŀ¼ʧ�ܣ���������Ƿ����Ȩ�ޣ������Զ��˳���", Toast.LENGTH_LONG).show();
				this.finish();
				return;
			}
			else
			{
				//����Ϊ�ǵ�һ������
				setNotify();
				Toast.makeText(this, "��ӭ�״�ʹ�ÿ��ѣ�ϣ�����ѵ���졣", Toast.LENGTH_SHORT).show();
				editor.putBoolean("isFirstTime", false);
				editor.commit();
			}
		}
		else {
			Toast.makeText(this, "��ӭ����������Ϊ���ṩ�����������",1000).show();
		}
		
	}
	private void setNotify() {
		Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.coolsou.com"));
		PendingIntent pi=PendingIntent.getActivity(LeadActivity.this, 0, intent, 0);
		Notification notify=new Notification();
		notify.icon=R.drawable.notify;
		notify.tickerText="��ӭ�״�ʹ�ÿ��ѣ���������ǵ���ҳ��ȡ������Ϣ��";
		notify.when=System.currentTimeMillis();
		notify.defaults=Notification.DEFAULT_SOUND;
		notify.flags= Notification.FLAG_AUTO_CANCEL;
		notify.setLatestEventInfo(LeadActivity.this, "����", "����������ǵ���ҳ��ȡ������Ϣ��", pi);
		NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(NOYIFICATION_ID,notify);
	}
	// �����߳���Ϣ��Handler
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// �յ��߳��������������Ϣ
			case HanderMessage.LOADLAYOUT:
				setContentView(R.layout.lead);
				init();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onBackPressed() {
		showExitDialog();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Uri mMediaUri;
		 if(data==null)
		 {
			 showCancel();
      				  return;
		 }
		switch (requestCode) {
		case RECODE_TAKEPHOTO:
			startPhotoFixer(analyseData(data));
			break;
		case RECODE_CHOOSEPHOTO:
			mMediaUri=data.getData();   
	            Cursor cursor = getContentResolver().query(mMediaUri, null, null, null, null);   
	            cursor.moveToFirst();   
	            String mediaFilePath = cursor.getString(1);
	            
	            Bitmap photo = BitmapFactory.decodeFile(mediaFilePath);
	            Display currentDisplay=getWindowManager().getDefaultDisplay();
	            float rate= (((float)photo.getWidth())/photo.getHeight());
	            Bitmap photoScaled=ThumbnailUtils.extractThumbnail(photo, currentDisplay.getWidth(), (int) (currentDisplay.getWidth()/rate));
			startPhotoFixer(photoScaled);
			break;
		case RECODE_TAKEVIDEO :
			if(resultCode!= RESULT_OK)
				break;
			Uri videoFileUri = data.getData();
			if(videoFileUri==null)
			{
				showCancel();
	      				  return;
			}
			Intent intent=new Intent(LeadActivity.this,PrevVideoActivity.class);
			intent.putExtra("videofile", videoFileUri);
			startActivity(intent);
			break;
		case RECODE_CHOOSEVIDEO:
			mMediaUri=data.getData();   
	        Cursor cursor1 = getContentResolver().query(mMediaUri, null, null, null, null);   
	        cursor1.moveToFirst();   
	        File mediaFilePath1 =new File( cursor1.getString(1));
	            Uri videofile= Uri.fromFile(mediaFilePath1);
	           	 if (videofile== null){
	            	showCancel();
	      				  return;
	            }
	           	Intent intent1=new Intent(LeadActivity.this,PrevVideoActivity.class);
	 			intent1.putExtra("videofile", videofile);
				startActivity(intent1);
			break;
		}
		
		
	}
	private void showCancel()
	{
		Toast.makeText(LeadActivity.this,
				   "����ȡ����", Toast.LENGTH_SHORT).show();
	}
	private Bitmap analyseData(Intent data) {
		 if (data==null)
		  {   
			 Toast.makeText(LeadActivity.this,
					   "����ȡ����", Toast.LENGTH_LONG).show();
			 return null;
		 }
			Uri uri = data.getData();
			Bitmap photo = null;
			if (uri != null) {
				photo = BitmapFactory.decodeFile(uri.getPath());
			}
			if (photo == null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					photo = (Bitmap) bundle.get("data");
				} else {
					Toast.makeText(LeadActivity.this,
					   "����ȡ����", Toast.LENGTH_LONG).show();
					  return  null;
				}
			}
			return photo;
	}
  private void startPhotoFixer(Bitmap photo) {
	   if(photo==null || photo.getWidth()==-1)
	   {
		   Toast.makeText(LeadActivity.this,
				   "����ȡ����", Toast.LENGTH_LONG).show();
		   return ;
	   }
		Intent intent = new Intent(LeadActivity.this,
				FixPhotoActivity.class);
		//intent.putExtra("bitmap", photo);
		intent.putExtra("bitmap", CommonOperation.bitmapToBytes(photo));
		startActivity(intent);
}


	// ������������߳�
	private class loadMain extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Message msg = new Message();
			msg.what = HanderMessage.LOADLAYOUT;
			mHandler.sendMessage(msg);
			super.run();
		}

	}

	// �˵�����ʵ��
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent =new Intent();
		switch (item.getItemId()) {
		case R.id.menuLogin:
			intent.setClass(LeadActivity.this,LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.menuRegister:
			intent.setClass(LeadActivity.this,RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.menuUser:
			if(Global.userid=="0")
			{
				CommonOperation.toast(LeadActivity.this, "����δ��¼�����ȵ�¼��");
			}
			else {
				intent.setClass(LeadActivity.this,UserCenterActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.menuSetting:
			intent.setClass(LeadActivity.this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.menuHelp:
			break;
		case R.id.menuexit:
			showExitDialog();
			break;
		}
		return true;
	}
	private void showExitDialog()
	{
		final Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ȷ��");
		builder.setMessage("ȷ���˳����ѣ�");
		builder.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LeadActivity.this.finish();
					}
				});
		builder.setNegativeButton("ȡ��", null);
		builder.create().show();
	}
	// �����˵�
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.searchmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// ���¹ؼ���������ť
		case R.id.btnskeywords:
			String keyWords = ((EditText) findViewById(R.id.edtKeywords))
					.getText().toString();
			// ���������������
			Intent skintent = new Intent(LeadActivity.this,
					ResultActivity.class);
			skintent.putExtra("KeyWords", keyWords);
			skintent.putExtra("type", SearchType.KEYWORDS);
			startActivity(skintent);
			break;
		//��������
		case R.id.btnsphoto:
			Intent tpintent = new Intent();
			tpintent.setAction("android.media.action.IMAGE_CAPTURE");
			startActivityForResult(tpintent, RECODE_TAKEPHOTO);
		   break;
		case R.id.btnscphoto:
			Intent cpintent=new Intent();
			cpintent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*" );   
			cpintent.setAction(Intent.ACTION_PICK); 
			startActivityForResult(cpintent, RECODE_CHOOSEPHOTO);
			break;
		//¼������
		case R.id.btnsvideo:
			Intent tvIntent=new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
			startActivityForResult(tvIntent, RECODE_TAKEVIDEO);
			break;
		case R.id.btnscvideo:
			Intent cvintent=new Intent();
			cvintent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*" );   
			cvintent.setAction(Intent.ACTION_PICK); 
			startActivityForResult(cvintent, RECODE_CHOOSEVIDEO);
			break;
			
		}
	}
}
