package com.margin.mgms.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.margin.mgms.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on May 05, 2016.
 *
 * @author Marta.Ginosyan
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.main_login_bt, R.id.main_damage_bt, R.id.main_card_locations, R.id.card_summary,
            R.id.main_card_dimensions, R.id.main_task_manager_btn, R.id.main_photo_capture,
            R.id.card_contact, R.id.card_uld})
    public void onButtonClicked(Button button) {
        switch (button.getId()) {
            case R.id.main_login_bt:
                LoginActivity.launch(this);
                break;
            case R.id.main_damage_bt:
                CardActivity.launch(this, CardActivity.CardType.DAMAGES);
                break;
            case R.id.main_task_manager_btn:
                TaskManagerActivity.launch(this, false);
                break;
            case R.id.main_card_dimensions:
                CardActivity.launch(this, CardActivity.CardType.DIMENSION);
                break;
            case R.id.main_card_locations:
                CardActivity.launch(this, CardActivity.CardType.LOCATIONS);
                break;
            case R.id.main_photo_capture:
                PhotoCaptureActivity.launch(this);
                break;
            case R.id.card_contact:
                CardActivity.launch(this, CardActivity.CardType.CONTACT);
                break;
            case R.id.card_summary:
                CardActivity.launch(this, CardActivity.CardType.SUMMARY);
                break;
            case R.id.card_uld:
                CardActivity.launch(this, CardActivity.CardType.ULD);
                break;
        }
    }

}

