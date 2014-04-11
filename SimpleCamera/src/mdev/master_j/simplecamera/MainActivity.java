package mdev.master_j.simplecamera;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	OnClickListener onTakePhotoButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO take photo
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button takePhotoButton = (Button) findViewById(R.id.button_take_photo);
		takePhotoButton.setOnClickListener(onTakePhotoButtonClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_save:
			// TODO save photo
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
