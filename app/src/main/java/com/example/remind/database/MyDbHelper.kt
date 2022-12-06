package com.example.remind.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.remind.adapters.CalendarItemJsonAdapter
import com.example.remind.model.CalendarItem
import com.example.remind.model.CategoryItem
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class MyDbHelper(context: Context):
    SQLiteOpenHelper(context, MyDbNameClass.DATABASE_NAME, null, MyDbNameClass.DATABASE_VERSION) {

    private val nameOfTables: List<String> = listOf(
        "daily_routine_table",
        "sport_table",
        "medicine_table",
        "significant_date_table"
    )

    override fun onCreate(db: SQLiteDatabase?) {
        nameOfTables.forEach {
            db?.execSQL(
                "CREATE TABLE IF NOT EXISTS $it (" +
                        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                        "${MyDbNameClass.TABLE_COLUMN_NAME} TEXT," +
                        "${MyDbNameClass.TABLE_COLUMN_DESCRIPTION} TEXT," +
                        "${MyDbNameClass.TABLE_COLUMN_CALENDAR_ITEM_JSON} TEXT)"
            )
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        nameOfTables.forEach {
            db?.execSQL(
                "DROP TABLE IF EXISTS $it"
            )
        }
        onCreate(db)
    }

    fun addCategoryItem(categoryItem: CategoryItem, tableName: String): Long {
        val db = this.writableDatabase
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(CalendarItem::class.java, CalendarItemJsonAdapter())
        val gson = gsonBuilder.create()
        val values = ContentValues().apply {
            put(MyDbNameClass.TABLE_COLUMN_NAME, categoryItem.name)
            put(MyDbNameClass.TABLE_COLUMN_DESCRIPTION, categoryItem.description)
            put(MyDbNameClass.TABLE_COLUMN_CALENDAR_ITEM_JSON, gson.toJson(categoryItem.listCalendarItem))
        }
        val success = db.insert(tableName, null, values)
        db.close()
        return success
    }

    fun deleteCategoryItem(categoryItem: CategoryItem, tableName: String) {
        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf("${categoryItem.id}")
        db.delete(tableName, selection, selectionArgs)
        db.close()
    }

    fun getAllCategoryItem(tableName: String): ArrayList<CategoryItem> {
        val result: ArrayList<CategoryItem> = ArrayList()
        val selectQuery = "SELECT * FROM $tableName"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if(cursor.moveToFirst()) {
            do {
                val id: Int = cursor.getInt(0)
                val name: String = cursor.getString(1)
                val description: String = cursor.getString(2)
                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(CalendarItem::class.java, CalendarItemJsonAdapter())
                val gson = gsonBuilder.create()
                val listType: Type = object : TypeToken<MutableList<CalendarItem>>() {}.type
                val listCalendarItem: MutableList<CalendarItem> = gson.fromJson(cursor.getString(3), listType)
                val categoryItem = CategoryItem(id, name, description, listCalendarItem)
                result.add(categoryItem)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return result
    }
}