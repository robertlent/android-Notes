package com.lentcoding.notes;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {
    private String action;
    private EditText subject, editor;
    private String noteFilter;
    private String oldSubject, oldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        subject = (EditText) findViewById(R.id.editSubject);
        editor = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + " = " + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                oldSubject = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_SUBJECT));
                oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
                subject.setText(oldSubject);
                editor.setText(oldText);
                editor.requestFocus();
                cursor.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishEditing();
                break;

            case R.id.action_delete:
                deleteNote();
                break;
        }

        return true;
    }

    private void finishEditing() {
        String newSubject = subject.getText().toString().trim();
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newSubject.length() == 0 && newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                    finish();
                } else {
                    insertNote(newSubject, newText);
                    finish();
                }
                break;

            case Intent.ACTION_EDIT:
                if (oldSubject.equals(newSubject) && oldText.equals(newText)) {
                    setResult(RESULT_CANCELED);
                    finish();
                } else if (newSubject.length() == 0 && newText.length() == 0) {
                    deleteNote();
                } else {
                    updateNote(newSubject, newText);
                    finish();
                }
                break;
        }
    }

    private void deleteNote() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
                    Toast.makeText(EditorActivity.this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener)
                .show();
    }

    private void updateNote(String noteSubject, String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_SUBJECT, noteSubject);
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteSubject, String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_SUBJECT, noteSubject);
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
