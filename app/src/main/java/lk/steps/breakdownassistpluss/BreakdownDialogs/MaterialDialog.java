package lk.steps.breakdownassistpluss.BreakdownDialogs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.MaterialList.MaterialObject;
import lk.steps.breakdownassistpluss.MaterialList.MaterialViewsAdapter;
import lk.steps.breakdownassistpluss.MaterialList.Store;
import lk.steps.breakdownassistpluss.Models.JobChangeStatus;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.Strings;
import lk.steps.breakdownassistpluss.Sync.SyncService;

public class MaterialDialog extends AppCompatActivity {
    Breakdown breakdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_material);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            String json  = bundle.getString("breakdown");
            breakdown = new Gson().fromJson(json, new TypeToken<Breakdown>() {}.getType());


            List<MaterialObject> MaterialList = new ArrayList<>();
            List<MaterialObject> _MaterialList = Globals.dbHandler.getMaterials(breakdown.get_Job_No());

            for (int i = 0; i < Store.Materials.length; i++) {
                MaterialObject _obj = Search(_MaterialList, Store.Materials[i][1]);
                int n = 0;
                if (_obj != null) {
                    n = _obj.getQuantity();
                }
                MaterialObject obj = new MaterialObject(false, Store.Materials[i][1], Store.Materials[i][2], n);
                MaterialList.add(obj);
            }

            ListView listView = (ListView) findViewById(R.id.listView);
            final MaterialViewsAdapter adapter = new MaterialViewsAdapter(this, R.layout.material_row, MaterialList);
            listView.setAdapter(adapter);


            TextView txtView = (TextView) findViewById(R.id.jobInfo);
            if (breakdown.NAME != null)
                txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());
            else
                txtView.setText(breakdown.get_Job_No());

            ImageButton btnAddMaterials = (ImageButton) findViewById(R.id.btnAddMaterials);
            // if button is clicked, close the job_dialog dialog
            btnAddMaterials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //for (int i=0;i<MaterialViewsAdapter.selectedMaterials.size();i++){
                    //    MaterialObject item = MaterialViewsAdapter.selectedMaterials.get(i);
                    //}
                    Globals.dbHandler.addMaterials(breakdown.get_Job_No(), MaterialViewsAdapter.selectedMaterials);
                    try{
                        SyncService.PostMaterials(getApplicationContext());
                    }catch(Exception e){}
                    finish();
                }
            });
            ImageButton btnCancel = (ImageButton) findViewById(R.id.btnCancel);
            // if button is clicked, close the job_dialog dialog
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //RestoreCard(fragment,breakdown, position);
                    finish();
                }
            });
        }
        this.setFinishOnTouchOutside(false);
    }

    private static MaterialObject Search(List<MaterialObject> list, String materialCode) {
        if (list == null) return null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCode().contains(materialCode)) {
                return list.get(i);
            }
        }
        return null;
    }

}
