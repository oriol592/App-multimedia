package com.example.cep.appmultimedia;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cep.appmultimedia.activity.ActivityMainRetallarVideo;
import com.example.cep.appmultimedia.activity.TrimmerActivity;
import com.example.cep.appmultimedia.videoTrimmer.utils.FileUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{

	public static final int PETICIO_PERMISOS_CAMERA = 1;
	private static final int BOTO_REPRODUIR = 42;
	private static final int BOTO_EDITAR = 43;

	public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
	private static final String TAG = "TAg";

	//Constants per engegar l'activity que retalla vídeo:
	static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
	static final String VIDEO_TOTAL_DURATION = "VIDEO_TOTAL_DURATION";


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		//PERMISOS
		if (Build.VERSION.SDK_INT >= 23) {

			//Demano múltiples permisos
			// Font: https://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions
			//https://stackoverflow.com/questions/33666071/android-marshmallow-request-permission
			if(checkAndRequestPermissions()) {
				// carry on the normal flow, as the case of  permissions  granted.
				codiPrincipalExecutar();



			} else {
				codiPrincipalExecutar();
			}

			//Comprovo que l'usuari no hagi concedit ja els permisos:
			/*
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

				//Si no els havia concedit, els hi demano:
				ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, PETICIO_PERMISOS_CAMERA);

				//Un cop demanats els permisos, executo el codi
				codiPrincipalExecutar();

			} else {
				//Si ja havia demanat els permisos (essent la api >= 23), executo el codi
				codiPrincipalExecutar();
			}*/
		} else {
			//Aquí aniria el codi a executar si la api és inferior a la 23:
			codiPrincipalExecutar();
		}


	}


	public void codiPrincipalExecutar() {

		//Creo aquesta funció per evitar repetir 3 vegades el mateix codi en l'if-else-else dels permisos
		//Importo a Java els elements de l'xml:
		ImageButton botoCamera = findViewById(R.id.botoCamera);
		ImageButton botoReproduir = findViewById(R.id.botoReproduir);
		ImageButton botoEditar = findViewById(R.id.botoEditar);
		ImageButton botoEnregistrarAudio = findViewById(R.id.botoEnregistrarAudio);


		//Clic al botó de la càmera:
		botoCamera.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				//Engego la càmera, comprovant abans si el dispostitiu té càmara. La imatge es guarda al directori per defecte
				// Font: https://stackoverflow.com/questions/13977245/android-open-camera-from-button
                //Font: https://www.mkyong.com/android/android-how-to-check-if-device-has-camera/
				if (MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
					Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
					startActivity(intent);
				}
			}
		});



		//Clic al botó de play (reproduir):
		botoReproduir.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				//Obro la galeria del mòbil.
				// Font: https://stackoverflow.com/questions/16928727/open-gallery-app-from-android-intent/23821227
				/*Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setType("image/*");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);*/

				//Aquest tros de codi serveix per obrir un browser
				//Font: https://developer.android.com/guide/topics/providers/document-provider?hl=es-419
				// ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

				// Filter to only show results that can be "opened", such as a file (as opposed to a list of contacts or timezones)
				intent.addCategory(Intent.CATEGORY_OPENABLE);

				// Filter to show only images, using the image MIME data type.
				// If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
				// To search for all documents available via installed storage providers,
				// it would be "*/*".
				intent.setType("*/*");

				startActivityForResult(intent, BOTO_REPRODUIR);
				//El resultat de l'intent s'executa al mètode de sota, l'onActivityResult():
			}
		});



		//Clic al botó d'editar:
		botoEditar.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				//Faig el mateix que al botó de play (reproduir). La info detallada es troba allà
				//Obro un browser on es veuen tots els fitxers:
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("*/*");

				//La resposta de l'activity que s'acaba d'engegar, també es produeix al mètode onActivityResult:
				startActivityForResult(intent, BOTO_EDITAR);

			}
		});


		//Quan l'usuari cliqui el botó del micròfon, s'obrirà una nova activity:
		botoEnregistrarAudio.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, EnregistrarAudio.class);
				startActivity(intent);
			}
		});

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

		//Aquesta funció s'executa per rebre el resultat de l'startActivityForResult. L'intent, no retorna el fitxer obert,
		//sinó que retorna la uri del fitxer

		// The ACTION_OPEN_DOCUMENT intent was sent with the request code
		// READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
		// response to some other intent, and the code below shouldn't run at all.


		if ((requestCode == BOTO_REPRODUIR || requestCode == BOTO_EDITAR) && resultCode == Activity.RESULT_OK) {
			// The document selected by the user won't be returned in the intent.
			// Instead, a URI to that document will be contained in the return intent provided to this method as a parameter.
			// Pull that URI using resultData.getData().
			Uri uri = null;
			if (resultData != null) {
				uri = resultData.getData();

				//Faig que em torni el nom del fitxer:
				String nomFitxerSeleccionat = getFileName(uri);

				//Amb això obtinc la ruta del fitxer
				//File fitxerObert = new File(nomFitxerSeleccionat);
				//String rutaFitxer = fitxerObert.getAbsolutePath();

				//Toast.makeText(getApplicationContext(), nomFitxer, Toast.LENGTH_LONG).show();

				//En aquest if-else gestiono la resposta depenent de si és una imatge o un vídeo, i de si el botó clicat és
				//el de reproduir o el de editar. Així evito repetir codi:
				if ((nomFitxerSeleccionat.endsWith(".png") || nomFitxerSeleccionat.endsWith(".jpg") ||
						nomFitxerSeleccionat.endsWith(".jpeg")) && requestCode == BOTO_REPRODUIR) {
					//https://stackoverflow.com/questions/47482468/android-image-full-screen-viewer?answertab=votes#tab-top

					//Creo un nou intent, i li envio la uri. Font: https://stackoverflow.com/questions/8017374/how-to-pass-a-uri-to-an-intent
					Intent intent = new Intent(MainActivity.this, ActivityImatgePantallaCompleta.class);
					intent.putExtra("uri", uri.toString());
					startActivity(intent);

				} else if (nomFitxerSeleccionat.endsWith(".mp4") && requestCode == BOTO_REPRODUIR){
					//Obro una nova activity per reproduir el vídeo
					Intent intent = new Intent(MainActivity.this, ActivityVideoPantallaCompleta.class);
					intent.putExtra("uriVideo", uri.toString());
					startActivity(intent);
				}




				//Si he apretat el botó d'editar:
				if ((nomFitxerSeleccionat.endsWith(".png") || nomFitxerSeleccionat.endsWith(".jpg") ||
						nomFitxerSeleccionat.endsWith(".jpeg")) && requestCode == BOTO_EDITAR) {
					//https://stackoverflow.com/questions/47482468/android-image-full-screen-viewer?answertab=votes#tab-top

					//Creo un nou intent i li envio la uri:
					Intent intent = new Intent(MainActivity.this, ActivityEditarImatge.class);
					intent.putExtra("uriEditarFoto", uri.toString());
					startActivity(intent);
					//Toast.makeText(this, uriImatgeRetallada.toString(), Toast.LENGTH_LONG).show();


				} else if (nomFitxerSeleccionat.endsWith(".mp4") && requestCode == BOTO_EDITAR){
					//Amb això obtinc la ruta del fitxer
					/*File fitxerObert = new File(nomFitxerSeleccionat);
					String rutaFitxer = fitxerObert.getAbsolutePath();

					//Obro una nova activity per reproduir el vídeo
					Intent intent = new Intent(MainActivity.this, ActivityEditarVideo.class);
					intent.putExtra("uriEditarVideo", uri.toString());
					intent.putExtra("rutaVideo", rutaFitxer);
					startActivity(intent);*/

					/*Intent intent = new Intent(MainActivity.this, ActivityMainRetallarVideo.class);
					startActivity(intent);*/

					//El projecte per retallar vídeo està extret de: https://github.com/HemendraGangwar/VideoTrimmingLikeWhatsapp/
					if (uri != null) {
						startTrimActivity(uri);
					} else {
						Toast.makeText(MainActivity.this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
					}

				}
			}
		}

	}



	private void startTrimActivity(@NonNull Uri uri) {
		Intent intent = new Intent(this, TrimmerActivity.class);
		intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(this, uri));
		intent.putExtra(VIDEO_TOTAL_DURATION, getMediaDuration(uri));
		startActivity(intent);
	}

	private int  getMediaDuration(Uri uriOfFile)  {
		MediaPlayer mp = MediaPlayer.create(this,uriOfFile);
		int duration = mp.getDuration();
		return  duration;
	}


	public String getFileName(Uri uri) {
		//Aquesta funció serveix per obtenir el nom del fitxer passant-li la uri.
		//Font: https://stackoverflow.com/questions/5568874/how-to-extract-the-file-name-from-uri-returned-from-intent-action-get-content
		String result = null;
		if (uri.getScheme().equals("content")) {
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				}
			} finally {
				cursor.close();
			}
		}

		//En cas que no s'hagi pogut obtenir el nom del fitxer amb l'if anterior, s'obtindà buscant la posició
		//de la última barra de la ruta i després fent un substring fins al final:
		if (result == null) {
			result = uri.getPath();
			int cut = result.lastIndexOf('/');
			if (cut != -1) {
				result = result.substring(cut + 1);
			}
		}
		return result;
	}



	private  boolean checkAndRequestPermissions() {
		int permissionSendMessage = ContextCompat.checkSelfPermission(this,
				Manifest.permission.CAMERA);
		int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		List<String> listPermissionsNeeded = new ArrayList<>();
		if (locationPermission != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
		if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.CAMERA);
		}
		if (!listPermissionsNeeded.isEmpty()) {
			ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
			return false;
		}
		return true;
	}


