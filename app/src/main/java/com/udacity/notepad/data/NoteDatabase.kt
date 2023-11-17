package com.udacity.notepad.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import com.udacity.notepad.data.NotesContract.NoteTable.CREATED_AT
import com.udacity.notepad.data.NotesContract.NoteTable.IS_PINNED
import com.udacity.notepad.data.NotesContract.NoteTable.TEXT
import com.udacity.notepad.data.NotesContract.NoteTable.UPDATED_AT
import com.udacity.notepad.data.NotesContract.NoteTable._TABLE_NAME
import java.util.Date

class NoteDatabase(context: Context?) {
    private val helper: NotesOpenHelper

    init {
        helper = NotesOpenHelper(context)
    }

    fun getAll(): List<Note> {
        val cursor = helper.readableDatabase.query(
            _TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            CREATED_AT
        )
        return cursor.use { allFromCursor(cursor) }
    }

    fun loadAllByIds(vararg ids: Int): List<Note> {
        val questionMarks = ids.map { "?" }.joinToString { ", " }
        val args =  ids.map { it.toString() }
        val selection = "${BaseColumns._ID} IN ($questionMarks)"
        val cursor = helper.readableDatabase.query(
            _TABLE_NAME,
            null,
            selection,
            args.toTypedArray(),
            null,
            null,
            CREATED_AT
        )
        return cursor.use { allFromCursor(cursor) }
    }

    fun insert(vararg notes: Note) {
        val values = fromNotes( notes)
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            for (value in values) {
                db.insert(_TABLE_NAME, null, value)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun update(note: Note) {
        val values = fromNote(note)
        helper.writableDatabase.update(
            _TABLE_NAME,
            values,
            BaseColumns._ID + " = ?", arrayOf<String>(note.id.toString())
        )
    }

    fun delete(note: Note) {
        helper.writableDatabase.delete(
            _TABLE_NAME,
            BaseColumns._ID + " = ?", arrayOf<String>(note.id.toString())
        )
    }

    companion object {
        private fun fromCursor(cursor: Cursor): Note {
            var col = 0
            return Note().apply {
                id = cursor.getInt(col++)
                text = cursor.getString(col++)
                isPinned = cursor.getInt(col++) != 0
                createdAt = Date(cursor.getLong(col++))
                updatedAt = Date(cursor.getLong(col))
            }
        }

        private fun allFromCursor(cursor: Cursor): List<Note> {
            val retval: MutableList<Note> = ArrayList()
            while (cursor.moveToNext()) {
                retval.add(fromCursor(cursor))
            }
            return retval
        }

        private fun fromNote(note: Note): ContentValues {
            return ContentValues().apply {
                val noteId = note.id
                if (noteId != -1) {
                    put(BaseColumns._ID, noteId)
                }
                put(TEXT, note.text)
                put(IS_PINNED, note.isPinned)
                put(CREATED_AT, note.createdAt.time)
                put(UPDATED_AT, note.updatedAt!!.time)
            }
        }

        private fun fromNotes(notes: Array<out Note>): List<ContentValues> {
            val values: MutableList<ContentValues> = ArrayList()
            for (note in notes) {
                values.add(fromNote(note))
            }
            return values
        }
    }
}
