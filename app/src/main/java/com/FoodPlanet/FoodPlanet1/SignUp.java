package com.FoodPlanet.FoodPlanet1;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUp extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    private static final String TAG = "LOOOK HERE ";
    public FirebaseAuth mAuth;
    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    ImageView imageView;
    EditText mName, mEmail;
    String name, email;
    Uri imgUri;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference, ref;
    private String downloadUri;
    Button signup, backToLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        imageView = findViewById(R.id.signup_image);
        mName = findViewById(R.id.txtUserName);
        signup = findViewById(R.id.signup);
        backToLogin = findViewById(R.id.signup_to_login);
        mStorageReference = FirebaseStorage.getInstance().getReference("Images");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Images");
        //pick up an imageProfile
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });
        // sign up case
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //uploadProfilePic();
                showProgressDialog();
                registerNewUser();
            }
        });

        // handle back to login
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //add exetension to file uploaded
    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // upload photo method
    private void uploadProfileData() {
        if (imgUri != null) {
            String uriContent = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref = mStorageReference.child(uriContent + "." + getExtension(imgUri));
            UploadTask uploadTask = ref.putFile(imgUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.e("looooooooooooook", "uploaded :)");
                    Toast.makeText(SignUp.this, "uploaded : ", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Log.e("looooooooooooook", "" + exception.getMessage());
                            Toast.makeText(SignUp.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
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
                        Toast.makeText(SignUp.this, "" + downloadUri, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "" + downloadUri);

                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        name = mName.getText().toString().trim();
                        Chef chef = new Chef(id, name, email, downloadUri);
                        mDatabase.child(id).setValue(chef);
                        Log.e(TAG, "name : " + name + "email : " + email + "uri " + downloadUri);
                        hideProgressDialog();
                        Toast.makeText(SignUp.this, "registered successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(SignUp.this, "registered successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(SignUp.this, "ca not get uri", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            hideProgressDialog();
            Toast.makeText(this, "no photo selected", Toast.LENGTH_SHORT).show();
        }
    }

    /* private void uploadProfilePic() {
         if (imgUri !=null){
             String uriContent=FirebaseAuth.getInstance().getCurrentUser().getUid();
             ref = mStorageReference.child(uriContent+"."+getExtension(imgUri));
             ref.putFile(imgUri)
                     .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             // Get a URL to the uploaded content
                             //Uri downloadUrl = taskSnapshot.getDownloadUrl();


                             Log.e("looooooooooooook","uploaded" );
                             Toast.makeText(SignUp.this, "uploaded : ", Toast.LENGTH_SHORT).show();
                         }
                     })
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception exception) {
                             // Handle unsuccessful uploads
                             // ...
                             Log.e("looooooooooooook",""+exception.getMessage() );
                             Toast.makeText(SignUp.this, exception.getMessage() , Toast.LENGTH_SHORT).show();
                         }
                     });
         }else {
             Toast.makeText(this, "no photo selected", Toast.LENGTH_SHORT).show();
         }
     }*/
    //upload profile data
    /*private void uploadProfileData(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        String userId = mDatabase.push().getKey();
        name=mName.getText().toString().trim();
        Chef chef=new Chef(name,email,downloadUri);
        Log.e(TAG,"name : "+name +"email : "+email +"uri "+downloadUri);
        mDatabase.child(userId).setValue(chef);

    }*/
    //handle regiser case
    public void registerNewUser() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.txtemail);
        email = mEmail.getText().toString();

        EditText pass = findViewById(R.id.pass);
        String person_pass = pass.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, person_pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    uploadProfileData();
                } else {
                    hideProgressDialog();
                    Toast.makeText(SignUp.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //pick image from gallery
    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    // show Progress Dialog
    private void showProgressDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(R.layout.progress_dialog);
        dialogBuilder.setMessage("uploading profile info ...");
        dialogBuilder.setCancelable(false);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    //hide progress dialog
    private void hideProgressDialog() {
        alertDialog.dismiss();
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
                    Glide.with(this).load(imgUri)
                            .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(R.dimen.reduis)))
                            .into(imageView);
            }
    }

}
