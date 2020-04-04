package com.FoodPlanet.FoodPlanet1.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.FoodPlanet.FoodPlanet1.CommentActivity;
import com.FoodPlanet.FoodPlanet1.R;
import com.FoodPlanet.FoodPlanet1.data.Chef;
import com.FoodPlanet.FoodPlanet1.data.Post;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.ContentValues.TAG;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private DatabaseReference mDatabaseReference;
    private List<Post> mPostsList;
    private Context mContext;

    //constructor
    public PostAdapter(List<Post> postsList, Context mContext) {
        this.mPostsList = postsList;
        this.mContext = mContext;
    }

    // attach viewItem of recycler view
    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View postItem = LayoutInflater.from(mContext).inflate(R.layout.post_view, parent, false);
        return new PostAdapter.PostViewHolder(postItem);
    }

    // bind recycler view
    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {

        //get user data (name, img)
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPostsList.get(position);
        // post img and caption
        Uri uri = Uri.parse(post.getPhototUri());
        Glide.with(mContext).load(post.getPhototUri()).into(holder.postImage);
        if (post.getCaption().equals("")) {

            holder.caption.setVisibility(View.GONE);
        } else {
            holder.caption.setVisibility(View.VISIBLE);
            holder.caption.setText(post.getCaption());
        }

        publisherInfo(post.getOwnerId(), holder.ownerPostImage, holder.ownerPostName);
        checkLikeState(holder.likeImage, post.getPostId());
        getLikesNum(holder.likes, post.getPostId());
        getComments(post.getPostId(), holder.comments);

        holder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.likeImage.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid())
                            .setValue(true);
                    holder.likeImage.setTag("liked");

                } else {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid())
                            .removeValue();
                    holder.likeImage.setTag("like");

                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("ownerId", post.getOwnerId());
                mContext.startActivity(intent);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("ownerId", post.getOwnerId());
                mContext.startActivity(intent);
            }
        });

    }

    private void getComments(String postId, final TextView comments) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText(dataSnapshot.getChildrenCount() + "Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get post owner name and photo
    private void publisherInfo(final String id, final ImageView userImg, final TextView userName) {

        //get posts for this user
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Chef chef = dataSnapshot.getValue(Chef.class);
                Uri uri = Uri.parse(chef.getChefPic());

                Glide.with(mContext).load(uri)
                        .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(R.dimen.reduis)))
                        .into(userImg);
                userName.setText(chef.getChefName());

                Log.e(TAG, "from adapter " + chef.getChefPic() + chef.getChefName());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(mContext, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // check like state
    private void checkLikeState(final ImageView likeView, String postId) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("likes");
        reference.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getUid()).exists()) {

                    likeView.setImageResource(R.drawable.ic_whatshot_foshia_24dp);
                    likeView.setTag("liked");
                } else {
                    likeView.setImageResource(R.drawable.ic_whatshot__24dp);
                    likeView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // count likes method
    private void getLikesNum(final TextView likesNum, String postId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("likes");
        reference.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likesNum.setText(dataSnapshot.getChildrenCount() + "Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get size of posts list
    @Override
    public int getItemCount() {
        return mPostsList.size();
    }

    //inner view holder class
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage, ownerPostImage, likeImage, comment;
        TextView ownerPostName, caption, likes, comments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ownerPostImage = itemView.findViewById(R.id.post_owner_pic);
            ownerPostName = itemView.findViewById(R.id.post_owner_name);
            caption = itemView.findViewById(R.id.post_caption);
            postImage = itemView.findViewById(R.id.post_imag);
            likeImage = itemView.findViewById(R.id.like);
            likes = itemView.findViewById(R.id.likes);
            comment = itemView.findViewById(R.id.comment);
            comments = itemView.findViewById(R.id.comments);
        }
    }


}
