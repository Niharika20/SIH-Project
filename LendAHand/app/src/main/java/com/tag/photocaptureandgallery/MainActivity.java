package com.tag.photocaptureandgallery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.location.*;
import android.widget.TextView;
import android.widget.Toast;
import com.example.takeimage.R;

public class MainActivity extends Activity implements LocationListener{

	private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
	private Button btnSelect;
	private Button btn2Select;
	private ImageView ivImage;
	private String userChoosenTask;
	protected LocationManager locationManager;
	TextView txtLat;
	private Bitmap bmap;
	private double latitude;
	private double longitude;

	@SuppressLint("MissingPermission")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
		btn2Select = (Button) findViewById(R.id.button2);
		Button btn3Select = (Button) findViewById(R.id.button3);
		txtLat = (TextView) findViewById(R.id.textview1);
		btnSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();
			}
		});
		btn2Select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uploadData();
			}
		});
		btn3Select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				VoiceRecord();
			}
		});

		ivImage = (ImageView) findViewById(R.id.ivImage);
		if(GlobalMessages.imagePath != null && GlobalMessages.imagePath != ""){
			ivImage.setImageURI(Uri.parse(GlobalMessages.imagePath));
		}

		try{
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		} catch(SecurityException e){

		}

		//Bundle bundle = getIntent().getExtras();
//		String message = getIntent().getStringExtra("audio_path");
		Log.i("audioPath",GlobalMessages.audioFileName);
	}

	//These all are the function of LocationListener interface which we are overriding
	@Override
	public void onLocationChanged(Location location) {
		//this function gets called when location is updated
		if(location != null){
			txtLat = (TextView) findViewById(R.id.textview1);
			txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
			this.latitude = location.getLatitude();
			this.longitude = location.getLongitude();
			Log.d("location","Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("Latitude","enable");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("Latitude","status");
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if(userChoosenTask.equals("Take Photo"))
						cameraIntent();
					else if(userChoosenTask.equals("Choose from Library"))
						galleryIntent();
				} else {
					//code for deny
				}
				break;
		}
	}

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				boolean result=Utility.checkPermission(MainActivity.this);

				if (items[item].equals("Take Photo")) {
					userChoosenTask ="Take Photo";
					if(result)
						cameraIntent();

				} else if (items[item].equals("Choose from Library")) {
					userChoosenTask ="Choose from Library";
					if(result)
						galleryIntent();

				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	private void galleryIntent() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);//
		startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
	}

	private void cameraIntent() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_CAMERA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_FILE)
				onSelectFromGalleryResult(data);
			else if (requestCode == REQUEST_CAMERA)
				onCaptureImageResult(data);
		}
	}

	public void onCaptureImageResult(Intent data) {
		Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
		this.bmap = thumbnail;
		ivImage.setImageBitmap(thumbnail);
		UploadPhoto();
	}

	@SuppressWarnings("deprecation")
	private void onSelectFromGalleryResult(Intent data) {
		Bitmap bm=getBitmap(data);
		if (bm != null) {
			this.bmap = bm;
			ivImage.setImageBitmap(bm);
		}
		UploadPhoto();
	}

	private Bitmap getBitmap(Intent data){
		Bitmap bm = null;
		if (data != null) {
			try {
				bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
				this.bmap = bm;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bm;
	}
	private void VoiceRecord(){
		Intent inent = new Intent(MainActivity.this,Voice_record.class);

		startActivity(inent);
	}
	private void UploadPhoto()
	{
		if(this.bmap == null){
			return;
		}
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/LendAHand");
		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String fname = "Image-" + n +"-"+this.latitude+"-"+this.longitude +".jpg";
		GlobalMessages.imagePath = root + "/LendAHand/" + fname;
		GlobalMessages.location = this.latitude+":"+this.longitude;
		File file = new File(myDir, fname);
		Log.i("log:", "" + file);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			this.bmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void uploadData(){
		if(GlobalMessages.imagePath == ""){
			Toast.makeText(MainActivity.this, "Please add image", Toast.LENGTH_SHORT).show();
		} else if(GlobalMessages.audioFileName == ""){
			Toast.makeText(MainActivity.this, "Please add audio", Toast.LENGTH_SHORT).show();
		} else {
			Log.d("Image",GlobalMessages.imagePath);
			Log.d("Audio",GlobalMessages.audioFileName);
			GlobalMessages.imagesPaths.add(GlobalMessages.imagePath);
			GlobalMessages.audioPaths.add(GlobalMessages.audioFileName);
			GlobalMessages.locations.add(GlobalMessages.location);
			GlobalMessages.imagePath = "";
			GlobalMessages.audioFileName = "";
			ivImage.setImageResource(android.R.color.transparent);
			Toast.makeText(MainActivity.this, "Data uploaded", Toast.LENGTH_SHORT).show();
		}

	}
}
