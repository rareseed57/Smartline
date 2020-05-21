package com.smartline.smartline;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private LinkedList<String> mDataset;
    private LinkedList<String> mDataset2;
    private Context mContext;
    private int mcurrentIndex;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView textView;
        private TextView textView2;
        private ImageView imageCheck;
        private ProgressBar progressBar;
        private ImageView imageAdmin;

        public MyViewHolder(View v)
        {
            super(v);
            textView2 = v.findViewById(R.id.textView2);
            textView = v.findViewById(R.id.textView);
            progressBar = v.findViewById(R.id.progressBar2);
            imageCheck = v.findViewById(R.id.imageCheck);
            imageAdmin = v.findViewById(R.id.imageAdmin);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, LinkedList<String> myDataset,LinkedList<String> myDataset2,int currentIndex)
    {
        mContext = context;
        mDataset = myDataset;
        mDataset2 = myDataset2;
        mcurrentIndex = currentIndex;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset.get(position));
        holder.textView2.setText(mDataset2.get(position));

            if (position == mcurrentIndex && position!=0)
            {
                (holder.progressBar).setVisibility(View.VISIBLE);
                holder.textView.setTextColor(Color.parseColor("#FFFF5722"));
            }

        if ((position < mcurrentIndex  || mcurrentIndex==-1) && position!=0)
        {
            (holder.imageCheck).setVisibility(View.VISIBLE);
            holder.imageCheck.setAlpha((float) 0.3);
            //holder.textView.setTextColor(Color.parseColor("#FFFF5722"));
        }
            if(position==0)
            {
                holder.imageAdmin.setVisibility(View.VISIBLE);

                //holder.textView.setTypeface(holder.textView.getTypeface(), Typeface.BOLD);
                //holder.textView2.setTypeface(holder.textView2.getTypeface(), Typeface.BOLD);
            }
            else holder.imageAdmin.setVisibility(View.GONE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}