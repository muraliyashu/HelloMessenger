package com.muraliyashu.hellomessenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.muraliyashu.hellomessenger.R.id.showImage;

public class ShowImage extends AppCompatActivity {

    public ArrayList<String> messages;
    public ArrayList<String> names;
    public ArrayList<String> dates;
    public ArrayList<String> images;
    public ArrayList<String> id;
    Bundle extras;
    String myNumber, chatNumber, strID, position;
    private TabbedActivity tabObj;

    @Override
    public void onStart() {
        super.onStart();
        tabObj.updateStatus("1",myNumber,true);
    }
    @Override
    public void onStop() {
        super.onStop();
        tabObj.updateStatus("0",myNumber,true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        tabObj = new TabbedActivity();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

        extras = getIntent().getExtras();

        String imagePath = (String) extras.get("image");
        myNumber = (String) extras.get("myNumber");
        chatNumber = (String) extras.get("chatNumber");
        position = (String) extras.get("position");
        strID = (String) extras.get("strID");



        ImageView image = (ImageView) findViewById(showImage);
        Glide.with(ShowImage.this).load(imagePath).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(image);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deleteimage, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.deleteimage:


                AlertDialog.Builder builder = new AlertDialog.Builder(ShowImage.this);

                builder.setTitle("Delete Message?");
                builder.setMessage("Are you sure you want to delete?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            if(generalValues.checkingConnection(ShowImage.this))
                            {
                                DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(myNumber);
                                DatabaseReference rootChild = root.child(chatNumber);
                                DatabaseReference drMessage = rootChild.child(strID);
                                drMessage.removeValue();
                                chat_room room = new chat_room();

                                room.myDBRoot(myNumber);

                                messages = (ArrayList<String>) extras.get("messages");
                                names = (ArrayList<String>) extras.get("names");
                                dates = (ArrayList<String>) extras.get("dates");
                                images = (ArrayList<String>) extras.get("images");
                                id = (ArrayList<String>) extras.get("id");

                                int pos = Integer.valueOf(position);

                                messages.remove(pos);
                                names.remove(pos);
                                dates.remove(pos);
                                images.remove(pos);
                                id.remove(pos);

                                Intent intent = getIntent();
                                intent.putExtra("position", position);
                                intent.putExtra("strID", strID);
                                intent.putExtra("messages", messages);
                                intent.putExtra("names", names);
                                intent.putExtra("dates", dates);
                                intent.putExtra("images", images);
                                intent.putExtra("id", id);
                                setResult(2, intent);
                                Toast.makeText(ShowImage.this, "Image deleted", Toast.LENGTH_LONG).show();
                                ShowImage.this.finish();
                            }
                            else
                            {
                                Toast.makeText(ShowImage.this,"Please check your Internet Connection",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        catch (Exception e)
                        {
                            if(!generalValues.checkingConnection(ShowImage.this))
                            {
                                Toast.makeText(ShowImage.this,"Please check your Internet Connection",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
