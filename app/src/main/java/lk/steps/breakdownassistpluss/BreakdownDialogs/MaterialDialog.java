package lk.steps.breakdownassistpluss.BreakdownDialogs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.MaterialList.MaterialAdapter;
import lk.steps.breakdownassistpluss.MaterialList.MaterialObject;
import lk.steps.breakdownassistpluss.MaterialList.Store;
import lk.steps.breakdownassistpluss.R;
import lk.steps.breakdownassistpluss.Sync.SyncService;
import mehdi.sakout.fancybuttons.FancyButton;

public class MaterialDialog extends AppCompatActivity {
    Breakdown breakdown;
    public static List<MaterialObject> mMaterialList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_material);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            String json  = bundle.getString("breakdown");
            breakdown = new Gson().fromJson(json, new TypeToken<Breakdown>() {}.getType());

            mMaterialList = new ArrayList<>();

          //  List<MaterialObject> _MaterialList = Globals.dbHandler.getMaterials(breakdown.get_Job_No());
            List<MaterialObject> selectedMaterials = Globals.dbHandler.getMaterials(breakdown.get_Job_No());

           for (int i = 0; i < Store.Materials.length; i++) {
                MaterialObject _obj = Search( selectedMaterials, Store.Materials[i][1]);
                int n = 0;
                boolean selected = false;
                if (_obj != null) {
                    n = _obj.getQuantity();
                    selected = true;
                }
                MaterialObject obj = new MaterialObject(selected, Store.Materials[i][1], Store.Materials[i][2], n);
                mMaterialList.add(obj);
            }


            ListView listView = (ListView) findViewById(R.id.listView);
            final MaterialAdapter adapter = new MaterialAdapter(this, R.layout.material_row,mMaterialList);
            listView.setAdapter(adapter);


            TextView txtView = (TextView) findViewById(R.id.jobInfo);
            if (breakdown.NAME != null)
                txtView.setText(breakdown.get_Job_No() + "\n" + breakdown.NAME.trim() + "\n" + breakdown.ADDRESS.trim());
            else
                txtView.setText(breakdown.get_Job_No());

            FancyButton btnAddMaterials = (FancyButton) findViewById(R.id.btnAddMaterials);
            // if button is clicked, close the job_dialog dialog
            btnAddMaterials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //for (int i=0;i<MaterialAdapter.selectedMaterials.size();i++){
                    //    MaterialObject item = MaterialAdapter.selectedMaterials.get(i);
                    //}
                    Globals.dbHandler.addMaterials(breakdown.get_Job_No(), mMaterialList);
                    try{
                        SyncService.PostMaterials(getApplicationContext());
                    }catch(Exception e){}
                    finish();
                }
            });
            FancyButton btnCancel = (FancyButton) findViewById(R.id.btnCancel);
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
