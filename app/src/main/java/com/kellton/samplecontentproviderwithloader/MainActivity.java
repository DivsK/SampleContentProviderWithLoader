package com.kellton.samplecontentproviderwithloader;

import android.Manifest;
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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private String[] mProjetion = new String[] {MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};;
    private String mSelection = null;
    private String mSortOrder = null;
    private String[] mColumnNames;
    private static final int REQUEST_MEDIA_READ_PERMISSION =1;
    private TextView mTextView;
    private ImageView mImageView;
    private Bitmap bitmap;
    private Button mBtnGetMediaData;

    private String TAG=MainActivity.class.getSimpleName();
    private boolean mMediaFirstTimeLoader =false;
    private int REQUEST_CONTACTS_READ_PERMISSION=2;
    private Button mBtnGetContactsData;
    private boolean mContactsFirstTimeLoader=false;

    String[] projection= new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.tv);
        mImageView = (ImageView) findViewById(R.id.iv);
        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        mBtnGetMediaData =(Button)findViewById(R.id.btn_get_gallery_data);
        mBtnGetContactsData=(Button)findViewById(R.id.btn_get_contacts_data);
        mBtnGetMediaData.setOnClickListener(this);
        mBtnGetContactsData.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_MEDIA_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(!mMediaFirstTimeLoader){
                getLoaderManager().initLoader(1,null,this);
                mMediaFirstTimeLoader =true;
            }
            else {
                getLoaderManager().restartLoader(1,null,this);
            }
        }
        else if(requestCode == REQUEST_CONTACTS_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(!mContactsFirstTimeLoader){
                getLoaderManager().initLoader(2,null,this);
                mContactsFirstTimeLoader =true;
            }
            else {
                getLoaderManager().restartLoader(2,null,this);
            }
        }
        else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_get_gallery_data:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MEDIA_READ_PERMISSION);
                }
                break;
            case R.id.btn_get_contacts_data:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_READ_PERMISSION);
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
                    mColumnNames = cursor.getColumnNames();
                    // Build URI to the main image from the cursor
                    int imageID = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                    bitmap = MediaStore.Images.Thumbnails.getThumbnail(this.getContentResolver(), imageID, MediaStore.Images.Thumbnails.MINI_KIND, null);
                }
                mTextView.setText(Arrays.toString(mColumnNames));
                mImageView.setImageBitmap(bitmap);
                break;

            case 2:
                while (cursor.moveToNext()) {
                  mColumnNames=cursor.getColumnNames();
                    mTextView.setText(String.format("%s\n", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))));
                    mTextView.append(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))+"\n");
                    mImageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))));
//String contactId = cursor.getString(cursor.getColumnIndex(String.valueOf(ContactsContract.CommonDataKinds.Phone.CONTENT_URI)));
                }
                mTextView.append(Arrays.toString(mColumnNames));
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

}
