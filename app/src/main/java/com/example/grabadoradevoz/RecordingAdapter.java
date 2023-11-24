package com.example.grabadoradevoz;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder> {

    private ArrayList<String> recordingList;
    private Context context;  // No inicializar aquí
    private MediaPlayer mediaPlayer;

    public RecordingAdapter(ArrayList<String> recordingList, Context context) {
        this.recordingList = recordingList;
        this.context = context;  // Inicializar en el constructor
        this.mediaPlayer = new MediaPlayer();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View recordingView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        // Return a new holder instance
        return new ViewHolder(recordingView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        String recording = recordingList.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.recordingTextView;
        textView.setText(recording);

        // Set a click listener to play the recording when the item is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecording(recording);
            }
        });
    }

    private void playRecording(String recordingFileName) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getRecordingFilePath(recordingFileName));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRecordingFilePath(String recordingFileName) {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        return new File(storageDir, recordingFileName).getAbsolutePath();
    }

    @Override
    public int getItemCount() {
        return recordingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView recordingTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            recordingTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
