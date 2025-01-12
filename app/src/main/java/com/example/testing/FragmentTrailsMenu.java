package com.example.testing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentTrailsMenu extends Fragment {

    private LinearLayout trailsContainer;
    List<String> savedTrails = new ArrayList<>();

    public FragmentTrailsMenu() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trails_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trailsContainer = view.findViewById(R.id.trails_container);
        trailsContainer.removeAllViews();

        List<String> loadedSavedTrails = loadSavedTrails(getContext());

        if(savedTrails != null)
        {
            for (String trailName : savedTrails) {
                addTrailButton(trailName);
            }
        }
    }

    private void addTrailButton(String trailName) {
        Button trailButton = new Button(getActivity());

        ProgressBar progressBar = getView().findViewById(R.id.progressBar1);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

        if(progressBar != null)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }

        trailButton.setText(trailName);

        int widthInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                340,
                getResources().getDisplayMetrics()
        );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                widthInPx,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 0, 0, 20);
        trailButton.setLayoutParams(params);

        trailButton.setHeight(200);
        trailButton.setTextSize(35);
        trailButton.setBackgroundColor(getResources().getColor(R.color.ViewColor, null));
        trailButton.getBackground().setAlpha(150);
        trailButton.setTextColor(getResources().getColor(R.color.white, null));
        trailButton.setBackgroundResource(R.drawable.button_radius);

        trailsContainer.addView(trailButton);
        trailButton.setOnClickListener(view -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
            ).replace(R.id.rootContainer, new FragmentTrailMaps(trailName, progressBar)).addToBackStack(null).commit();

            if(progressBar != null)
            {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    private List<String> loadSavedTrails(Context context)
    {
        try
        {
            FileInputStream fis = context.openFileInput("trails.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line = reader.readLine();
            line = reader.readLine();

            while(line != null)
            {
                if (!savedTrails.contains(line))
                {
                    savedTrails.add(line);
                }
                line = reader.readLine();
            }
            fis.close();
        }
        catch (Exception e)
        {
            Log.e("TrailMenus","Error loading index: " + e.getMessage(), e);
        }

       return savedTrails;
    }
}