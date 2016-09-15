package com.dkondratov.opengame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.model.Category;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

import static com.dkondratov.opengame.util.ImageLoaderHelper.imageLoader;

/**
 * Created by andrew on 09.04.2015.
 */
public class CategoriesListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<Category> categories;
    private DisplayImageOptions opt = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    public CategoriesListAdapter(List<Category> categories, Context context) {
        this.categories = categories;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryHolder holder;
        if (convertView != null) {
            holder = (CategoryHolder) convertView.getTag();
        } else {
            holder = new CategoryHolder();
            convertView = inflater.inflate(R.layout.category_item, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.category_image);
            holder.categoryName = (TextView) convertView.findViewById(R.id.category_name);
            convertView.setTag(holder);
        }

        imageLoader().displayImage(categories.get(position).getIconUrl(), holder.imageView, opt);
        holder.categoryName.setText(categories.get(position).getName().toUpperCase());

        return convertView;
    }

    static class CategoryHolder {
        ImageView imageView;
        TextView categoryName;
    }
}
