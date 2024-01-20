package com.example.practica3;


import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.practica3.Recycler.BikeList;
import com.example.practica3.databinding.FragmentMyBikeBinding;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyBikeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyBikeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_CODIGO = 123;
    private final int FOTO=1;


    double latitud, longitud;
    Bitmap bitmPhoto;

    boolean photoExists;
    // TODO: Rename and change types of parameters
    public static Address address;
    FragmentMyBikeBinding binding;

    public MyBikeFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyBikeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyBikeFragment newInstance(String param1, String param2) {
        MyBikeFragment fragment = new MyBikeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyBikeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODIGO);

        }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                               latitud = location.getLatitude();
                               longitud = location.getLongitude();

                                binding.txtLatitude.setText(String.valueOf(latitud));
                                binding.txtLongitude.setText(String.valueOf(longitud));


                                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);

                                    if (addresses.size() > 0) {
                                        address = addresses.get(0);

                                        binding.txtCity.setText(address.getLocality());
                                        binding.txtLocation.setText(address.getThoroughfare());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    });


        binding.btnAddMyBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( MainActivity.isLoged){
                    Bike bike= new Bike();
                    bike.setOwner( MainActivity.mAuth.getCurrentUser().getDisplayName());
                    bike.setCity(binding.txtCity.getText().toString());
                    bike.setDescription(binding.txtDescription.getText().toString());
                    bike.setLocation( binding.txtLocation.getText().toString());
                    bike.setEmail(MainActivity.mAuth.getCurrentUser().getEmail());
                    bike.setLatitude(latitud);
                    bike.setLongitude(longitud);
                    if(photoExists){
                        bike.setPhoto(bitmPhoto);
                        BikeList.bikes.add(bike);
                        BikeList.miAdaptader.notifyDataSetChanged();

                        Toast.makeText(getContext(),"Bike added to availables list ",Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(getContext(),"Please add a photo ",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getContext(),"You must log in to add bikes ",Toast.LENGTH_LONG).show();

                }

            }
        });
        binding.btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,FOTO);

            }
        });

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FOTO){
            bitmPhoto=(Bitmap)data.getExtras().get("data");
            binding.imgSofa.setImageBitmap(bitmPhoto);
            photoExists=true;
        }
    }



}