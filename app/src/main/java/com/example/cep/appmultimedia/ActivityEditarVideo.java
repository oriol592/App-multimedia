package com.example.cep.appmultimedia;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import life.knowledge4.videotrimmer.utils.FileUtils;

public class ActivityEditarVideo extends AppCompatActivity
{

	static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
	String rutaVideo;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar_video);

		//Rebo l'intent i n'extrec la Uri:
		Intent intent = getIntent();
		Uri uriRebuda = Uri.parse(intent.getStringExtra("uriEditarVideo"));

		rutaVideo = intent.getStringExtra("rutaVideo");

		/*
		//https://stackoverflow.com/questions/21675480/start-the-trim-video-activity-with-an-intent/21752769
		//https://stackoverflow.com/questions/11205299/android-sdk-cut-trim-video-file
		// The Intent action is not yet published as a constant in the Intent class
		// This one is served by the com.android.gallery3d.app.TrimVideo activity
		// which relies on having the Gallery2 app or a compatible derivative installed
		Intent trimVideoIntent = new Intent("com.android.camera.action.TRIM");

		// The key for the extra has been discovered from com.android.gallery3d.app.PhotoPage.KEY_MEDIA_ITEM_PATH
		trimVideoIntent.putExtra("media-item-path", rutaVideo);
		//Toast.makeText(getApplicationContext(), rutaVideo, Toast.LENGTH_LONG).show();
		trimVideoIntent.setData(uriRebuda);

		// Check if the device can handle the Intent
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(trimVideoIntent, 0);
		if (null != list && list.size() > 0) {
			startActivity(trimVideoIntent); // Fires TrimVideo activity into being active
		} */


		//startTrimActivity(uriRebuda);


	}


	/*private void startTrimActivity(@NonNull Uri uri) {
		Intent intent = new Intent(this, TrimmerActivity.class);
		intent.putExtra(EXTRA_VIDEO_PATH, rutaVideo); //FileUtils.getPath(this, uri)
		startActivity(intent);
	}*/




}
