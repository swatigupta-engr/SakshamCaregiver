package com.zuccessful.trueharmony;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.zuccessful.trueharmony.DAOInterfaces.DailyProgressDAO;
import com.zuccessful.trueharmony.DAOInterfaces.LogRecordDAO;
import com.zuccessful.trueharmony.DAOInterfaces.MedicineProgressDAO;


import com.zuccessful.trueharmony.RoomEntity.DailyProgressEntity;
import com.zuccessful.trueharmony.RoomEntity.LogEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineProgressEntity;

@Database(entities = {LogEntity.class, MedicineProgressEntity.class, DailyProgressEntity.class},version = 8,exportSchema = false)
public abstract class DatabaseRoom extends RoomDatabase
{
    private static final String databaseName="SakshamPatientDB";
    private static DatabaseRoom instance;

    public static synchronized DatabaseRoom getInstance(Context context)
    {
        if(instance==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(), DatabaseRoom.class,databaseName).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
    public abstract LogRecordDAO logRecords();
    public abstract MedicineProgressDAO medicineProgressRecords();
    public abstract DailyProgressDAO dailyProgressRecords();
}
