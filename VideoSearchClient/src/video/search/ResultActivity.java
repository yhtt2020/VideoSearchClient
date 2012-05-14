package video.search;

import java.io.File;

import video.adpter.ItemAdapter;
import video.main.*;
import video.module.GoodAdapter;
import video.module.Searcher;
import video.protocol.Good;
import video.search.R.layout;
import video.search.page.PageEvent;
import video.search.page.ShowPageNumber;
import video.values.Const;
import video.values.HanderMessage;
import video.values.SearchType;
import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 * 
 */
public class ResultActivity extends Activity {
	private static final String HTTP_AD = "http://m.tmall.com/channel/act/new/chaoliutxu.html?sid=9896c162f322c7c6&v=0&spm=41.135707.248138.4&sprefer=sygd09";
	private TextView resultCount;
	private final int REQUEST_MapPos = 1212;

	private static final String VIDEO_URL = "http://www.coolsou.com/Videos/3gp/";
	private static final int NOPROGRESSS_DIALOG = 0;
	private static final int PROGRESS_DIALOG = 1;
	private static final int ITEM_COUNT_PER_PAGE = 10;
	private static ListView lvResult = null;
	private static Gallery glyResult = null;

	PopupWindow popup=null;
	PopupWindow popContextMenu =null;
	
	
	private Thread searchThread = null;
	public static int position = 0;
	// ��ʾ�������Ի���
	private ProgressDialog progressDialog = null;

	// �����Ʒ
	public Good[] content = null;
	private int pageCount;
	static String featureString = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		LinearLayout llresultCount = (LinearLayout) findViewById(R.id.lltop);
		resultCount = (TextView) llresultCount.findViewById(R.id.tvShower);
		lvResult = (ListView) findViewById(R.id.lvResult);
		glyResult = (Gallery) findViewById(R.id.glyResult);

