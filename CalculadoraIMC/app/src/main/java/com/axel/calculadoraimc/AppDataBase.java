package com.axel.calculadoraimc;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract UserDao userDao();

    public UserDao userdao() {
        return null;
    }
}
