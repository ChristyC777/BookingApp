package gr.aueb.ebookingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import gr.aueb.ebookingapp.R;
import src.backend.lodging.Lodging;

public class FilteredRoomsAdapter extends ArrayAdapter<Lodging> {

    private Context context;
    private ArrayList<Lodging> lodges;

    public FilteredRoomsAdapter(Context context, ArrayList<Lodging> lodges) {
        super(context, R.layout.viewrooms, lodges);
        this.context = context;
        this.lodges = lodges;
    }

    private static class ViewHolder {
        ImageView lodgeImage;
        TextView lodgeName;
        TextView lodgeStar;
        ImageView starImage;
        TextView lodgePrice;
        TextView priceSymbol;
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
            viewHolder.starImage = convertView.findViewById(R.id.imageView3);
            viewHolder.lodgePrice = convertView.findViewById(R.id.lodgePrice);
            viewHolder.priceSymbol = convertView.findViewById(R.id.textPrice);

            viewHolder.lodgeName.setText(lodge.getRoomName());
            viewHolder.lodgeStar.setText(String.valueOf(lodge.getStars()));
            viewHolder.lodgePrice.setText(String.valueOf(lodge.getPrice()));
            viewHolder.priceSymbol.setText("€");

            // Get the drawable id from the lodge object
            int imageResId = context.getResources().getIdentifier(lodge.getRoomImage(), "drawable", context.getPackageName());
            viewHolder.lodgeImage.setImageResource(imageResId);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}
