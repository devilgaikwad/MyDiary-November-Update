package com.ajaygaikwad.mydiary.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ajaygaikwad.mydiary.pojo.Contact;


/**
 * Created by gurleensethi on 04/02/18.
 */
@Database(entities = {Contact.class}, version = 1)
//@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContactDAO getContactDAO();
}
