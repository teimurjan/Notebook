package com.example.android.notebook;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
/**
 * A simple {@link Fragment} subclass.
 */
import android.app.ListFragment;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityListFragment extends ListFragment {
    public static final String NEW_NOTE_EXTRA = "New note";
    private ArrayList<Note> notes;
    private NotesAdapter notesAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);



        NotebookDbAdapter dbAdapter = new NotebookDbAdapter(getActivity().getBaseContext());
        dbAdapter.open();
        addNotes(dbAdapter);
        dbAdapter.close();



        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newNote = true;

                Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
                intent.putExtra(NEW_NOTE_EXTRA, newNote);
                intent.putExtra(MainActivity.NOTE_CATEGORY_EXTRA, Note.Category.PERSONAL);
                startActivity(intent);

            }
        });



        notesAdapter = new NotesAdapter(getActivity(), notes);
        setListAdapter(notesAdapter);

        registerForContextMenu(getListView());

        ColorDrawable sage = new ColorDrawable(Color.TRANSPARENT);
        getListView().setDivider(sage);
        getListView().setDividerHeight(10);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);

        launchNoteDetailActivity(position);
    }




    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.long_press_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Note note = (Note) getListAdapter().getItem(position);
        switch (item.getItemId()){

            case R.id.delete:
                NotebookDbAdapter dbAdapter = new NotebookDbAdapter(getActivity().getBaseContext());
                dbAdapter.open();
                dbAdapter.deleteNote(note.getNoteId());
                notes.clear();
                notes.addAll(addNotes(dbAdapter));
                notesAdapter.notifyDataSetChanged();
                dbAdapter.close();
                return true;

            case R.id.copy:
                dbAdapter = new NotebookDbAdapter(getActivity().getBaseContext());
                dbAdapter.open();
                dbAdapter.createNote(note.getTitle()+"",note.getBody()+"",note.getCategory(),note.getColor());
                notes.clear();
                notes.addAll(addNotes(dbAdapter));
                notesAdapter.notifyDataSetChanged();
                dbAdapter.close();
                return true;
        }

        return super.onContextItemSelected(item);
    }


    private void launchNoteDetailActivity(int position){

        Note note = (Note) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
        intent.putExtra(MainActivity.NOTE_TITLE_EXTRA, note.getTitle());
        intent.putExtra(MainActivity.NOTE_MESSAGE_EXTRA, note.getBody());
        intent.putExtra(MainActivity.NOTE_CATEGORY_EXTRA, note.getCategory());
        intent.putExtra(MainActivity.NOTE_ID_EXTRA, note.getNoteId());
        intent.putExtra(MainActivity.NOTE_COLOR, note.getColor());
        intent.putExtra("USE", "EDIT");
        startActivity(intent);
    }




    public ArrayList<Note> addNotes(NotebookDbAdapter dbAdapter){

        if(MainActivity.nav_all)
            notes = dbAdapter.getAllNotes();
        else if(MainActivity.nav_personal)
            notes = dbAdapter.getSomeNotes(Note.Category.PERSONAL);
        else if(MainActivity.nav_education)
            notes = dbAdapter.getSomeNotes(Note.Category.EDUCATION);
        else if(MainActivity.nav_business)
            notes = dbAdapter.getSomeNotes(Note.Category.WORK);
        return notes;
    }
}
