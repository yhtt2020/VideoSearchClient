package video.search;

import video.values.Global;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author CYX
 *用户中心类
 */
public class UserCenterActivity extends Activity {
	private TextView tvWelcome;
	private TextView tvName;
	private Button btnCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercenter);
		tvName=(TextView)findViewById(R.id.tvName);
		tvWelcome=(TextView)findViewById(R.id.tvWelcome);
		btnCancel=(Button)findViewById(R.id.btnCancel);
		tvWelcome.setText("欢迎回来，亲爱的 "+Global.userName);
		tvName.setText(Global.userName);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserCenterActivity.this.finish();
			}
		});
	}

	
}
