package com.example.myapplication.ui.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.ui.chart.ChartViewModel;

public class SettingsFragment extends Fragment {

    private Button notifyBtn;
    private SettingsViewModel settingsViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root =  inflater.inflate(R.layout.fragment_settings, container, false);
        notifyBtn = (Button) root.findViewById(R.id.CreateSampleNotification);
        notifyBtn.setOnClickListener(new View.OnClickListener()
        { public void onClick(View v) { onCreateNotification(v);} } );
        return root;
    }


    public void onCreateNotification(View v) {

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(getActivity(), getActivity().getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        Intent dismissIntent = new Intent(getActivity(), getActivity().getClass());
        dismissIntent.setAction("DISMISS");
        dismissIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingDismissIntent = PendingIntent.getActivity(getActivity(), 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "channel")
                .setSmallIcon(R.drawable.ic_bruxfree)
                .setContentTitle("You Are Clenching")
                .setContentText("Alert - this habit can seriously damage your teeth over time.")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .addAction(R.drawable.ic_check, "DISMISS", pendingDismissIntent)
                .setVibrate(new long[] { 200, 200, 200, 200, 200 })
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        notificationManager.notify(0, builder.build());
    }

}
