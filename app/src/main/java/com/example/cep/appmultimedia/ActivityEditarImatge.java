package com.example.cep.appmultimedia;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class ActivityEditarImatge extends AppCompatActivity
{

	private ImageView imatgeRetallada;
	private Uri uriImatgeRetallada = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar_imatge);

		//En tot aquest fitxer he reutilitzat trossos de codi d'aquest repositori de GitHub, que hi ha un projecte.
		//He hagut de modificar a ma i cridar funcions de manera diferent a com estan a l'exemple, perquè no hi havia manera que funcionés
		//He estat gairebé 5 hores per fer-ho funcionar. Desaconsello remenar el codi.

		//Repositori de GitHub: https://github.com/jdamcd/android-crop
		//Fonts originària: https://medium.com/mindorks/android-top-image-cropper-libraries-3bc4a4f8f2df
		//Llibreria alternativa per si aquesta falla: https://github.com/ArthurHub/Android-Image-Cropper
		//https://mindorks.com/android/store/Image-Croppers/arthurhub/android-image-cropper


		imatgeRetallada = (ImageView) findViewById(R.id.imatgeRetallada);

		//Rebo l'intent i n'extrec la Uri:
		Intent intent = getIntent();
		Uri uriRebuda = Uri.parse(intent.getStringExtra("uriEditarFoto"));

		//Crop.of(uriRebuda, uriImatgeRetallada).asSquare().start(this);

		//imatgeRetallada.setImageDrawable(null);
		//Crop.pickImage(this);
		beginCrop(uriRebuda);

		//Per si vull amagar la status bar:
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Amb aquest mètode col·loco el botó de Guardar a la Action Bar. El contingut del botó es troba a la carpeta menu(xml)
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Amb aquest mètode faig que el botó Guardar respongui al clic

		if (item.getItemId() == R.id.action_select) {
			//Amb aquestes dues línies guardo la imatge retallada:
			//Font: http://www.geeks.gallery/saving-image/
			Bitmap thumbnail = (BitmapFactory.decodeFile(uriImatgeRetallada.getPath()));
			saveImage(thumbnail);

			//Tanco la activity actual i torno al menú principal
			//Creo un nou intent, esborro la resta d'activitats que hi ha obertes (pq al donar al botó enrenre
			//no hi torni, i després inicio la nova activity:
			/*Intent activityMenuPrincipal = new Intent(getApplicationContext(), MainActivity.class);
			activityMenuPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(activityMenuPrincipal);*/

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
			beginCrop(result.getData());
		} else if (requestCode == Crop.REQUEST_CROP) {
			handleCrop(resultCode, result);
		}
	}

	private void beginCrop(Uri source) {
		Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));

		//La següent línia és la que obre la imatge amb un requadre per retallar
		//El .start és com un intent, que és recollit per l'onActivityResult, que allà (amb el requestCode que li he posat
		//cridarà al mètode handleCrop()
		Crop.of(source, destination).asSquare().start(this, Crop.REQUEST_CROP);

	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == RESULT_OK) {

			uriImatgeRetallada = Crop.getOutput(result);
			//imatgeRetallada.setImageURI(Crop.getOutput(result));
			imatgeRetallada.setImageURI(uriImatgeRetallada);

		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
		}
	}


	private void saveImage(Bitmap finalBitmap) {
		String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
		System.out.println(root +" Root value in saveImage Function");
		File myDir = new File(root + "/Retalls_AppMultimedia");

		if (!myDir.exists()) {
			myDir.mkdirs();
		}

		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String iname = "Image-" + n + ".jpg";
		File file = new File(myDir, iname);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Tell the media scanner about the new file so that it is
		// immediately available to the user.
		MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						Log.i("ExternalStorage", "Scanned " + path + ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
					}
				});

		String Image_path = Environment.getExternalStorageDirectory()+ "/Pictures/folder_name/"+iname;

		File[] files = myDir.listFiles();
		int numberOfImages=files.length;
		System.out.println("Total images in Folder "+numberOfImages);

		Toast.makeText(getApplicationContext(), "Imatge guardada!", Toast.LENGTH_LONG).show();
	}
}
