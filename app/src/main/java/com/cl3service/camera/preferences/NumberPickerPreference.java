package com.cl3service.camera.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.cl3service.camera.R;

public class NumberPickerPreference extends DialogPreference {
    private NumberPicker nbInt;
    private NumberPicker nbFract;
    private float value;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializa();
    }

    public NumberPickerPreference(Context context, AttributeSet attrs,  int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inicializa();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inicializa();
    }

    private void inicializa(){
        setDialogLayoutResource(R.layout.dialog_numberpicker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setPersistent(true);
    }

    private float getValue(){
        return (float)nbInt.getValue()+ ( (float)nbFract.getValue() / 100f);
    }

    @Override
    protected void onBindDialogView(View view) {
        nbInt = (NumberPicker) view.findViewById(R.id.integer_picker);
        nbInt.setWrapSelectorWheel(true);

        nbFract = (NumberPicker) view.findViewById(R.id.fraction_picker);
        nbFract.setWrapSelectorWheel(true);
        nbFract.setMinValue(0);
        nbFract.setMaxValue(99);

        TextView textUnit = (TextView) view.findViewById(R.id.unit_sign);

        if( getKey().contentEquals( "gain_value" ) ){
            nbInt.setMinValue(0);
            nbInt.setMaxValue(10);
            textUnit.setText("db");
        }else
        if( getKey().contentEquals( "expo_value" )){
            nbInt.setMinValue(2);
            nbInt.setMaxValue(8);
            textUnit.setText("ms");
        }

        nbFract.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d",value);
            }
        });

        View firstItem = nbFract.getChildAt(0);
        if (firstItem != null) {
            firstItem.setVisibility(View.INVISIBLE);
        }

        nbInt.setValue((int)value);
        nbFract.setValue(  (int)(( (value - (int)value) ) * 100) );

        super.onBindDialogView(view);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        value = restorePersistedValue ? getPersistedFloat(0f) : (float) defaultValue;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            setValue();
        }
    }

    private void setValue(){
        value = getValue();
        persistFloat(value);
        Preference.OnPreferenceChangeListener ev = getOnPreferenceChangeListener();
        ev.onPreferenceChange(this, value);
    }
}
