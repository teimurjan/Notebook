package com.example.android.notebook;

/**
 * Created by Теймур on 16.06.2016.
 */
public class Note {
    private String title, body;
    private Category category;
    private long noteId, dateCreated;
    private String color;

    public enum Category{ PERSONAL, WORK, EDUCATION}



    public Note(String title, String body, Category category, long noteId, String color, long dateCreated){
        this.title = title;
        this.body = body;
        this.category = category;
        this.noteId = noteId;
        this.dateCreated = dateCreated;
        this.color = color;
    }


    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getNoteId() {
        return noteId;
    }

    public Category getCategory() {
        return category;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public String getColor() {
        return color;
    }

    public int getDrawable(){
        return categoryToDrawable(category);
    }


    public static int categoryToDrawable(Category category){
        switch (category){
            case PERSONAL:
                return R.drawable.personal;
            case WORK:
                return R.drawable.business;
            case EDUCATION:
                return R.drawable.educational;
        }
        return R.drawable.personal;
    }

}
