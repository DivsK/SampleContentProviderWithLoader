package com.kellton.samplecontentproviderwithloader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ubuntu on 4/9/17.
 */

class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<ContactDetails> mContactDetailsList;

    ContactsRecyclerViewAdapter(ArrayList<ContactDetails> contactDetailsList) {
        mContactDetailsList = contactDetailsList;
    }

    @Override
    public ContactsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_row, parent, false);

        return new ViewHolder(view);
    }

    void setmContactDetailsList(ArrayList<ContactDetails> mContactDetailsList) {
        this.mContactDetailsList = mContactDetailsList;
    }

    @Override
    public void onBindViewHolder(ContactsRecyclerViewAdapter.ViewHolder holder, int position) {
        ContactDetails contactDetails = mContactDetailsList.get(position);
        if (contactDetails.ContactPhoto != null)
            holder.ivContact.setImageURI(contactDetails.ContactPhoto);
        holder.tvContactName.setText(contactDetails.ContactName);
        holder.tvContactNo.setText(contactDetails.ContactNo);

    }

    @Override
    public int getItemCount() {
        return (null != mContactDetailsList ? mContactDetailsList.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        ImageView ivContact;
        TextView tvContactName;
        TextView tvContactNo;

        ViewHolder(View itemView) {
            super(itemView);
            ivContact = (ImageView) itemView.findViewById(R.id.iv_contacts);
            tvContactName = (TextView) itemView.findViewById(R.id.tv_contact_name);
            tvContactNo = (TextView) itemView.findViewById(R.id.tv_contact_number);
        }
    }
}
