package com.sun.imageloader.aexample;

import java.net.URISyntaxException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.imageloader.R;
import com.sun.imageloader.core.UrlImageLoader;
import com.sun.imageloader.core.UrlImageLoaderConfiguration;

public class MainActivity extends Activity {

	 String[] imageUrls = Constant.IMAGES;
	 private ListView listView;
	 UrlImageLoader sicImageLoader;

	@Override
     public void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.activity_main);
             ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 10, 120, TimeUnit.SECONDS,
                     new LinkedBlockingQueue<Runnable>());

             UrlImageLoaderConfiguration configs = new UrlImageLoaderConfiguration.Builder()
             .setDirectoryName("/storage/extSdCard/Test").setImageQuality(100).setMaxCacheMemorySize(10)
             .shouldLog(true).setImageType(CompressFormat.JPEG).useExternalStorage(true)
             .setThreadExecutor(executor).setOnloadingImage(new ColorDrawable(Color.BLACK)). 
             build(this);
             
             UrlImageLoader.getInstance().init(configs);
              sicImageLoader = UrlImageLoader.getInstance();

             listView = (ListView) findViewById(R.id.listView);
             ((ListView) listView).setAdapter(new ItemAdapter());
 
     }


     class ItemAdapter extends BaseAdapter {


             private class ViewHolder {
                     public TextView text;
                     public ImageView image;
             }

             @Override
             public int getCount() {
                     return imageUrls.length;
             }

             @Override
             public Object getItem(int position) {
                     return position;
             }

             @Override
             public long getItemId(int position) {
                     return position;
             }

             @Override
             public View getView(final int position, View convertView, ViewGroup parent) {
                     View view = convertView;
                     final ViewHolder holder;
                     if (convertView == null) {
                             view = getLayoutInflater().inflate(R.layout.list_entry, parent, false);
                             holder = new ViewHolder();
                             holder.text = (TextView) view.findViewById(R.id.text);
                             holder.image = (ImageView) view.findViewById(R.id.image);
                             view.setTag(holder);
                     } else {
                             holder = (ViewHolder) view.getTag();
                     }

                     holder.text.setText("Item " + (position + 1));
                     
                    try {
						sicImageLoader.displayImage(imageUrls[position], holder.image, 1);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}

                     return view;
             }
     }
}
