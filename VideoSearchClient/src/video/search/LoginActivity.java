package video.search;

import video.main.CommonOperation;
import video.protocol.Engine;
import video.values.HanderMessage;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends Activity {
	ProgressDialog pd = null;
	Handler handler = null;
	video.search.LoginActivity.LoginListener.LoginThread loginThread = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		listenLoginButton();
		listenRegisterButton();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				pd.dismiss();
				switch (msg.what) {
				case HanderMessage.CANCEL:
					break;
				case HanderMessage.ERROR:
					CommonOperation.toast(LoginActivity.this, "µÇÂ½Ê§°Ü¡£");
					break;
				case HanderMessage.OK:
					CommonOperation.toast(LoginActivity.this, "µÇÂ½³É¹¦¡£");
					LoginActivity.this.finish();
					break;

				}

			}
		};

	}

	private void listenLoginButton() {
		Button btnlogin = (Button) findViewById(R.id.login);
		btnlogin.setOnClickListener(new LoginListener());
	}

	private void listenRegisterButton() {
		Button b = (Button) findViewById(R.id.register);
		b.setOnClickListener(new RegisterListener());
	}

	private class RegisterListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(i);
		}
	}

	private class LoginListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String userName = getUserName();
			String password = getPassword();
			if (userName.isEmpty() || password.isEmpty()) {
				return;
			}
			pd = new ProgressDialog(LoginActivity.this);
			pd.setMessage("ÕýÔÚµÇÂ½¡£");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
			loginThread = new LoginThread(userName, password);
			loginThread.start();
		}

		private String getUserName() {
			return getText(R.id.userName);
		}

		private String getPassword() {
			return getText(R.id.password);
		}

		private String getText(int id) {
			TextView v = (TextView) findViewById(id);
			return v.getText().toString();
		}

		private class LoginThread extends Thread {
			private String userName;
			private String password;

			public LoginThread(String userName, String password) {
				this.userName = userName;
				this.password = password;
			}

			@Override
			public void run() {
				String result = "";
				try {
					result = (new Engine()).Login(userName, password);
				} catch (Exception e) {
					handler.sendEmptyMessage(HanderMessage.ERROR);
				}
				if (!result.endsWith("Error")) {
					Global.userid = Integer.parseInt(result);
					handler.sendEmptyMessage(HanderMessage.OK);
					return;
				}
				handler.sendEmptyMessage(HanderMessage.ERROR);
				return;
			}

		}
	}

}