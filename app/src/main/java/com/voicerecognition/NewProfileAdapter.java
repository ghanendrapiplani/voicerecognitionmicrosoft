package com.voicerecognition;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class NewProfileAdapter extends RecyclerView.Adapter<NewProfileAdapter.MyViewHolder> {
    ArrayList<NewProfileModel> list = new ArrayList<>();
    private Activity context;


    public NewProfileAdapter(ArrayList<NewProfileModel> list, Activity context) {
        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, identificationProfileId,sno;

        public MyViewHolder(View itemView) {
            super(itemView);
            identificationProfileId = (TextView) itemView.findViewById(R.id.identificationProfileId);
            username = (TextView) itemView.findViewById(R.id.username);
            sno = (TextView) itemView.findViewById(R.id.sno);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_newuser, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        NewProfileModel topic = list.get(position);
        System.out.println("bindview holder adapter"+topic.getUsername()+topic.getIdentificationProfileId());
        holder.username.setText(topic.getUsername());
        holder.identificationProfileId.setText(topic.getIdentificationProfileId());
        holder.sno.setText((position+1)+" ");
        holder.identificationProfileId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked item list");
                System.out.println(list.get(holder.getLayoutPosition()).getUsername());
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        VolleyHelper.getProfile(list.get(holder.getLayoutPosition()).getIdentificationProfileId(),context);
                        return null;
                    }
                }.execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        System.out.println(" listsize adapter getitemcount"+ list.size());
        return list.size();
    }

}
