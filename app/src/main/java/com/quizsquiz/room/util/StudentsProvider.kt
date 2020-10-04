package com.quizsquiz.room.util

import android.content.*
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import java.util.*

class StudentsProvider : ContentProvider() {
    companion object {
        private const val PROVIDER_NAME = "com.quizsquiz.room.util.StudentsProvider"
        private const val URL = "content://$PROVIDER_NAME/students"
        val CONTENT_URI: Uri = Uri.parse(URL)
        const val _ID = "_id"
        const val NAME = "name"
        const val GRADE = "grade"
        private val STUDENTS_PROJECTION_MAP: HashMap<String, String>? = null
        const val STUDENTS = 1
        const val STUDENT_ID = 2
        var uriMatcher: UriMatcher? = null
        const val DATABASE_NAME = "College"
        const val STUDENTS_TABLE_NAME = "students"
        const val DATABASE_VERSION = 1
        const val CREATE_DB_TABLE = "CREATE TABLE " + STUDENTS_TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " name TEXT NOT NULL, " +
                " grade TEXT NOT NULL);"

        init {
            uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher!!.addURI(PROVIDER_NAME, "students", STUDENTS)
            uriMatcher!!.addURI(PROVIDER_NAME, "students/#", STUDENT_ID)
        }
    }

    /**
     * Database specific constant declarations
     */
    private var db: SQLiteDatabase? = null

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private class DatabaseHelper(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_DB_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $STUDENTS_TABLE_NAME")
            onCreate(db)
        }
    }

    override fun onCreate(): Boolean {
        val context = context
        val dbHelper = DatabaseHelper(context)
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.writableDatabase
        return db != null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        /**
         * Add a new student record
         */
        val rowID = db!!.insert(STUDENTS_TABLE_NAME, "", values)
        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            val _uri = ContentUris.withAppendedId(CONTENT_URI, rowID)
            context!!.contentResolver.notifyChange(_uri, null)
            return _uri
        }
        throw SQLException("Failed to add a record into $uri")
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        var sortOrder = sortOrder
        val qb = SQLiteQueryBuilder()
        qb.tables = STUDENTS_TABLE_NAME
        when (uriMatcher!!.match(uri)) {
            STUDENTS -> qb.projectionMap = STUDENTS_PROJECTION_MAP
            STUDENT_ID -> qb.appendWhere(_ID + "=" + uri.pathSegments[1])
            else -> {
            }
        }
        if (sortOrder == null || sortOrder === "") {
            /**
             * By default sort on student names
             */
            sortOrder = NAME
        }
        val c = qb.query(
            db, projection, selection,
            selectionArgs, null, null, sortOrder
        )
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var count = 0
        count = when (uriMatcher!!.match(uri)) {
            STUDENTS -> db!!.delete(
                STUDENTS_TABLE_NAME,
                selection,
                selectionArgs
            )
            STUDENT_ID -> {
                val id = uri.pathSegments[1]
                db!!.delete(
                    STUDENTS_TABLE_NAME,
                    _ID + " = " + id +
                            if (!TextUtils.isEmpty(selection)) " AND($selection)" else "",
                    selectionArgs
                )
            }
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun update(
        uri: Uri, values: ContentValues?,
        selection: String?, selectionArgs: Array<String>?
    ): Int {
        var count = 0
        count = when (uriMatcher!!.match(uri)) {
            STUDENTS -> db!!.update(
                STUDENTS_TABLE_NAME,
                values,
                selection,
                selectionArgs
            )
            STUDENT_ID -> db!!.update(
                STUDENTS_TABLE_NAME, values,
                _ID + " = " + uri.pathSegments[1] +
                        if (!TextUtils.isEmpty(selection)) " AND($selection)" else "", selectionArgs
            )
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher!!.match(uri)) {
            STUDENTS -> "vnd.android.cursor.dir/vnd.example.students"
            STUDENT_ID -> "vnd.android.cursor.item/vnd.example.students"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }
}