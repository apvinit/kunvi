package xyz.redbooks.kunvi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Contact> contactList;

    public ContactAdapter(List<Contact> contactList){
        this.contactList = contactList;
    }

    @Override
    public int getItemCount(){
        return contactList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_card, viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Contact contact = contactList.get(i);
        viewHolder.vid.setText(Integer.toString(contact.getId()));
        viewHolder.vname.setText(contact.getName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView vid;
        TextView vname;

        public ViewHolder(View v) {
            super(v);
            vid = v.findViewById(R.id.srNo);
            vname = v.findViewById(R.id.name);
        }
    }
}
