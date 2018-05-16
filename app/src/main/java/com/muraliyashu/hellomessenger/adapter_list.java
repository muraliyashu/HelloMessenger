package com.muraliyashu.hellomessenger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import static com.muraliyashu.hellomessenger.R.id.first;

public class adapter_list extends BaseAdapter
{
	public ArrayList<String> messages= new ArrayList<String>();
	public ArrayList<String> names= new ArrayList<String>();
	public ArrayList<String> dates= new ArrayList<String>();
    public ArrayList<String> images= new ArrayList<String>();
	public ArrayList<String> id= new ArrayList<String>();
	public ArrayList<String> messageSeenArray= new ArrayList<String>();
	public Activity context;
	private StorageReference mStorageRef;
	int currentPosition;
	LinearLayout fulllayout;
	String myNumber, chatNumber;
	int selectedHour;
	public LayoutInflater inflater;
	public adapter_list(Activity context, ArrayList<String> messages, ArrayList<String> names,ArrayList<String> dates,
    ArrayList<String> images, ArrayList<String> id, String myNumber, String chatNumber, ArrayList<String> messageSeenArray) {
		super();
		this.context = context;
		this.messages = messages;
		this.names = names;
		this.dates = dates;
        this.images = images;
		this.id = id;
		this.myNumber = myNumber;
		this.chatNumber = chatNumber;
		this.messageSeenArray = messageSeenArray;
		mStorageRef = FirebaseStorage.getInstance().getReference();

		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public int getCount()
	{
		return messages.size();
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
		TextView first;
		TextView second;
		TextView firstDate;
		TextView secondDate, secondDateSeen;
		ImageView firstImg, secondImg, down1, down2, sentID, recievedID;
		RelativeLayout one, two;
		ProgressBar progressBar1, progressBar2;
		LinearLayout fulllayout;
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
				convertView = inflater.inflate(R.layout.latest_adapter_list, null);
				holder.first = (TextView) convertView.findViewById(first);
				holder.second = (TextView) convertView.findViewById(R.id.second);
				holder.firstDate = (TextView) convertView.findViewById(R.id.firstDate);
				holder.secondDate = (TextView) convertView.findViewById(R.id.secondDate);
				holder.secondDateSeen = (TextView) convertView.findViewById(R.id.secondDateSeen);
				holder.firstImg = (ImageView) convertView.findViewById(R.id.firstImage);
				holder.secondImg = (ImageView) convertView.findViewById(R.id.secondImage);
				holder.one = (RelativeLayout) convertView.findViewById(R.id.id1);
				holder.two = (RelativeLayout) convertView.findViewById(R.id.id2);
				holder.down1 = (ImageView) convertView.findViewById(R.id.downloadImage);
				holder.down2 = (ImageView) convertView.findViewById(R.id.downloadImage1);
				holder.progressBar1 = (ProgressBar) convertView.findViewById(R.id.progress1);
				holder.progressBar2 = (ProgressBar) convertView.findViewById(R.id.progress2);
				holder.sentID = (ImageView) convertView.findViewById(R.id.sentID);
				holder.recievedID = (ImageView) convertView.findViewById(R.id.recievedID);
				holder.fulllayout = (LinearLayout) convertView.findViewById(R.id.fulllayout);
				convertView.setTag(holder);
			}
			else
				holder=(ViewHolder)convertView.getTag();


			holder.first.setVisibility(View.VISIBLE);
			holder.second.setVisibility(View.VISIBLE);
			holder.firstDate.setVisibility(View.VISIBLE);
			holder.secondDate.setVisibility(View.VISIBLE);
			holder.secondDateSeen.setVisibility(View.VISIBLE);
			holder.sentID.setVisibility(View.VISIBLE);
			holder.recievedID.setVisibility(View.VISIBLE);
			holder.firstImg.setVisibility(View.VISIBLE);
			holder.secondImg.setVisibility(View.VISIBLE);
			holder.one.setVisibility(View.VISIBLE);
			holder.two.setVisibility(View.VISIBLE);
			holder.down1.setVisibility(View.GONE);
			holder.down2.setVisibility(View.GONE);
			holder.fulllayout.setVisibility(View.VISIBLE);
			if(names.get(position).equals("me"))
			{
				if(messageSeenArray.get(position).toString().equals("sent"))
				{
					holder.secondDateSeen.setVisibility(View.GONE);
					holder.recievedID.setVisibility(View.GONE);
				}
				else
				{
					holder.secondDateSeen.setText(messageSeenArray.get(position));
				}

                if(messages.get(position).toString().equals("0"))
                {
					if(!images.get(position).contains("png"))
					{
						//holder.secondImg.setImageResource(R.drawable.download);
						//Glide.with(context).load(R.drawable.download).into(holder.secondImg);
						holder.secondImg.setVisibility(View.GONE);
						holder.down2.setVisibility(View.VISIBLE);
						Glide.with(context).load(R.drawable.download).into(holder.down2);
						holder.progressBar2.setVisibility(View.GONE);
					}
					else
					{
						//Bitmap myBitmap = BitmapFactory.decodeFile(images.get(position));
						//holder.secondImg.setImageBitmap(myBitmap);
						Glide.with(context).load(images.get(position)).listener(new RequestListener<Drawable>() {
							@Override
							public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
								holder.progressBar2.setVisibility(View.GONE);
								return false;
							}

							@Override
							public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
								holder.progressBar2.setVisibility(View.GONE);
								return false;
							}
						}).into(holder.secondImg);
					}
					holder.second.setVisibility(View.GONE);
					holder.fulllayout.setVisibility(View.GONE);
					holder.firstImg.setVisibility(View.GONE);
					holder.one.setVisibility(View.GONE);
                }
                else
                {
					holder.secondImg.setVisibility(View.GONE);
					holder.firstImg.setVisibility(View.GONE);
					holder.one.setVisibility(View.GONE);
					holder.two.setVisibility(View.GONE);
					holder.fulllayout.setBackgroundResource(R.drawable.message2);
                    holder.second.setText(messages.get(position));
                    holder.second.setBackgroundResource(R.drawable.message2);
                }
				holder.secondDate.setText(dates.get(position));
				holder.first.setVisibility(View.GONE);
				holder.firstDate.setVisibility(View.GONE);
			}
			else
			{
				holder.secondDateSeen.setVisibility(View.GONE);
				holder.recievedID.setVisibility(View.GONE);

                if(messages.get(position).toString().equals("0"))
                {
					if(!images.get(position).contains("png"))
					{

						//holder.firstImg.setImageResource(R.drawable.download);
						//Glide.with(context).load(R.drawable.download).into(holder.firstImg);
						holder.firstImg.setVisibility(View.GONE);
						holder.down1.setVisibility(View.VISIBLE);
						Glide.with(context).load(R.drawable.download).into(holder.down1);
						holder.progressBar1.setVisibility(View.GONE);
					}
					else
					{
						//Bitmap myBitmap = BitmapFactory.decodeFile(images.get(position));
						//holder.firstImg.setImageBitmap(myBitmap);F9F2CB
						Glide.with(context).load(images.get(position)).listener(new RequestListener<Drawable>() {
							@Override
							public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
								holder.progressBar1.setVisibility(View.GONE);
								return false;
							}

							@Override
							public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
								holder.progressBar1.setVisibility(View.GONE);
								return false;
							}
						}).into(holder.firstImg);
					}
					holder.first.setVisibility(View.GONE);
					holder.secondImg.setVisibility(View.GONE);
					holder.two.setVisibility(View.GONE);
                }
                else
                {
					holder.secondImg.setVisibility(View.GONE);
					holder.firstImg.setVisibility(View.GONE);
					holder.one.setVisibility(View.GONE);
					holder.two.setVisibility(View.GONE);
                    holder.first.setText(messages.get(position));
                    holder.first.setBackgroundResource(R.drawable.message1);
                }
				holder.second.setVisibility(View.GONE);
				holder.secondDate.setVisibility(View.GONE);
				holder.fulllayout.setVisibility(View.GONE);
				holder.sentID.setVisibility(View.GONE);
				holder.firstDate.setText(dates.get(position));
			}
			holder.down2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					final ProgressDialog dialog = new ProgressDialog(context);
					dialog.setTitle("Downloading image");
                    dialog.setCanceledOnTouchOutside(false);
					dialog.show();
					StorageReference islandRef = mStorageRef.child("images/"+images.get(position)+".png");
					MainActivity obj = new MainActivity();
					obj.folder();
                            final File file = new File(Environment.getExternalStorageDirectory(), "/ChatApp/"+images.get(position)+".png");

                            islandRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Local temp file has been created
									images.add(position,file.toString());
									notifyDataSetChanged();
									dialog.dismiss();
									Toast.makeText(context,"Image downloaded successfully",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception)
                                {
                                    dialog.dismiss();
                                    Toast.makeText(context,exception.getMessage(),Toast.LENGTH_LONG).show();
                                    // Handle any errors
                                }
                            })
							.addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
								@Override
								public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                    dialog.setMessage("Downloading "+(int)progress+"%");
								}
							});
				}
			});
			holder.down1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					final ProgressDialog dialog = new ProgressDialog(context);
					dialog.setTitle("Downloading image");
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
					StorageReference islandRef = mStorageRef.child("images/"+images.get(position)+".png");
					MainActivity obj = new MainActivity();
					obj.folder();
					final File file = new File(Environment.getExternalStorageDirectory(), "/ChatApp/"+images.get(position)+".png");

					islandRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
						@Override
						public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
							// Local temp file has been created
							images.add(position,file.toString());
							notifyDataSetChanged();
							dialog.dismiss();
							Toast.makeText(context,"Image downloaded successfully",Toast.LENGTH_LONG).show();
						}
					}).addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception exception)
						{
							dialog.dismiss();
							Toast.makeText(context,exception.getMessage(),Toast.LENGTH_LONG).show();
							// Handle any errors
						}
					})
							.addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
								@Override
								public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
									double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
									dialog.setMessage("Downloading "+(int)progress+"%");
								}
							});
				}
			});
			holder.firstImg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onClcikOfImage(position);
				}
			});

			holder.secondImg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onClcikOfImage(position);
				}
			});
            holder.firstImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
					//onLongClcikOfImage(position);
                    return false;
                }
            });
			holder.secondImg.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					//onLongClcikOfImage(position);
					return false;
				}
			});
		}
		catch(Exception e)
		{
			String getMessage = e.getMessage().toString();
		}
		return convertView;
	}
	private void onLongClcikOfImage(int pos)
	{
		final int position = pos;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle("Delete Message?");
		builder.setMessage("Are you sure you want to delete?");
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				chat_room room = new chat_room();
				room.deleteMessage(id.get(position),position);
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
	}
	private void onClcikOfImage(int pos)
	{
		final int position = pos;
		Intent showImage = new Intent(context,ShowImage.class);
		showImage.putExtra("image",images.get(position));
		showImage.putExtra("myNumber",myNumber);
		showImage.putExtra("chatNumber",chatNumber);
		showImage.putExtra("strID",id.get(position));
		showImage.putExtra("position",String.valueOf(position));

		showImage.putStringArrayListExtra("messages",messages);
		showImage.putStringArrayListExtra("names",names);
		showImage.putStringArrayListExtra("dates",dates);
		showImage.putStringArrayListExtra("images",images);
		showImage.putStringArrayListExtra("id",id);
		context.startActivityForResult(showImage, 3);
	}
}
