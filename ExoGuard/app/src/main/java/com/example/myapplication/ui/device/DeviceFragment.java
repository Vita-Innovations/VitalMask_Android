package com.example.myapplication.ui.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;

public class DeviceFragment extends Fragment {
    public interface Listener {
        public void onDeviceView();
    }
    private DeviceViewModel deviceViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        deviceViewModel =
                ViewModelProviders.of(this).get(DeviceViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);
//        final TextView textView = root.findViewById(R.id.text_device);
//        deviceViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        return root;
    }
    public void onViewCreated(View view, Bundle savedInstanceState){
        ((Listener) getActivity()).onDeviceView();
    }
}