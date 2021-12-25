package com.satyajit.petsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.satyajit.petsapp.data.PetContract.PetsEntry;
import com.satyajit.petsapp.data.PetdbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Integer constant for loader
    private static final int PET_LOADER = 0;

    //instance of the adapter
    PetCursorAdapter mCursorAdapter;

    private PetdbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mDbHelper = new PetdbHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ListView petsList = findViewById(R.id.listItem);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petsList.setEmptyView(emptyView);

        //set up an adapter to create a list item for each row of the pet data in the cursor loader.
        //there is no pet data yet so we will pass in null for the cursor.
        mCursorAdapter = new PetCursorAdapter(this,null);
        petsList.setAdapter(mCursorAdapter); //attach the cursor adapter to our listView.

        petsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, MainActivity.class);

                //Form a Content URi that represents the specific pets we clicked on,
                //by appending the "id" that we have passed in the function.
                //For example: the URI would be content://com.satyajit.petsApp/pets/2
                //if the pet with id 2 was clicked
                Uri contentUri = ContentUris.withAppendedId(PetsEntry.CONTENT_URI,id);
                intent.setData(contentUri);
                startActivity(intent);
            }
        });

        //kick off the loader
        getLoaderManager().initLoader(PET_LOADER,null,this);
    }
    private void insert(){

        ContentValues values = new ContentValues();
        values.put(PetsEntry.COLUMN_PET_NAME,"Toto");
        values.put(PetsEntry.COLUMN_PET_BREED,"Terrier");
        values.put(PetsEntry.COLUMN_PET_GENDER,PetsEntry.GENDER_MALE);
        values.put(PetsEntry.COLUMN_PET_WEIGHT,7);

        Uri newUri = getContentResolver().insert(PetsEntry.CONTENT_URI,values);

        if(newUri==null){
            Toast.makeText(this,getString(R.string.editor_insert_failed), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.editor_insert_Dummy_successful), Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insert();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPets() {
        //Why the content URI? Because that’s the generic __/pets uri which in our content provider will delete all pets.
        int deleteAll = getContentResolver().delete(PetsEntry.CONTENT_URI,null,null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //define a projection that specifies the column we specifies.
        String[] projection = {
                PetsEntry._ID,  // only needed for thr cursor
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED
        };
        //this loader will execute the contentProvider's query method in background thread.
        return new CursorLoader(this,PetsEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //update with this new cursor containing updated petData.
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //callback called when data needs to be deleted.
        mCursorAdapter.swapCursor(null);
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg2);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}