package com.voicerecognition;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

/**
 * Created by ghanendra on 29/07/2017.
 */

public class NewProfileAddActivity extends Activity {
    static int PICK_AUDIO = 123;
    UserProfilesId uprofid;
    UserProfilesNames unames;
    EditText editusername;
    Button clicktocont, btn1, btnrec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_newprofile);
         uprofid = new UserProfilesId(this);
        unames = new UserProfilesNames(this);
        editusername = (EditText) findViewById(R.id.editusername);
        clicktocont = (Button) findViewById(R.id.clicktocont);
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickAudio();
            }
        });
        clicktocont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewProfileAddActivity.this, NewProfileListActivity.class));
                finish();
            }
        });
        btnrec = (Button) findViewById(R.id.btnrec);

        btnrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editusername.getText().toString().trim().matches("")) {
                    String filePath = Environment.getExternalStorageDirectory()+"/"+editusername.getText().toString()+".wav";
                    File f = new File(filePath);
                    try {
                        f.createNewFile();
                        int color = getResources().getColor(R.color.colorPrimaryDark);
                        int requestCode = 0;
                        AndroidAudioRecorder.with(NewProfileAddActivity.this)
                                .setFilePath(filePath)
                                .setColor(color)
                                .setRequestCode(requestCode)
                                .setSource(AudioSource.MIC)
                                .setChannel(AudioChannel.MONO)
                                .setSampleRate(AudioSampleRate.HZ_16000)
                                .setAutoStart(true)
                                .setKeepDisplayOn(true)
                                .record();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else
                    Toast.makeText(NewProfileAddActivity.this, "Please fill a username to continue.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
     }


    public void pickAudio() {
        if (!editusername.getText().toString().trim().matches("")) {
            Intent intent_upload = new Intent();
            intent_upload.setType("audio/wav");
            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent_upload, PICK_AUDIO);
        } else {
            Toast.makeText(this, "Please fill a username to continue.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_AUDIO) {
            if (resultCode == RESULT_OK) {

                //the selected audio.
                final Uri uri = data.getData();
                File f = new File(uri.getPath());
                System.out.println(f.getAbsolutePath() + "file n " + f.getName());
                System.out.println("uri picked" + uri);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        System.out.println("file path newprofaddact " + getPath(NewProfileAddActivity.this, uri));
                        String a = getPath(NewProfileAddActivity.this, uri);
                        String newp = a.substring(0, a.lastIndexOf("/"));
                        System.out.println("path new " + newp);
                        createidentificationProfileId(uri, newp, getFileName(uri));
                        return null;
                    }
                }.execute();

            } else {
                Toast.makeText(this, "Something went wrong, click button to pick audio again.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Audio File Saved successfully.", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Audio File Save failed. Please try again", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createidentificationProfileId(Uri uri, String path, String filename) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NewProfileAddActivity.this, "Please wait, trying to registering.", Toast.LENGTH_SHORT).show();
            }
        });
        VolleyHelper.getType(this, path, filename, editusername.getText().toString(), uprofid, unames);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        System.out.println("file name " + result);
        return result;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


}
