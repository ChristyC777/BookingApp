package gr.aueb.ebookingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.content.Context;

import gr.aueb.ebookingapp.R;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import src.backend.lodging.Lodging;

public class CollectionHomepageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Lodging> rooms;
    private LayoutInflater inflater;

    public CollectionHomepageAdapter(Context context, ArrayList<Lodging> rooms) {
        this.context = context;
        this.rooms = rooms;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.viewrooms, parent, false);
        }

        ImageView lodgeImage = convertView.findViewById(R.id.lodgeImage);
        TextView lodgeName = convertView.findViewById(R.id.lodgeName);
        TextView lodgeStar = convertView.findViewById(R.id.lodgeStar);
        TextView lodgePrice = convertView.findViewById(R.id.lodgePrice);
        TextView textPrice = convertView.findViewById(R.id.textPrice);

        Lodging room = rooms.get(position);

        // Set the data
        lodgeName.setText(room.getRoomName());
        lodgeStar.setText(String.valueOf(room.getStars()));
        lodgePrice.setText(String.valueOf(room.getPrice()));

        // Assuming you have a method to get the drawable id from room
        int imageResId = context.getResources().getIdentifier(room.getRoomImage(), "drawable", context.getPackageName());
        lodgeImage.setImageResource(imageResId);

        return convertView;
    }
}

