package com.joesoft.books;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BooksRecyclerAdapter extends RecyclerView.Adapter<BooksRecyclerAdapter.ViewHolder> {
    private final ArrayList<Book> mBooks;

    public BooksRecyclerAdapter(ArrayList<Book> books) {
        mBooks = books;
    }

    @NonNull
    @Override
    public BooksRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_book_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksRecyclerAdapter.ViewHolder holder, int position) {
        Book book = mBooks.get(position);
        holder.mTextTitle.setText(book.title);
        String authors = "";
        int i = 0;
        for (String author: book.authors) {
            authors += author;
            i++;
            if (i < book.authors.length) {
                authors+=", ";
            }
        }
        holder.mTextAuthors.setText(authors);
        holder.mTextPublisher.setText(book.publisher);
        holder.mTextPublishedDate.setText(book.publishedDate);

    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextTitle;
        TextView mTextAuthors;
        TextView mTextPublisher;
        TextView mTextPublishedDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextTitle = itemView.findViewById(R.id.text_title);
            mTextAuthors = itemView.findViewById(R.id.text_authors);
            mTextPublisher = itemView.findViewById(R.id.text_publisher);
            mTextPublishedDate = itemView.findViewById(R.id.text_published_date);
        }
    }
}
