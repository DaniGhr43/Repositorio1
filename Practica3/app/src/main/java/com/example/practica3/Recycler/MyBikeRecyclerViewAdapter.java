package com.example.practica3.Recycler;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practica3.Bike;
import com.example.practica3.FirstFragment;
import com.example.practica3.MainActivity;
import com.example.practica3.UserBooking;
import com.example.practica3.databinding.FragmentItemBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyBikeRecyclerViewAdapter extends RecyclerView.Adapter<MyBikeRecyclerViewAdapter.ViewHolder> {

    private final List<Bike> mValues;


    private static StorageReference mStorageReference;



    public MyBikeRecyclerViewAdapter(List<Bike> items) {
        mValues=items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.mItem = mValues.get(position);

        Log.d("Adapter22", "Bike list size: " + mValues.size());
        holder.ciudadView.setText(mValues.get(position).getCity());
        holder.calleView.setText(mValues.get(position).getLocation());
        holder.nombreView.setText(mValues.get(position).getOwner());
        holder.descView.setText(mValues.get(position).getDescription());

        holder.imagenView.setImageBitmap(mValues.get(position).getPhoto());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cuerpo = "Dear Mr/Mrs " + mValues.get(position).getOwner() + "\n" +
                        "I'd like to use your bike at " + MainActivity.address.getLocality() + "\n" +
                        "for the following date: " + FirstFragment.fecha + "\n" +
                        "Can you confirm its availability?\n" +
                        "Kindest regards";
                String subject = "Bike renting";

                UserBooking reserva = new UserBooking(MainActivity.mAuth.getCurrentUser().getDisplayName(),MainActivity.mAuth.getCurrentUser().getEmail(),(mValues.get(position).getEmail()),(mValues.get(position).getCity()),FirstFragment.fecha  );
                reserva.addToDatabase();


                Toast.makeText(v.getContext(), "Reserve done  ", Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public final ImageView imagenView;
        public final TextView ciudadView;
        public final TextView nombreView;
        public final TextView calleView;
        public final TextView descView;
        public final ImageButton button;
        public Bike mItem;


        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            imagenView = binding.imageView3;
            ciudadView = binding.bycicleCity;
            nombreView = binding.bycicleOwner;
            calleView = binding.bycicleStreet;
            descView = binding.bycicleDesc;
            button = binding.btnEmailList;


        }

        @Override
        public String toString() {
            return super.toString() + " '" + nombreView.getText() + "'";
        }
    }

    public void downloadPhoto(Bike c) {

        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(c.getImage());
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final File localFile = File.createTempFile("PNG_" + timeStamp, ".png");
            mStorageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                    String url = "gs://" + taskSnapshot.getStorage().getBucket() + "/" + taskSnapshot.getStorage().getName();
                    ;
                    Log.d(TAG, "Loaded " + url);
                    for (Bike c : mValues) {
                        if (c.getImage().equals(url)) {
                            c.setPhoto(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                            notifyDataSetChanged();
                            Log.d(TAG, "Loaded pic " + c.getImage() + ";" + url + localFile.getAbsolutePath());
                        }
                    }
                }

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadBikesList() {
        Log.d("BikeFragment", "Entering loadBikesList");
        if (mValues.isEmpty()) {
            Log.d("BikeFragment", "mValues is empty, loading bikes list");
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();;
            Log.d("BikeFragment", "Entro en la BD");

            mDatabase.child("bikes_list").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("BikeFragment", "Entro en al onDataChange");

                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Log.d("BikeFragment", "Entro al for");
                        Bike bike = productSnapshot.getValue(Bike.class);
                        downloadPhoto(bike);
                        mValues.add(bike);


                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("BikeFragment", "onCancelled: " + error.getMessage());

                }
            });
        }
    }


}