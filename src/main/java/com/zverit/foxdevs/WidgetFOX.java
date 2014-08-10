package com.zverit.foxdevs;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class WidgetFOX extends AppWidgetProvider {
    //public static String  str;
 //   public static String imgs;
    public static String url;
    public static String text;
    public static String ACTION_WIDGET_RECEIVER = "SYNC_EVENT";

    public void onUpdate(Context context, AppWidgetManager appWidgetProvider, int[] appWidgets){
        RemoteViews remoteViews;
        ComponentName componentName;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
        componentName = new ComponentName(context,WidgetFOX.class);

        remoteViews.setOnClickPendingIntent(R.id.sync_button, WidPend(context,ACTION_WIDGET_RECEIVER));
        appWidgetProvider.updateAppWidget(componentName,remoteViews);

    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context,intent);

        if(ACTION_WIDGET_RECEIVER.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName componentName;

            remoteViews = new RemoteViews(context.getPackageName(),R.layout.main);

            componentName = new ComponentName(context,WidgetFOX.class);
            getLinks(remoteViews, appWidgetManager,componentName,context);

           // update(context,appWidgetManager,"http://foxdevs.net" + url, componentName);

            appWidgetManager.updateAppWidget(componentName,remoteViews);
        }
    }

    public static  void update(final Context context, final  AppWidgetManager appWidgetManager, final String url,
                              final ComponentName componentName){
        new Thread(){
            public void run(){
                Bitmap bitmap = grab(url);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
                remoteViews.setImageViewBitmap(R.id.imgf, bitmap);
          //      appWidgetManager.updateAppWidget(componentName,remoteViews);
            }
        }.start();
    }
    private static Bitmap grab(final String url){
       try{
        return BitmapFactory.decodeStream(((java.io.InputStream) new URL(url).getContent()));
       }catch (Exception e){
           return null;
       }
    }
    private void getLinks(RemoteViews remoteViews,AppWidgetManager appWidgetManager,
                          ComponentName componentName, Context context){
          new ParseSite(remoteViews, appWidgetManager, componentName, context).execute("http://foxdevs.net/");
    }

    protected PendingIntent WidPend(Context context, String action){
        Intent  intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context,0,intent,0);
    }

    public class ParseSite extends AsyncTask<String, Void, List<String>> {

        private RemoteViews views;
        private AppWidgetManager widgetManager;
        private ComponentName componentName;
        private Context context;

        public ParseSite(RemoteViews remoteViews, AppWidgetManager appWidgetManager,
                        ComponentName componentName, Context context){
            this.views = remoteViews;
            this.widgetManager = appWidgetManager;
            this.componentName = componentName;
            this.context = context;
        }

        protected List<String> doInBackground(String... arg) {
            List<String> output = new ArrayList<String>();
            try
            {
                HtmlCleaner htmlCleaner = new HtmlCleaner();
                TagNode tagNode = htmlCleaner.clean(new URL(arg[0]));

                output.add(tagNode.evaluateXPath("//div[@id='mainEvent']/h3/a/text()")[0].toString().trim());
                output.add(tagNode.evaluateXPath("//div[@class='eventInfo']/div[@class='text']/text()")[0].toString().trim());
                output.add(tagNode.evaluateXPath("//div[@class='lector']/a/img/@src")[0].toString().trim());

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return output;
        }

        protected void onPostExecute(List<String> output) {
            text = output.get(1);
            url = output.get(2);
            views.setTextViewText(R.id.title,output.get(0));
            views.setTextViewText(R.id.body, output.get(1));
        //    update(context,widgetManager,output.get(2),componentName);
        //    widgetManager.updateAppWidget(componentName,views);
            new getImage(views, widgetManager,componentName).execute(output.get(2));
        }
    }

    public class getImage extends AsyncTask<String, Void, Bitmap> {

        private RemoteViews views;
        private AppWidgetManager widgetManager;
        private ComponentName componentName;
   //     private Context context;
        private Bitmap bitmap;

        public getImage(RemoteViews remoteViews, AppWidgetManager appWidgetManager,
                         ComponentName componentName){
            this.views = remoteViews;
            this.widgetManager = appWidgetManager;
            this.componentName = componentName;
    //        this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... arg) {
           bitmap = grab("http://foxdevs.net"+url);
           return bitmap;
        }

        protected void onPostExecute(Bitmap output) {
            views.setImageViewBitmap(R.id.imgf, bitmap);
            widgetManager.updateAppWidget(componentName,views);
        }
    }

}