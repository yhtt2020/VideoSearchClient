package video.search;

import video.main.CommonOperation;
import video.protocol.Engine;
import video.values.Const;
import video.values.Global;
import video.values.HanderMessage;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	ProgressDialog pd = null;
	Handler handler = null;
	video.search.LoginActivity.LoginListener.LoginThread loginThread = null;
	SharedPreferences sp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		CheckBox cbSavePassword=(CheckBox)findViewById(R.id.cbSavePassword);
		CheckBox cbAutoLogin=(CheckBox)findViewById(R.id.cbAutoLogin);
		//获取设置
		sp=PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edt=sp.edit();
		//取得属性并赋值
		cbSavePassword.setChecked(sp.getBoolean("savePassword", false));
		cbAutoLogin.setChecked(sp.getBoolean("autoLogin", false));
		if(cbSavePassword.isChecked())
		{
			EditText edtUserName=(EditText)findViewById(R.id.userName);
			EditText edtPassword=(EditText)findViewById(R.id.password);
			edtUserName.setText(sp.getString(Const.SETTING_USERNAME, ""));
			edtPassword.setText(sp.getString(Const.SETTING_PASSWORD, ""));
		}
		
		cbSavePassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					edt.putBoolean(Const.SETTING_SAVEPASSWORD, isChecked);
					edt.commit();
			}
		});
		
		cbAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				edt.putBoolean(Const.SETTING_AUTOLOGIN, isChecked);
				edt.commit();
			}
		});
		
		listenLoginButton();
		listenRegisterButton();
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				pd.dismiss();
				switch (msg.what) {
				case HanderMessage.ERROR:
					CommonOperation.toast(LoginActivity.this, "登陆失败。用户名或密码错误。");
					break;
				case HanderMessage.OK:
					CommonOperation.toast(LoginActivity.this, "登陆成功。");
					Intent intent=new Intent(LoginActivity.this,UserCenterActivity.class);
					startActivity(intent);
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
			LoginActivity.this.finish();
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
			pd.setMessage("正在登陆。");
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
					Global.userid = result;
					Global.userName=userName;
					Editor edt=sp.edit();
					if(sp.getBoolean(Const.SETTING_SAVEPASSWORD, false)==true)
					{
						edt.putString(Const.SETTING_USERNAME, userName);
						edt.putString(Const.SETTING_PASSWORD, password);
						
					}
					if(sp.getBoolean(Const.SETTING_AUTOLOGIN, false)==true)
					{
						edt.putString(Const.SETTING_USERID, result);
					}
					edt.commit();
					handler.sendEmptyMessage(HanderMessage.OK);
					return;
				}
				handler.sendEmptyMessage(HanderMessage.ERROR);
				return;
			}

		}
	}

}