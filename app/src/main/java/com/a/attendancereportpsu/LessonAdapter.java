package com.a.attendancereportpsu;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import static com.a.attendancereportpsu.R.layout.cardviewless;
import java.util.ArrayList;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
private List<LessonModel> listOfLessons = new ArrayList<>();

        int pos = 0;

public void setItems(ArrayList<LessonModel> lessons) {
        listOfLessons.addAll(lessons);

        notifyDataSetChanged();
        }

public void clearItems() {
        listOfLessons.clear();

        notifyDataSetChanged();
        }

@Override
public LessonAdapter.LessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
        .inflate(cardviewless, parent, false);
        return new LessonAdapter.LessonViewHolder(view);
        }



    @Override
public void onBindViewHolder(LessonAdapter.LessonViewHolder holder, int position) {
        //holder.cv.setSelected(selectedItems.get(position, false));
        holder.bind(listOfLessons.get(position));
        }


    @Override
public int getItemCount() {
        return listOfLessons.size();
        }

// Предоставляет прямую ссылку на каждый View-компонент
// Используется для кэширования View-компонентов и последующего быстрого доступа к ним
class LessonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    // Ваш ViewHolder должен содержать переменные для всех
    // View-компонентов, которым вы хотите задавать какие-либо свойства
    // в процессе работы пользователя со списком

    CardView cv;
    TextView Name;
    TextView time;
    Button change;


    public void bind(LessonModel lesson) {
        Name.setText(lesson.subject_id);
        time.setText(lesson.time);
    }
    // Мы также создали конструктор, который принимает на вход View-компонент строкИ
    // и ищет все дочерние компоненты

    @SuppressLint("WrongViewCast")
    public LessonViewHolder(@NonNull View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cv);
        Name = (TextView) itemView.findViewById(R.id.subject_name);
        time = (TextView) itemView.findViewById(R.id.date);
//        change = (Button) itemView.findViewById(R.id.change_btn);
        cv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }

}
}
