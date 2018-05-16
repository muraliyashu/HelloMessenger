package com.muraliyashu.hellomessenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sqlite_database
{

	private static final String DATABASE_NAME = "Birthday_Database.db";
	private static final int DATABASE_VERSION = 1;
	public static Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public sqlite_database(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db)
		{
			try
			{
				String sql = String.format("Create Table CONTACTS(_id integer primary key autoincrement,NUMBER string,NAME string)", "CONTACTS");
				db.execSQL(sql);
				sql = String.format("Create Table NUMBER(MOBILE string)", "NUMBER");
				db.execSQL(sql);
				sql = String.format("Create Table CONTACTSARRAY(CONTACTNAME string,CONTACTNUMBER string)", "CONTACTSARRAY");
				db.execSQL(sql);
			}
			catch(Exception e)
			{
				String getMessage = e.getMessage();
				final sqlite_database dbObject = new sqlite_database(context);
				dbObject.open();
				ContentValues valuesDOB = new ContentValues();
				valuesDOB.put("getMessage",getMessage);
				valuesDOB.put("className","sqlite_database");
				dbObject.Insert(valuesDOB, "EXCEPTION");
				dbObject.close();
				//Toast.makeText(context, "Exception noted", Toast.LENGTH_LONG).show();
			}
		}
		//when version is change Onupgrade part will working
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
							  int newVersion)
		{
		}
	}

	public Cursor query(String query) {

		Cursor cursor = db.rawQuery(query, null);
		return cursor;
	}


	//Delete one particular row using tablename,keyId and keyValue
	public void DeleteRow(String tblName, String keyId, String keyvalue)
	{
		try
		{
			db.delete(tblName, keyId+"="+keyvalue,null);
		}
		catch(SQLException e)
		{
			//Toast.makeText(context, "Row Not deleted", Toast.LENGTH_LONG).show();
		}
	}


	//Delete all data from table for using table name
	public void DeleteAll(String tblName)
	{
		try
		{
			db.delete(tblName, null, null);
			//Toast.makeText(context, "all tables deleted", Toast.LENGTH_LONG).show();
		}
		catch(SQLException e)
		{
			//Toast.makeText(context, "all tables not deleted", Toast.LENGTH_LONG).show();
		}
	}


	//Insert data into local DB for using column name and tablename
	public void Insert(ContentValues Values, String TblName)
	{
		try
		{
			db.insert(TblName,null,Values);
		}
		catch(SQLException e)
		{
			//Toast.makeText(context, "values Not inserted", Toast.LENGTH_LONG).show();
		}
	}


	//executes the sql statement statement=db.execSQL(sql); and tablename
	public void executeSqlStatement(String Statement, String strTableName) {
		try
		{
			String strCount = "";

			db.execSQL(Statement);
			Cursor curCount = query("SELECT changes() FROM " + strTableName);
			while(curCount.moveToNext())
			{
				//Toast.makeText(context, "statement executed", Toast.LENGTH_LONG).show();
			}
			return;
		}
		catch(SQLException e)
		{
			//Toast.makeText(context, "statement Not executed", Toast.LENGTH_LONG).show();
		}
	}


	public int update(String table, ContentValues values, String whereClause, String[] whereArgs)
	{

		try {
			int i = db.update(table, values, whereClause, whereArgs);
			/*if(i>0)
			{
				//Toast.makeText(context, "added", Toast.LENGTH_LONG).show();
			}
			else
			{
				//Toast.makeText(context, "no", Toast.LENGTH_LONG).show();
			}*/
		}
		catch (SQLException e)
		{/*
			String check = e.getMessage().toString();
			check=e.getCause().toString();*/
		}
		return 0;
	}


	//delete the table using tablename
	public void Delete(String TblName)
	{

		try
		{
			db.delete(TblName, null, null);
			//Toast.makeText(context, "Table deleted", Toast.LENGTH_LONG).show();
		}
		catch(SQLException e)
		{
			//Toast.makeText(context, "Table Not deleted", Toast.LENGTH_LONG).show();
		}

	}
	//---opens the database---
	//dbadapter open
	public sqlite_database open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}

	//---closes the database---
	public void close()
	{
		DBHelper.close();
	}
	public String getDBversion()
	{
		return DATABASE_VERSION +"";
	}

}
