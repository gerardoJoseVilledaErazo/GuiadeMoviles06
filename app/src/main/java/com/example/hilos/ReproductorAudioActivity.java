package com.example.hilos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hilos.utils.AudioAsincrono;

import java.util.Timer;
import java.util.TimerTask;

public class ReproductorAudioActivity extends AppCompatActivity {

    private Button btnIniciar, btnReiniciar;
    private TextView txvActual, txvFinal;
    private AudioAsincrono audioAsincrono;

    //private ProgressBar pb;
    //private TextView mostrarPorcentaje;
    private SeekBar audioTraverse;
    MediaPlayer reproductorMusica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor_audio);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        reproductorMusica = MediaPlayer.create(this, R.raw.nokia_tune);

        //pb = (ProgressBar) findViewById(R.id.progressBar);

        //Para mostrar el porcentaje
        //mostrarPorcentaje = (TextView)findViewById(R.id.txtCargar);

        // SeekBar
        audioTraverse = (SeekBar) findViewById(R.id.audioTraverse);
        // get maximum value of the Seek bar
        int maxValue = audioTraverse.getMax();
        // get progress value from the Seek bar
        int seekBarValue= audioTraverse.getProgress();
        // Valor Inicial
        audioTraverse.setProgress(0);
        // Valor Final
        audioTraverse.setMax(reproductorMusica.getDuration());

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                audioTraverse.setProgress(reproductorMusica.getCurrentPosition());
            }
        },0, 1000);
        // here 0 represents first time this timer will run...
        // In this case it will run after 0 seconds after activity is launched
        // And 1000 represents the time interval afer which this thread ie, "run()" will execute.
        // In this case, it will execute after every 1 second

        audioTraverse.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progressChangedValue = 0;
                    //hace un llamado a la perilla cuando se arrastra
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int i/*progress*/,
                                                  boolean b/*fromUser*/) {
                        if(b){
                            progressChangedValue = i;
                            Log.i("Tranverse Change: ", Integer.toString(i) );
                            reproductorMusica.seekTo(i);
                            audioTraverse.setProgress(i);
                            //mostrarPorcentaje.setText(String.valueOf(/*progress*/i)+" %");
                        }
                    }
                    //hace un llamado  cuando se toca la perilla
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }
                    //hace un llamado  cuando se detiene la perilla
                    @Override public void onStopTrackingTouch(SeekBar seekBar) {
                        Toast.makeText(ReproductorAudioActivity.this,
                                "Seek bar progress is :" + progressChangedValue,
                                Toast.LENGTH_SHORT).show();
                    }
                });


        txvActual = findViewById(R.id.txvActual);
        txvFinal  = findViewById(R.id.txvFinal);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnReiniciar = findViewById(R.id.btnReiniciar);

        btnIniciar.setOnClickListener(v -> {
            iniciar();
        });

        btnReiniciar.setOnClickListener(  v -> {
            reiniciar();
        });
    }

    private void iniciar() {
        if ( audioAsincrono == null ) {
            audioAsincrono = new AudioAsincrono(ReproductorAudioActivity.this,
                                                        txvActual, txvFinal);
            audioAsincrono.execute();
            btnIniciar.setText("Pausar");
            Toast.makeText(
                    ReproductorAudioActivity.this,
                    "Reproduciendo", Toast.LENGTH_SHORT).show();
        } else
            if ( audioAsincrono.getStatus() == AsyncTask.Status.FINISHED ) {
                audioAsincrono = new AudioAsincrono(ReproductorAudioActivity.this,
                                                        txvActual, txvFinal);
                audioAsincrono.execute();
                Toast.makeText(
                        ReproductorAudioActivity.this,
                        "Reproduciendo", Toast.LENGTH_SHORT).show();
        } else
            if ( audioAsincrono.getStatus() == AsyncTask.Status.RUNNING && !audioAsincrono.esPause() )
            {
                // En caso de que este corriendo y no este pausado; entonces se pausa.
                audioAsincrono.pausarAudio();
                btnIniciar.setText("Reanudar");
        } else
            if ( audioAsincrono.esPause() )
            {
                // En caso de que este pausado; entonces se debe reanudar
                audioAsincrono.reanudarAudio();
                btnIniciar.setText("Pausar");
        }
    }

    private void reiniciar(){
        if (audioAsincrono.getStatus() == AsyncTask.Status.RUNNING || audioAsincrono.esPause() )
        {
            // En caso de que este corriendo o este pausado; entonces se reinicia.
            audioAsincrono.reiniciarAudio();
            audioAsincrono = new AudioAsincrono(ReproductorAudioActivity.this,
                    txvActual, txvFinal);
            audioAsincrono.execute();
            btnIniciar.setText("Pausar");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}