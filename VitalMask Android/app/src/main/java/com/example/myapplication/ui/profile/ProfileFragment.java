package com.example.myapplication.ui.profile;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.ui.settings.SettingsViewModel;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class ProfileFragment extends Fragment {

    private Button notifyBtn;
    private SettingsViewModel settingsViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root =  inflater.inflate(R.layout.fragment_profile, container, false);
        return root;
    }

}
