package com.a.attendancereportpsu;

import android.app.AlertDialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
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
import static com.a.attendancereportpsu.R.layout.cardview_device;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder>{
    public List<DeviceModel> listOfDevices = new ArrayList<>();

    int chosen = 0;
    boolean checkChoose = false;


    public void clearItems() {
        listOfDevices.clear();
        notifyDataSetChanged();
    }

    @Override
    public DevicesAdapter.DevicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(cardview_device, parent, false);
        return new DevicesAdapter.DevicesViewHolder(view);

    }

    @Override
    public void onBindViewHolder(DevicesAdapter.DevicesViewHolder holder, int position) {
        DeviceModel itemList = listOfDevices.get(position);
       // holder.StudentName.setText(itemList.student_name);
        holder.DeviceName.setText(itemList.name);

    }

    @Override
    public int getItemCount() {
        return listOfDevices.size();
    }

    // Предоставляет прямую ссылку на каждый View-компонент
    // Используется для кэширования View-компонентов и последующего быстрого доступа к ним
    class DevicesViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        // Ваш ViewHolder должен содержать переменные для всех
        // View-компонентов, которым вы хотите задавать какие-либо свойства
        // в процессе работы пользователя со списком

        CardView cv;
        TextView StudentName;
        TextView DeviceName;

        public void bind(DeviceModel device) {
          //  StudentName.setText(device.student_name);
            DeviceName.setText(device.name);
        }
        // Мы также создали конструктор, который принимает на вход View-компонент строкИ
        // и ищет все дочерние компоненты

        public DevicesViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            StudentName = (TextView) itemView.findViewById(R.id.subject_name);
            DeviceName = (TextView) itemView.findViewById(R.id.date);

            cv.setOnLongClickListener(this);

        }


        @Override
        public boolean onLongClick(View view) {

           return false;
        }
    }
}
