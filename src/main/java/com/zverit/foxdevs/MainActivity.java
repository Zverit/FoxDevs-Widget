package com.zverit.foxdevs;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity {
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = ProgressDialog.show(MainActivity.this, "Working...", "Связь с FoxDevs", true, false);
        new ParseSite().execute("http://www.foxdevs.net");

    }

    private class ParseSite extends AsyncTask<String, Void, List<String>> {
        protected List<String> doInBackground(String... arg) {
            List<String> output = new ArrayList<String>();
            String str  = new String();
            try
            {
                HtmlHelper hh = new HtmlHelper(new URL(arg[0]));
                HtmlCleaner htmlCleaner = new HtmlCleaner();
                TagNode tagNode = htmlCleaner.clean(new URL(arg[0]));
                 output.add(tagNode.evaluateXPath("//di[@id='mainEvent']/h3/a/text()")[0].toString().trim());
//
//                List<TagNode> links = hh.getLinksByClass("/event/4[1-9]");
//
//                for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext();)
//                {
//                    TagNode divElement = (TagNode) iterator.next();
//                    output.add(divElement.getText().toString());
//                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return output;
        }

        protected void onPostExecute(List<String> output) {
            pd.dismiss();
            ListView listview = (ListView) findViewById(R.id.dd);
            listview.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1 , output));
            TextView textView = (TextView) findViewById(R.id.tx);
            textView.setText(output.get(0));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}

