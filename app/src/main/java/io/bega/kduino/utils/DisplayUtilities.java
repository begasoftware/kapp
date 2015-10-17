package io.bega.kduino.utils;



import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import io.bega.kduino.R;
import io.bega.kduino.activities.MainActivity;
import io.bega.kduino.kdUINOApplication;


/**
 * Utilidades generales, mensajes y alertas
 */
public class DisplayUtilities {

    public static void ShowNotification(Context ctx, Activity activity, View view, String message)
    {



        //utilizamos un NotificationCompat.Builder para configurar nuestra notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);

        //añadimos el icono que aparecerá en la notificación
        builder.setSmallIcon(R.drawable.citclopslogo);

        //añadimos la funcionalidad que tendrá cuando la clicken, accederá a la pantalla main.
        Intent resultIntent = new Intent(ctx, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, resultIntent, 0);
        builder.setContentIntent(pendingIntent);

        //añadimos el texto a mostrar
        builder.setTicker("Kduino Data Sended");

        //le añadimos funcionalidades de vibración
        builder.setVibrate(new long[]{100, 800});

        //añadimos el mensaje a mostrar
        builder.setContentText(message);

        //lanzamos la notifiación en la barra
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, builder.build());




    }

    public static void ShowLargeMessage(String message,
                                        String action,
                                        View parentView,
                                        Boolean always,
                                        View.OnClickListener listener) {

        // TODO REVIEW WHEN ARDUINO IS DISCONNECTED
        int duration = Snackbar.LENGTH_LONG;
        if (always) {
            duration = Snackbar.LENGTH_INDEFINITE;
        }

        Snackbar snackbar = Snackbar
                .make(parentView, message, duration);
        if (listener != null) {
            snackbar.setAction(action, listener);
        }

        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        try {
            snackbar.show();
        }
        catch(Exception ex)
        {
            Log.d(kdUINOApplication.TAG, "Error displaying snackbar", ex);
        }
    }
	

	public static void AMostrarmensajelargo(Context context,View view,String message)
    {
        if (context == null)
        {
            return;
        }

        if (view == null)
        {
            return;
        }

        CharSequence text = message;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) view.findViewById(R.id.custom_toast_layout));
        TextView text1 = (TextView) layout.findViewById(R.id.textToShow);
        text1.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }

    public static void AMostrarmensajecorto(Context context,View view,String message) {

        if (context == null)
        {
            return;
        }

        if (view == null)
        {
            return;
        }

        CharSequence text = message;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) view.findViewById(R.id.custom_toast_layout));
        TextView text1 = (TextView) layout.findViewById(R.id.textToShow);
        text1.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    public static void Adialogo(View view,String cadena) {


        //se prepara la alerta creando nueva instancia
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
        dialogBuilder.setMessage(cadena);
        dialogBuilder.setIcon(R.drawable.citclopslogo);
        dialogBuilder.setCancelable(true).setTitle("Look!");
        dialogBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

                dialog.cancel();
            }
        });
        //mostramos el dialogBuilder
        dialogBuilder.create().show();
    }
}

