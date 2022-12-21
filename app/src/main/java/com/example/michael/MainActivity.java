package com.example.michael;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Serializable {
    private TabLayout myTab;
    private ViewPager2 myPager2;
    private OnClickActivityListener mOnClickActivityListener;

    List<Fragment> mFragments = new ArrayList<>();
    List<String> mTitles = new ArrayList<>();
    List<String> mTagList = new ArrayList<>();

    public interface OnClickActivityListener{
        void OnSearchActivity(String s);
        void OnCloseSearchActivity();
    }

    //因为搜索键在activity上面所以只能在activity触发了，写了个接口回调触发
    @Override
    public void onAttachFragment(Fragment fragment) {
        try {
            mOnClickActivityListener = (OnClickActivityListener) fragment;
        }catch (Exception e){
            Log.i("Error:",e.toString());
        }
        super.onAttachFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mOnClickActivityListener!=null){
                    mOnClickActivityListener.OnSearchActivity(newText);
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(mOnClickActivityListener!=null){
                    mOnClickActivityListener.OnCloseSearchActivity();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void createFragment(){
        int x=mFragments.size();
        for(int i=1;i<x;i++){
            mFragments.remove(1);
            mTitles.remove(1);
        }
        for(int i=0;i<mTagList.size();i++){
            String title = mTagList.get(i);
            mTitles.add(title);
            Fragment f = new tagFragment();
            Bundle b = new Bundle();
            b.putString("title",title);
            f.setArguments(b);
            mFragments.add(f);
        }
        new TabLayoutMediator(myTab, myPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mTitles.get(position));
            }
        }).attach();
        save();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTab = findViewById(R.id.my_tab);
        myPager2 = findViewById(R.id.my_pager2);
        //add title
        read();
        //实例化适配器
        FraAdapter myAdapter=new FraAdapter(getSupportFragmentManager(),getLifecycle(),mFragments);
        //设置适配器
        myPager2.setAdapter(myAdapter);

        new TabLayoutMediator(myTab, myPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mTitles.get(position));
            }
        }).attach();
    }
    public void initFragment(){
        mTitles.add("Bookshelf");
        mTagList.add("Bookshelf");
        mFragments.add(new BookshelfFragment());
        FraAdapter myAdapter=new FraAdapter(getSupportFragmentManager(),getLifecycle(),mFragments);
        //设置适配器
        myPager2.setAdapter(myAdapter);
        new TabLayoutMediator(myTab, myPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mTitles.get(position));
            }
        }).attach();
    }

    class FraAdapter extends FragmentStateAdapter{
        List<Fragment> mFragments;
        int position;
        public FraAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,List<Fragment> mFragments) {
            super(fragmentManager, lifecycle);
            this.mFragments = mFragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            this.position = position;
            return mFragments.get(position);
        }
        @Override
        public int getItemCount() {
            return mFragments.size();
        }

    }
    public void save(){
        FileOutputStream book_tag = null;
        List[] lists = {mFragments,mTitles};
        try{
            book_tag = this.openFileOutput("fragment.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(book_tag);
            oos.writeObject(lists);
            oos.close();
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
            in = this.openFileInput("fragment.ser");
            ObjectInputStream ois = new ObjectInputStream(in);
            Object[] objects = (Object[]) ois.readObject();
            mFragments = (ArrayList<Fragment>) objects[0];
            mTitles = (ArrayList<String>) objects[1];
            ois.close();
        }catch (Exception e){
            initFragment();
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