package com.example.michael;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookshelfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookshelfFragment extends Fragment implements MainActivity.OnClickActivityListener, Serializable {
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    boolean isFirstLoading;
    List<News> mNewsList = new ArrayList<>();
    List<News> keepList = new ArrayList<>();
    List<String> tagList = new ArrayList<>();
    MainActivity mainActivity;
    public ActivityResultLauncher edit_result;//接收编辑的activity保存结束后的callback数据
    //call back mainActivity's listener
    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoading) {
            read();
            mMyAdapter.notifyDataSetChanged();
        }
        isFirstLoading = false;
    }
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    public void updateTagList(){
        if(tagList.size()==0)tagList.add("NULL");
        for(int i=0;i<tagList.size();i++){
            for(int j=i;j<mNewsList.size();j++){
                if(tagList.get(i).equals(mNewsList.get(i).tag)){
                    continue;
                }
                else{
                    tagList.add(mNewsList.get(i).tag);
                }
            }
        }
        if(tagList.size()!=0) {
            for (int i = 0; i < tagList.size(); i++) {
                for (int j =i+1; j < tagList.size(); j++) {
                    if(tagList.get(i).equals(tagList.get(j))){
                        tagList.remove(j);
                        j--;
                    }
                }
            }
            for (int i = 0; i < tagList.size(); i++) {
                if (tagList.get(i).equals("NULL")) {
                    tagList.remove(i);
                }
            }
        }
        else if(tagList.size()==0)tagList.add("NULL");
    }
    @Override
    public void OnSearchActivity(String s) {
        List<News> mTmpList=new ArrayList<News>();
        for (News n:
                keepList) {
            if(n.title.toLowerCase().indexOf(s.toLowerCase()) != -1 || n.author.toLowerCase().indexOf(s.toLowerCase()) != -1){
                mTmpList.add(n);
            }
        }
        mNewsList=mTmpList;
        mMyAdapter.notifyDataSetChanged();
    }
    @Override
    public void OnCloseSearchActivity() {
        mNewsList = keepList;
        mMyAdapter.notifyDataSetChanged();
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
    public void addBookButtonListener(View view){
        FloatingActionButton f = view.findViewById(R.id.addBookButton);
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

    public BookshelfFragment() {
        // Required empty public constructor
    }

    public static BookshelfFragment newInstance(String param1, String param2) {
        BookshelfFragment fragment = new BookshelfFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //添加各个元素的tag到list里面
        updateTagList();
        isFirstLoading = true;
        mRecyclerView = view.findViewById(R.id.recyclerview2);
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
                            updateTagList();
                            mMyAdapter.notifyDataSetChanged();
                            save();
                            mainActivity.mTagList =tagList;
                            mainActivity.createFragment();
                        }
                    }
                });
        read();
        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);
        keepList = mNewsList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        addBookButtonListener(view);

        return view;
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private Context mContext;
        private int position;
        ArrayList<String> tagList;
        public int getContextMenuPosition() { return position; }
        public void setContextMenuPosition(int position) { this.position = position; }
        MyAdapter(){
             tagList= (ArrayList<String>) BookshelfFragment.this.tagList;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(mContext==null){
                mContext = parent.getContext();
            }
            View view = View.inflate(getContext(), R.layout.item_list, null);
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
            else Glide.with(getContext()).load(news.image_path).into(holder.smallImg);
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
                    updateTagList();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news",mNewsList.get(holder.getAdapterPosition()));
                    bundle.putInt("position",holder.getAdapterPosition());
                    bundle.putStringArrayList("tagList", tagList);
                    intent.setClass(getContext(),ContentActivity.class);
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
                CreateMenu(contextMenu);
            }
        }
    }

    public void save(){
        FileOutputStream book_tag = null;
        List[] lists = {mNewsList,tagList};
        try{
            book_tag = getActivity().openFileOutput("book.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(book_tag);
            oos.writeObject(lists);
            oos.close();
            //            oos.writeObject(mNewsList);
//            oos.writeObject(tagList);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(book_tag != null)
                    book_tag.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void read(){
        FileInputStream in = null;
        try{
            in = getActivity().openFileInput("book.ser");
            ObjectInputStream ois = new ObjectInputStream(in);
            //Log.e("这里没问题","这里没问题");
            Object[] object = (Object[]) ois.readObject();
            mNewsList =(List<News>) object[0];
            tagList = (List<String>) object[1];
            ois.close();
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