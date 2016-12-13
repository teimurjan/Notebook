package com.example.android.notebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NoteDetailActivity extends AppCompatActivity {

    private ImageButton icon;
    private EditText title, message;
    private Note.Category savedButtonCategory, noteCat;
    private AlertDialog categoryDialogObject;
    private long noteId = 0;
    private String color;

    private RelativeLayout noteBackground;

    private boolean newNote = false;

    Animation show_fab_1;
    Animation hide_fab_1;
    Animation show_fab_2;
    Animation hide_fab_2;
    Animation show_fab_3;
    Animation hide_fab_3;


    private FloatingActionButton fab, fab1, fab2, fab3;
    private boolean FAB_Status = false;

    private static final String MODIFIED_CATEGORY = "Modified category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            savedButtonCategory = (Note.Category) savedInstanceState.get(MODIFIED_CATEGORY);

        setContentView(R.layout.activity_note_detail);

        title = (EditText) findViewById(R.id.myTitle);
        message = (EditText) findViewById(R.id.NoteBody);
        icon = (ImageButton) findViewById(R.id.NoteIcon);

        Intent intent = this.getIntent();

        color = intent.getExtras().getString(MainActivity.NOTE_COLOR, "#FFFFFF");

        newNote = intent.getBooleanExtra(MainActivityListFragment.NEW_NOTE_EXTRA, false);

        noteId = intent.getExtras().getLong(MainActivity.NOTE_ID_EXTRA);


        if(savedButtonCategory != null){
            icon.setImageResource(Note.categoryToDrawable(savedButtonCategory));
        }else if(!newNote){
            noteCat = (Note.Category) intent.getSerializableExtra(MainActivity.NOTE_CATEGORY_EXTRA);
            savedButtonCategory = noteCat;
            icon.setImageResource(Note.categoryToDrawable(noteCat));
        }

        noteBackground = (RelativeLayout) findViewById(R.id.note_background);

        title.setText(intent.getExtras().getString(MainActivity.NOTE_TITLE_EXTRA, "Title..."));
        message.setText(intent.getExtras().getString(MainActivity.NOTE_MESSAGE_EXTRA, "Text..."));
        noteBackground.setBackgroundColor(Color.parseColor(color));

        buildCategoryDialog();

        icon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                categoryDialogObject.show();
            }
        });


        //floating action buttons
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_3);
        fab = (FloatingActionButton) findViewById(R.id.fab_detail);

        //animations
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);





        //main floating actio button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FAB_Status == false) {
                    //Display FAB menu
                    expandFAB();
                    FAB_Status = true;
                } else {
                    //Close FAB menu
                    hideFAB();
                    FAB_Status = false;
                }

            }
        });




        //extra floating action buttons
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteBackground.setBackgroundColor(Color.parseColor("#40C4FF"));
                color = "#40C4FF";
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteBackground.setBackgroundColor(Color.parseColor("#FFF59D"));
                color = "#FFF59D";
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
                color = "#FFFFFF";
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(MODIFIED_CATEGORY, savedButtonCategory);
    }




    private void buildCategoryDialog(){
        final String[] categories = new String[]{"Personal", "Business", "Education"};
        final AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(this);
        categoryBuilder.setTitle("Choose note category");
        categoryBuilder.setSingleChoiceItems(categories, 0, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int item){
                categoryDialogObject.cancel();
                switch (item){
                    case 0:
                        savedButtonCategory = Note.Category.PERSONAL;
                        icon.setImageResource(R.drawable.personal);
                        break;
                    case 1:
                        savedButtonCategory = Note.Category.WORK;
                        icon.setImageResource(R.drawable.business);
                        break;
                    case 2:
                        savedButtonCategory = Note.Category.EDUCATION;
                        icon.setImageResource(R.drawable.educational);
                        break;
                }
            }
        });

        categoryDialogObject = categoryBuilder.create();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity

                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }


    @Override
    public void onBackPressed()
    {
        // code here to show dialog

        NotebookDbAdapter dbAdapter = new NotebookDbAdapter(this.getBaseContext());
        dbAdapter.open();

        if (newNote){

            dbAdapter.createNote(title.getText() +"", message.getText() +"",
                 (savedButtonCategory == null)?Note.Category.PERSONAL : savedButtonCategory, color);
        }
        else{

            dbAdapter.updateNote(noteId ,title.getText() + "", message.getText()+"", savedButtonCategory, color);

        }

        dbAdapter.close();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }




    private void expandFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin += (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);
    }


    private void hideFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);
    }



}
