package com.ruoyan.map500px.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ruoyan.map500px.R;
import com.ruoyan.map500px.data.LocalDataSource;

public class PreferenceActivity extends Activity {
    private Spinner spinner1, spinner2, spinner3;
    private SeekBar seekBar;
    private TextView progressText;
    private BootstrapButton bootstrapButton;
    private LocalDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        getActionBar().hide();
        dataSource = new LocalDataSource(this);
        spinner1 = (Spinner)findViewById(R.id.photo_stream);
        spinner2 = (Spinner)findViewById(R.id.photo_category);
        spinner3 = (Spinner)findViewById(R.id.photo_sorting);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.photo_stream_options, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.photo_category_options, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.photo_sort_options, android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
        spinner3.setAdapter(adapter3);

        int streamOrder = dataSource.getSettingsOrder("stream");
        int categoryOrder = dataSource.getSettingsOrder("category");
        int sortingOrder = dataSource.getSettingsOrder("sorting");
        int photoesReturned = dataSource.getSettingsOrder("photo_number");

        if (streamOrder!=-1)
            spinner1.setSelection(streamOrder);
        if (categoryOrder!=-1)
            spinner2.setSelection(categoryOrder);
        if (sortingOrder!=-1)
            spinner3.setSelection(sortingOrder);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] streamArray = getResources().getStringArray(R.array.photo_stream_code);
                dataSource.updateSettings("stream",streamArray[position]);
                dataSource.updateSettingsOrder("stream",position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        dataSource.updateSettings("category","all");
                        break;
                    case 1:
                        dataSource.updateSettings("category","no people");
                        break;
                    case 2:
                        dataSource.updateSettings("category","people");
                        break;
                }
                dataSource.updateSettingsOrder("category",position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] sortingArray = getResources().getStringArray(R.array.photo_sort_code);
                dataSource.updateSettings("sorting",sortingArray[position]);
                dataSource.updateSettingsOrder("sorting",position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        progressText = (TextView)findViewById(R.id.progress_value);
        if (photoesReturned != -1) {
            seekBar.setProgress(photoesReturned);
            progressText.setText(Integer.toString(photoesReturned));
        }
        else {
            seekBar.setProgress(20);
            progressText.setText("default:20");
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                    progress++;
                progressText.setText(Integer.toString(progress));
                dataSource.updateSettingsOrder("photo_number",progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bootstrapButton = (BootstrapButton)findViewById(R.id.bootstrap_button);
        bootstrapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
