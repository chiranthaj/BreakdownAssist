package lk.steps.breakdownassistpluss.MaterialList;

import android.content.Context;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lk.steps.breakdownassistpluss.MaterialList.MaterialObject;
import lk.steps.breakdownassistpluss.R;

/**
 * Created by JagathPrasanga on 10/22/2017.
 */

public class MaterialViewsAdapter extends ArrayAdapter<MaterialObject> {
    private int layoutResource;
    public static ArrayList<MaterialObject> selectedMaterials = new ArrayList<>();

    public MaterialViewsAdapter(@NonNull Context context, @LayoutRes int layoutResource , List<MaterialObject> materialList) {
        super(context, layoutResource, materialList);
        this.layoutResource = layoutResource;

    }

    /*@Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
       // DataModel dataModel=(DataModel)object;

        switch (v.getId()) {
            case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
        }
    }*/


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(layoutResource, null);
        }

        final MaterialObject item = getItem(position);

        CheckBox chkItem = (CheckBox) convertView.findViewById(R.id.chkItem);
        final EditText etCount = (EditText) convertView.findViewById(R.id.etCount);
        final ImageButton up = (ImageButton) convertView.findViewById(R.id.btnUp);
        final ImageButton down = (ImageButton) convertView.findViewById(R.id.btnDown);

        chkItem.setChecked(item.getQuantity()!=0);
        chkItem.setText(item.getName());
        Typeface fontSinhala = Typeface.createFromAsset(getContext().getAssets(), "fonts/iskoolapota.ttf");
        chkItem.setTypeface(fontSinhala);
        etCount.setText(String.valueOf(item.getQuantity()));

        etCount.setEnabled(item.getQuantity()!=0);
        down.setEnabled(item.getQuantity()!=0);
        up.setEnabled(item.getQuantity()!=0);

        chkItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                etCount.setText("0");
                etCount.setEnabled(isChecked);
                down.setEnabled(isChecked);
                up.setEnabled(isChecked);
                }
            }
        );

        /*up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int n = Integer.parseInt(etCount.getText().toString())+1 ;
            etCount.setText(String.valueOf(n));
            down.setEnabled(true);
            item.setQuantity(n);
            selectedMaterials.add(item);
            }
        });
        up.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int n = Integer.parseInt(etCount.getText().toString())+10 ;
                etCount.setText(String.valueOf(n));
                down.setEnabled(true);
                item.setQuantity(n);
                selectedMaterials.add(item);
                return true;
            }
        });*/
        up.setOnTouchListener(new RepeatListener(1000, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // the code to execute repeatedly
                int n = Integer.parseInt(etCount.getText().toString())+1 ;
                etCount.setText(String.valueOf(n));
                down.setEnabled(true);
                item.setQuantity(n);
                selectedMaterials.add(item);
            }
        }));

        /*down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!etCount.getText().toString().equals("0")){
                int n = Integer.parseInt(etCount.getText().toString())-1 ;
                etCount.setText(String.valueOf(n));
                item.setQuantity(n);

                if(item.getQuantity()>0)selectedMaterials.add(item);
                else selectedMaterials.remove(item);

            }else{
                down.setEnabled(false);
            }
            }
        });
        down.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!etCount.getText().toString().equals("0")){
                    int n = Integer.parseInt(etCount.getText().toString())-10 ;
                    if(n<0)n=0;
                    etCount.setText(String.valueOf(n));
                    item.setQuantity(n);

                    if(item.getQuantity()>0)selectedMaterials.add(item);
                    else selectedMaterials.remove(item);

                }else{
                    down.setEnabled(false);
                }
                return true;
            }
        });*/
        down.setOnTouchListener(new RepeatListener(1000, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // the code to execute repeatedly
                if(!etCount.getText().toString().equals("0")){
                    int n = Integer.parseInt(etCount.getText().toString())-1 ;
                    etCount.setText(String.valueOf(n));
                    item.setQuantity(n);

                    if(item.getQuantity()>0)selectedMaterials.add(item);
                    else selectedMaterials.remove(item);

                }else{
                    down.setEnabled(false);
                }
            }
        }));


        return convertView;
    }

}
