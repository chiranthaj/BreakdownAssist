package lk.steps.breakdownassistpluss.MaterialList;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import lk.steps.breakdownassistpluss.BreakdownDialogs.MaterialDialog;
import lk.steps.breakdownassistpluss.R;

/**
 * Created by JagathPrasanga on 2/10/2018.
 */

public class MaterialAdapter extends ArrayAdapter<MaterialObject> {
    private Context context;
    public  List<MaterialObject> materialList;
    Typeface fontSinhala = Typeface.createFromAsset(getContext().getAssets(), "fonts/iskoolapota.ttf");

    public MaterialAdapter(@NonNull Context context, @LayoutRes int layoutResource, List<MaterialObject> materialList ) {
        super(context, layoutResource, materialList);
        this.context = context;
        this.materialList = materialList;

    }
   /* public MaterialAdapter(Context context, ArrayList<MaterialObject> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }*/
    /*@Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getCount() {
        return modelArrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.material_row, null, true);

            holder.chkItem = (CheckBox) convertView.findViewById(R.id.chkItem);
            holder.txtMaterialName = (TextView) convertView.findViewById(R.id.txtMaterialName);
            holder.txtPL = (TextView) convertView.findViewById(R.id.txtPl);
            holder.etCount = (EditText) convertView.findViewById(R.id.etCount);
            holder.up = (ImageButton) convertView.findViewById(R.id.btnUp);
            holder.down = (ImageButton) convertView.findViewById(R.id.btnDown);

           // holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb);
           // holder.tvAnimal = (TextView) convertView.findViewById(R.id.animal);

            convertView.setTag(holder);
        }else {
        // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        final MaterialObject item = getItem(position);

        holder.chkItem.setChecked(item.getSelected());
        holder.txtMaterialName.setText(item.getName());
        holder.txtPL.setText(item.getCode());

        holder.txtMaterialName.setTypeface(fontSinhala);
        holder.etCount.setText(String.valueOf(item.getQuantity()));

        holder.etCount.setEnabled(item.getSelected());
        holder.down.setEnabled(item.getSelected());
        holder.up.setEnabled(item.getSelected());


       /* holder.checkBox.setText("Checkbox "+position);
        holder.tvAnimal.setText(modelArrayList.get(position).getAnimal());
        holder.checkBox.setChecked(modelArrayList.get(position).getSelected());
        holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag( position);*/
        holder.chkItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // View tempview = (View) holder.chkItem.getTag(R.integer.btnplusview);
              //  TextView tv = (TextView) tempview.findViewById(R.id.animal);
              //  Integer pos = (Integer)  holder.chkItem.getTag();
              //  Toast.makeText(context, "Checkbox "+pos+" clicked!", Toast.LENGTH_SHORT).show();
              /*  if(modelArrayList.get(pos).getSelected()){
                    modelArrayList.get(pos).setSelected(false);
                }else {
                    modelArrayList.get(pos).setSelected(true);
                }*/
              boolean isChecked = ((CheckBox)v).isChecked();
                if(isChecked & !item.getSelected()){
                    holder.etCount.setEnabled(true);
                    holder.down.setEnabled(true);
                    holder.up.setEnabled(true);
                    Log.e("onCheckedChanged","2");
                    holder.etCount.setText("1");
                    SetMaterialQuantity(item.getCode(),1);
                }
                else if(!isChecked & item.getSelected()){
                    holder.etCount.setEnabled(false);
                    holder.down.setEnabled(false);
                    holder.up.setEnabled(false);
                    Log.e("onCheckedChanged","3");
                    holder.etCount.setText("0");
                    SetMaterialQuantity(item.getCode(),0);
                }
            }
        });

        /*holder.chkItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                   Log.e("onCheckedChanged","1");

                   if(isChecked & !item.getSelected()){
                       holder.etCount.setEnabled(true);
                       holder.down.setEnabled(true);
                       holder.up.setEnabled(true);
                       Log.e("onCheckedChanged","2");
                       holder.etCount.setText("1");
                       SetMaterialQuantity(item.getCode(),1);
                   }
                   else if(!isChecked & item.getSelected()){
                       holder.etCount.setEnabled(false);
                       holder.down.setEnabled(false);
                       holder.up.setEnabled(false);
                       Log.e("onCheckedChanged","3");
                       holder.etCount.setText("0");
                       SetMaterialQuantity(item.getCode(),0);
                   }
               }
           }
        );*/

        holder.up.setOnTouchListener(new RepeatListener(1000, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // the code to execute repeatedly
                int n = Integer.parseInt(holder.etCount.getText().toString())+1 ;
                holder.etCount.setText(String.valueOf(n));
                holder.down.setEnabled(true);
                item.setQuantity(n);
                // selectedMaterials.add(item);
                SetMaterialQuantity(item.getCode(),n);
            }
        }));

        holder.down.setOnTouchListener(new RepeatListener(1000, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // the code to execute repeatedly
                if(!holder.etCount.getText().toString().equals("0")){
                    try{
                        int n = Integer.parseInt(holder.etCount.getText().toString())-1 ;
                        holder.etCount.setText(String.valueOf(n));
                        item.setQuantity(n);
                        SetMaterialQuantity(item.getCode(),n);
                        // if(item.getQuantity()>0)selectedMaterials.add(item);
                        // else selectedMaterials.remove(item);
                    }catch(Exception e){

                    }
                }else{
                    holder.down.setEnabled(false);
                }
            }
        }));
        return convertView;
    }

    private class ViewHolder {
        private TextView tvAnimal;
        protected CheckBox chkItem;
        private TextView txtMaterialName;
        private TextView txtPL;
        private EditText etCount;
        private ImageButton up;
        private ImageButton down;
    }

    private void SetMaterialQuantity(String code, int n) {
        for (int i = 0; i < MaterialDialog.mMaterialList.size(); i++) {
            if (MaterialDialog.mMaterialList.get(i).getCode().equals(code)) {
                //  MaterialObject m = MaterialDialog.mMaterialList.get(i);
                //  m.setQuantity(n);
                //  MaterialDialog.mMaterialList.add(i,m);
                MaterialDialog.mMaterialList.get(i).setQuantity(n);
                MaterialDialog.mMaterialList.get(i).setSelected(n>0);
                break;
            }
        }
        this.materialList=MaterialDialog.mMaterialList;

      //  notifyDataSetChanged();
    }
}
