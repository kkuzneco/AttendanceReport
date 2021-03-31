package com.a.attendancereportpsu;

import java.util.ArrayList;
import java.util.Collections;

public class FilterSort {
    ArrayList<StudentModel> start;
    public ArrayList<String> Find (ArrayList<String> list, String find){
       list =  sortAZ(list);
        list = showOnlyFilter(list, find);
        return list;
    }
    public ArrayList<String> sortAZ(ArrayList<String> list){
        Collections.sort(list);

        return list;
    }
    public ArrayList<StudentModel> sortA(ArrayList<StudentModel> students){
        start = new ArrayList<StudentModel>();
        return start;
    }
    public ArrayList<String> showOnlyFilter(ArrayList<String> list, String find){
        ArrayList<String> list1 = new ArrayList<>();
      for (int i = 0; i < list.size() - 1; i++){
          if (list.get(i).contains(find))
              list1.add(list.get(i));
      }
      return list1;
    }

}
