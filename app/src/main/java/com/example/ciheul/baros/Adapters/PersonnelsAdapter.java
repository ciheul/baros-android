package com.example.ciheul.baros.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ciheul.baros.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ciheul on 09/03/17.
 */

public class PersonnelsAdapter extends RecyclerView.Adapter<PersonnelsAdapter.ViewHolder> {

    private JSONArray listOfPersonnel;

    public PersonnelsAdapter(JSONObject lPersonnel) {

        try {
            this.listOfPersonnel = (JSONArray) lPersonnel.get("data");
            this.listOfPersonnel.length();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_card_item_view, viewGroup, false);
            return new PersonnelsAdapter.ViewHolder(view);
        } else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_item, viewGroup, false);
            CasesAdapter.LoadingViewHolder loader = new CasesAdapter.LoadingViewHolder(view);
            return null;
        }

        return null;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbLoaderSpinner);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        try {
            System.out.println("currentDataAppended-on-"+i+": "+getItemCount());
            viewHolder.itemNama.setText(""+this.listOfPersonnel.getJSONObject(i).get("name"));
            viewHolder.itemPosition.setText(""+ listOfPersonnel.getJSONObject(i).get("rank")
                    + "/" + this.listOfPersonnel.getJSONObject(i).get("nrp"));
            viewHolder.itemPhone.setText(""+this.listOfPersonnel.getJSONObject(i).get("phone"));
//            viewHolder.itemNrp.setText(""+this.listOfPersonnel.getJSONObject(i).get("nrp"));
//            viewHolder.itemRank.setText(""+this.listOfPersonnel.getJSONObject(i).get("rank"));
//            viewHolder.itemIsInvestigator.setText(""+this.listOfPersonnel.getJSONObject(i).get("is_investigator"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return this.listOfPersonnel.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemNama;
        public TextView itemPosition;
        public TextView itemPhone;
        public TextView itemNrp;
        public TextView itemRank;
        public TextView itemIsInvestigator;

        public ViewHolder(View itemView) {
            super(itemView);

            itemNama = (TextView)itemView.findViewById(R.id.group_item_name);
            itemPosition = (TextView)itemView.findViewById(R.id.group_item_position);
            itemPhone = (TextView)itemView.findViewById(R.id.group_item_phone);
        }
    }
}
