package mdev.master_j.simplecamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
	// TODO think of generalizing message toasting
	private static final int IMAGE_TAKEN_REQUEST_CODE = 1;

	private static final String KEY_BITMAP_PHOTO = "mdev.master_j.simplecamera.CameraActivity.KEY_BITMAP_PHOTO";

	private Bitmap photoBitmap;

	OnClickListener onTakePhotoButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
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
		item.setEnabled(photoBitmap != null);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.item_save)
			new SaveAsyncTask().execute();

		return super.onOptionsItemSelected(item);
	}

	private class SaveAsyncTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			File albumFile = getAlbumDirectory();
			if (!albumFile.exists() && !albumFile.mkdirs())
				return "Cannot create " + albumFile.getAbsolutePath();

			String pictureName = getString(R.string.name_picture);
			File file = new File(albumFile.getAbsolutePath() + "/" + pictureName);
			FileOutputStream outStream = null;
			boolean success = false;
			try {
				outStream = new FileOutputStream(file);
				photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					outStream.close();
					success = true;
				} catch (Throwable t) {
				}
			}

			if (success) {
				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				intent.setData(Uri.fromFile(file));
				sendBroadcast(intent);
				return "Saved";
			} else
				return "File saving error";
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(CameraActivity.this, result, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
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
