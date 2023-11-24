package com.example.grabadoradevoz;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnRecordPause, btnStop, btnListRecordings;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String currentOutputFile;
    private boolean isRecording = false;
    private boolean isPaused = false;
    private RecyclerView recyclerViewRecordings;
    private ArrayList<String> recordingList = new ArrayList<>();
    private RecordingAdapter recordingAdapter;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecordPause = findViewById(R.id.btnRecordPause);
        btnStop = findViewById(R.id.btnStop);
        btnListRecordings = findViewById(R.id.btnListRecordings);
        recyclerViewRecordings = findViewById(R.id.recyclerViewRecordings);

        // Configura el adaptador y el diseño para la RecyclerView
        recordingList = new ArrayList<>();
        recordingAdapter = new RecordingAdapter(recordingList, this);

        recordingAdapter.setContext(this); // Asegúrate de llamar a setContext
        recyclerViewRecordings.setAdapter(recordingAdapter);
        recyclerViewRecordings.setLayoutManager(new LinearLayoutManager(this));

        // Solicitar permiso en tiempo de ejecución si no se ha otorgado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
            }
        }

        btnListRecordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar u ocultar la RecyclerView al hacer clic en el botón
                toggleRecyclerViewVisibility();
            }
        });

        btnRecordPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    if (isPaused) {
                        // Continuar la grabación
                        resumeRecording();
                    } else {
                        // Pausar la grabación
                        pauseRecording();
                    }
                } else {
                    // Iniciar la grabación
                    startRecording();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Detener la grabación
                stopRecording();

                // Reproducir el archivo grabado
                playRecording();

                // Actualizar la lista de grabaciones
                updateRecordingList();
            }
        });
    }

    private void startRecording() {
        currentOutputFile = getOutputFilePath();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(currentOutputFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
        isRecording = true;
        isPaused = false;
        btnRecordPause.setText("Pausar");
    }

    private void pauseRecording() {
        // Pausar la grabación
        mediaRecorder.pause();
        isPaused = true;
        btnRecordPause.setText("Continuar grabación");
    }

    private void resumeRecording() {
        // Continuar la grabación
        mediaRecorder.resume();
        isPaused = false;
        btnRecordPause.setText("Pausar");
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            isPaused = false;
            btnRecordPause.setText("Grabar");
        }
    }

    private void playRecording() {
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(currentOutputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOutputFilePath() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Recording_" + timeStamp + ".3gp";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        return new File(storageDir, fileName).getAbsolutePath();
    }

    private void updateRecordingList() {
        recordingList.clear();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (storageDir != null) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    recordingList.add(file.getName());
                }
            }
        }
        // Notificar al adaptador sobre el cambio en los datos
        recordingAdapter.notifyDataSetChanged();
    }

    private void toggleRecyclerViewVisibility() {
        if (recyclerViewRecordings.getVisibility() == View.VISIBLE) {
            // Si la RecyclerView es visible, ocúltala
            recyclerViewRecordings.setVisibility(View.GONE);
        } else {
            // Si la RecyclerView está oculta, muéstrala y actualiza la lista de grabaciones
            recyclerViewRecordings.setVisibility(View.VISIBLE);
            updateRecordingList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado, puedes comenzar la grabación
            } else {
                // Permiso denegado, muestra un mensaje o realiza alguna acción adecuada
            }
        }
    }
}
