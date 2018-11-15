package com.belstu.gulko.passwordHelper.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.belstu.gulko.passwordHelper.R;
import com.belstu.gulko.passwordHelper.activity.DetailsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PasswordRecyclerViewAdapter extends RecyclerView.Adapter<PasswordRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<String> accounts;
    private List<String> logins;
    private int position;

    public PasswordRecyclerViewAdapter() {
    }

    public PasswordRecyclerViewAdapter(Context context, List<String> accounts, List<String> logins) {
        this.context = context;
        this.accounts = accounts;
        this.logins = logins;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.linear_layout_simple_text, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.accountName.setText(accounts.get(position));
        holder.description.setText(logins.get(position));
        holder.iconText.setText(accounts.get(position).substring(0,1).toUpperCase());
        holder.iconBg.setImageResource(R.drawable.bg_circle);
        holder.iconBg.setColorFilter(getRandomMaterialColor());
        holder.accountContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = accounts.get(position);
                String dataL = logins.get(position);
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("Site", data);
                intent.putExtra("Login", dataL);
                context.startActivity(intent);
            }
        });
    }

    private int getRandomMaterialColor() {
        int returnColor = Color.GRAY;
        int arrayId = context.getResources().getIdentifier("mdcolor_400", "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.account_container)
        LinearLayout accountContainer;
        @BindView(R.id.account_name)
        TextView accountName;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.icon_text)
        TextView iconText;
        @BindView(R.id.icon_bg)
        ImageView iconBg;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
