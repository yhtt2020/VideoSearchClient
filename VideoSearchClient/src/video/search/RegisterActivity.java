package video.search;

import video.main.CommonOperation;
import video.protocol.Engine;
import video.values.HanderMessage;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	video.search.RegisterActivity.Register.RegisterThread registerThread=null;
	ProgressDialog pd = null;
	Handler handler = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		listenRegisterButton();
		Button btnCancel=(Button)findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RegisterActivity.this.finish();
			}
		});
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				pd.dismiss();
				switch (msg.what) {
				case HanderMessage.ERROR:
					CommonOperation.toast(RegisterActivity.this, "×¢²áÊ§°Ü£¬Çë¼ì²éÍøÂç¡£");
					break;
				case HanderMessage.OK:
					CommonOperation.toast(RegisterActivity.this, "×¢²á³É¹¦¡£");
					RegisterActivity.this.finish();
					break;

				}

			}
		};
	}

	private void listenRegisterButton() {
		Button b = (Button) findViewById(R.id.register);
		b.setOnClickListener(new Register());
	}

	private class Register implements OnClickListener {
		@Override
		public void onClick(View v) {
			String userName = getText(R.id.userName);
			String password = getText(R.id.password);
			String sex = getSex();
			String email = getText(R.id.email);
			if (userName.isEmpty() || password.isEmpty() || email.isEmpty()) {
				return;
			}
			pd = new ProgressDialog(RegisterActivity.this);
			pd.setMessage("ÕýÔÚ×¢²á¡£");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			registerThread=new RegisterThread(userName,password,sex,email);
			registerThread.start();
		}

		private String getSex() {
			RadioGroup a = (RadioGroup) findViewById(R.id.sex);
			if (a.getCheckedRadioButtonId() == R.id.male) {
				return "true";
			} else {
				return "false";
			}
		}
		private class RegisterThread extends Thread
		{
	
		private String userName;
		private String password;
		private String sex;
		private String email;

		public RegisterThread(String userName, String password, String sex,
					String email) {
				this.userName=userName;
				this.password=password;
				this.sex=sex;
				this.email=email;
			}

		@Override
		public void run() {
			String result="0";
			try {
				result = new Engine().Register(userName, password, sex, email);
			} catch (Exception e) {
				handler.sendEmptyMessage(HanderMessage.ERROR);
			}
			if (Integer.parseInt(result) != 0) {
				Global.userid = Integer.parseInt(result);
				handler.sendEmptyMessage(HanderMessage.OK);
				return;
			}
			else
			{
				handler.sendEmptyMessage(HanderMessage.ERROR);
			}
		}
		
		
		}

		private String getText(int id) {
			return ((TextView) findViewById(id)).getText().toString();
		}
	}
}