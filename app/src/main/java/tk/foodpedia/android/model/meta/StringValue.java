package tk.foodpedia.android.model.meta;

import java.io.Serializable;

public class StringValue implements Serializable {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
