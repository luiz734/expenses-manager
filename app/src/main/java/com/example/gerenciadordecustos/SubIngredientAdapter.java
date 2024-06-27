package com.example.gerenciadordecustos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gerenciadordecustos.model.Ingredient;

import java.util.List;

public class SubIngredientAdapter extends BaseAdapter {

    private List<Ingredient> ingredients;
    private LayoutInflater inflater;

    public SubIngredientAdapter(Context context, List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sub_product_row, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.SubProductName);
        TextView textViewAmount = convertView.findViewById(R.id.SubProductAmount);

        Ingredient ingredient = (Ingredient) getItem(position);

        textViewName.setText(ingredient.name);
        String costAmountText = String.format("Qtde: %.2f    R$%.2f", ingredient.amount, ingredient.cost);
        textViewAmount.setText(costAmountText);

        return convertView;
    }
}