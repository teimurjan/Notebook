package com.example.android.notebook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Теймур on 16.06.2016.
 */
public class NotesAdapter extends ArrayAdapter<Note> {


    public static class ViewHolder{
        TextView title;
        TextView note;
        ImageView noteIcon;
        RelativeLayout layout;

    }
    public NotesAdapter(Context context, ArrayList<Note> notes){
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Note note = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null){

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);

            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.list_row);

            viewHolder.title = (TextView) convertView.findViewById(R.id.noteTitleText);
            viewHolder.note = (TextView) convertView.findViewById(R.id.noteBodyText);
            viewHolder.noteIcon = (ImageView) convertView.findViewById(R.id.noteImage);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }



      
        viewHolder.layout.setBackgroundColor(Color.parseColor(note.getColor()));
        viewHolder.title.setText(note.getTitle());
        viewHolder.note.setText(note.getBody());
        viewHolder.noteIcon.setImageResource(note.getDrawable());

        return convertView;
    }


}

