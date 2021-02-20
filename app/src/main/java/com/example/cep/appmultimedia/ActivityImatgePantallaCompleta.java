package com.example.cep.appmultimedia;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class ActivityImatgePantallaCompleta extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imatge_pantalla_completa);

		//Amb aquesta línia amago la status bar (la barra que diu la bateria, l'hora)
		//Per amagar la Action Bar (que conté el nom de l'aplicació, ho faig amb un style, i posant-lo al Manifest.
		//Font: http://www.albertgao.xyz/2018/04/17/how-to-hide-actionbar-in-android/
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


		//Rebo l'intent i n'extrec la uri:
		Intent intent = getIntent();
		Uri uriFitxer = Uri.parse(intent.getStringExtra("uri"));

		//TextView provaText = findViewById(R.id.provaText);
		//provaText.setText(rutaFitxer + " - " + nomFitxer);

		//Adjudico la uri a la imatge:
		PhotoView fotoPantallaCompleta = findViewById(R.id.imatgePantallaCompleta);
		fotoPantallaCompleta.setImageURI(uriFitxer);
	}
}
