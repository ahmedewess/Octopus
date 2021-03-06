package com.ramadan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.core.DownloadManager;
import com.core.DownloadTask;
import com.status.DownloadResult;
import com.status.DownloadStatus;
import com.ui.UIThreadCallback;
import com.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements UIThreadCallback {
    public static final String url3 = "https://doc-00-50-docs.googleusercontent.com/docs/securesc/1r14jt81f7jnfbd9piaptmojvlobuogr/13epqo5he43gt6o7bntmpn8k9lc12tt7/1553083200000/14676411655443883941/14676411655443883941/0BwnvTqAnwmkaYVlfQzRrRU1uX3c?e=download&nonce=g7ft2cf127jho&user=14676411655443883941&hash=rh3qsfv5cvk2udc05sn2hrb0j4chbb0f";
    public static final String DOWNLOADING_FOLDER_PATH = Environment.getExternalStorageDirectory().toString() + "/ramadan";
    private static int REQUEST_PERMISSION = 0x0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private DownloadManager mDownloadManager;
    private String url1, url2;
    private File file1, file2;
    private String localPath1, localPath2;
    private RecyclerView downloadTasksRecyclerView;
    private List<DownloadTask> downloadTasks = new ArrayList<>();
    TasksAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mockData();
        isStoragePermissionGranted();
        initUi();


    }

    private void mockData() {
        url1 = "https://images.homedepot-static.com/productImages/612ae505-9daf-45c3-ac16-67f97dcb251d/svn/globalrose-flower-bouquets-prime-100-red-roses-64_1000.jpg";
        url2 = "https://i-h2.pinimg.com/564x/14/5c/69/145c69be0d39bfc078d2ea17502281a8.jpg";
        // Get the external storage directory path
        String path = Environment.getExternalStorageDirectory().toString() + "/ramadan";
        new File(path).mkdir();
        file1 = new File(path, "filex" + generateUniqueId() + "." + Util.getFileExtension(url1));
        localPath1 = file1.getPath();

        file2 = new File(path, "filey" + generateUniqueId() + "." + Util.getFileExtension(url2));
        localPath2 = file2.getPath();

        DownloadTask downloadTask1 = new DownloadTask(generateUniqueId(),
                "https://sample-videos.com/img/Sample-jpg-image-1mb.jpg", localPath1, this);
        downloadTask1.setFileName("task 1 - 1 MB");

        DownloadTask downloadTask2 = new DownloadTask.Builder("https://sample-videos.com/img/Sample-jpg-image-2mb.jpg").destination(localPath1)
                .fileId(generateUniqueId()).fileName("task 2 - 2 MB").UiThreadCallback(this).build();

        DownloadTask downloadTask3 = new DownloadTask(generateUniqueId(),
                "https://sample-videos.com/img/Sample-jpg-image-5mb.jpg"
                , localPath2, this);
        downloadTask3.setFileName("task 3 - 5 MB");

        DownloadTask downloadTask4 = new DownloadTask.Builder("https://sample-videos.com/img/Sample-jpg-image-10mb.jpg").destination(localPath1)
                .fileId(generateUniqueId()).fileName("task 4 - 10 MB").UiThreadCallback(this).build();

        DownloadTask downloadTask5 = new DownloadTask.Builder("https://sample-videos.com/img/Sample-jpg-image-15mb.jpeg").destination(localPath1)
                .fileId(generateUniqueId()).fileName("task 5 - 15 MB").UiThreadCallback(this).build();
        DownloadTask downloadTask6 = new DownloadTask.Builder("https://sample-videos.com/pdf/Sample-pdf-5mb.pdf").destination(localPath1)
                .fileId(generateUniqueId()).fileName("task 6 pdf - 5 MB").UiThreadCallback(this).build();
        DownloadTask downloadTask7 = new DownloadTask.Builder("http://enos.itcollege.ee/~jpoial/allalaadimised/reading/Android-Programming-Cookbook.pdf").destination(localPath1)
                .fileId(generateUniqueId()).fileName("task 7 pdf - 8 MB").UiThreadCallback(this).build();



        downloadTasks.add(downloadTask1);
        downloadTasks.add(downloadTask2);
        downloadTasks.add(downloadTask3);
        downloadTasks.add(downloadTask4);
        downloadTasks.add(downloadTask5);
        downloadTasks.add(downloadTask6);
        downloadTasks.add(downloadTask7);

        for(DownloadTask downloadTask : downloadTasks){
            downloadTask.setDestination(new File(path, downloadTask.getFileName() + "_" + generateUniqueId() + "." + Util.getFileExtension(downloadTask.getUrl())).getPath());
        }
    }

    private int generateUniqueId() {
        return new Random().nextInt();
    }

    private void initUi() {
        downloadTasksRecyclerView = findViewById(R.id.rv_tasks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        downloadTasksRecyclerView.setLayoutManager(linearLayoutManager);

        tasksAdapter = new TasksAdapter(this, downloadTasks);
        downloadTasksRecyclerView.setAdapter(tasksAdapter);




    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }


    @Override
    public void publishToUIThread(DownloadResult result) {
        for (DownloadTask downloadTask : downloadTasks) {
            if (downloadTask.getId() == result.getId()) {
                downloadTask.setProgress(result.getProgress());
            }
        }
        tasksAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_files_button:
                Utils.deleteFiles(DOWNLOADING_FOLDER_PATH);
                break;
            case R.id.download_all_files:
                downloadAllFiles();
                break;
        }
        return true;
    }

    private void downloadAllFiles() {
        for(DownloadTask downloadTask : downloadTasks){
            DownloadManager.getInstance().downloadFile(downloadTask);
        }
    }
}
