package com.energysistem.earspeaker;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import java.io.File;
import java.io.FileInputStream;


public class MainActivity extends Activity {

    private static final int AUDIO_GALLERY_REQUEST_CODE = 1234;
    final MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Button botonReproducir = (Button) findViewById(R.id.boton_reproducir);
        final Button botonParar = (Button) findViewById(R.id.botonPara);
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        setVolumeControlStream(AudioManager.STREAM_MUSIC );
        botonReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MODE_IN_CALL","Activado");
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setSpeakerphoneOn(false);
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, AUDIO_GALLERY_REQUEST_CODE);

                v.setEnabled(false);
                botonReproducir.setText("Reproduciendo");

            }
        });

        botonParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MODE_NORMAL","Activado");
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(true);
                if(!botonReproducir.isEnabled()) {
                    botonReproducir.setEnabled(true);
                    botonReproducir.setText(R.string.reproducir);
                }
                mp.stop();
                mp.reset();
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == AUDIO_GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String path = _getRealPathFromURI(getApplicationContext(), intent.getData());
                    File audio = new File(path);

                    FileInputStream inputStream = new FileInputStream(audio);
                    mp.setDataSource(inputStream.getFD());
                    mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                    mp.prepare();
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            mp.setLooping(true);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String _getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
