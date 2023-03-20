package com.example.meditation;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ProfileActivity extends AppCompatActivity {

    ImageView image; //иконка
    TextView Name; //имя пользователя
    OutputStream Stream;
    public static MaskPhoto maskImage;
    private AdapterPhoto pAdapter;
    private List<MaskPhoto> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        image = findViewById(R.id.Avatar);
        Name = findViewById(R.id.NameProfile);
        Name.setText(MainActivity.Name);
        new AdapterQuete.DownloadImageTask((ImageView) image).execute(MainActivity.image);
    }

    public void NextMenu(View view) {
        startActivity(new Intent(this, MenuActivity.class));
    }

    public void NextLogin(View view) { //переход на страницу авторизации
        SharedPreferences prefs = getSharedPreferences("Date", Context.MODE_PRIVATE); // Сохранение данных
        prefs.edit().putString("image", "").apply();
        prefs.edit().putString("Name", "").apply();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void NextListen(View view) {
        startActivity(new Intent(this, ListenActivity.class));
    }

    public void NextFeeling(View view) {
        startActivity(new Intent(this, MainMenuActivity.class));
    }

    public void addImage() //добавление фото
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        someActivityResultLauncher.launch(photoPickerIntent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Bitmap bitmap = null;
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri selectedImage = result.getData().getData();
                        try
                        {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        File dir = new File(getApplicationInfo().dataDir + "/MyFiles/");
                        dir.mkdirs();
                        File file = new File(dir, System.currentTimeMillis() + ".jpg");
                        try {
                            Stream = new FileOutputStream(file); //отправка в файл на диске
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, Stream);
                            Stream.flush();
                            Stream.close();
                            Toast.makeText(ProfileActivity.this, "Успешное сохранение изображения!", Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Возникла ошибка!", Toast.LENGTH_LONG).show();
                        }
                        ImgProfile();
                    }
                }
            });

    private void ImgProfile() { //заполнение картинок
        File pathIm = new File(getApplicationInfo().dataDir + "/MyFiles/");
        pathIm.mkdirs();
        list.clear();
        pAdapter.notifyDataSetInvalidated();
        String path = getApplicationInfo().dataDir + "/MyFiles";
        File directory = new File(path);
        File[] file = directory.listFiles();
        int j = 0;
        for(int i=0;i<file.length;i++){
            Long last = file[i].lastModified();
            MaskPhoto img = new MaskPhoto(
                    j,
                    file[i].getAbsolutePath(),
                    file[i],
                    getFullTime(last)
            );
            list.add(img);
            pAdapter.notifyDataSetInvalidated();
        }
        //последний элемент кнопка
        MaskPhoto img = new MaskPhoto(
                j,
                null,
                null,
                "null"
        );
        list.add(img);
        pAdapter.notifyDataSetInvalidated();
    }
    //преобразование в формат часы и минуты
    private static final String getFullTime(final long timeInMillis) {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.setTimeZone(TimeZone.getDefault());
        return format.format(calendar.getTime());
    }
}