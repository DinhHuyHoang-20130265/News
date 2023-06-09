package com.example.news.data;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.news.models.Item;
import com.example.news.models.News;
import com.example.news.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FirebaseData {
    private static FirebaseDatabase database;
    private static DatabaseReference myRef;
    User user = null;

    public static FirebaseDatabase getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
        return database;
    }

//    @SuppressLint("RestrictedApi")
//    public void createTable() {
//        myRef = database.getReference("NewsData");
//        String id = myRef.push().getKey();
//        News news = new News("VTV Công Nghệ", "https://vtv.vn/cong-nghe.rss", new BigInteger(id.getBytes()).intValue());
//        myRef.child(id).setValue(news);
//        id = myRef.push().getKey();
//        news = new News("VTV Thế Giới", "https://vtv.vn/the-gioi/the-gioi-do-day.rss", new BigInteger(id.getBytes()).intValue());
//        myRef.child(id).setValue(news);
//        id = myRef.push().getKey();
//        news = new News("VTV Điện Ảnh", "https://vtv.vn/van-hoa-giai-tri/dien-anh.rss", new BigInteger(id.getBytes()).intValue());
//        myRef.child(id).setValue(news);
//        id = myRef.push().getKey();
//        news = new News("'VTV Bóng Đá'", "https://vtv.vn/the-thao/bong-da.rss", new BigInteger(id.getBytes()).intValue());
//        myRef.child(id).setValue(news);
//        id = myRef.push().getKey();
//        news = new News("VTV Du Lịch", "https://vtv.vn/doi-song/du-lich.rss", new BigInteger(id.getBytes()).intValue());
//        myRef.child(id).setValue(news);
//        id = myRef.push().getKey();
//        news = new News("VTV Thời Tiết", "https://vtv.vn/du-bao-thoi-tiet.rss", new BigInteger(id.getBytes()).intValue());
//        myRef.child(id).setValue(news);
//        myRef = database.getReference("Users");
//        id = myRef.push().getKey();
//        User user = new User(id, "admin", "123123", 1);
//        myRef.child(id).setValue(user);
//        id = myRef.push().getKey();
//        user = new User(id, "user1", "123123", 0);
//        myRef.child(id).setValue(user);
//        id = myRef.push().getKey();
//        user = new User(id, "user2", "123456", 0);
//        myRef.child(id).setValue(user);
//        id = myRef.push().getKey();
//        user = new User(id, "user3", "123123", 0);
//        myRef.child(id).setValue(user);
//    }

    public boolean insertSaved(Item item, User user) {
        myRef = getDatabase().getReference("Saved/" + user.getId());
        String id = myRef.push().getKey();
        assert id != null;
        myRef.child(id).setValue(item);
        return true;
    }

    public static boolean insert(News news) {
        myRef = getDatabase().getReference("NewsData");
        String id = myRef.push().getKey();
        assert id != null;
        news.setId(new BigInteger(id.getBytes()).intValue());
        myRef.child(id).setValue(news);
        return true;
    }


    public boolean delete(int id) {
        myRef = getDatabase().getReference("NewsData");
        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    News value = postSnapshot.getValue(News.class);
                    assert value != null;
                    if (value.getId() == id)
                        myRef.child(Objects.requireNonNull(postSnapshot.getKey())).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Error Firebase", "Failed to read value.", error.toException());
            }

        });
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<News>> getALL() {
        CompletableFuture<List<News>> completableFuture = new CompletableFuture<>();
        myRef = getDatabase().getReference("NewsData");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<News> newsList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    News value = postSnapshot.getValue(News.class);
                    newsList.add(value);
                    assert value != null;
                    Log.d("Value", value.toString());
                }
                completableFuture.complete(newsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                completableFuture.completeExceptionally(databaseError.toException());
            }
        });
        return completableFuture;
    }

    public News getNews(int id) {
        myRef = getDatabase().getReference("NewsData");
        final News[] get = {new News()};
        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    News value = postSnapshot.getValue(News.class);
                    assert value != null;
                    if (value.getId() == id) get[0] = value;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Error Firebase", "Failed to read value.", error.toException());
            }

        });
        return get[0];
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<Item>> getSaved(User user) {
        myRef = getDatabase().getReference("Saved/" + user.getId());
        CompletableFuture<List<Item>> completableFuture = new CompletableFuture<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Item> ItemList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Item value = postSnapshot.getValue(Item.class);
                    ItemList.add(value);
                }
                completableFuture.complete(ItemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                completableFuture.completeExceptionally(databaseError.toException());
            }
        });
        return completableFuture;
    }

    public boolean deleteSaved(Item item, User user) {
        myRef = getDatabase().getReference("Saved/" + user.getId());
        // Read from the database
        Log.d("Ready", String.valueOf(item.getId()));
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Item value = postSnapshot.getValue(Item.class);
                    assert value != null;
                    if (value.equals(item))
                        myRef.child(Objects.requireNonNull(postSnapshot.getKey())).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Error Firebase", "Failed to read value.", error.toException());
            }

        });
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<User> checkLogin(String user, String pass) {
        myRef = getDatabase().getReference("Users");
        CompletableFuture<User> completableFuture = new CompletableFuture<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User value = postSnapshot.getValue(User.class);
                    if (value.getUsername().equals(user) && value.getPassword().equals(pass)) {
                        u = value;
                    }
                }
                completableFuture.complete(u);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                completableFuture.completeExceptionally(databaseError.toException());
            }
        });
        return completableFuture;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletionStage<Boolean> checkReg(String user, String pass) {
        myRef = getDatabase().getReference("Users");
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check = true;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User value = postSnapshot.getValue(User.class);
                    assert value != null;
                    if (value.getUsername().equals(user)) {
                        check = false;
                    }
                }
                if (check) {
                    String id = myRef.push().getKey();
                    myRef.child(id).setValue(new User(id, user, pass, 0));
                }
                completableFuture.complete(check);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                completableFuture.completeExceptionally(databaseError.toException());
            }
        });
        return completableFuture;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletionStage<List<User>> getALLUser() {
        myRef = getDatabase().getReference("Users");
        CompletableFuture<List<User>> completableFuture = new CompletableFuture<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> ItemList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User value = postSnapshot.getValue(User.class);
                    ItemList.add(value);
                }
                completableFuture.complete(ItemList);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                completableFuture.completeExceptionally(databaseError.toException());
            }
        });
        return completableFuture;

    }

    public void deleteUser(User user) {
        myRef = getDatabase().getReference("Users");
        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User value = postSnapshot.getValue(User.class);
                    if (value.equals(user))
                        myRef.child(Objects.requireNonNull(postSnapshot.getKey())).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Error Firebase", "Failed to read value.", error.toException());
            }

        });
    }

    public void updateUser(String id, String name, String pass, int type) {
        myRef = getDatabase().getReference("Users");
        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User value = postSnapshot.getValue(User.class);
                    assert value != null;
                    if (value.getId().equals(id)) {
                        value.setUsername(name);
                        value.setPassword(pass);
                        value.setType(type);
                        myRef.child(Objects.requireNonNull(postSnapshot.getKey())).setValue(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Error Firebase", "Failed to read value.", error.toException());
            }

        });
    }
}
