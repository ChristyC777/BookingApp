package gr.aueb.ebookingapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.selectedlodge.SelectedLodge;
import src.backend.lodging.Lodging;

public class CollectionHomepageAdapter extends ArrayAdapter<Lodging> implements View.OnClickListener {
    private Context context;
    private ArrayList<Lodging> rooms;
    private String username;

    public CollectionHomepageAdapter(ArrayList<Lodging> rooms, Context context) {
        super(context, R.layout.viewrooms, rooms);
        this.rooms = rooms;
        this.context = context;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        Lodging lodging = getItem(position);

        Intent intent = new Intent(context, SelectedLodge.class);
        intent.putExtra("username", getUsername());
        intent.putExtra("lodging", lodging);
        context.startActivity(intent);
    }

    private static class ViewHolder {
        ImageView lodgeImage;
        TextView lodgeName;
        TextView lodgeStar;
        TextView lodgePrice;
        TextView textPrice;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Lodging lodge = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.viewrooms, parent, false);

            viewHolder.lodgeImage = convertView.findViewById(R.id.lodgeImage);
            viewHolder.lodgeName = convertView.findViewById(R.id.lodgeName);
            viewHolder.lodgeStar = convertView.findViewById(R.id.lodgeStar);
            viewHolder.lodgePrice = convertView.findViewById(R.id.lodgePrice);
            viewHolder.textPrice = convertView.findViewById(R.id.textPrice);

            viewHolder.lodgeName.setText(lodge.getRoomName());
            viewHolder.lodgeStar.setText(String.valueOf(lodge.getStars()));
            viewHolder.lodgePrice.setText(String.valueOf(lodge.getPrice()));
            viewHolder.textPrice.setText("€");

            // Get drawable id from the lodge object
            int imageResId = context.getResources().getIdentifier(lodge.getRoomImage(), "drawable", context.getPackageName());
            viewHolder.lodgeImage.setImageResource(imageResId);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(this);

        return convertView;
    }
}
