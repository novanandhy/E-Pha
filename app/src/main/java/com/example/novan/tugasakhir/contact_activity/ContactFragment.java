package com.example.novan.tugasakhir.contact_activity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.novan.tugasakhir.R;
import com.example.novan.tugasakhir.models.Contact;
import com.example.novan.tugasakhir.models.Medicine;
import com.example.novan.tugasakhir.util.DataHelper;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Novan on 05/03/2017.
 */

public class ContactFragment extends Fragment {
    private View view;
    private ListView listView;
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    private String TAG = "TAGapp";
    DataHelper dataHelper;
    ArrayList<Contact> contacts;
    MyListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact, container, false);
        dataHelper = new DataHelper(getActivity().getApplicationContext());
        contacts = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.list_contact);


        PopulateAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ContactActivity.class);
                intent.putExtra("contact", contacts.get(i));
                intent.putExtra("name",contacts.get(i).getName());
                intent.putExtra("number",contacts.get(i).getNumber());
                intent.putExtra("id",contacts.get(i).getId());
                Log.d(TAG,"id selected= "+contacts.get(i).getId());
                startActivityForResult(intent,10);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_contact);
        fab.attachToListView(listView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {

            }

            @Override
            public void onScrollUp() {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, REQUEST_CODE_PICK_CONTACTS);
            }
        });
        return view;
    }

    private void PopulateAdapter() {
        contacts.clear();
        contacts = dataHelper.getAllContacts();
        listView.setAdapter(new MyListAdapter(getActivity().getApplicationContext(),R.layout.content_contact_list,contacts));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            contactPicked(data);
            PopulateAdapter();
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getActivity().getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            // Set the value to the textviews
            dataHelper.save_contact(name,phoneNo);
           Log.d(TAG,"Name= "+name);
           Log.d(TAG,"Phone= "+phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class MyListAdapter extends ArrayAdapter<Contact> {
        private int layout;
        public MyListAdapter(Context context, int resource, ArrayList<Contact> contact) {
            super(context, resource, contact);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout,parent,false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.contact_name = (TextView) convertView.findViewById(R.id.contact_name);
                viewHolder.contact_name.setText(contacts.get(position).getName());
                viewHolder.contact_number = (TextView) convertView.findViewById(R.id.contact_number);
                viewHolder.contact_number.setText(contacts.get(position).getNumber());
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_contact);
                Log.d(TAG,"id= "+contacts.get(position).getId());
                convertView.setTag(viewHolder);
            }else {
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.contact_name.setText(contacts.get(position).getName());
            }
            return convertView;
        }
    }

    public class ViewHolder{
        TextView contact_name;
        TextView contact_number;
        ImageView imageView;
    }
}
