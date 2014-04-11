package mdev.master_j.simplecamera;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	// TODO generalize message toasting
	private static final int IMAGE_TAKEN_REQUEST_CODE = 1;

	OnClickListener onTakePhotoButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			File albumFile = getAlbumDirectory();
			if (!albumFile.exists() && !albumFile.mkdirs()) {
				Toast.makeText(MainActivity.this, "Cannot create " + albumFile.getAbsolutePath(), Toast.LENGTH_SHORT)
						.show();
				return;
			}

			String pictureName = getString(R.string.name_picture);
			String picturePath = albumFile.getAbsolutePath() + "/" + pictureName;
			File pictureFile = new File(picturePath);
			Uri pictureUri = Uri.fromFile(pictureFile);

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (intent.resolveActivity(getPackageManager()) == null) {
				Toast.makeText(MainActivity.this, "Cannot find default camera app", Toast.LENGTH_SHORT).show();
				return;
			}
			startActivityForResult(intent, IMAGE_TAKEN_REQUEST_CODE);
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == IMAGE_TAKEN_REQUEST_CODE) {
				Bundle extras = data.getExtras();
				Bitmap bitmap = (Bitmap) extras.get("data");

				ImageView photoImageView = (ImageView) findViewById(R.id.image_view_photo);
				photoImageView.setImageBitmap(bitmap);
			}
		} else if (resultCode == RESULT_CANCELED)
			Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "Something wrong with your camera", Toast.LENGTH_SHORT).show();
	}

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

	private File getAlbumDirectory() {
		String albumName = getString(R.string.name_album);
		return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
	}

}
