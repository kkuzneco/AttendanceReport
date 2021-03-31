package com.a.attendancereportpsu;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.a.attendancereportpsu.R.layout.cardview;

public class LecturerAdapter extends RecyclerView.Adapter<LecturerAdapter.LecturerViewHolder>{

    public List<LecturerModel> listOfLecturers = new ArrayList<>();
    private static CheckBox lecturer_choice = null;
    //public ArrayList<AttendanceModel> attendance = new ArrayList<>();
    int chosen = 0;
    boolean checkChoose = false;
    public void setItems() {
        //  listOfStudents.addAll(students);
        //for (int i =0; i<listOfLecturers.size();i++)
        //    attendance.add(new AttendanceModel(listOfStudents.get(i).id, false));
        //notifyDataSetChanged();

    }

    public void clearItems() {
        listOfLecturers.clear();
        notifyDataSetChanged();
    }

    @Override
    public LecturerAdapter.LecturerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(cardview, parent, false);
        return new LecturerAdapter.LecturerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LecturerAdapter.LecturerViewHolder holder, int position) {
        LecturerModel itemList = listOfLecturers.get(position);
        holder.Name.setText(itemList.name);
        holder.Institute.setText(itemList.institute);
        holder.bind(listOfLecturers.get(position));

        if(position == chosen) {

            holder.cv.setCardBackgroundColor(Color.parseColor("#cef2cb"));
        }
        else
            holder.cv.setCardBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return listOfLecturers.size();
    }

    // Предоставляет прямую ссылку на каждый View-компонент
    // Используется для кэширования View-компонентов и последующего быстрого доступа к ним
    class LecturerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Ваш ViewHolder должен содержать переменные для всех
        // View-компонентов, которым вы хотите задавать какие-либо свойства
        // в процессе работы пользователя со списком

        CardView cv;
        TextView Name;
        TextView Institute;

        public void bind(LecturerModel lecturer) {
            Name.setText(lecturer.name);
            Institute.setText(lecturer.institute);
        }
        // Мы также создали конструктор, который принимает на вход View-компонент строкИ
        // и ищет все дочерние компоненты

        public LecturerViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            Name = (TextView) itemView.findViewById(R.id.subject_name);
            Institute = (TextView) itemView.findViewById(R.id.date);

            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            chosen = getAdapterPosition();
            Log.d("index", String.valueOf(chosen));
            cv.setSelected(true);
            checkChoose = true;
            notifyDataSetChanged();
        }

    }
}
