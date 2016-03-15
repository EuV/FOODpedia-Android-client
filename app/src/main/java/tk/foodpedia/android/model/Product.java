package tk.foodpedia.android.model;

import android.text.TextUtils;

import java.util.ArrayList;

import tk.foodpedia.android.R;
import tk.foodpedia.android.model.meta.DoubleValue;
import tk.foodpedia.android.model.meta.StringValue;

import static tk.foodpedia.android.util.StringHelper.format;

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
    private final ArrayList<String> additives = new ArrayList<>();

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

    public String getMassFormatted() {
        return format(R.string.format_mass, getMass() == null ? "?" : getMass());
    }

    public void setMass(StringValue mass) {
        this.mass = mass;
    }

    public Double getEnergy() {
        return energy == null ? null : energy.getValue();
    }

    public String getEnergyFormatted() {
        return format(R.string.format_energy, getEnergy() == null ? "?" : getEnergy());
    }

    public void setEnergy(DoubleValue energy) {
        this.energy = energy;
    }

    public Double getProteins() {
        return proteins == null ? null : proteins.getValue();
    }

    public String getProteinsFormatted() {
        return format(R.string.format_proteins, getProteins() == null ? "?" : getProteins());
    }

    public void setProteins(DoubleValue proteins) {
        this.proteins = proteins;
    }

    public Double getFat() {
        return fat == null ? null : fat.getValue();
    }

    public String getFatFormatted() {
        return format(R.string.format_fat, getFat() == null ? "?" : getFat());
    }

    public void setFat(DoubleValue fat) {
        this.fat = fat;
    }

    public Double getCarbohydrates() {
        return carbohydrates == null ? null : carbohydrates.getValue();
    }

    public String getCarbohydratesFormatted() {
        return format(R.string.format_carbohydrates, getCarbohydrates() == null ? "?" : getCarbohydrates());
    }

    public void setCarbohydrates(DoubleValue carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public String getIngredients() {
        return ingredients == null ? null : ingredients.getValue();
    }

    public String getIngredientsFormatted() {
        return getIngredients() == null ? "" : format(R.string.format_ingredients, getIngredients());
    }

    public void setIngredients(StringValue ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getAdditives() {
        return additives;
    }

    public String getAdditivesFormatted() {
        return additives.isEmpty() ? "" : format(R.string.format_additives, TextUtils.join(", ", additives));
    }

    public void addAdditive(String additive) {
        if (additive != null) {
            additives.add(additive);
        }
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
                ", additives=" + additives +
                '}';
    }
}
