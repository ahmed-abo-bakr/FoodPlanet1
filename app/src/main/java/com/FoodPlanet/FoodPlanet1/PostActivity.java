package com.FoodPlanet.FoodPlanet1;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.FoodPlanet.FoodPlanet1.data.Chef;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

public class PostActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 10;
    private static final String TAG = "POST AVTIVITY";

    Uri imgUri;
    ImageView postImage, ownerPostImage;
    TextView ownerPostName;
    EditText caption;
    Button postBtn;
    ImageButton delete;
    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference, ref;
    private String downloadUri;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);
        postImage = findViewById(R.id.pPost_imag);
        caption = findViewById(R.id.pPost_caption);
        ownerPostImage = findViewById(R.id.pPost_owner_pic);
        ownerPostName = findViewById(R.id.pPost_owner_name);
        postBtn = findViewById(R.id.post_btn);
        delete = findViewById(R.id.delete_pic);


        getProfileInfo();
        pickFromGallery();

        mStorageReference = FirebaseStorage.getInstance().getReference("Images");

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postImage.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }
        });


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                publishPost();
            }
        });
    }


    //add exetension to file uploaded
    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //show progrss dialog
    private void showProgressDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(R.layout.progress_dialog);
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadTask.cancel();
            }
        });
        dialogBuilder.setCancelable(false);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    //hide progrss dialog
    private void hideProgressDialog() {
        alertDialog.dismiss();
    }

    //pick image from gallery
    private void pickFromGallery() {

        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    //catch callback in intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    imgUri = data.getData();
                    //Picasso.get().load(selectedImage).transform(new CircleImage()).into(imageView);
                    Glide.with(this).load(imgUri).fitCenter().into(postImage);


            }
    }

    //get profile info
    private void getProfileInfo() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Chef chef = dataSnapshot.getValue(Chef.class);
                Uri uri = Uri.parse(chef.getChefPic());

                Glide.with(PostActivity.this).asBitmap().load(uri)
                        .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(R.dimen.reduis)))
                        .into(ownerPostImage);
                ownerPostName.setText(chef.getChefName());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(PostActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // upload post inf method
    private void publishPost() {
        if (imgUri != null) {
            String uriContent = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref = mStorageReference.child(System.currentTimeMillis() + "." + getExtension(imgUri));


            Bitmap bitmap = ((BitmapDrawable) postImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();


            uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.e("looooooooooooook", "uploaded :)");
                    Toast.makeText(PostActivity.this, "uploaded : ", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    // ...
                    Log.e("looooooooooooook", "" + exception.getMessage());
                    Toast.makeText(PostActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {

                        Toast.makeText(PostActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri result = task.getResult();
                        downloadUri = result.toString();
                        Toast.makeText(PostActivity.this, "" + downloadUri, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "" + downloadUri);
                        String chefId = FirebaseAuth.getInstance().getUid();
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("posts");
                        String postId = mDatabase.push().getKey();
                        String postCaption = caption.getText().toString();

                        HashMap<String, Object> postsMap = new HashMap<>();
                        postsMap.put("postId", postId);
                        postsMap.put("ownerId", chefId);
                        postsMap.put("caption", postCaption);
                        postsMap.put("photoUri", downloadUri);


                        mDatabase.child(postId).setValue(postsMap);

                        hideProgressDialog();
                        Toast.makeText(PostActivity.this, "registered successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PostActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(PostActivity.this, "registered successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}
