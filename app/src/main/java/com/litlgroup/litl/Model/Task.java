package com.litlgroup.litl.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrj148 on 8/16/16.
 */
public class Task {
    public Task()
    {

    }

    //private Date deadlineDate;
    private String deadlineDate;
    private Date createdAt;
    private Address address;
    private String description;
    private Type type;
    private State state;
    private String workImageURL;
    private String workVideoURL;
    private ArrayList<String> categories;
    private String title;
    private double price;
    private long id;
    private int favorPoints;
    private Bookmark bookmark;
    private User user;

//    public Date getDeadlineDate() {
//        return deadlineDate;
//    }
    public String getDeadlineDate() {
    return "September 7, 2016";
}

    public Date getCreatedAt() {
        return createdAt;
    }

    public Address getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }

    public State getState() {
        return state;
    }

    public String getWorkImageURL() {
        return workImageURL;
    }

    public String getWorkVideoURL() {
        return workVideoURL;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public long getId() {
        return id;
    }

    public int getFavorPoints() {
        return favorPoints;
    }

    public User getUser() {
        return user;
    }

    public enum Type {
        PROPOSAL,
        OFFER
    }
    public enum State {
        inBiddingProcess,
        successfullyAccepted,
        notAccepted,
        EXPIRED
    }


    public static ArrayList<Task> getFakeTaskDataProposals() {
        ArrayList<Task> taskList = new ArrayList<>();
        Task task1 = new Task();
        Address address1 = new Address();
        address1.setHouseNumber(123);
        address1.setCity("Detroit");
        address1.setStateAbbreviation("MI");
        address1.setStreet("Fake St.");
        address1.setZipcode(68198);

        task1.address = address1;
        task1.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        task1.type = Type.PROPOSAL;
        task1.state = State.inBiddingProcess;
        task1.workImageURL = "http://neighbourhoodhandyman.ca/wp-content/uploads/2015/05/yard-work.jpg";
        task1.title = "Yardwork";
        task1.price = 20.00;
        task1.favorPoints = 7;
        task1.user = User.getFakeUser();
        task1.bookmark = getBookmark(true);

        Task task2 = new Task();
        task2.address = address1;
        task2.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        task2.type = Type.PROPOSAL;
        task2.state = State.inBiddingProcess;
        task2.workImageURL = "http://irepairhvac.com/wp-content/uploads/2014/06/Plumbing-Services.jpg";
        task2.title = "Plumbing";
        task2.price = 35.00;
        task2.favorPoints = 10;
        task2.user = User.getFakeUser();
        task2.bookmark = getBookmark(false);

        Task task3 = new Task();
        task3.address = address1;
        task3.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        task3.type = Type.PROPOSAL;
        task3.state = State.inBiddingProcess;
        task3.workImageURL = "http://listdose.com/wp-content/uploads/2013/06/10..bmp";
        task3.title = "Parenting";
        task3.price = 135.00;
        task3.favorPoints = 1000;
        task3.user = User.getFakeUser();
        task3.bookmark = getBookmark(false);

        Task task4 = new Task();
        task4.address = address1;
        task4.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        task4.type = Type.PROPOSAL;
        task4.state = State.notAccepted;
        task4.workImageURL = "http://pictures.dealer.com/c/coulternissan/1967/9c3b5c55b085babe06347934ff4efd1cx.jpg";
        task4.title = "Oil Change";
        task4.price = 28.00;
        task4.favorPoints = 4;
        task4.user = User.getFakeUser();
        task4.bookmark = getBookmark(true);

        Task task5 = new Task();
        task5.address = address1;
        task5.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        task5.type = Type.PROPOSAL;
        task5.state = State.successfullyAccepted;
        task5.workImageURL = "http://www.dpccars.com/gallery/var/albums/Car-Wash-Fail/Car%20Wash%20Fail%20-%2030.jpg";
        task5.title = "Car Wash";
        task5.price = 5.00;
        task5.favorPoints = 1;
        task5.user = User.getFakeUser();
        task5.bookmark = getBookmark(true);

        taskList.add(task1);
        taskList.add(task2);
        taskList.add(task3);
        taskList.add(task4);
        taskList.add(task5);

        return  taskList;
    }

    public static ArrayList<Task> getFakeTaskDataOfferss() {
        ArrayList<Task> taskList = new ArrayList<>();
        Task task1 = new Task();
        Address address1 = new Address();
        address1.setHouseNumber(123);
        address1.setCity("Detroit");
        address1.setStateAbbreviation("MI");
        address1.setStreet("Fake St.");
        address1.setZipcode(68198);

        task1.address = address1;
        task1.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        task1.type = Type.OFFER;
        task1.state = State.inBiddingProcess;
        task1.workImageURL = "http://www.transformationsmassage.com/wp-content/uploads/2012/06/gardener.jpg";
        task1.title = "Yardwork";
        task1.price = 40.00;
        task1.favorPoints = 17;
        task1.user = User.getFakeUser();
        task1.bookmark = getBookmark(true);

        Task task2 = new Task();
        task2.address = address1;
        task2.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        task2.type = Type.OFFER;
        task2.state = State.inBiddingProcess;
        task2.workImageURL = "http://irepairhvac.com/wp-content/uploads/2014/06/Plumbing-Services.jpg";
        task2.title = "Plumbing";
        task2.price = 85.00;
        task2.favorPoints = 20;
        task2.user = User.getFakeUser();
        task2.bookmark = getBookmark(true);

        Task task3 = new Task();
        task3.address = address1;
        task3.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        task3.type = Type.OFFER;
        task3.state = State.EXPIRED;
        task3.workImageURL = "https://gergie.files.wordpress.com/2012/05/polls_scolding_0345_424529_poll_xlarge.jpeg?w=560";
        task3.title = "Parenting";
        task3.price = 255.00;
        task3.favorPoints = 3000;
        task3.user = User.getFakeUser();
        task3.bookmark = getBookmark(false);

        Task task4 = new Task();
        task4.address = address1;
        task4.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        task4.type = Type.PROPOSAL;
        task4.state = State.notAccepted;
        task4.workImageURL = "http://www.bls.gov/ooh/images/3491.jpg";
        task4.title = "Pick up perscription";
        task4.price = 48.00;
        task4.favorPoints = 4;
        task4.user = User.getFakeUser();
        task4.bookmark = getBookmark(false);

        Task task5 = new Task();
        task5.address = address1;
        task5.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
        task5.type = Type.PROPOSAL;
        task5.state = State.successfullyAccepted;
        task5.workImageURL = "http://cdn.skim.gs/image/upload/c_fill,dpr_1.0,w_940/v1456338269/msi/woman-cleaning-kitchen-horiz_nrzwt6.jpg";
        task5.title = "Clean Kitchen";
        task5.price = 15.00;
        task5.favorPoints = 2;
        task5.user = User.getFakeUser();
        task5.bookmark = getBookmark(true);

        taskList.add(task1);
        taskList.add(task2);
        taskList.add(task3);
        taskList.add(task4);
        taskList.add(task5);

        return  taskList;
    }

    private static Bookmark getBookmark(boolean exists) {
        Bookmark bookmark = new Bookmark();
        bookmark.setBookmarked(exists);
        return bookmark;
    }

    public static Task fromTaskObjectMap(Map<String, Object> taskObjectMap)
    {
        try
        {
            Task task = new Task();
            task.price = Float.parseFloat(taskObjectMap.get("price").toString().replace("$",""));
            task.title = taskObjectMap.get("title").toString();
            task.description = taskObjectMap.get("description").toString();
            return task;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;

    }


    public Task(String title, String description,
                String price, String deadlineDate, final String categoryValue )
    {
        this.title = title;
        this.description = description;
        if(price.contains("$"))
            price = price.replace("$", "");
        this.price = Float.parseFloat(price);
        this.deadlineDate = deadlineDate;
        this.categories = new ArrayList<>();
        this.categories.add(categoryValue);

    }



    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("price", String.valueOf(price));
        result.put("deadline_date", deadlineDate);
        result.put("address",address);
        result.put("title", title);
        result.put("description", description);
        result.put("user", user);
        return result;
    }


}


