package mdev.master_j.simplecamera;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity {
	// TODO generalize message toasting
	private static final int IMAGE_TAKEN_REQUEST_CODE = 1;

	private static final String KEY_BITMAP_PHOTO = "mdev.master_j.simplecamera.CameraActivity.KEY_BITMAP_PHOTO";

	private Bitmap photoBitmap;

	OnClickListener onTakePhotoButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			File albumFile = getAlbumDirectory();
			if (!albumFile.exists() && !albumFile.mkdirs()) {
				Toast.makeText(CameraActivity.this, "Cannot create " + albumFile.getAbsolutePath(), Toast.LENGTH_SHORT)
						.show();
				return;
			}

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (intent.resolveActivity(getPackageManager()) == null) {
				Toast.makeText(CameraActivity.this, "Cannot find default camera app", Toast.LENGTH_SHORT).show();
				return;
			}
			startActivityForResult(intent, IMAGE_TAKEN_REQUEST_CODE);
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == IMAGE_TAKEN_REQUEST_CODE) {
				Bundle extras = data.getExtras();
				photoBitmap = (Bitmap) extras.get("data");

				updateUI();
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

		if (savedInstanceState != null) {
			Parcelable parcelable = savedInstanceState.getParcelable(KEY_BITMAP_PHOTO);
			if (parcelable != null)
				photoBitmap = (Bitmap) parcelable;
			updateUI();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(KEY_BITMAP_PHOTO, photoBitmap);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem item = menu.findItem(R.id.item_save);
		// MenuItem item = (MenuItem) findViewById(R.id.item_save);
		item.setEnabled(photoBitmap != null);
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

	private void updateUI() {
		ImageView photoImageView = (ImageView) findViewById(R.id.image_view_photo);
		photoImageView.setImageBitmap(photoBitmap);
	}

}
