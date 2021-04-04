package com.a.attendancereportpsu;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import static com.a.attendancereportpsu.R.layout.cardview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder>{

    public List<StudentModel> listOfStudents = new ArrayList<>();
    public ArrayList<AttendanceModel> attendance = new ArrayList<>();
    int pos = 0;

    public void setItems() {
      //  listOfStudents.addAll(students);
        for (int i =0; i<listOfStudents.size();i++)
            attendance.add(new AttendanceModel(listOfStudents.get(i).id, false));
        //notifyDataSetChanged();
    }
    public void check_items(ArrayList<AttendanceModel> m){
        for (int i = 0; i<m.size();i++){
            for (int j =0; j<attendance.size();j++){
                if (m.get(i).student_id.equals(attendance.get(j).student_id)) {
                    this.attendance.get(j).setStatus(m.get(i).status);
                    Log.d("selectItemEXIT", String.valueOf(m.get(i).status));
                    Log.d("selectItemEXIT", String.valueOf(this.attendance.get(j).status));
                }
            }
            Log.d("selectItem", String.valueOf(attendance.get(0).student_id));
            Log.d("selectItem", String.valueOf(attendance.get(0).status));
        }

    }
    public void setAttendance(String student_id, boolean status){
        try{
            for (int j =0; j<attendance.size();j++) {
                if(attendance.get(j).student_id.equals(student_id)) {
                    attendance.get(j).setStatus(status);
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void clearItems() {
        listOfStudents.clear();
        attendance.clear();
         notifyDataSetChanged();
    }

    @Override
    public StudentAdapter.StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(cardview, parent, false);
        return new StudentAdapter.StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentAdapter.StudentViewHolder holder, int position) {
         //holder.cv.setSelected(selectedItems.get(position, false));
        holder.cv.setSelected(attendance.get(position).status);
        holder.bind(listOfStudents.get(position));
        Log.d("mLog",String.valueOf(attendance.get(position).status));
        if(!holder.cv.isSelected()){
            // Log.d("selectItem", "remove selection");
            holder.cv.setCardBackgroundColor(Color.WHITE);
            holder.cv.setSelected(false);
        }
        else{
            holder.cv.setCardBackgroundColor(Color.parseColor("#cef2cb"));
            holder.cv.setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return listOfStudents.size();
    }

    // Предоставляет прямую ссылку на каждый View-компонент
    // Используется для кэширования View-компонентов и последующего быстрого доступа к ним
    class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Ваш ViewHolder должен содержать переменные для всех
        // View-компонентов, которым вы хотите задавать какие-либо свойства
        // в процессе работы пользователя со списком

        CardView cv;
        TextView Name;


        public void bind(StudentModel student) {
            Name.setText(student.name);
        }
        // Мы также создали конструктор, который принимает на вход View-компонент строкИ
        // и ищет все дочерние компоненты

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            Name = (TextView) itemView.findViewById(R.id.subject_name);
            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int positionIndex = getAdapterPosition();
           if(attendance.get(positionIndex).status)
              attendance.get(positionIndex).status= false ;
            else attendance.get(positionIndex).status=true;
          //  Log.d("selectItem", String.valueOf(selects[positionIndex]));
            if(cv.isSelected()){
                // Log.d("selectItem", "remove selection");
                cv.setCardBackgroundColor(Color.WHITE);
                cv.setSelected(false);
            }
            else{
                cv.setCardBackgroundColor(Color.parseColor("#cef2cb"));
                cv.setSelected(true);
            }
        }

    }

}