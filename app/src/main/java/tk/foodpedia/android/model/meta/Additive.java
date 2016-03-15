package tk.foodpedia.android.model.meta;

public class Additive {
    private StringValue additive;

    public void setAdditive(StringValue additive) {
        this.additive = additive;
    }

    public String getValue() {
        return additive == null ? null : additive.getValue();
    }
}
