package com.example.gerenciadordecustos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gerenciadordecustos.model.Product;

import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private List<Product> products;
    private LayoutInflater inflater;

    public ProductAdapter(Context context, List<Product> products) {
        this.products = products;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_product, parent, false);
        }

        TextView listItemName = convertView.findViewById(R.id.listItemName);
        TextView listItemPrice = convertView.findViewById(R.id.listItemPrice);

        Product product = (Product) getItem(position);

        listItemName.setText(product.name);
        String costAmountText = String.format("R$%.2f", product.price);
        listItemPrice.setText(costAmountText);

        return convertView;
    }
}