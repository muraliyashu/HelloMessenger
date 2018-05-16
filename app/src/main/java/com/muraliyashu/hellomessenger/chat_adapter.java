package com.muraliyashu.hellomessenger;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class chat_adapter extends BaseAdapter
{
    public ArrayList<String> names= new ArrayList<String>();
    public ArrayList<String> imagePaths= new ArrayList<String>();
    public Activity context;
    int currentPosition;
    public LayoutInflater inflater;
    public chat_adapter(Activity context, ArrayList<String> names, ArrayList<String> imagePaths) {
        super();
        this.context = context;
        this.names = names;
        this.imagePaths = imagePaths;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public int getCount()
    {
        return names.size();
    }

    public Object getItem(int position)
    {
        return position;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public static class ViewHolder
    {
        TextView onlineName;
        ImageView profilepic;
        ProgressBar rotate;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        try
        {
            final ViewHolder holder;

            if(convertView==null)
            {
                currentPosition=position;
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.chat_adapter, null);
                holder.onlineName = (TextView) convertView.findViewById(R.id.onlineName);
                holder.profilepic = (ImageView) convertView.findViewById(R.id.profilepic);
                holder.rotate = (ProgressBar) convertView.findViewById(R.id.rotate);
                holder.rotate.setVisibility(View.GONE);
                convertView.setTag(holder);
            }
            else
            holder=(ViewHolder)convertView.getTag();
            holder.onlineName.setText(names.get(position));

            Glide.with(context).load(imagePaths.get(position)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.rotate.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.rotate.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.profilepic);

            holder.profilepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                }
            });


        }
        catch(Exception e)
        {
            String getMessage = e.getMessage().toString();
        }
        return convertView;
    }
}
