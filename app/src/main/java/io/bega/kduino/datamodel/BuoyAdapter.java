/*package com.begasoftware.worktask.datamodel;


import android.widget.ArrayAdapter;

import java.util.List;

import com.begasoftware.worktask.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderTaskAdapter extends BaseSwipeListViewListener {
	List   data;
    Context context;
    int layoutResID;

    public OrderTaskAdapter(Context context, int layoutResourceId,List data) {
	    super(context, layoutResourceId, data);
	
	    this.data=data;
	    this.context=context;
	    this.layoutResID=layoutResourceId;

    // TODO Auto-generated constructor stub
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    NewsHolder holder = null;
	    View row = convertView;
	     holder = null;
	
	   if(row == null)
	   {
	       LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	       row = inflater.inflate(layoutResID, parent, false);
	
	       holder = new NewsHolder();	
	       holder.itemName = (TextView)row.findViewById(R.id.custom_row_textview);
	       holder.icon= (ImageView)row.findViewById(R.id.custom_row_imageview);
	       holder.button1=(Button)row.findViewById(R.id.custom_button_swipe1);
	       holder.button2=(Button)row.findViewById(R.id.custom_button_swipe2);
	       holder.button3=(Button)row.findViewById(R.id.custom_button_swipe3);
	       row.setTag(holder);

	   }
	   else
	   {
	       holder = (NewsHolder)row.getTag();
	   }
	
	   OrderTask itemdata = (OrderTask)data.get(position);
	   holder.itemName.setText(itemdata.getOrderID() + " " + itemdata.getOrderLocator());
	   // holder.icon.setImageDrawable(itemdata.getIcon());
	
	   holder.button1.setOnClickListener(new View.OnClickListener() {
	
	             @Override
	             public void onClick(View v) {
	                   // TODO Auto-generated method stub
	                   Toast.makeText(context, "Button 1 Clicked",Toast.LENGTH_SHORT).show();
	             }
	       });
	
	        holder.button2.setOnClickListener(new View.OnClickListener() {
	
	                         @Override
	                         public void onClick(View v) {
	                               // TODO Auto-generated method stub
	                               Toast.makeText(context, "Button 2 Clicked",Toast.LENGTH_SHORT).show();
	                         }
	                   });
	
	        holder.button3.setOnClickListener(new View.OnClickListener() {
	
	                   @Override
	                   public void onClick(View v) {
	                         // TODO Auto-generated method stub
	                         Toast.makeText(context, "Button 3 Clicked",Toast.LENGTH_SHORT);
	                   }
	             });
	
	   return row;
	}
	
	static class NewsHolder{
		 
	      TextView itemName;
	      ImageView icon;
	      Button button1;
	      Button button2;
	      Button button3;
	      }

}*/

package io.bega.kduino.datamodel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.R;
import io.bega.kduino.kdUINOApplication;

public class BuoyAdapter extends BaseAdapter {

    private List<KDUINOBuoy> data;
    private Context context;
    private int viewId;

    public BuoyAdapter(Context context, List<KDUINOBuoy> data, int viewId) {
        this.context = context;
        this.data = data;
        this.viewId = viewId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public KDUINOBuoy getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final KDUINOBuoy item = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(viewId, parent, false);
            holder = new ViewHolder();
            holder.txtName = (TextView)convertView.findViewById(R.id.kduino_row_name_value);
            holder.txtMaker = (TextView)convertView.findViewById(R.id.kduino_row_maker_value);
            holder.txtUser = (TextView)convertView.findViewById(R.id.kduino_row_user_value);
            holder.txtSensors = (TextView)convertView.findViewById(R.id.kduino_row_sensors_value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtUser.setText(item.User);
        holder.txtName.setText(item.Name);
        holder.txtSensors.setText(item.Sensors + "");
        holder.txtMaker.setText(item.Maker);
       /* holder.editTextDeep.setText(Double.toString(item.Deep));
        holder.editTextDeep.setTag(item);
        holder.editTextDeep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d(kdUINOApplication.TAG, "Set Data Deep");
                    String value = ((EditText) v).getText().toString();
                    if (value.length() > 0) {
                        ((Sensor) v.getTag()).Deep = Double.parseDouble(value);
                    }
                }
            }
        }); */

        return convertView;

    }

    static class ViewHolder {
        TextView txtUser;
        TextView txtName;
        TextView txtMaker;
        TextView txtSensors;
    }


}






