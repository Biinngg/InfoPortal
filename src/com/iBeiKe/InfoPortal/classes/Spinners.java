package com.iBeiKe.InfoPortal.classes;

import com.iBeiKe.InfoPortal.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class Spinners {
	public SpinnerCreater[] createdSpinners = {
			new FloorSpinnerCreater1(),
			new FloorSpinnerCreater2(),
			new ClassSpinnerCreater1(),
			new ClassSpinnerCreater2()
	};
}

class SpinnerCreater extends Activity {
	private int number;
    public void onCreate(Bundle savedInstanceState) {
    }
    class MyOnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	number = pos;
        }
        public void onNothingSelected(AdapterView parent) {
        }
    }
    int GetPos() {
    	return number;
    }
}

class FloorSpinnerCreater1 extends SpinnerCreater {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.search, null);
    	System.out.println("********************************arrived here0 spinners.java");
    	Spinner floor_spinner1 = (Spinner) findViewById(R.id.floor_spinner1);
    	ArrayAdapter<CharSequence> floor_adapter1 = ArrayAdapter.createFromResource(
            this, R.array.floor_array, android.R.layout.simple_spinner_item);
    	floor_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	floor_spinner1.setAdapter(floor_adapter1);
    	floor_spinner1.setOnItemSelectedListener(new MyOnItemSelectedListener());
    }
}
class FloorSpinnerCreater2 extends SpinnerCreater {
    void SpinnersCreater() {
    	Spinner floor_spinner2 = (Spinner) findViewById(R.id.floor_spinner2);
    	ArrayAdapter<CharSequence> floor_adapter2 = ArrayAdapter.createFromResource(
                this, R.array.floor_array, android.R.layout.simple_spinner_item);
    	floor_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	floor_spinner2.setAdapter(floor_adapter2);
    	floor_spinner2.setOnItemSelectedListener(new MyOnItemSelectedListener());
    }
}
class ClassSpinnerCreater1 extends SpinnerCreater {
    void SpinnersCreater() {
    	Spinner class_spinner1 = (Spinner) findViewById(R.id.class_spinner1);
    	ArrayAdapter<CharSequence> class_adapter1 = ArrayAdapter.createFromResource(
            this, R.array.class_array, android.R.layout.simple_spinner_item);
    	class_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	class_spinner1.setAdapter(class_adapter1);
    	class_spinner1.setOnItemSelectedListener(new MyOnItemSelectedListener());
    }
}
class ClassSpinnerCreater2 extends SpinnerCreater {
    void SpinnersCreater() {
    	Spinner class_spinner2 = (Spinner) findViewById(R.id.class_spinner2);
    	ArrayAdapter<CharSequence> class_adapter2 = ArrayAdapter.createFromResource(
            this, R.array.class_array, android.R.layout.simple_spinner_item);
    	class_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	class_spinner2.setAdapter(class_adapter2);
    	class_spinner2.setOnItemSelectedListener(new MyOnItemSelectedListener());
    }
}