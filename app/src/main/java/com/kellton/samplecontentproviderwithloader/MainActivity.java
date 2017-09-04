package com.kellton.samplecontentproviderwithloader;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private String[] mProjetion = null;
    private String mSelection = null;
    private String mSortOrder = null;
    private String[] mColumnNames;

    private static final int REQUEST_MEDIA_READ_PERMISSION =1;
    private int count=0;
    private TextView mTextView;

    private String TAG=MainActivity.class.getSimpleName();
    private boolean mMediaFirstTimeLoader =false;
    private int REQUEST_CONTACTS_READ_PERMISSION=2;
    private boolean mContactsFirstTimeLoader=false;

    String[] projection= new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.PHOTO_URI};

    private ArrayList<Bitmap> mMediaImages;
    private ArrayList<ContactDetails> mContactDetailsList;
    private MediaRecyclerViewAdapter mMediaRecyclerViewAdapter;
    private ContactsRecyclerViewAdapter mContactsRecyclerViewAdapter;
    private RecyclerView mMediaRecyclerView;
    private RecyclerView mContactsRecyclerView;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

    }

    private void initUI() {

        mMediaRecyclerView = (RecyclerView) findViewById(R.id.media_recycler_view);
        mMediaRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        mMediaRecyclerView.setLayoutManager(gridLayoutManager);

        mMediaImages =new ArrayList<>();
        mMediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(this,mMediaImages);
        mMediaRecyclerView.setAdapter(mMediaRecyclerViewAdapter);

        mContactDetailsList=new ArrayList<>();
        mContactsRecyclerViewAdapter=new ContactsRecyclerViewAdapter(mContactDetailsList);

        mContactsRecyclerView= (RecyclerView) findViewById(R.id.contacts_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mContactsRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration=new DividerItemDecoration(MainActivity.this,LinearLayoutManager.VERTICAL);
        mContactsRecyclerView.addItemDecoration(itemDecoration);
        mContactsRecyclerView.setAdapter(mContactsRecyclerViewAdapter);

        Button btnGetMediaData = (Button) findViewById(R.id.btn_get_gallery_data);
        Button btnGetContactsData = (Button) findViewById(R.id.btn_get_contacts_data);

        btnGetMediaData.setOnClickListener(this);
        btnGetContactsData.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_MEDIA_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadData(1,mMediaFirstTimeLoader);
        }
        else if(requestCode == REQUEST_CONTACTS_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            loadData(2,mContactsFirstTimeLoader);
        }
        else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
        }
    }


    private void loadData(int loadId, boolean loadDataFor) {
        if(!loadDataFor){
            getLoaderManager().initLoader(loadId,null,this);
            loadDataFor =true;
        }
        else {
            getLoaderManager().restartLoader(loadId,null,this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_get_gallery_data:

                createProgressDialog();
                mContactsRecyclerView.setVisibility(View.GONE);
                mMediaRecyclerView.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MEDIA_READ_PERMISSION);
                }
                else{
                    loadData(1,mMediaFirstTimeLoader);
                }

                break;
            case R.id.btn_get_contacts_data:

                createProgressDialog();
                mMediaRecyclerView.setVisibility(View.GONE);
                mContactsRecyclerView.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_READ_PERMISSION);
                }
                else {
                    loadData(2,mContactsFirstTimeLoader);
                }

                break;
            default:

                Log.d(TAG,"Wrong Selection");
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id){
            case 1:
                return new CursorLoader(this,MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mProjetion, mSelection, null, mSortOrder);
            case 2:
                return new CursorLoader(this,  ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, mSelection, null, mSortOrder);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            switch (loader.getId()){
                case 1:
                    while (cursor.moveToNext()) {
                        if(count<=103){
                            mColumnNames = cursor.getColumnNames();

                            int imageID = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                            if(MediaStore.Images.Thumbnails.getThumbnail(this.getContentResolver(), imageID, MediaStore.Images.Thumbnails.MINI_KIND, null)!=null){
                                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(this.getContentResolver(), imageID, MediaStore.Images.Thumbnails.MINI_KIND, null);
                                mMediaImages.add(bitmap);
                            }
                            else{
                                break;
                            }
                            count++;
                        }
                    }
                    mMediaRecyclerViewAdapter.setmMediaImages(mMediaImages);
                    mMediaRecyclerViewAdapter.notifyDataSetChanged();
                    Log.i("All columns present", Arrays.toString(mColumnNames));
                    progressDialog.dismiss();
                    break;

                case 2:
                    while (cursor.moveToNext()) {
                        mColumnNames=cursor.getColumnNames();

                        ContactDetails contactDetails = new ContactDetails();
                        contactDetails.ContactName=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        contactDetails.ContactNo=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))!=null){
                            contactDetails.ContactPhoto=Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
                        }
                        mContactDetailsList.add(contactDetails);
                    }
                    mContactsRecyclerViewAdapter.setmContactDetailsList(mContactDetailsList);
                    mContactsRecyclerViewAdapter.notifyDataSetChanged();
                    Log.i("All columns present", Arrays.toString(mColumnNames));
                    progressDialog.dismiss();
                    break;

                default:
                    Log.d(TAG,"Wrong Selection");
                    break;

            }
        }
        else{
            Toast.makeText(this, "Nothing here", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void createProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setMessage("Loading Data Please Wait!!!");
        progressDialog.show();
    }
}
