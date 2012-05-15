package video.search;

import video.main.Player;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class VideoPlayerActivity extends Activity {
	private SurfaceView surfaceView;
	private Button btnPause, btnPlayUrl, btnStop;
	private SeekBar skbProgress;
	private Player player;
	private String url = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoplayer);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView1);
		setTitle(getIntent().getStringExtra("name"));
		url = getIntent().getStringExtra("url");

		surfaceView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LinearLayout llPlayBar = (LinearLayout) findViewById(R.id.playbar);
				if (llPlayBar.getVisibility() == View.INVISIBLE)
					llPlayBar.setVisibility(View.VISIBLE);
				else {
					llPlayBar.setVisibility(View.INVISIBLE);
				}
			}
		});

		btnPlayUrl = (Button) this.findViewById(R.id.btnPlayUrl);
		btnPlayUrl.setOnClickListener(new ClickEvent());

		btnPause = (Button) this.findViewById(R.id.btnPause);
		btnPause.setOnClickListener(new ClickEvent());

		btnStop = (Button) this.findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new ClickEvent());

		skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
		skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		player = new Player(surfaceView, skbProgress);

	}

	class ClickEvent implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (arg0 == btnPause) {
				player.pause();
			} else if (arg0 == btnPlayUrl) {
				player.playUrl(url);
			} else if (arg0 == btnStop) {
				player.stop();
			}

		}
	}

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// ԭ����(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			this.progress = progress * player.mediaPlayer.getDuration()
					/ seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()�Ĳ����������ӰƬʱ������֣���������seekBar.getMax()��Ե�����
			player.mediaPlayer.seekTo(progress);
		}
	}

}