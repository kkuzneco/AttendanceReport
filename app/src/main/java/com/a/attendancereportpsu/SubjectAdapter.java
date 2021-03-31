package com.a.attendancereportpsu;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.a.attendancereportpsu.R.layout.cardview;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    public List<SubjectModel> listOfSubjects = new ArrayList<>();
    int chosen = 0;
    boolean checkChoose = false;
    public void setItems() {


    }

    public void clearItems() {
        listOfSubjects.clear();
        notifyDataSetChanged();
    }

    @Override
    public SubjectAdapter.SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(cardview, parent, false);
        return new SubjectAdapter.SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubjectAdapter.SubjectViewHolder holder, int position) {
        SubjectModel itemList = listOfSubjects.get(position);
        holder.Name.setText(itemList.name);
        holder.Type.setText(itemList.type);
        holder.bind(listOfSubjects.get(position));

        if(position == chosen) {

            holder.cv.setCardBackgroundColor(Color.parseColor("#cef2cb"));
        }
        else
            holder.cv.setCardBackgroundColor(Color.WHITE);

    }

    @Override
    public int getItemCount() {
        return listOfSubjects.size();
    }

    // Предоставляет прямую ссылку на каждый View-компонент
    // Используется для кэширования View-компонентов и последующего быстрого доступа к ним
    class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Ваш ViewHolder должен содержать переменные для всех
        // View-компонентов, которым вы хотите задавать какие-либо свойства
        // в процессе работы пользователя со списком

        CardView cv;
        TextView Name;
        TextView Type;

        public void bind(SubjectModel Subject) {
            Name.setText(Subject.name);
            Type.setText(Subject.type);
        }
        // Мы также создали конструктор, который принимает на вход View-компонент строкИ
        // и ищет все дочерние компоненты

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            Name = (TextView) itemView.findViewById(R.id.subject_name);
            Type = (TextView) itemView.findViewById(R.id.date);

            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            chosen = getAdapterPosition();
            Log.d("index1", String.valueOf(chosen));
            Log.d("index1",listOfSubjects.get(chosen).name);
            cv.setSelected(true);
            notifyDataSetChanged();
        }

    }
}
