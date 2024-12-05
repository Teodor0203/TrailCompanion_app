package com.example.testing;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentConnection#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentConnection extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentConnection() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentConnection.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentConnection newInstance(String param1, String param2) {
        FragmentConnection fragment = new FragmentConnection();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView deviceConected = view.findViewById(R.id.textView4);
        Button connectDevice = view.findViewById(R.id.button);
        Button startButton = view.findViewById(R.id.button2);
        ProgressBar loadingConnetion = view.findViewById(R.id.progressBar);
        loadingConnetion.getIndeterminateDrawable().setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
        loadingConnetion.setVisibility(View.INVISIBLE);


        if(startButton != null)
        {
            startButton.setVisibility(View.INVISIBLE);
        }

        connectDevice.setOnClickListener(v -> {
            ConnectionManager.getInstance(getActivity(), loadingConnetion, deviceConected, startButton).enableBluetooth(getActivity());
            ConnectionManager.getInstance(getActivity(), loadingConnetion, deviceConected, startButton).connectToESP();
        });

        startButton.setOnClickListener(v -> {
            Log.d("ConnectionManager", "onCreate: StartButton clicked");

            if (view.findViewById(R.id.rootContainer) == null) {
                Log.e("Debug", "rootContainer nu este găsit!");
            } else {
                Log.d("Debug", "rootContainer este prezent.");
            }
            getParentFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_in
            ).replace(R.id.rootContainer, new FragmentMainMenu()).commit();
        });
    }

}