package video.search;

import video.main.CommonOperation;
import video.protocol.Engine;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		listenRegisterButton();
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
			if (register(userName, password, sex, email)) {
				CommonOperation.toast(RegisterActivity.this, "¹§Ï²£¬×¢²á³É¹¦£¡");
				RegisterActivity.this.finish();
			} else {
				CommonOperation.toast(RegisterActivity.this,"²»ºÃÒâË¼£¬×¢²áÊ§°Ü£¬Çë¼ì²éÍøÂç×´¿ö£¬ÉÔºóÔÙ³¢ÊÔ×¢²á£¡");
			}
		}

		private String getSex() {
			RadioGroup a = (RadioGroup) findViewById(R.id.sex);
			if (a.getCheckedRadioButtonId() == R.id.male) {
				return "true";
			} else {
				return "false";
			}
		}

		private boolean register(String userName, String password, String sex,
				String email) {

			String r = "0";
			try {
				r = new Engine().Register(userName, password, sex, email);
			} catch (Exception e) {

			}
			if (Integer.parseInt(r) != 0) {
				Global.userid = Integer.parseInt(r);
				return true;
			} else {
				return false;
			}
		}

		private String getText(int id) {
			return ((TextView) findViewById(id)).getText().toString();
		}
	}
}