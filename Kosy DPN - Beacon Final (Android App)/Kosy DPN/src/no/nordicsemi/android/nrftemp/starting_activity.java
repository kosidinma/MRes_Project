package no.nordicsemi.android.nrftemp;



import com.nordic.android.kosydpnfinal.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;


public class starting_activity extends Activity{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_activity);
        Button button = (Button) findViewById(R.id.done);
        button.setOnClickListener(new OnClickListener()
        {
          public void onClick(View v)
          {
        	  EditText txtDescription = (EditText) findViewById(R.id.editText1);
        	  String PatientName = txtDescription.getText().toString();
        	  final Intent intent = new Intent(starting_activity.this, SensorsActivity.class);
        	  intent.putExtra("PatientName", PatientName);
        	  startActivity(intent);
        	  finish();
          }
        });
      
    }

	
}
