package com.satyajit.petsapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.satyajit.petsapp.data.PetContract;

public class PetCursorAdapter extends CursorAdapter {
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    //creates and return a blank list item view.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pets_item,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView petsName = view.findViewById(R.id.PetsNames);
        TextView breed = view.findViewById(R.id.PetsBreeds);

        //find or extract the attributes
        int nameColumnIndex = cursor.getColumnIndex(PetContract.PetsEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetContract.PetsEntry.COLUMN_PET_BREED);

        //read the attributes from the cursor
        String getName = cursor.getString(nameColumnIndex);
        String getBreed = cursor.getString(breedColumnIndex);
        if (TextUtils.isEmpty(getBreed)) {
            getBreed = context.getString(R.string.unknown_breed);
        }

        petsName.setText(getName);
        breed.setText(getBreed);
    }
}
