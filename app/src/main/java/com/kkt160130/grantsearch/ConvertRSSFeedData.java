/* ConvertRSSFeedData.java
 *
 * This file pulls RSS feed data from grants.gov using an Async Task.
 */
package com.kkt160130.grantsearch;

import android.os.AsyncTask;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ConvertRSSFeedData extends AsyncTask<Void, Void, Void> {

    // URL of RSS feed
    private static final String RSS_FEED_URL = "https://www.grants.gov/rss/GG_NewOppByCategory.xml";
    private Document document = null;
    private AsyncOutput asyncOutput;

    // interface for returning the document
    public interface AsyncOutput
    {
        void retrieveGrantList(Document doc);
    }

    public ConvertRSSFeedData (AsyncOutput output)
    {
        this.asyncOutput = output;
    }

    // pre-execute unused
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    // connects to the URL and creates a document of the RSS feed data
    @Override
    protected Void doInBackground(Void... params) {
        try
        {
            URL url = new URL(RSS_FEED_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            document = builder.parse(is);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return null;
    }

    // returns the file to the activity
    @Override
    protected void onPostExecute(Void v)
    {
        asyncOutput.retrieveGrantList(this.document);
    }

}