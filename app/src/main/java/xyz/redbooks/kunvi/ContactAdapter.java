package xyz.redbooks.kunvi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Contact> contactList;
    private CustomClickListener customClickListener;

    public interface CustomClickListener {
        void onDeleteButtonClick(View v, int position);
    }

    public ContactAdapter(List<Contact> contactList, CustomClickListener clickListener){
        this.contactList = contactList;
        customClickListener = clickListener;
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
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        Contact contact = contactList.get(i);
        viewHolder.vid.setText(Integer.toString(contact.getId()));
        viewHolder.vname.setText(contact.getName());
        viewHolder.vDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customClickListener.onDeleteButtonClick(view,i);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView vid;
        TextView vname;
        ImageButton vDelButton;

        public ViewHolder(View v) {
            super(v);
            vid = v.findViewById(R.id.srNo);
            vname = v.findViewById(R.id.name);
            vDelButton = v.findViewById(R.id.delButton);
        }
    }
}
