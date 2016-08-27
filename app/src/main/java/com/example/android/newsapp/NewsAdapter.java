package com.example.android.newsapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aspun_000 on 8/25/2016.
 */
public class NewsAdapter extends ArrayAdapter {

    public NewsAdapter(Activity context, ArrayList<News> news, int colorResourceId) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        News thisItem = (News) getItem(position);

        TextView newsTitle = (TextView) listItemView.findViewById(R.id.title_view);
        String title = thisItem.getTitle();
        newsTitle.setText(title);

        TextView newsSection = (TextView) listItemView.findViewById(R.id.section_view);
        String section = thisItem.getSectionName();
        newsSection.setText(section);

        TextView newsDate = (TextView) listItemView.findViewById(R.id.date_view);
        String date = thisItem.getDate();
        newsDate.setText(date);

        TextView newsAuthor = (TextView) listItemView.findViewById(R.id.author_view);
        String author = thisItem.getAuthorName();
        newsAuthor.setText(author);

        return listItemView;
    }

}
