package io.bega.kduino.api;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import io.bega.kduino.R;
import io.bega.kduino.datamodel.BuoyDefinition;
import io.bega.kduino.datamodel.KdUINOErrorHandler;
import io.bega.kduino.maps.KdUINOOverlayItem;
import io.bega.kduino.maps.LocationOverlayItem;
import io.bega.kduino.utils.DisplayUtilities;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by usuario on 01/08/15.
 */
    public class KdUINOMapAPIService {

    DefaultResourceProxyImpl mResourceProxy;

    ArrayList<OverlayItem> items;

    ArrayList<LocationOverlayItem> centerItems;

    ItemizedIconOverlay itemizedIconOverlay;

    ItemizedIconOverlay<LocationOverlayItem> locationIconOverlay;

    MapView map;

    Activity activity;

    String currentID;

    MaterialDialog dialog;

    GeoPoint currentPosition;

    public KdUINOMapAPIService(
            Activity activity,
            MapView map,
            DefaultResourceProxyImpl proxy,
            ArrayList<OverlayItem> items,
            ItemizedIconOverlay itemizedIconOverlay,
            ArrayList<LocationOverlayItem> centerPoints,
            ItemizedIconOverlay<LocationOverlayItem> centerPointsIconOverlay)
    {
        this.activity = activity;
        this.map = map;
        this.items = items;
        this.itemizedIconOverlay = itemizedIconOverlay;
        this.mResourceProxy = proxy;
        this.centerItems = centerPoints;
        this.locationIconOverlay = centerPointsIconOverlay;
    }

    private void getDialog(KdUINOOverlayItem item)
    {
        ImageView image = new ImageView(this.activity);

        Picasso.with(this.activity).load(item.getPlotKD()).into(image);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this.activity).
                        setMessage("").
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        setView(image);
        builder.create().show();
    }

    public void setCenter(GeoPoint point)
    {
        if (items == null)
        {
            return;
        }

        this.currentPosition = point;
        LocationOverlayItem toDelete = null;
        for (OverlayItem item : items)
        {
            if (item instanceof  LocationOverlayItem)
            {
                toDelete = (LocationOverlayItem)item;
                break;
            }
        }

        if (toDelete != null)
        {
            items.remove(toDelete);
            map.invalidate();
        }

        if (currentPosition != null)
        {
            /*Polygon circle = new Polygon(activity);
            circle.setPoints(Polygon.pointsAsCircle(currentPosition, 20.0));
            circle.setPoints(Polygon.pointsAsCircle(currentPosition, 200.0));
            circle.setFillColor(0x12121212);
            circle.setStrokeColor(Color.RED);
            circle.setStrokeWidth(3);
            circle.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, map));
            circle.setTitle("Current position: " + currentPosition.getLatitude() + "," + currentPosition.getLongitude()); */

            if (map.getOverlays().size() > 1) {
                map.getOverlays().remove(1);
            }

            for(Overlay overlay : map.getOverlays())
            {
                 if (overlay instanceof  ItemizedIconOverlay)
                 {
                     String test = overlay.toString();
                 }
            }

           // map.getOverlays().add(circle);

            centerItems.clear();
            LocationOverlayItem locationOverlayItem = new LocationOverlayItem(
                    this.activity.getString(R.string.gps_my_poistion),
                    "", currentPosition);
            locationOverlayItem.setMarker(activity.getResources().getDrawable(R.drawable.ic_action_my_position));
            centerItems.add(locationOverlayItem);

            if (this.locationIconOverlay == null) {
                this.locationIconOverlay = new ItemizedIconOverlay<LocationOverlayItem>(centerItems, null, mResourceProxy);
            }

            if (map.getOverlays().contains(locationIconOverlay)) {
                map.getOverlays().remove(locationIconOverlay);
            }

            map.getOverlays().add(locationIconOverlay);
            map.invalidate();
        }

    }

    public void getAllBuoys(String id)
    {
        RestAdapter retrofit = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new KdUINOErrorHandler())
                .setEndpoint(KdUINOApiServer.API_URL)
                .build();

        currentID = id;

        // Create an instance of our GitHub API interface.
        final KdUINOApiServer kdUINOApiService = retrofit.create(KdUINOApiServer.class);

        this.showDataDialog();

        kdUINOApiService.getBuoy
                (new Callback<List<BuoyDefinition>>() {

                    @Override
                    public void success(List<BuoyDefinition> buoys, Response response) {
                        Log.d("test", response.toString());
                        items.clear();
                        map.getOverlays().clear();
                        map.invalidate();
                        if (currentPosition != null) {
                            setCenter(currentPosition);
                        }

                        for (BuoyDefinition buoy : buoys) {

                            if (currentID.length() > 0) {
                                if (!Long.toString(buoy.BuoyID).startsWith(currentID)) {
                                    continue;
                                }
                            }


                            KdUINOOverlayItem overlayItem =
                                    new KdUINOOverlayItem(buoy.Name, buoy.Maker, new GeoPoint(buoy.Lat, buoy.Lon), buoy.plotkd, buoy.User);
                            overlayItem.setMarker(activity.getResources().getDrawable(R.drawable.kduino_marker));
                            items.add(overlayItem);
                        }


                        itemizedIconOverlay = new ItemizedIconOverlay(items,
                                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                                    @Override
                                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                                        if (item instanceof LocationOverlayItem) {
                                            return true;
                                        }

                                        KdUINOOverlayItem kdUINOItem = ((KdUINOOverlayItem) item);
                                        String url_plotKd = kdUINOItem.getPlotKD();

                                        if (url_plotKd == null || url_plotKd.length() == 0) {
                                            AlertDialog.Builder builder =
                                                    new AlertDialog.Builder(activity).
                                                            setMessage(item.getTitle()).
                                                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            }).
                                                            setMessage("Generating chart in server. Processing data in Server.");
                                            builder.create().show();


                                            /* DisplayUtilities.ShowLargeMessage("No process data in this buoy",
                                                    null,
                                                    map,
                                                    false,
                                                    null); */


                                            return true;
                                        }

                                        ImageView image = new ImageView(activity);
                                        Picasso.with(activity).load(url_plotKd).into(image);

                                        /* TextView textView = new TextView(activity);
                                        textView.setText(kdUINOItem.getDescription()); */
                                        AlertDialog.Builder builder =
                                                new AlertDialog.Builder(activity).
                                                        setMessage(item.getTitle()).
                                                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        }).
                                                        setView(image);
                                        builder.create().show();


                                        return true;
                                    }

                                    @Override
                                    public boolean onItemLongPress(final int index, final OverlayItem item) {

                                        return false;
                                    }
                                },
                                mResourceProxy);

                        try {

                            if (map.getOverlays().contains(itemizedIconOverlay))
                            {
                                map.getOverlays().remove(itemizedIconOverlay);
                            }

                            /*if (map.getOverlays().size() > 1) {
                                map.getOverlays().remove(1);
                            }*/
                            map.getOverlays().add(itemizedIconOverlay);
                            map.invalidate();

                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }

                        } catch (Exception ex) {
                            Log.e("MAP", "error invalidation map", ex);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }


    private void showDataDialog()
    {
        dialog = new MaterialDialog.Builder(activity)
                .title(R.string.internet_download_title)
                .content(R.string.internet_download_message)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .progress(true,0)
                .build();
        dialog.show();
    }


}
