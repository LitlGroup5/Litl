package com.litlgroup.litl.network;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.litlgroup.litl.Model.User;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Hari on 8/21/2016.
 */
public class FireBaseService {



    public static ArrayList<User> getUser(final ArrayList<String> userIds)
    {
        try
        {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

            final ArrayList<User> users = new ArrayList<>();

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Map<String, Object> usersMap = (Map<String, Object>) dataSnapshot.getValue();
                    for (String id :
                            userIds) {

                        try
                        {
                            User user = dataSnapshot.child(id).getValue(User.class);
                            users.add(user);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("user fetch error", "There was an error when fetching Offers data");
                    //Toast.makeText(parent, "There was an error when fetching Offers data", Toast.LENGTH_SHORT).show();
                }
            });
            return users;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

}
