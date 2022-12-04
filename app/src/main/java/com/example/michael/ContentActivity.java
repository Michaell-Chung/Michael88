package com.example.michael;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ContentActivity extends AppCompatActivity {
    Button savebtn;
    Button cancelbtn;
    ImageView imageView;
    String img_uri = "NULL";
    Context context;
    public ActivityResultLauncher get_img;
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
        edit_img();
    }
    public void edit_img(){
        get_img = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent intent = result.getData();
                        Glide.with(context).load(intent.getData()).into(imageView);
                        Log.i("Image_uri",intent.getData().toString());
                        img_uri = intent.getData().toString();
                        onResume();
                    }
                }
        );
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                get_img.launch(intent);
            }
        });
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