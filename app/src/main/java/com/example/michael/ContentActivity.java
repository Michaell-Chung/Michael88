package com.example.michael;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ContentActivity extends AppCompatActivity {
    Button savebtn;
    Button cancelbtn;
    ImageView imageView;
    Uri uri = null;
    String img_uri = "NULL";
    Context context;
    public ActivityResultLauncher getImg_local;
    public ActivityResultLauncher getImg_carama;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        context = this.getApplicationContext();
        imageView = findViewById(R.id.imageView2);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        News news = (News) bundle.getSerializable("news");
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.set(news);
        savebtn = findViewById(R.id.save_button);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                News update = viewHolder.get(new News());
                bundle.putSerializable("edit_update",update);
                intent.putExtra("status_code","save");
                intent.putExtra("position",bundle.getInt("position"));
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        cancelbtn = findViewById(R.id.cancel_button);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK,intent);
                intent.putExtra("status_code","cancel");
                finish();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupMenu(imageView);
            }
        });
        getImg_local = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent intent = result.getData();
                        img_uri = intent.getData().toString();
                        Glide.with(context).load(img_uri).into(imageView);
                        Log.i("Image_uri",intent.getData().toString());
                        onResume();
                    }
                }
        );
        getImg_carama = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent intent = result.getData();
//                        img_uri = intent.getStringExtra(MediaStore.EXTRA_OUTPUT);
                        Glide.with(context).load(uri).into(imageView);
                        onResume();
                    }
                }
        );
    }
    public void createPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.inflate(R.menu.imgedit_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.camarabtn:
                        editimg_camara();
                        return true;
                    case R.id.localbtn:
                        editimg_local();
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    //相册中选择图片
    public void editimg_local(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        getImg_local.launch(intent);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                get_img.launch(intent);
//            }
//        });
    }
    public void editimg_camara(){
        File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
        try {
            if(outputImage.exists()){
                outputImage.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        uri = FileProvider.getUriForFile(context,"com.example.michael.fileProvider",outputImage);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.i("uri!!!",uri.toString());
        img_uri = uri.toString();
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        getImg_carama.launch(intent);
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        cancelbtn.callOnClick();
    }
    class ViewHolder{
        TextView title;
        TextView author;
        TextView publish_time;
        TextView publish_house;
        TextView ISBN;
        ImageView Icon;
        public ViewHolder(){
            title = findViewById(R.id.editTextTextPersonName2);
            author = findViewById(R.id.editTextTextPersonName3);
            publish_time = findViewById(R.id.editTextTextPersonName4);
            publish_house = findViewById(R.id.editTextTextPersonName5);
            ISBN = findViewById(R.id.editTextTextPersonName6);
            Icon = findViewById(R.id.imageView2);
        }
        public void set(News news){
            title.setText(news.title);
            author.setText(news.author);
            publish_time.setText(news.publish_time);
            publish_house.setText(news.publish_house);
            ISBN.setText(news.ISBN);
            if(news.image_path.equals("NULL")){
                Icon.setImageResource(news.Icon);
            }
            else{
                Glide.with(context).load(news.image_path).into(imageView);
            }
        }
        public News get(News news){
            news.author = author.getText().toString();
            news.title = title.getText().toString();
            news.publish_time = publish_time.getText().toString();
            news.ISBN = ISBN.getText().toString();
            news.publish_house = publish_house.getText().toString();
            news.image_path = img_uri;
            return news;
        }
    }
    public Bitmap getResource(String imageName) {
        Bitmap bitmap = null;
        try {
            FileInputStream localStream = this.openFileInput(imageName);
            bitmap = BitmapFactory.decodeStream(localStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}