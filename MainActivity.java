package com.example.shoppyfile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private NotesAdapter adapter;
    private List<Note> notesList;
    private EditText editTextNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        editTextNote = findViewById(R.id.editTextNote);
        Button buttonAdd = findViewById(R.id.buttonAdd);

        // Настройка RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notesList = db.getAllNotes();
        adapter = new NotesAdapter(notesList);
        recyclerView.setAdapter(adapter);

        // Добавление новой заметки
        buttonAdd.setOnClickListener(v -> {
            String text = editTextNote.getText().toString().trim();
            if (!text.isEmpty()) {
                Note note = new Note(text, ""); // Используем title как основной текст
                db.addNote(note);
                notesList.add(note);
                adapter.notifyItemInserted(notesList.size() - 1);
                editTextNote.setText("");
            }
        });

        // Удаление по свайпу
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note note = notesList.get(position);
                db.deleteNote(note.getId());
                notesList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }).attachToRecyclerView(recyclerView);
    }
}
