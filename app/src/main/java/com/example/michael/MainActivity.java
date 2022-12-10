package com.example.michael;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    List<News> mNewsList = new ArrayList<>();
    List<News> keepList = new ArrayList<>();
    public ActivityResultLauncher edit_result;//接收编辑的activity保存结束后的callback数据

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        keepList = mNewsList;
        getMenuInflater().inflate(R.menu.search_menu,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                List<News> mTmpList=new ArrayList<News>();
                for (News n:
                     keepList) {
                    if(n.title.indexOf(s) != -1 || n.author.indexOf(s) != -1){
                        mTmpList.add(n);
                    }
                }
                mNewsList=mTmpList;
                mMyAdapter.notifyDataSetChanged();
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mNewsList = keepList;
                mMyAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 1:
                mNewsList.remove(mNewsList.get(mMyAdapter.getContextMenuPosition()));
                mMyAdapter.notifyDataSetChanged();
                save();
                break;
            case 2:
                mNewsList.clear();
                mMyAdapter.notifyDataSetChanged();
                save();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
    public void addBookButtonListener(){
        FloatingActionButton f = findViewById(R.id.addBookButton);
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewsList.add(0,new News());
                mMyAdapter.notifyDataSetChanged();
                save();
            }
        });
    }
    public void CreateMenu(Menu menu) {
        int groupID = 0;
        int order = 0;
        int[] itemID = {1,2};

        for(int i=0;i<itemID.length;i++)
        {
            switch(itemID[i])
            {
                case 1:
                    menu.add(groupID, itemID[i], order, "删除");
                    break;
                case 2:
                    menu.add(groupID, itemID[i], order, "清空所有图书");
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mRecyclerView = findViewById(R.id.recyclerview);
        edit_result = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent intent = result.getData();
                        String return_back = intent.getStringExtra("status_code");
                        if(return_back.equals("save")){
                            Bundle bundle = intent.getExtras();
                            mNewsList.set(bundle.getInt("position"),(News)bundle.getSerializable("edit_update"));
                            mMyAdapter.notifyDataSetChanged();
                            save();
                        }
                    }
                });
        // 构造一些数据
//        for (int i = 0; i < 10; i++) {
//            News news = new News();
//            news.title = "标题" + i;
//            news.author = "michael" + i;
//            news.publish_time = "2022/11/25";
//            news.Icon = drawImg.get(i%2);
//            news.image_path = "NULL";
//            mNewsList.add(news);
//        }
        read();

        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        addBookButtonListener();
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private Context mContext;
        private int position;
        public int getContextMenuPosition() { return position; }
        public void setContextMenuPosition(int position) { this.position = position; }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(mContext==null){
                mContext = parent.getContext();
            }
            View view = View.inflate(MainActivity.this, R.layout.item_list, null);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            News news = mNewsList.get(position);
            holder.mTitleTv.setText(news.title);
            holder.mTitleContent.setText(news.author);
            holder.time.setText(news.publish_time);
            if(news.image_path=="NULL")
                holder.smallImg.setImageResource(news.Icon);
            else Glide.with(MainActivity.this).load(news.image_path).into(holder.smallImg);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setContextMenuPosition(holder.getLayoutPosition());
                    return false;
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news",mNewsList.get(holder.getAdapterPosition()));
                    bundle.putInt("position",holder.getAdapterPosition());
                    intent.setClass(MainActivity.this,ContentActivity.class);
                    intent.putExtras(bundle);
                    edit_result.launch(intent);
//                    startActivity(intent);
                }
            });
        }
        @Override
        public int getItemCount() {
            return mNewsList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
            TextView mTitleTv;
            TextView mTitleContent;
            TextView time;
            ImageView smallImg;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                mTitleTv = itemView.findViewById(R.id.textView);
                mTitleContent = itemView.findViewById(R.id.textView2);
                time = itemView.findViewById(R.id.textView3);
                smallImg = itemView.findViewById(R.id.imageView);
                itemView.setOnCreateContextMenuListener(this);
            }
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                News mSelectModelUser = mNewsList.get(getContextMenuPosition());
                Log.i("UserAdapter", "onCreateContextMenu: "+getContextMenuPosition());
                contextMenu.setHeaderTitle(mSelectModelUser.title);
                ((MainActivity)mContext).CreateMenu(contextMenu);
            }
        }
    }
    public void save(){
        FileOutputStream out = null;
        try{
            out = this.openFileOutput("book.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(mNewsList);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(out != null)
                    out.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void read(){
        FileInputStream in = null;
        try{
            in = this.openFileInput("book.ser");
            ObjectInputStream ois = new ObjectInputStream(in);
            //Log.e("这里没问题","这里没问题");
            mNewsList = (List<News>) ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(in != null){
                try{
                    in.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}