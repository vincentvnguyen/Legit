
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.v2.legit.DisplayMessageActivity;
import com.example.v2.legit.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import javax.naming.Context;
import javax.swing.text.View;

public class MainActivity extends AppCompatActivity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	private NfcAdapter nfcAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PackageManager pm = this.getPackageManager();
		// Check whether NFC is available on device
		if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
			// NFC is not available on the device.
			Toast.makeText(this, "The device does not has NFC hardware.",
					Toast.LENGTH_SHORT).show();
		}
		// Check whether device is running Android 4.1 or higher
		else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			// Android Beam feature is not supported.
			Toast.makeText(this, "Android Beam is not supported.",
					Toast.LENGTH_SHORT).show();
		}
		else {
			// NFC and Android Beam file transfer is supported.
			Toast.makeText(this, "Android Beam is supported on your device.",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void sendFile(View view) {
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// Check whether NFC is enabled on device
		if(!nfcAdapter.isEnabled()){
			// NFC is disabled, show the settings UI
			// to enable NFC
			Toast.makeText(this, "Please enable NFC.",
					Toast.LENGTH_SHORT).show();
			startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
		}
		// Check whether Android Beam feature is enabled on device
		else if(!nfcAdapter.isNdefPushEnabled()) {
			// Android Beam is disabled, show the settings UI
			// to enable Android Beam
			Toast.makeText(this, "Please enable Android Beam.",
					Toast.LENGTH_SHORT).show();
			startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
		}
		else {
			// NFC and Android Beam both are enabled

			// File to be transferred
			// For the sake of this tutorial I've placed an image
			// named 'wallpaper.png' in the 'Pictures' directory
			String fileName = "wallpaper.png";

			// Retrieve the path to the user's public pictures directory
			File fileDirectory = Environment
					.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_PICTURES);

			// Create a new file using the specified directory and name
			File fileToTransfer = new File(fileDirectory, fileName);
			fileToTransfer.setReadable(true, false);

			nfcAdapter.setBeamPushUris(
					new Uri[]{Uri.fromFile(fileToTransfer)}, this);
		}
	}

	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	private void writeToFile(String data, Context context) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					context.openFileOutput("config.txt", Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	private String readFromFile(Context context) {

		String ret = "";

		try {
			InputStream inputStream = context.openFileInput("config.txt");

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return ret;
	}
}