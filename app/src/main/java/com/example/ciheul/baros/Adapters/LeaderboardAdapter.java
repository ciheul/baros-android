package com.example.ciheul.baros.Adapters;

import android.support.v7.app.AlertDialog;
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
 * Created by ciheul on 08/03/17.
 */

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private JSONArray listOfPersonnel;

    public LeaderboardAdapter(JSONObject lPersonnel) {
        try {
            this.listOfPersonnel = (JSONArray) lPersonnel.get("leaderboard");
            this.listOfPersonnel.length();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemCounter;
        public TextView itemName;
        public TextView itemTotalScore;


        public ViewHolder(final View itemView) {
            super(itemView);
            itemCounter = (TextView)itemView.findViewById(R.id.card_leaderboard_counter);
            itemName = (TextView)itemView.findViewById(R.id.card_leaderboard_name);
            itemTotalScore = (TextView)itemView.findViewById(R.id.card_leaderboard_totalscore);

/*            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        int position = getAdapterPosition();
                        int pk, total_score, total_1 = 0;
                        String nama, posisi = "";


                        try {
                            pk = (int) listOfPersonnel.getJSONObject(position).get("pk");
                            nama = (String) listOfPersonnel.getJSONObject(position).get("name");
                            posisi = (String) listOfPersonnel.getJSONObject(position).get("position");
                            total_score = (int) listOfPersonnel.getJSONObject(position).get("total_score");

                            AlertDialog.Builder alert = new AlertDialog.Builder(itemView.getContext());

                            View popup = LayoutInflater.from(itemView.getContext()).inflate(R.layout.leaderboard_item,null);
                            TextView tNama = (TextView) popup.findViewById(R.id.leaderboard_item_name);
                            tNama.setText(nama);
                            TextView tPosisi = (TextView) popup.findViewById(R.id.leaderboard_item_position);
                            tPosisi.setText(posisi);
                            TextView tTotalScore = (TextView) popup.findViewById(R.id.leaderboard_item_totalscore);
                            tTotalScore.setText(Integer.toString(total_score));

                            alert.setView(popup);
                            alert.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                     }

                });
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("hellow");
            }*/
        }
    }

    private int VIEW_TYPE_ITEM = 0;
    private int VIEW_TYPE_LOADING = 1;

    @Override
    public int getItemViewType(int position) {
        try {
            return this.listOfPersonnel.getJSONObject(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        System.out.println("oncreateviewholder++: "+i);

        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.leaderboard_card_item, viewGroup, false);
            return new ViewHolder(view);
        } else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_item, viewGroup, false);
            LoadingViewHolder loading = new LoadingViewHolder(view);
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
        System.out.println("currentData"+i+": "+getItemCount());
        try {
            viewHolder.itemCounter.setText(""+(i+1));
            viewHolder.itemName.setText(""+this.listOfPersonnel.getJSONObject(i).get("name"));
            viewHolder.itemTotalScore.setText(""+this.listOfPersonnel.getJSONObject(i).get("total_score"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return this.listOfPersonnel.length();
    }
}
