package com.ajaygaikwad.mydiary.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ajaygaikwad.mydiary.pojo.Contact;


import java.util.List;

/**
 * Created by gurleensethi on 04/02/18.
 */

@Dao
public interface ContactDAO {
    @Insert
    public void insert(Contact... contacts);

    @Update
    public void update(Contact... contacts);

    @Delete
    public void delete(Contact contact);

    @Query("SELECT firstName FROM contact group by firstName")
    public List<String> getContacts();

    @Query("SELECT disc FROM contact where firstName = :name group by disc")
    public List<String> getContactsAgainstName(String name);

    //@Query("SELECT * FROM contact WHERE phoneNumber = :number")
    //@Query("SELECT * FROM contact ")
    //public Contact getContactWithId(String number);
}
