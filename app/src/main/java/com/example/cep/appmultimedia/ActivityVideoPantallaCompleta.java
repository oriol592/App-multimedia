package com.example.cep.appmultimedia;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class ActivityVideoPantallaCompleta extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_pantalla_completa);

		//Amago la status bar (la info completa esta al fitxer ActivityImatgePantallaCompleta.java
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//Rebo l'intent:
		Intent intent = getIntent();
		Uri uriVideo = Uri.parse(intent.getStringExtra("uriVideo"));

		//Adjudico la uri al vídeo i l'engego
		VideoView videoView = findViewById(R.id.videoPantallaCompleta);
		videoView.setVideoURI(uriVideo);

		//Faig que apareguin els controls per engegar, pausar, tirar endavant i enrere:
		//Font: https://www.techotopia.com/index.php/An_Android_Studio_VideoView_and_MediaController_Tutorial#Introducing_the_Android_VideoView_Class
		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);

		//Faig que el vídeo s'engege automàticament:
		videoView.start();
	}
}
