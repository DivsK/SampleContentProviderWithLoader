package com.kellton.samplecontentproviderwithloader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * <h1><font color="orange">ContactsRecyclerViewAdapter</font></h1>
 * Adapter class for setting fetched contacts to the recycler view.
 *
 * @author Divya Khanduri
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
        if (contactDetails.ContactPhoto == null) {
            holder.ivContact.setImageResource(R.drawable.default_contact);
        } else {
            holder.ivContact.setImageURI(contactDetails.ContactPhoto);
        }
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
            ivContact = itemView.findViewById(R.id.iv_contacts);
            tvContactName = itemView.findViewById(R.id.tv_contact_name);
            tvContactNo = itemView.findViewById(R.id.tv_contact_number);
        }
    }
}
