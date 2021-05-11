package com.a.attendancereportpsu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.a.attendancereportpsu.R.layout.lesson_cardview;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonCardViewHolder> {
    List <LessonCard> cards;

    @NonNull
    @Override
    public LessonCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(lesson_cardview, parent, false);

        return new LessonCardViewHolder(cv, cv.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull LessonCardViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView subject = (TextView)cardView.findViewById(R.id.subject2_name);
        subject.setText(cards.get(position).subject);
        TextView type = (TextView)cardView.findViewById(R.id.type2);
        type.setText(cards.get(position).type);
        TextView time = (TextView)cardView.findViewById(R.id.time2);
        time.setText(cards.get(position).time);
        holder.currentCardPosition = position;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public static class LessonCardViewHolder extends RecyclerView.ViewHolder {
        ImageButton ib;
        CardView cardView;
        int currentCardPosition;
        Context mContext;
        LessonCardViewHolder(CardView cv, Context context) {
            super(cv);
            cardView = cv;
            ib = cv.findViewById(R.id.editLesson);
            mContext=context;
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    if(mContext instanceof ShowLessons){
                        ((ShowLessons)mContext).onDeleteClick(currentCardPosition);
                    }
                    return false;
                }
            });
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mContext instanceof ShowLessons){
                        ((ShowLessons)mContext).onEditClick(currentCardPosition);
                    }
                }
            });
        }
    }

    LessonAdapter(List cards){
        this.cards = cards;
    }


}
