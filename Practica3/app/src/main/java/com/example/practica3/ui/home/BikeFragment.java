package com.example.practica3.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.practica3.MainActivity;
import com.example.practica3.Recycler.BikeList;
import com.example.practica3.databinding.FragmentBikeBinding;

public class BikeFragment extends Fragment {

private FragmentBikeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

            binding = FragmentBikeBinding.inflate(inflater, container, false);
            View root = binding.getRoot();
            LinearLayout miLy = binding.recycler;
            miLy.post(new Runnable() {
                @Override
                public void run() {
                    if(MainActivity.isLoged){
                        Toast.makeText(getContext(),"You must log in to see the available bikes ",Toast.LENGTH_LONG).show();
                    }
                    BikeList.miAdaptader.loadBikesList();
                }
            });

            return root;
    }


@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}