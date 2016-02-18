package tk.foodpedia.android.model;

import tk.foodpedia.android.model.meta.DoubleValue;
import tk.foodpedia.android.model.meta.StringValue;

public class Product extends Downloadable {
    private StringValue ean;
    private StringValue name;
    private StringValue description;
    private StringValue mass;
    private DoubleValue energy;
    private DoubleValue proteins;
    private DoubleValue fat;
    private DoubleValue carbohydrates;
    private StringValue ingredients;

    public String getEan() {
        return ean == null ? null : ean.getValue();
    }

    public void setEan(StringValue ean) {
        this.ean = ean;
    }

    public String getName() {
        return name == null ? null : name.getValue();
    }

    public void setName(StringValue name) {
        this.name = name;
    }

    public String getDescription() {
        return description == null ? null : description.getValue();
    }

    public void setDescription(StringValue description) {
        this.description = description;
    }

    public String getMass() {
        return mass == null ? null : mass.getValue();
    }

    public void setMass(StringValue mass) {
        this.mass = mass;
    }

    public Double getEnergy() {
        return energy == null ? null : energy.getValue();
    }

    public void setEnergy(DoubleValue energy) {
        this.energy = energy;
    }

    public Double getProteins() {
        return proteins == null ? null : proteins.getValue();
    }

    public void setProteins(DoubleValue proteins) {
        this.proteins = proteins;
    }

    public Double getFat() {
        return fat == null ? null : fat.getValue();
    }

    public void setFat(DoubleValue fat) {
        this.fat = fat;
    }

    public Double getCarbohydrates() {
        return carbohydrates == null ? null : carbohydrates.getValue();
    }

    public void setCarbohydrates(DoubleValue carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public String getIngredients() {
        return ingredients == null ? null : ingredients.getValue();
    }

    public void setIngredients(StringValue ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "Product{" +
                "ean=" + ean +
                ", name=" + name +
                ", description=" + description +
                ", mass=" + mass +
                ", energy=" + energy +
                ", proteins=" + proteins +
                ", fat=" + fat +
                ", carbohydrates=" + carbohydrates +
                ", ingredients=" + ingredients +
                '}';
    }
}
