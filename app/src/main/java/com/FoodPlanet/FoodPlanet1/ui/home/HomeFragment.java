package com.FoodPlanet.FoodPlanet1.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.FoodPlanet.FoodPlanet1.PostActivity;
import com.FoodPlanet.FoodPlanet1.R;
import com.FoodPlanet.FoodPlanet1.data.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private List<Post> postsList;
    private List<String> followingList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = view.findViewById(R.id.home_recycler_view);
        setRecyclerView();
        getFollowingList();

        return view;
    }


    // set recycler view method
    private void setRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        postsList = new ArrayList<>();
        mPostAdapter = new PostAdapter(postsList, getContext());
        mRecyclerView.setAdapter(mPostAdapter);

    }

    // get post method
    private void getPosts() {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("posts");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postsList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    for (String id : followingList) {
                        if (post.getOwnerId().equals(id)) {
                            Log.e("post id ", "" + post.getPostId() + "\n post img" + post.getPhototUri() + "\n post caption"
                                    + post.getCaption()
                                    + "\n post owner " + post.getOwnerId());
                            Toast.makeText(getContext(), "post id " + post.getCaption() + "\n" + post.getPhototUri(), Toast.LENGTH_LONG).show();
                            postsList.add(post);
                        }
                    }
                }
                mPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("look", "" + databaseError.getMessage());
                Toast.makeText(getContext(), " " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // get following chefs and call getPosts in ...
    private void getFollowingList() {

        followingList = new ArrayList<>();
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("current id ", "" + id);
        Toast.makeText(getContext(), "current id " + id, Toast.LENGTH_SHORT).show();

        DatabaseReference Reference = FirebaseDatabase.getInstance().getReference("follow")
                .child(id)
                .child("following");
        Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    followingList.add(ds.getKey());
                    Log.e("following id ", "" + ds.getKey());
                    Toast.makeText(getContext(), "following id " + ds.getKey(), Toast.LENGTH_SHORT).show();

                }
                getPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("", "" + databaseError.getMessage());
                Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton button = view.findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), PostActivity.class));
            }
        });
    }
}