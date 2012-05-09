package video.search;

import video.adpter.PusherAdapter;
import video.module.PusherView;
import video.values.Global;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;

/**
 * @author CYX
 *�û�������
 */
public class UserCenterActivity extends Activity {
	private TextView tvWelcome;
	private TextView tvName;
	private Button btnCancel;
	private Gallery glyPusher;
	private video.module.PusherEntity[] PusherEntity=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercenter);
		tvName=(TextView)findViewById(R.id.tvName);
		tvWelcome=(TextView)findViewById(R.id.tvWelcome);
		btnCancel=(Button)findViewById(R.id.btnCancel);
		glyPusher=(Gallery)findViewById(R.id.glyPusher);
		
		tvWelcome.setText("��ӭ�������װ��� "+Global.userName);
		tvName.setText(Global.userName);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserCenterActivity.this.finish();
			}
		});
		PusherEntity=PusherView.createSamples();
		PusherAdapter pa=new PusherAdapter(this, PusherEntity, 170, 170, 0);
		glyPusher.setAdapter(pa);
		
		glyPusher.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PusherEntity[position].weblink));
				startActivity(browserIntent);
			}
		});
	}

	
}
