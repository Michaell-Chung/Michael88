package com.example.michael;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ContentActivity extends AppCompatActivity {
    Button savebtn;
    Button cancelbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
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
                Bitmap bitmap = getResource(news.image_path);
                Icon.setImageBitmap(bitmap);
            }
        }
        public News get(News news){
            news.author = author.getText().toString();
            news.title = title.getText().toString();
            news.publish_time = publish_time.getText().toString();
            news.ISBN = ISBN.getText().toString();
            news.publish_house = publish_house.getText().toString();
            news.image_path = "NULL";
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