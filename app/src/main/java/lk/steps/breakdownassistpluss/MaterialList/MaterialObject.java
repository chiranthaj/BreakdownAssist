package lk.steps.breakdownassistpluss.MaterialList;

/**
 * Created by JagathPrasanga on 10/22/2017.
 */

public class MaterialObject {
    private boolean selected;
    private String materialName;
    private String materialCode;
    private int quantity;
    private String UserId;

    public MaterialObject(boolean selected, String materialCode, String materialName, int quantity) {
        this.selected = selected;
        this.materialName = materialName;
        this.materialCode = materialCode;
        this.quantity = quantity;

    }

    public boolean getSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public String getName() {
        return materialName;
    }

    public String getCode() {
        return materialCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public void setMaterialCode(String code) {
        this.materialCode = code;
    }
}
