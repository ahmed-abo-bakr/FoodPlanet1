package com.FoodPlanet.FoodPlanet1.ui.notification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.FoodPlanet.FoodPlanet1.R;
import com.FoodPlanet.FoodPlanet1.data.Chef;
import com.FoodPlanet.FoodPlanet1.data.Notification;
import com.FoodPlanet.FoodPlanet1.data.Post;
import com.FoodPlanet.FoodPlanet1.ui.ProfileActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context context, List<Notification> notification) {
        mContext = context;
        mNotification = notification;
    }

    @NonNull
    @Override
    public NotificationAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        final Notification notification = mNotification.get(position);

        holder.text.setText(notification.getText());

        getUserInfo(holder.image_profile, holder.username, notification.getUserid());

        if (notification.isIspost()) {
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image, notification.getPostid());
        } else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isIspost()) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostid());
                    editor.apply();
/*
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PostDetailFragment()).commit();*/
                } else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserid());
                    editor.apply();

                    mContext.startActivity(new Intent(mContext, ProfileActivity.class));
                }
            }
        });


    }

    //
    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(publisherid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chef user = dataSnapshot.getValue(Chef.class);
                Glide.with(mContext).load(user.getChefPic())
                        .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(R.dimen.reduis)))
                        .into(imageView);
                username.setText(user.getChefName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPostImage(final ImageView post_image, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(postid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPhototUri()).into(post_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image;
        public TextView username, text;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }
}

