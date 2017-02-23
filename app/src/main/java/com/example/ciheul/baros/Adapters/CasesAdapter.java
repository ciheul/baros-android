package com.example.ciheul.baros.Adapters;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ciheul.baros.Fragments.CasesFragment;
import com.example.ciheul.baros.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ciheul on 21/02/17.
 * NOTE: ViewHolder for rendering to template
 **/
public class CasesAdapter extends RecyclerView.Adapter<CasesAdapter.ViewHolder> {

    private JSONArray listOfCases;
    private boolean noMoreDataToLoad;

    public CasesAdapter(JSONObject lCases) {

        try {
            this.listOfCases = (JSONArray) lCases.get("cases");
            this.listOfCases.length();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemNumber;
        public TextView itemLPDate;
        public TextView itemDescription;
        public TextView itemCaseType;
        public TextView itemCaseStatus;

        public Snackbar snackbar;

        public ViewHolder(View itemView) {
            super(itemView);

            itemNumber = (TextView)itemView.findViewById(R.id.card_item_number);
            itemLPDate = (TextView)itemView.findViewById(R.id.card_item_date);
            itemDescription = (TextView)itemView.findViewById(R.id.card_item_description);
            itemCaseType = (TextView)itemView.findViewById(R.id.card_item_type);
            itemCaseStatus = (TextView)itemView.findViewById(R.id.case_item_status);


            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        int position = getAdapterPosition();

                        // TODO go INTENT case detail view class
                        Snackbar.make(v, "Click detected on item " + position,
                                Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                                .setAction(R.string.snack_bar_action, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        v.getRootView().scrollTo(0,0);
                                    }
                                }).show();
                    }

                });
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("hellow");
            }
        }
    }


    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    @Override
    public int getItemViewType(int position) {
        try {
            return this.listOfCases.getJSONObject(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbLoaderSpinner);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        /*View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        System.out.println("oncreateviewholder~~: "+i);
        return viewHolder;*/

        System.out.println("oncreateviewholder~~: "+i);

        /*SHOULD BE LIKE THIS but current class is not support this next time maybe*/
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_view, viewGroup, false);
            return new ViewHolder(view);
        } else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_item, viewGroup, false);
            LoadingViewHolder loader = new LoadingViewHolder(view);
            return null;
        }


        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        try {
            /*FloatingActionButton vFab = (FloatingActionButton) viewHolder.itemView.getRootView().findViewById(R.id.fab);
            vFab.setVisibility(View.GONE);*/

            if (i == getItemCount()-1) {
                /*FloatingActionButton vFab = (FloatingActionButton) viewHolder.itemView.getRootView().findViewById(R.id.fab);
                vFab.setVisibility(View.GONE);*/
                Snackbar.make(viewHolder.itemView.getRootView(),"Tidak ada kasus, kembali ke atas",Snackbar.LENGTH_LONG)
                        .setAction(R.string.snack_bar_action, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecyclerView rv = (RecyclerView) viewHolder.itemView.getRootView().findViewById(R.id.recycler_view);
                                rv.smoothScrollToPosition(0);
                            }
                        }).show();
            } /*else {
                vFab.setVisibility(View.VISIBLE);
            }*/

            System.out.println("currentDataAppended-on-"+i+": "+getItemCount());
            viewHolder.itemNumber.setText(""+this.listOfCases.getJSONObject(i).get("number"));
            viewHolder.itemDescription.setText(""+ cutOfDescription(this.listOfCases.getJSONObject(i).get("description")+""));
            viewHolder.itemLPDate.setText(""+this.listOfCases.getJSONObject(i).get("lp_date"));
            viewHolder.itemCaseType.setText(""+this.listOfCases.getJSONObject(i).get("type"));
            viewHolder.itemCaseStatus.setText(""+this.listOfCases.getJSONObject(i).get("progress"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.listOfCases.length();
    }

    /***HELPER***/
    private String cutOfDescription(String desc) {
        if (desc.length() >= 160) {
            desc = desc.substring(0,160) + "...";
        }
        return desc;
    }
}