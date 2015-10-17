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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.bega.kduino.R;
import io.bega.kduino.kdUINOApplication;
import io.bega.valuebar.ValueBar;
import io.bega.valuebar.ValueBarSelectionListener;

public class SensorBuoyAdapter extends BaseAdapter {

    private List<Sensor> data;
    private Context context;
    private int viewId;
    private boolean activateChecks = false;
    private Map mapDeeps = new HashMap();
    private float maxDeep;

    public void refreshMaxDeep()
    {
        double max = 0f;
        for (Sensor sensor:data)
        {
            max = Math.max(max, sensor.Deep);
        }

        this.setMaxDeep((float) max);
    }

    public void setMaxDeep(float maxDeep)
    {
        if (maxDeep == 0)
        {
            if (this.data.size() > 0)
            refreshMaxDeep();
            return;
        }

        this.maxDeep = maxDeep;
        this.notifyDataSetChanged();
    }

    public float getMaxDeep()
    {
        return this.maxDeep;
    }

    public SensorBuoyAdapter(Context context, List<Sensor> data, int viewId, boolean activateChecks, int maxDeep) {
        this.context = context;
        this.data = data;
        this.viewId = viewId;
        this.activateChecks = activateChecks;
        this.setMaxDeep(maxDeep);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getViewTypeCount() {
        if (data == null || data.size() == 0)
        {
            return 1;
        }

        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Sensor getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Sensor item = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(viewId, parent, false);
            holder = new ViewHolder();
            holder.textViewID = (TextView)convertView.findViewById(R.id.sensor_definition_id_value);
            holder.editTextDeep = (EditText)convertView.findViewById(R.id.sensor_definition_deep_value);
            holder.valueBar = (ValueBar)convertView.findViewById(R.id.sensor_definition_valueBar);
            holder.valueBar.setInterval(0.01f);
            holder.valueBar.setTouchEnabled(false);
            holder.valueBar.setTag(item);
            holder.valueBar.setValueBarSelectionListener(new ValueBarSelectionListener() {
                @Override
                public void onSelectionUpdate(float val, float maxval, float minval, ValueBar bar) {
                    Sensor sensor  = (Sensor)bar.getTag();
                    sensor.Deep = val;
                }

                @Override
                public void onValueSelected(float val, float maxval, float minval, ValueBar bar) {

                }
            });

           // holder.checkBoxEnable = (CheckBox)convertView.findViewById(R.id.sensor_definition_active_cb_value);
           // holder.checkBoxEnable.setEnabled(activateChecks);
            holder.editTextDeep.setEnabled(!activateChecks);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

            Sensor sensor = (Sensor)holder.valueBar.getTag();
            //holder.valueBar.setValue((float)sensor.Deep);
        }


        if (this.maxDeep != 0) {
            holder.valueBar.setMinMax(0f, maxDeep);
        }
        holder.valueBar.setValue((float)item.Deep);

        /*if (holder.checkBoxEnable.getTag()== null) {
            holder.checkBoxEnable.setChecked(item.Enabled);
            holder.checkBoxEnable.setTag(item);
            holder.checkBoxEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Sensor sensor = (Sensor)buttonView.getTag();
                    sensor.Enabled =  isChecked;
                    //buttonView.setTag(isChecked);
                }
            });
        } */


        holder.sensorData = item;
        holder.textViewID.setText(item.SensorID);

       /* if (item.Deep != 0)
        {
            String deep = Double.toString(item.Deep);
            holder.editTextDeep.setText(deep);
        }
        else
        {
            holder.editTextDeep.setText("0");
            holder.editTextDeep.setHint("0.0");
        }

        holder.editTextDeep.setTag(item);
        holder.editTextDeep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d(kdUINOApplication.TAG, "Set Data Deep");
                    String value = ((EditText) v).getText().toString();
                    if (value.length() > 0) {
                        ((Sensor) v.getTag()).Deep = Double.parseDouble(value);
                    } else {
                        ((EditText) v).setError("Sensor deep is required!");
                    }
                } else {
                    ((EditText) v).selectAll();
                }
            }
        }); */

        return convertView;

    }

    class ViewHolder {
        TextView textViewID;
        EditText editTextDeep;
        Sensor sensorData;
        ValueBar valueBar;
        // CheckBox checkBoxEnable;
    }




}






