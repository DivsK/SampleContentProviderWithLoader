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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <h1><font color="orange">MainActivity</font></h1>
 * Activity class for fetching data from contacts and media app.
 * On this activity user can fetch data with the help of content resolver and loader</p>
 *
 * @author Divya Khanduri
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private String[] mProjetion = null;
    private String mSelection = null;
    private String mSortOrder = null;
    private String[] mColumnNames;

    private static final int REQUEST_MEDIA_READ_PERMISSION = 1;
    private int count = 0;

    private String TAG = MainActivity.class.getSimpleName();
    private int REQUEST_CONTACTS_READ_PERMISSION = 2;

    String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_URI};

    private ArrayList<Bitmap> mMediaImages;
    private ArrayList<ContactDetails> mContactDetailsList;
    private MediaRecyclerViewAdapter mMediaRecyclerViewAdapter;
    private ContactsRecyclerViewAdapter mContactsRecyclerViewAdapter;
    private RecyclerView mMediaRecyclerView;
    private RecyclerView mContactsRecyclerView;
    private ProgressBar mProgressBar;

    private static boolean dataDownloaded = false;
    private static int mLoadId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            dataDownloaded = savedInstanceState.getBoolean(getString(R.string.data_downloaded), false);
            mLoadId = savedInstanceState.getInt(getString(R.string.loader_id));
            loadData(mLoadId);
        }

        initUI();

    }

    /**
     * Initialize all views related to this activity
     */
    private void initUI() {

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mMediaRecyclerView = (RecyclerView) findViewById(R.id.media_recycler_view);
        mMediaRecyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mMediaRecyclerView.setLayoutManager(gridLayoutManager);

        mMediaImages = new ArrayList<>();
        mMediaRecyclerViewAdapter = new MediaRecyclerViewAdapter(mMediaImages);
        mMediaRecyclerView.setAdapter(mMediaRecyclerViewAdapter);

        mContactDetailsList = new ArrayList<>();
        mContactsRecyclerViewAdapter = new ContactsRecyclerViewAdapter(mContactDetailsList);

        mContactsRecyclerView = (RecyclerView) findViewById(R.id.contacts_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mContactsRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(MainActivity.this, LinearLayoutManager.VERTICAL);
        mContactsRecyclerView.addItemDecoration(itemDecoration);
        mContactsRecyclerView.setAdapter(mContactsRecyclerViewAdapter);

        Button btnGetMediaData = (Button) findViewById(R.id.btn_get_gallery_data);
        Button btnGetContactsData = (Button) findViewById(R.id.btn_get_contacts_data);

        btnGetMediaData.setOnClickListener(this);
        btnGetContactsData.setOnClickListener(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outSave) {
        outSave.putBoolean(getString(R.string.data_downloaded), dataDownloaded);
        outSave.putInt(getString(R.string.loader_id), mLoadId);
    }

    /**
     * Handling permissions for API 23 and above
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_MEDIA_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadData(1);
        } else if (requestCode == REQUEST_CONTACTS_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadData(2);
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Initialize loader with load Id
     */
    private void loadData(int loadId) {
        mLoadId = loadId;
        getLoaderManager().initLoader(mLoadId, null, MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_gallery_data:

                mProgressBar.setVisibility(View.VISIBLE);
                mContactsRecyclerView.setVisibility(View.GONE);
                mMediaRecyclerView.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MEDIA_READ_PERMISSION);
                } else {
                    loadData(1);
                }

                break;
            case R.id.btn_get_contacts_data:

                mProgressBar.setVisibility(View.VISIBLE);
                mMediaRecyclerView.setVisibility(View.GONE);
                mContactsRecyclerView.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_READ_PERMISSION);
                } else {
                    loadData(2);
                }
                break;
            default:

                Log.d(TAG, getString(R.string.wrong_case_selection));
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader cursorLoader;
        switch (id) {
            case 1:
                cursorLoader = new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mProjetion, mSelection, null, mSortOrder);
                break;
            case 2:
                cursorLoader = new CursorLoader(this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, mSelection, null, mSortOrder);
                break;
            default:
                return null;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToPosition(-1);
            switch (loader.getId()) {
                case 1:
                    getImages(cursor);
                    break;

                case 2:
                    getContacts(cursor);
                    break;

                default:
                    Log.d(TAG, "Wrong Selection");
                    break;

            }
            dataDownloaded = true;
        } else {
            Toast.makeText(this, "Nothing here", Toast.LENGTH_LONG).show();
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    /**
     * Getting Contacts with the help of cursor and setting them
     */
    private void getContacts(Cursor cursor) {

        mContactDetailsList.clear();
        while (cursor.moveToNext()) {
            mColumnNames = cursor.getColumnNames();

            ContactDetails contactDetails = new ContactDetails();
            contactDetails.ContactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            contactDetails.ContactNo = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)) != null) {
                contactDetails.ContactPhoto = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
            }
            mContactDetailsList.add(contactDetails);
        }
        mContactsRecyclerViewAdapter.setmContactDetailsList(mContactDetailsList);
        mContactsRecyclerViewAdapter.notifyDataSetChanged();
        Log.i("All columns present", Arrays.toString(mColumnNames));
    }

    /**
     * Getting Images with the help of cursor and setting them
     */
    private void getImages(Cursor cursor) {
        mMediaImages.clear();
        while (cursor.moveToNext()) {
            /*
             * Since there can be many images to handle we show only first 102 images
             */
            if (count <= 103) {
                mColumnNames = cursor.getColumnNames();

                int imageID = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                if (MediaStore.Images.Thumbnails.getThumbnail(this.getContentResolver(), imageID, MediaStore.Images.Thumbnails.MINI_KIND, null) != null) {
                    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(this.getContentResolver(), imageID, MediaStore.Images.Thumbnails.MINI_KIND, null);
                    mMediaImages.add(bitmap);
                } else {
                    break;
                }
                count++;
            }
        }
        mMediaRecyclerViewAdapter.setmMediaImages(mMediaImages);
        mMediaRecyclerViewAdapter.notifyDataSetChanged();
        Log.i("All columns present", Arrays.toString(mColumnNames));
    }

}
