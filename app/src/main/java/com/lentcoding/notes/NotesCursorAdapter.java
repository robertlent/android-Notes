package com.lentcoding.notes;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressWarnings("SameParameterValue")
class NotesCursorAdapter extends CursorAdapter {
    NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String listItem, noteSubject, noteText;
        noteSubject = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_SUBJECT));
        noteText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));

        if (noteSubject.length() == 0) {
            listItem = noteText;
        } else {
            listItem = noteSubject;
        }
        int pos = listItem.indexOf(10);
        if (pos != -1) {
            listItem = listItem.substring(0, pos) + "...";
        }

        TextView tv = (TextView) view.findViewById(R.id.tvNote);
        tv.setText(listItem);
    }
}
