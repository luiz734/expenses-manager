package com.example.gerenciadordecustos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gerenciadordecustos.model.Ingredient;

import java.util.List;

public class IngredientAdapter extends BaseAdapter {

    private List<Ingredient> ingredients;
    private LayoutInflater inflater;

    public IngredientAdapter(Context context, List<Ingredient> ingredients) {
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
            convertView = inflater.inflate(R.layout.list_item_ingredient, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewCost = convertView.findViewById(R.id.textViewCostAmount);

        Ingredient ingredient = (Ingredient) getItem(position);

        textViewName.setText(ingredient.name);
        String costAmountText = String.format("R$%.2f por %.2f unidade(s)", ingredient.cost, ingredient.amount);
        textViewCost.setText(costAmountText);

        return convertView;
    }
}