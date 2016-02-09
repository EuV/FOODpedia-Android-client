package tk.foodpedia.android.model.meta;

import java.io.Serializable;

public class DoubleValue implements Serializable {
    private Double value;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + "";
    }
}
