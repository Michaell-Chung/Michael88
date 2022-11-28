package com.example.michael;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Map;

public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        News news = (News) bundle.getSerializable("news");
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.set(news);
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
            Icon.setImageResource(news.Icon);
        }
    }
}