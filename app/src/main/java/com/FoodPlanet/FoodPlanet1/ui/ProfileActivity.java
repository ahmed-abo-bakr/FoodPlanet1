package com.FoodPlanet.FoodPlanet1.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.FoodPlanet.FoodPlanet1.MainActivity;
import com.FoodPlanet.FoodPlanet1.ui.home.PostAdapter;
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

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private final String TAG = "PROFILE ACIVITY";
    ImageView profilePic;
    TextView profileName, profileEmail;
    String profileId;
    SharedPreferences preferences;
    Button editProfile;
    TextView postsNum;
    ArrayList<Post> postsList;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser currentUser;
    private PostAdapter mAdapter;
    private TextView followers, following;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        editProfile = findViewById(R.id.edit_profile);
        preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        profileId = preferences.getString("id", null);


        //.........................
        postsList = new ArrayList<Post>();

        setRecyclerView();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        getProfileInfo(currentUser.getUid());
        getPosts(currentUser.getUid());

        postsNum = findViewById(R.id.posts_number);
        followers = findViewById(R.id.followers_number);
        following = findViewById(R.id.following_number);
        assert currentUser != null;
        if (currentUser.getUid().equals(profileId)) {
            // current user profile
            getProfileInfo(currentUser.getUid());
            getFollowers(currentUser.getUid());
            getPosts(currentUser.getUid());
        } else {

            if (profileId == null) {
                getProfileInfo(currentUser.getUid());
                getFollowers(currentUser.getUid());
                getPosts(currentUser.getUid());
            } else {
                getProfileInfo(profileId);
                getFollowers(profileId);
                getPosts(profileId);
                checkFollowState();
                preferences.edit().clear().apply();
            }
        }


        Button backButton = findViewById(R.id.profile_to_home);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().clear().apply();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    // get posts
    private void getPosts(final String profileId) {

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("posts");

        //Query query=mDatabaseReference.orderByChild("caption");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Post post = ds.getValue(Post.class);
                    if (post.getOwnerId().equals(profileId)) {
                        postsList.add(post);
                    }
                }
                postsNum.setText(String.valueOf(postsList.size()));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // get followers and following
    private void getFollowers(String profileId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("follow").child(profileId).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("follow").child(profileId).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //set posts recycler view
    private void setRecyclerView() {

        RecyclerView mRecyclerView = findViewById(R.id.profile_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new PostAdapter(postsList, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    //check follow state
    private void checkFollowState() {

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("follow").child(currentUser.getUid())
                .child("following");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // user didnot follow before, follow now
                if (dataSnapshot.child(profileId).exists()) {
                    editProfile.setText("Following");
                    editProfile.setTextColor(getResources().getColor(R.color.whit));
                    editProfile.setBackground(getResources().getDrawable(R.drawable.btn_foushia));
                } else {
                    editProfile.setText("Follow");
                    editProfile.setTextColor(getResources().getColor(R.color.colorAccent));
                    editProfile.setBackground(getResources().getDrawable(R.drawable.btn_border_foshia));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    //get profile info
    private void getProfileInfo(String userId) {


        profilePic = findViewById(R.id.user_image);
        profileEmail = findViewById(R.id.user_email_header);
        profileName = findViewById(R.id.user_name);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Chef chef = dataSnapshot.getValue(Chef.class);
                Uri uri = Uri.parse(chef.getChefPic());

                Glide.with(ProfileActivity.this).asBitmap().load(uri)
                        .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(R.dimen.reduis)))
                        .into(profilePic);
                profileEmail.setText(chef.getChefEmail());
                profileName.setText(chef.getChefName());
                Log.d(TAG, "User name: " + chef.getChefName() + ", email " + chef.getChefEmail());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(ProfileActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        preferences.edit().clear().apply();

    }
}
