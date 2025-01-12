package com.example.testing;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentMainMenu extends Fragment {

    private final String TAG = "FragmentMainMenu";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam2, mParam1;

    public FragmentMainMenu() {}

    private static FragmentMainMenu INSTANCE = null;

    public static FragmentMainMenu newInstance(String param1, String param2) {
        FragmentMainMenu fragment = new FragmentMainMenu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INSTANCE = this;

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Button goButton = view.findViewById(R.id.button3);

        Button trailsButton = view.findViewById(R.id.button6);

        goButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(
                            R.anim.slide_in,
                            R.anim.slide_out,
                            R.anim.slide_in,
                            R.anim.slide_out
            ).replace(R.id.rootContainer, new FragmentConnection()).addToBackStack(null).commit();
        });

        trailsButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
            ).replace(R.id.rootContainer, new FragmentTrailsMenu()).addToBackStack(null).commit();
        });
    }
}