package video.search;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HelpActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.help);
	}

}
