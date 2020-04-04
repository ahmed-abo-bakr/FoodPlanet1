package com.FoodPlanet.FoodPlanet1.ui.seach;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.FoodPlanet.FoodPlanet1.data.Chef;
import com.FoodPlanet.FoodPlanet1.R;
import com.FoodPlanet.FoodPlanet1.ui.ProfileActivity;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private ArrayList<Chef> mSearchArrayList;
    private Chef chef;
    private Context mContext;
    FirebaseUser currentUser;

    // search adapter construcor
    public SearchAdapter(ArrayList<Chef> mSearchArrayList, Context context) {
        this.mSearchArrayList = mSearchArrayList;
        mContext = context;
    }

    // inner view holder class
    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mName, mEmail;
        Button mFolllow;

        // view holder constructor
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.search_pic);
            mName = itemView.findViewById(R.id.search_name);
            mEmail = itemView.findViewById(R.id.search_email);
            mFolllow = itemView.findViewById(R.id.search_follow);
        }
    }

    // oncreate view holder : set view holder view
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View searchItem = LayoutInflater.from(mContext).inflate(R.layout.search_card_view, parent, false);
        SearchViewHolder searchViewHolder = new SearchViewHolder(searchItem);
        return searchViewHolder;
    }

    // publish view
    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder holder, int position) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chef = mSearchArrayList.get(position);
        //check following state
        checkFollowingstate(chef.chefId, holder.mFolllow);
        holder.mFolllow.setVisibility(View.VISIBLE);
        // if current user show in search result
        if (chef.getChefId().equals(currentUser.getUid())) {
            holder.mFolllow.setVisibility(View.GONE);
        }

        Uri uri = Uri.parse(chef.getChefPic());
        Glide.with(mContext).asBitmap().load(uri)
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(R.dimen.reduis)))
                .into(holder.mImageView);
        holder.mName.setText(chef.getChefName());
        holder.mEmail.setText(chef.getChefName());


        //handle on search Item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, " this is required Id : "+chef.getChefId(), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("id", chef.getChefId());
                editor.apply();

                mContext.startActivity(new Intent(mContext, ProfileActivity.class));
            }
        });

        holder.mFolllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mFolllow.getText().toString().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(currentUser.getUid())
                            .child("following").child(chef.getChefId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("follow").child(chef.getChefId())
                            .child("followers").child(currentUser.getUid()).setValue(true);
                } else {
                    holder.mFolllow.setText("Follow");
                    holder.mFolllow.setBackground(mContext.getResources().getDrawable(R.drawable.btn_border_foshia));
                    FirebaseDatabase.getInstance().getReference().child("follow").child(currentUser.getUid())
                            .child("following").child(chef.getChefId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow").child(chef.getChefId())
                            .child("followers").child(currentUser.getUid()).removeValue();

                }
            }
        });

    }

    private void checkFollowingstate(final String id, final Button button) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow").child(currentUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // user didnot follow before, follow now
                if (dataSnapshot.child(id).exists()) {
                    button.setText("Following");
                    button.setTextColor(mContext.getResources().getColor(R.color.whit));
                    button.setBackground(mContext.getResources().getDrawable(R.drawable.btn_foushia));
                } else {
                    button.setText("Follow");
                    button.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                    button.setBackground(mContext.getResources().getDrawable(R.drawable.btn_border_foshia));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return mSearchArrayList.size();
    }

}
