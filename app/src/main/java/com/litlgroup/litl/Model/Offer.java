package com.litlgroup.litl.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrj148 on 8/16/16.
 */
public class Offer {
    public void setUserObject(User userObject) {
        this.userObject = userObject;
    }

    public void setTaskObject(Task taskObject) {
        this.taskObject = taskObject;
    }

    private User userObject;
    private Task taskObject;

    private String user;
    private String task;
    private float price;
    private Date expiration;
    private String created_at;

    public User getUserObject() {

        return userObject;
    }

    public Task getTaskObject() {
        return taskObject;
    }

    public String getUser() {
        return user;
    }

    public String getTask() {
        return task;
    }

    public float getPrice() {
        return price;
    }

    public String getPriceFormatted() {
        return String.format( "$%.2f", price);
    }

    public Date getExpiration() {
        return expiration;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Offer() {
    }

    public Offer(float price, String taskId, String userId)
    {
        this.price = price;
        this.user = userId;
        this.task = taskId;
        this.created_at = ServerValue.TIMESTAMP.toString();
    }

    public static ArrayList<Offer> fromDataSnapshot(DataSnapshot dataSnapshot) {
        try {

            ArrayList<Offer> offers = new ArrayList<>();

            for (DataSnapshot offerSnapShot :
                    dataSnapshot.getChildren()) {
                try {
                    Map<String, Object> dataMap = (Map<String, Object>) offerSnapShot.getValue();

                    Offer offer = new Offer();

//                    offer.task = (((Map<String, Object>)dataMap.get("task")).get("task_id")).toString();
//                    offer.user = (((Map<String, Object>)dataMap.get("user")).get("user_id")).toString();

                    if(dataMap.get("user") != null) {
                        offer.user = dataMap.get("user").toString();
                    }

                    if(dataMap.get("task") != null) {
                        offer.task = dataMap.get("task").toString();
                    }

                    offer.price = Float.parseFloat(dataMap.get("price").toString().replace("$", ""));
                    offer.created_at = dataMap.get("created_at").toString();
                    offers.add(offer);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            return offers;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("price", String.valueOf(price));
        result.put("created_at", created_at);
        result.put("user", user);
        result.put("task", task);
        return result;
    }

}
