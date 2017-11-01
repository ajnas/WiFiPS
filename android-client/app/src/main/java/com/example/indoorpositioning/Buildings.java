package com.example.indoorpositioning;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class Buildings extends Activity {

	ArrayList<String> buildings;
	DatabaseHelper db;
	ListView buildingsList ;
	Button add;
	EditText buildingName;
	ArrayAdapter<String> arrayAdapter;
    Context context = this;

    private void switchToPositionsActivity(String data) {
        Intent intent = new Intent(context, Positions.class);
        intent.putExtra("BUILDING_NAME", data);
        startActivity(intent);
    }

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.buildings);
		db=new DatabaseHelper(this);
		add=(Button) findViewById(R.id.add);
		buildingName= (EditText) findViewById(R.id.name);
        buildingName.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(charSequence.toString().equals("")){
                    add.setEnabled(false);
                } else {
                    boolean exists = false;
                    for (String buildingName: buildings) {
                        if (charSequence.toString().equals(buildingName)) {
                            add.setEnabled(false);
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        add.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        buildingName.setText("");

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final String input = buildingName.getText().toString();
                if (input.length() < 4) {
                    new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.dialog_message))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    switchToPositionsActivity(input);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    switchToPositionsActivity(input);
                }
            }
        });

		buildings = db.getBuildings();
		buildingsList = (ListView) findViewById(R.id.buildingslist);
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,buildings);

		buildingsList.setAdapter(arrayAdapter);
		buildingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    public void onItemClick(AdapterView parent, View v, int position, long id){
		    String selectedBuilding=(String)parent.getItemAtPosition(position);
		    Intent intent=new Intent(getApplicationContext(),Positions.class);
			intent.putExtra("BUILDING_NAME",selectedBuilding);
			startActivityForResult(intent,0);

		    }


		});

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        buildingsList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    db.deleteBuilding(buildings.get(position));
                                    buildings.remove(position);
                                    arrayAdapter.notifyDataSetChanged();

                                }

                            }
                        });
        buildingsList.setOnTouchListener(touchListener);


	}

    @Override
    protected void onResume() {
        buildings = db.getBuildings();
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,buildings);
        buildingsList.setAdapter(arrayAdapter);
        buildingName.setText("");
        add.setEnabled(false);
        super.onResume();
    }
}