		ImageView imgAd=(ImageView)findViewById(R.id.imgAd);
		imgAd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommonOperation.startWebBrowser(ResultActivity.this, HTTP_AD);
			}
		});
		
		// AdBanner.create(this, (LinearLayout)findViewById(R.id.llAd));

		Intent intent = getIntent();
		int type = intent.getIntExtra("type", 0);

		// ��������������
		if (type == 0) {
			ResultActivity.this.finish();
			return;
		}
		String alpha = intent.getStringExtra("alpha");
		String samedegree = intent.getStringExtra("samedegree");
		String kind = intent.getStringExtra("kind");
		switch (type) {
		case SearchType.KEYWORDS:
			String keyWords = intent.getStringExtra("KeyWords");
			// ��ʼ�����߳�
			if (keyWords != null) {
				if (keyWords != "") {
					startSearchByKeyWords(keyWords, kind);
					return;
				}
			}
			break;
		case SearchType.PHOTO:
			ImageView imgPreview=(ImageView)findViewById(R.id.imgPreview);
			 byte[] photo = intent.getByteArrayExtra("photo");
			 imgPreview.setVisibility(View.VISIBLE);
			imgPreview.setImageBitmap(BitmapFactory.decodeByteArray(photo	, 0, photo.length));
			startSearchByPhoto(intent.getByteArrayExtra("photo"), alpha,
					samedegree, kind);
			break;
		case SearchType.VIDEO:
			int cutNum = Integer.parseInt(intent.getStringExtra("cutNum"));
			startSearchByVideo((Uri) intent.getParcelableExtra("videofile"),
					cutNum, alpha, samedegree, kind);
			break;
		}
	}

	public void setContent(Good[] a) {
		this.content = a;
	}

	private void showResult() {
		pageCount = calculatePageCount(content.length);

		lvResult.setOnScrollListener(new PageEvent(new ShowPageNumber(
				(TextView) findViewById(R.id.tvPageNumber), pageCount),
				ITEM_COUNT_PER_PAGE));
		GoodAdapter goodAdapter = new GoodAdapter(ResultActivity.this, content,
				R.layout.itemview);
		lvResult.setAdapter(goodAdapter);

		ItemAdapter itemAdapter = new ItemAdapter(ResultActivity.this, content,
				310, 310, 0);

		glyResult = (Gallery) findViewById(R.id.glyResult);
		glyResult.setAdapter(itemAdapter);
		glyResult.setOnItemClickListener(new itemClicListener());

		// relativeLayout.setOnLongClickListener(new View.OnLongClickListener()
		// {

		// @Override
		// public boolean onLongClick(View v) {
		// int i= (Integer) v.getTag();
		// Log.e("����", String.valueOf(i));
		// ResultActivity.position= (Integer) v.getTag();
		// return false;
		// }
		// });

		lvResult.setOnItemClickListener(new itemClicListener());

	}

	class itemClicListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
				long arg3) {
			// Intent browserIntent = new Intent(Intent.ACTION_VIEW,
			// Uri.parse(content[arg2].getUrl()));
			// startActivity(browserIntent);
			if(popup!=null)
				return;
			View root = ResultActivity.this.getLayoutInflater().inflate(
					R.layout.glyitemview, null);
			popup = new PopupWindow(root,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			initeItemView(content[arg2], root);
			// popup.showAtLocation( root, Gravity.CENTER,
			// (getWindowManager().getDefaultDisplay().getWidth()-300)/2,
			// (getWindowManager().getDefaultDisplay().getHeight()-400)/2);
			popup.setAnimationStyle(android.R.style.Animation_Translucent);

			popup.showAtLocation(root, Gravity.CENTER, 0, 0);

			root.findViewById(R.id.btnCancel).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							popup.dismiss();
							popup=null;
						}
					});
			
			root.findViewById(R.id.imgItemPic).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(popContextMenu==null)
						loadContextMenu(content[arg2]);
				}
			});
			
			root.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(popContextMenu!=null && popContextMenu.isShowing())
					{
						popContextMenu.dismiss();
						popContextMenu=null;
					}
				}
			});
		}
	}

	private void loadContextMenu(final Good good) {
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
								"����Ʒ��λ����Ϣ����ʱ�޷���λ��");
						break;
					}
					Intent mapintent = new Intent(ResultActivity.this,
							MapPosActivity.class);
					mapintent.putExtra("pos", good.getExactPosition());
					startActivityForResult(mapintent, REQUEST_MapPos);
					break;
				case R.id.btnDetail:
					CommonOperation.startWebBrowser(ResultActivity.this, good.getUrl());
					break;
				case R.id.btnAgain:
					File file = new File(Const.APP_DIR_TEMP
							+ String.valueOf(good.getId()) + ".jpg");
					if (file.exists()) {
						Intent intent2 = new Intent(ResultActivity.this,
								FixPhotoActivity.class);
						// intent.putExtra("bitmap", photo);
						intent2.putExtra("bitmap",
										Const.APP_DIR_TEMP
												+ String.valueOf(good.getId())
												+ ".jpg");
						startActivity(intent2);
						finish();
					} else {
						{
							Toast.makeText(ResultActivity.this,
									"����Ʒ��������ͼ���޷�������", Toast.LENGTH_LONG).show();
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

	/**
	 * ��ʼ���������ƷͼƬ����
	 * 
	 * @param good
	 *            ��Ʒ
	 * @param root
	 *            ����ͼ
	 */
	private void initeItemView(Good good, View root) {

		ImageView imageView = (ImageView) root.findViewById(R.id.imgItemPic);
		String filePath = Const.APP_DIR_TEMP + String.valueOf(good.getId())
				+ ".jpg";
		File file = new File(filePath);
		if ((file.exists())) {
			imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
		} else {
			CommonOperation.loadImageOnLoadingThread(imageView,
					good.getFullUrl(), file);
		}

		if (!good.isRetire()) {
			ImageView imgRetire = (ImageView) root.findViewById(R.id.imgRetire);
			imgRetire.setVisibility(View.INVISIBLE);
		}
		if (!good.isDescribe()) {
			ImageView imgDescrib = (ImageView) root
					.findViewById(R.id.imgDescrib);
			imgDescrib.setVisibility(View.INVISIBLE);
		}
		if (good.getExactPosition().isEmpty()) {
			ImageView imgPoint = (ImageView) root.findViewById(R.id.imgPoint);
			imgPoint.setVisibility(View.INVISIBLE);
		}
		// ���������ı�
		TextView tvName = (TextView) root.findViewById(R.id.tvName);
		tvName.setText(good.getName());
		// ����λ��
		TextView tvPosition = (TextView) root.findViewById(R.id.tvPosition);
		tvPosition.setText(good.getPosition());

		// ���ü۸�
		TextView tvPrice = (TextView) root.findViewById(R.id.tvPrice);
		tvPrice.setText(String.valueOf(good.getPrice()));
	}

	private int calculatePageCount(int itemCount) {
		if (itemCount % ITEM_COUNT_PER_PAGE == 0) {
			return itemCount / ITEM_COUNT_PER_PAGE;
		} else {
			return itemCount / ITEM_COUNT_PER_PAGE + 1;
		}
	}

	private void showResultCount() {
		resultCount.setText("�����������Ʒ " + String.valueOf(content.length) + " ��");
	}

	// ��ȡ��Ϣ�ľ��
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// ��ʾ���
			String hint = "";
			switch (msg.what) {
			// �����޽��
			case HanderMessage.NORES:
				hint = "������˼��û���Ŷ���ס�";
				Toast.makeText(ResultActivity.this, hint, Toast.LENGTH_SHORT)
						.show();
				finish();
				return;
				// ����������ʾ������Ϣ
			case HanderMessage.ERROR:
				hint = "�ף�ò�Ƴ��������ˡ���������԰ɣ�Ҳ������ˡ�";
				Toast.makeText(ResultActivity.this, hint, Toast.LENGTH_LONG)
						.show();
				ResultActivity.this.finish();
				return;
			case HanderMessage.UPDATE:
				progressDialog.setMessage("�ѳɹ�����" + " "
						+ msg.getData().getInt("progress") + " %");
				progressDialog.setProgress(msg.getData().getInt("progress"));
				return;
				// ��ʼ����
			case HanderMessage.STARTANY:
				progressDialog.setMessage("��ʼ������Ƶ��");
				return;
				// �������
			case HanderMessage.FINISHANY:
				progressDialog.setMessage("������ɣ����ڼ�����");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				return;
				// ��ʾ���������
			case HanderMessage.SHOWRES:
				hint = "�������," + "�����������Ʒ " + String.valueOf(content.length)
						+ " ����";
				findViewById(R.id.tvPageNumber).setVisibility(View.INVISIBLE);
				showResultCount();
				showResult();
				break;
			default:
				return;
			}
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if (hint.isEmpty())
				return;
			Toast.makeText(ResultActivity.this, hint, Toast.LENGTH_SHORT)
					.show();
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(popContextMenu!=null && popContextMenu.isShowing())
		{
			popContextMenu.dismiss();
			popContextMenu=null;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// ��ʼ��������
		progressDialog = new ProgressDialog(this);
/*		progressDialog.setTitle("������ʾ");*/
		progressDialog.setMessage("�ף����Եȣ����Ͼͺá���");
/*	progressDialog.setCancelable(true);*/
		switch (id) {
		case NOPROGRESSS_DIALOG:
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			break;
		case PROGRESS_DIALOG:
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		default:
			break;
		}
		progressDialog.setIndeterminate(false);
/*		progressDialog.setButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (searchThread != null) {
					searchThread.stop();
					searchThread.interrupt();
				}
				ResultActivity.this.finish();
			}
		});*/
		return progressDialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog, args);
	}

	// �����߳�
	private void startSearchByKeyWords(final String keyWords, final String kind) {
		// ���δ����ؼ���
		if (keyWords.trim() == "") {
			Toast.makeText(ResultActivity.this, "�ؼ��ֲ���Ϊ�ա�", Toast.LENGTH_SHORT)
					.show();
			ResultActivity.this.finish();
			return;
		}

		searchThread = new Thread() {
			@Override
			public void run() {
				new Searcher(mHandler, ResultActivity.this).SearchByKeyWords(
						keyWords, kind);
				super.run();
			}
		};
		showDialog(NOPROGRESSS_DIALOG);
		searchThread.start();
	}

	private void startSearchByPhoto(final byte[] photo, final String alpha,
			final String samedegree, final String kind) {
		showDialog(NOPROGRESSS_DIALOG);

		searchThread = new Thread() {
			@Override
			public void run() {
				new Searcher(mHandler, ResultActivity.this).SearchByFeature(
						FeatureCode.calculateImageFeatureCode(BitmapFactory
								.decodeByteArray(photo, 0, photo.length)),
						alpha, samedegree, kind);
				super.run();
			}
		};
		searchThread.start();
	}

	private void startSearchByVideo(final Uri videoUri, final int cutNum,
			final String alpha, final String samedegree, final String kind) {
		showDialog(PROGRESS_DIALOG);

		searchThread = new Thread() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(HanderMessage.STARTANY);
				String feature = FeatureCode.calculateVideoFeatureCode(
						ResultActivity.this, mHandler, videoUri, cutNum);
				mHandler.sendEmptyMessage(HanderMessage.FINISHANY);
				new Searcher(mHandler, ResultActivity.this).SearchByFeature(
						feature, alpha, samedegree, kind);
				super.run();
			}
		};
		searchThread.start();
	}

	// ����ѡ��˵�
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.resultmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// ���������Ĳ˵�
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.gooditem, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	// ���������Ĳ˵��¼�
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ListView lvListView = (ListView) findViewById(R.id.lvResult);

		GoodAdapter gAdapter = (GoodAdapter) lvListView.getAdapter();
		Good gd = (Good) gAdapter.getItem(position);
		switch (item.getItemId()) {
		case R.id.menuVideo:

			Intent intent = new Intent(ResultActivity.this,
					VideoPlayerActivity.class);

			intent.putExtra("name", gd.getName());
			intent.putExtra("url", ResultActivity.VIDEO_URL + gd.getId()
					+ ".3gp");
			startActivity(intent);
			break;
		case R.id.menuPosGood:
			if (gd.getExactPosition().isEmpty()) {
				CommonOperation.toast(this, "����Ʒ��λ����Ϣ����ʱ�޷���λ��");
				break;
			}
			Intent mapintent = new Intent(ResultActivity.this,
					MapPosActivity.class);
			mapintent.putExtra("pos", gd.getExactPosition());
			startActivityForResult(mapintent, REQUEST_MapPos);
			break;
		case R.id.menuDetail:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gd
					.getUrl()));
			startActivity(browserIntent);
			break;
		case R.id.menuAgain:
			File file = new File(Const.APP_DIR_TEMP
					+ String.valueOf(gd.getId()) + ".jpg");
			if (file.exists()) {
				Intent intent2 = new Intent(ResultActivity.this,
						FixPhotoActivity.class);
				// intent.putExtra("bitmap", photo);
				intent2.putExtra("bitmap", CommonOperation
						.bitmapToBytes(BitmapFactory
								.decodeFile(Const.APP_DIR_TEMP
										+ String.valueOf(gd.getId()) + ".jpg")));
				startActivity(intent2);
				finish();
			} else {
				{
					Toast.makeText(ResultActivity.this, "����Ʒ��������ͼ���޷�������",
							Toast.LENGTH_LONG).show();
				}
			}
		default:
			break;
		}
		return false;
	}

	// ����ѡ��˵��¼�
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuLogin:
			break;

		case R.id.menuexit:
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