/*
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		Log.d(TAG, "Permission callback called-------");
		switch (requestCode) {
			case REQUEST_ID_MULTIPLE_PERMISSIONS: {

				Map<String, Integer> perms = new HashMap<>();
				// Initialize the map with both permissions
				perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
				// Fill with actual results from user
				if (grantResults.length > 0) {
					for (int i = 0; i < permissions.length; i++)
						perms.put(permissions[i], grantResults[i]);
					// Check for both permissions
					if (perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
							&& perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
						Log.d(TAG, "sms & location services permission granted");
						// process the normal flow
						//else any one or both the permissions are not granted
					} else {
						Log.d(TAG, "Some permissions are not granted ask again ");
						//permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
						//show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
						if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
							showDialogOK("SMS and Location Services Permission required for this app",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											switch (which) {
												case DialogInterface.BUTTON_POSITIVE:
													checkAndRequestPermissions();
													break;
												case DialogInterface.BUTTON_NEGATIVE:
													// proceed with logic by disabling the related features or quit the app.
													break;
											}
										}
									});
						}
						//permission is denied (and never ask again is  checked)
						//shouldShowRequestPermissionRationale will return false
						else {
							Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
									.show();
							//                            //proceed with logic by disabling the related features or quit the app.
						}
					}
				}
			}
		}

	}

	private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(this)
				.setMessage(message)
				.setPositiveButton("OK", okListener)
				.setNegativeButton("Cancel", okListener)
				.create()
				.show();
	}*/
}
