package com.example.michael;

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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    List<News> mNewsList = new ArrayList<>();
    List<Integer> drawImg = new ArrayList<>();
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 1:
                mNewsList.remove(item.getItemId());
                mMyAdapter.notifyDataSetChanged();
                break;
            case 2:

                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
    public void deleteView(){

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
                    menu.add(groupID, itemID[i], order, "修改");
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
        mRecyclerView = findViewById(R.id.recyclerview);
        drawImg.add(R.drawable.fuck);
        drawImg.add(R.drawable.desert);
        // 构造一些数据
        for (int i = 0; i < 10; i++) {
            News news = new News();
            news.title = "标题" + i;
            news.author = "michael" + i;
            news.publish_time = "2022/11/25";
            news.Icon = drawImg.get(i%2);
//            news.Icon = R.id.imageView;
            mNewsList.add(news);
        }
        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
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
            holder.smallImg.setImageResource(news.Icon);
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
                    intent.setClass(MainActivity.this,ContentActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
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
}