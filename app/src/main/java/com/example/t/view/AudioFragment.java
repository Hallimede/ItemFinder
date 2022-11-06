package com.example.t.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t.model.User;
import com.example.t.msc.RecordListener;
import com.example.t.view.animator.VolumeWaveView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.t.R;

import java.util.concurrent.Executor;


public class AudioFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AudioFragment.class.getSimpleName();

    private View view;
    private Toast mToast;
    private User mUser = null;
    private MainPageActivity pageActivity;
    private VolumeWaveView wave;
    private TextView txtWord;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myTest", getTag());
        pageActivity = (MainPageActivity) getActivity();
        pageActivity.setAudioFragment();
        mUser = pageActivity.mUser;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.audio_page, container, false);
        txtWord = view.findViewById(R.id.isr_text);
        view.findViewById(R.id.isr_recognize).setOnClickListener(this);
        wave = view.findViewById(R.id.volume_wave);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        wave.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        pageActivity.msc.setTextFeed(txtWord);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        pageActivity.msc.listen(new RecordListener() {
            @Override
            public void onRecordBegin() {
                wave.setVisibility(View.VISIBLE);
                Log.d(TAG, "wave begins");
                wave.startAnimation();
            }

            @Override
            public void onRecordEnd() {
                wave.setVisibility(View.INVISIBLE);
                wave.removeAnimation();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showTip(final String str) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

}

