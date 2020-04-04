package com.FoodPlanet.FoodPlanet1;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.FoodPlanet.FoodPlanet1.data.Chef;
import com.FoodPlanet.FoodPlanet1.data.Comment;
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
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    EditText addcomment;
    ImageView image_profile;
    TextView post;
    String postid;
    String publisherid;
    FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_ativity);

        Button button = findViewById(R.id.comment_to_home);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommentActivity.this, MainActivity.class));
            }
        });

        Intent intent = getIntent();
        postid = intent.getStringExtra("postId");
        publisherid = intent.getStringExtra("ownerId");

        recyclerView = findViewById(R.id.commment_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postid);
        recyclerView.setAdapter(commentAdapter);

        post = findViewById(R.id.post);
        addcomment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addcomment.getText().toString().equals("")) {
                    Toast.makeText(CommentActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        getImage();
        readComments();

    }

    private void addComment() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments");

        String commentid = reference.child(postid).push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentid", commentid);

        reference.child(postid).child(commentid).setValue(hashMap);
        addNotification();
        addcomment.setText("");

    }

    private void addNotification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "commented: " + addcomment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }

    private void getImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chef user = dataSnapshot.getValue(Chef.class);
                Glide.with(CommentActivity.this).load(user.getChefPic())
                        .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(R.dimen.reduis)))
                        .into(image_profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments");

        reference.child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
