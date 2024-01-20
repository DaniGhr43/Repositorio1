package com.example.practica3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.practica3.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment implements CalendarView.OnDateChangeListener {

    private FragmentFirstBinding binding;
    public static String fecha;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.calendarView.setOnDateChangeListener( this);
        binding.btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getContext(),MainPanelActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

        binding.textFecha.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
        fecha = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
    }
}