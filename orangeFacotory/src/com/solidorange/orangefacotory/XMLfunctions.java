package com.solidorange.orangefacotory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.text.format.DateUtils;
import android.util.Log;


public class XMLfunctions extends MAppComm{
	final static String replceamp="0x11001";
	
	static String tag = XMLfunctions.class.getName();
	
	public final static Document XMLfromString(String xml){
		if(xml!=null)
		{
		String xmlNew=clearRefs(xml);
		Document doc = null;
		//Log.v(tag,"After Clearing refs::"+xmlNew);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Log.v(tag,"Trying Reading Document");
        try {
        	
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xmlNew));
	        doc = db.parse(is); 
	        
		} catch (ParserConfigurationException e) {
			Log.e(tag,"XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
            
		} catch (IOException e) {
				Log.e(tag,"I/O exeption: " + e.getMessage());
			return null;
		}
		       
        return doc;
		}
		else
		{
			connectionStatus="No data reiceved";
			return null;
		}
			
        
		
	}
	
	
	public static String postData(ArrayList<HashMap<String, String>> mylist,HashMap<String, String> formelements) {
	    // Create a new HttpClient and Post Header
		
		Log.d(tag,"Size of Comm List"+commlist.size());
	    
	    String URL;
		HttpClient httpclient = new DefaultHttpClient();
		if(formelements.get("formOnAlert") != null||formelements.get("formOnAlert") != "")
			URL=formelements.get("formOnAlert");
		else
			return "Nothing";
	    HttpPost httppost = new HttpPost(URL);
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	    nameValuePairs.add(new BasicNameValuePair("simid",simID()));
	    for(int k=0;k<mylist.size();k++)
	    {
	        // Add your data
	    	if(mylist.get(k).containsKey("choicegroupFnVal")){
	    		nameValuePairs.add(new BasicNameValuePair(mylist.get(k).get("choicegroupFn"),mylist.get(k).get("choicegroupFnVal")));
	    		Log.e(tag,"choicef "+mylist.get(k).get("choicegroupFn")+mylist.get(k).get("choicegroupFnVal"));
	    	}	
	        if(mylist.get(k).containsKey("textfieldFnVal"))
	        {
	        	nameValuePairs.add(new BasicNameValuePair(mylist.get(k).get("textfieldFn"),mylist.get(k).get("textfieldFnVal")));
	        	Log.e(tag,"textf "+mylist.get(k).get("textfieldFn")+mylist.get(k).get("textfieldFnVal"));
	        }
	        if(mylist.get(k).containsKey("datefieldFnVal"))
	        {
	        	nameValuePairs.add(new BasicNameValuePair(mylist.get(k).get("datefieldFn"),mylist.get(k).get("datefieldFnVal")));
	        	Log.e(tag,"datef "+mylist.get(k).get("datefieldFn")+mylist.get(k).get("datefieldFnVal"));
	        }
	    }
	    Log.e(tag,"Form func name"+formelements.get("formFn"));
	    Log.e(tag,"Form func val"+formelements.get("formFnVal")+"meow");
	    if(formelements.get("formFnVal")!=null&&formelements.get("formFn")!=null){
	    	Log.d(tag,"I am Here");
	    	nameValuePairs.add(new BasicNameValuePair(formelements.get("formFn"),formelements.get("formFnVal")));}
	    
	    Log.e(tag,"Name Value Pair"+nameValuePairs);
	    try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.d("myapp", "works till here. 2");
            try {
                HttpResponse response = httpclient.execute(httppost);
                Log.d("myapp", "response " + response.getEntity());
                return getResponseBody(response);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
	    return "Nothing";
	    
	}
	    
	
	
	/** Returns element value
	  * @param elem element (it is XML tag)
	  * @return Element value otherwise empty String
	  */
	 public final static String getElementValue( Node elem ) {
	     Node kid;
	     if( elem != null){
	         if (elem.hasChildNodes()){
	             for( kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling() ){
	                 if( kid.getNodeType() == Node.TEXT_NODE  ){
	                     return kid.getNodeValue();
	                 }
	             }
	         }
	     }
	     else
	    	 return null;
		return "";
	 }
		 
	 public static String getXML(String URL){	 
//			String line = null;
//			Log.v(tag,"Trying Connection");
//			try {
//				DefaultHttpClient httpClient = new DefaultHttpClient();
//				HttpGet httpget = new HttpGet(URL);
//				HttpResponse httpResponse = httpClient.execute(httpget);
//				HttpEntity httpEntity = httpResponse.getEntity();
//				line = EntityUtils.toString(httpEntity);
//				
//			} catch (UnsupportedEncodingException e) {
//				line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
//			} catch (MalformedURLException e) {
//				line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
//			} catch (IOException e) {
//				line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
//			}
//			Log.v(tag,"Done Connection...");
//			return line;
		 	
			String result = null;
			//Log.d(tag,"Verify URL "+ URL);
			HttpGet request = new HttpGet(URL);
			

			// As Jeff Sharkey does in the android-sky example, 
			// use request.setHeader to optionally set the User-Agent header.

			HttpParams httpParams = new BasicHttpParams();
			
			
			int some_reasonable_timeout = (int) (20 * DateUtils.SECOND_IN_MILLIS);
			HttpConnectionParams.setConnectionTimeout(httpParams, some_reasonable_timeout);
			HttpConnectionParams.setSoTimeout(httpParams, some_reasonable_timeout);
			HttpClient client = new DefaultHttpClient(httpParams);
			try
			{
				
			  HttpResponse response = client.execute(request);
			  StatusLine status = response.getStatusLine();
			  //Log.d(tag, "HTTP Status: "+ status.getStatusCode());
			  if (status.getStatusCode() == HttpStatus.SC_OK)
			  {
			    ResponseHandler<String> responseHandler = new BasicResponseHandler();
			    result = responseHandler.handleResponse(response);
			    connectionStatus="";
			    
			  }
			  else
			  {
				  connectionStatus=status.getReasonPhrase();
				  Log.e(tag, "HTTP Error: "+connectionStatus+" :::");
				  Log.e(tag, "HTTP Error: "+ status.getStatusCode());
				  Log.d(tag, "HTTP Error: "+ status.getReasonPhrase());
				  
				  
				  
				  return result;
			  }
			}
			catch (ClientProtocolException e)
			{
				connectionStatus="ClientProtocolException";
				Log.e(tag, "HTTP Error: "+connectionStatus+" :::" , e);
				
				return result;
			}
			catch (IOException e)
			{
				connectionStatus="IOException";
				Log.e(tag, "HTTP Error: "+connectionStatus+" :::" , e);
				
			}
			finally
			{
			  client.getConnectionManager().shutdown();
			}
			return result;
	}
	 
	

	public static String getValue(Element item, String str) {		
		NodeList n = item.getElementsByTagName(str);
			return XMLfunctions.getElementValue(n.item(0));
	}
	public static String clearRefs(String doc) {
		String x="&";
		
		String y=replceamp;
		doc= doc.replace(x, y);
		x="\n";
		y="";
		doc= doc.replace(x, y);
		x="\r\n";
		y="";
		doc= doc.replace(x, y);
		x="\r";
		y="";
		doc= doc.replace(x, y);
		return doc;
	}
	public static String insertRefs(String doc) {
		String x="&";
		
		String y=replceamp;
		doc= doc.replace(y, x);
		return doc;
	}


	public static HashMap<String, String> getChildValue(Element e) {
		HashMap<String, String> map2 = new HashMap<String, String>();
		
		int x;
		Node img=e.getFirstChild();
		if(img.getNodeName().equals("img"))
			x=1;
		else if(img.getNodeName().equals("stringitem"))
			x=2;
		else if(img.getNodeName().equals("choicegroup"))
			x=3;
		else if(img.getNodeName().equals("textfield"))
			x=4;
		else if(img.getNodeName().equals("datefield"))
			x=5;
		else if(img.getNodeName().equals("inlinecommand"))
			x=6;
		else
			x=7;
		switch(x){
			case 1:{
				Log.e(tag,"IMG");
				NodeList child = img.getChildNodes();
				if(child.getLength()>0){
					for (int j = 0; j < child.getLength(); j++){ 
						Element f = (Element)child.item(j);
						if(XMLfunctions.getValue(f, "detail")!=null){
							Log.i(tag,"Label: "+XMLfunctions.getValue(f, "detail"));
							if(map2.get("imgDetail")!=null)
								map2.put("imgDetail",map2.get("imgDetail")+"\n"+XMLfunctions.getValue(f, "detail") );
							else
								map2.put("imgDetail",XMLfunctions.getValue(f, "detail") );
							}
						NamedNodeMap attr =f.getAttributes();
						if(attr!=null){
							if(attr.getNamedItem("fontstyle")!=null){
								Log.i(tag,"detailFontStyle :"+ attr.getNamedItem("fontstyle").getNodeValue());
								map2.put("detailFontStyle", attr.getNamedItem("fontstyle").getNodeValue());
							}if(attr.getNamedItem("fontsize")!=null){
								Log.i(tag,"detailFontSize :"+ attr.getNamedItem("fontsize").getNodeValue());
								map2.put("detailFontSize", attr.getNamedItem("fontsize").getNodeValue());
							}if(attr.getNamedItem("bgcolor")!=null){
								Log.i(tag,"detailBGcolor :"+ attr.getNamedItem("bgcolor").getNodeValue());
								map2.put("detailBGcolor", attr.getNamedItem("bgcolor").getNodeValue());
							}if(attr.getNamedItem("fgcolor")!=null){
								Log.i(tag,"detailFGcolor :"+ attr.getNamedItem("fgcolor").getNodeValue());
								map2.put("detailFGcolor", attr.getNamedItem("fgcolor").getNodeValue());
							}
						}
					}
				}break;
			}case 2:{	
				Log.e(tag,"StringItem");
				NodeList child = img.getChildNodes();
				if(child.getLength()>0){
					for (int j = 0; j < child.getLength(); j++){
						Element f = (Element)child.item(j);
						if(XMLfunctions.getValue(f, "text")!=null){
							Log.d(tag,"Text: "+XMLfunctions.getValue(f, "text"));
							map2.put("stringitemText",XMLfunctions.getValue(f, "text") );}
						if(XMLfunctions.getValue(f, "detail")!=null){
							Log.d(tag,"Detail: "+j+XMLfunctions.getValue(f, "detail"));
							if(map2.get("stringitemDetail")!=null)
								map2.put("stringitemDetail",map2.get("stringitemDetail")+"\n"+XMLfunctions.getValue(f, "detail") );
							else
								map2.put("stringitemDetail",XMLfunctions.getValue(f, "detail") );
							}
					}
				}break;
			}case 3:{	
				Log.d(tag,"ChoiceGroup");
				NodeList child = img.getChildNodes();
				if(child.getLength()>0){
					for (int j = 0; j < child.getLength(); j++)
					{				
						Element f = (Element)child.item(j);
						if(XMLfunctions.getValue(f, "label")!=null)
						{
							Log.i(tag,"Label: "+XMLfunctions.getValue(f, "label"));
							map2.put("choicegroupLabel",XMLfunctions.getValue(f, "label") );
						}
						
						if(XMLfunctions.getValue(f, "text")!=null)
						{
							Log.i(tag,"Text: "+XMLfunctions.getValue(f, "text"));
							map2.put(("choicegroupText"+j),XMLfunctions.getValue(f, "text") );
						}
						
						if(XMLfunctions.getValue(f, "op")!=null)
						{				    			 
							Log.i(tag,"OP: "+XMLfunctions.getValue(f, "op"));
							map2.put(("choicegroupOP"+j),XMLfunctions.getValue(f, "op") );
							if(j==1)
								map2.put(("choicegroupFnVal"),XMLfunctions.getValue(f, "op") );
							NodeList x1=f.getElementsByTagName("op");
			    			NamedNodeMap attr=x1.item(0).getAttributes();
							if(attr.getNamedItem("type").getNodeValue()!=null)
							{
								Log.e(tag,"OP Type :"+ attr.getNamedItem("type").getNodeValue());
								map2.put(("choicegroupOPtype"+j),attr.getNamedItem("type").getNodeValue() );
							}
							if(attr.getNamedItem("fn").getNodeValue()!=null)
							{
								Log.e(tag,"OP Fn :"+ attr.getNamedItem("fn").getNodeValue());
								map2.put(("choicegroupFn"+j),attr.getNamedItem("fn").getNodeValue() );
								if(j==1)
									map2.put(("choicegroupFn"),attr.getNamedItem("fn").getNodeValue() );
							}
						}
						map2.put(("choicegroupNos"),""+j );
						
					}
				}break;
			}case 4:{	
				Log.d(tag,"TextField");
				if(XMLfunctions.getValue(e, "text")!=null)
				{
					Log.i(tag,"Text: "+XMLfunctions.getValue(e, "text"));
					map2.put(("textfieldText"),XMLfunctions.getValue(e, "text") );
				}					
				if(XMLfunctions.getValue(e, "label")!=null)
				{
					Log.i(tag,"Label: "+XMLfunctions.getValue(e, "label"));
					map2.put(("textfieldLabel"),XMLfunctions.getValue(e, "label") );
				}
				if(XMLfunctions.getValue(e, "constraint")!=null)
				{
					Log.i(tag,"Constraint: "+XMLfunctions.getValue(e, "constraint"));
					map2.put(("textfieldConstraint"),XMLfunctions.getValue(e, "constraint") );
				}
				if(XMLfunctions.getValue(e, "op")!=null)
				{
					Log.i(tag,"OP : "+XMLfunctions.getValue(e, "op"));
					map2.put(("textfieldOP"),XMLfunctions.getValue(e, "op") );
					NodeList x1=e.getElementsByTagName("op");
	    			NamedNodeMap attr=x1.item(0).getAttributes();
					if(attr.getNamedItem("type").getNodeValue()!=null)
					{
						Log.e(tag,"OP Type :"+ attr.getNamedItem("type").getNodeValue());
						map2.put(("textfieldOPtype"),attr.getNamedItem("type").getNodeValue() );	
					}
					if(attr.getNamedItem("fn").getNodeValue()!=null)
					{
						Log.e(tag,"OP Fn :"+ attr.getNamedItem("fn").getNodeValue());
						map2.put(("textfieldFn"),attr.getNamedItem("fn").getNodeValue() );
					}
				}
				break;
			}case 5:{	
				Log.d(tag,"Datefield");
				if(XMLfunctions.getValue(e, "label")!=null)
				{
					Log.i(tag,"Label: "+XMLfunctions.getValue(e, "label"));
					map2.put(("datefieldLabel"),XMLfunctions.getValue(e, "label") );
				}
				if(XMLfunctions.getValue(e, "op")!=null)
				{
					Log.i(tag,"OP : "+XMLfunctions.getValue(e, "op"));
					map2.put(("datefieldOP"),XMLfunctions.getValue(e, "op") );
					NodeList x1=e.getElementsByTagName("op");
	    			NamedNodeMap attr=x1.item(0).getAttributes();
					if(attr.getNamedItem("type").getNodeValue()!=null)
					{
						Log.e(tag,"OP Type :"+ attr.getNamedItem("type").getNodeValue());
						map2.put(("datefieldOPtype"),attr.getNamedItem("type").getNodeValue() );	
					}
					if(attr.getNamedItem("fn").getNodeValue()!=null)
					{
						Log.e(tag,"OP Fn :"+ attr.getNamedItem("fn").getNodeValue());
						map2.put(("datefieldFn"),attr.getNamedItem("fn").getNodeValue() );
					}
				}
				break;
			}case 6:{
				Log.d(tag,"InlineCommand");
				if(XMLfunctions.getValue(e, "text")!=null)
				{
					Log.i(tag,"Text: "+XMLfunctions.getValue(e, "text"));
					map2.put(("inlinecommandText"),XMLfunctions.getValue(e, "text"));
				}
				if(XMLfunctions.getValue(e, "detail")!=null)
				{
					Log.i(tag,"Detail: "+XMLfunctions.getValue(e, "detail"));
					map2.put(("inlinecommandDetail"),XMLfunctions.getValue(e, "detail"));
					NodeList x1=e.getElementsByTagName("detail");
		    		NamedNodeMap attr=x1.item(0).getAttributes();
					if(attr.getNamedItem("bgcolor").getNodeValue()!=null)
					{
						Log.e(tag,"BGcolor :"+ attr.getNamedItem("bgcolor").getNodeValue());
						map2.put(("inlinecommandBGcolor"),attr.getNamedItem("bgcolor").getNodeValue() );
					}
					if(attr.getNamedItem("fgcolor").getNodeValue()!=null)
					{
						Log.e(tag,"FGcolor :"+ attr.getNamedItem("fgcolor").getNodeValue());
						map2.put(("inlinecommandFGcolor"),attr.getNamedItem("fgcolor").getNodeValue() );
					}
				}
				if(XMLfunctions.getValue(e, "op")!=null){
					Log.i(tag,"Text: "+XMLfunctions.getValue(e, "op"));
					map2.put(("inlinecommandOP"),XMLfunctions.getValue(e, "op") );
					NodeList x1=e.getElementsByTagName("op");
		    		NamedNodeMap attr=x1.item(0).getAttributes();
					if(attr.getNamedItem("type").getNodeValue()!=null)
					{
						Log.e(tag,"OP Type :"+ attr.getNamedItem("type").getNodeValue());
						map2.put(("inlinecommandOPtype"),attr.getNamedItem("type").getNodeValue() );
					}
					if(attr.getNamedItem("fn").getNodeValue()!=null)
					{
						Log.e(tag,"OP Fn :"+ attr.getNamedItem("fn").getNodeValue());
						map2.put(("inlinecommandFn"),attr.getNamedItem("fn").getNodeValue() );
					}
				}break;
			}default: Log.e(tag,"Default");
		}return map2;
	}
	
	public static String getResponseBody(HttpResponse response) 
	{
		String response_text = null;
		HttpEntity entity = null;
		try {
			entity = response.getEntity();
			response_text = _getResponseBody(entity);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException e1) {}
			}
		}
		return response_text;
	}
	
	public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException 
	{
		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
		InputStream instream = entity.getContent();
		if (instream == null) { return ""; }
		if (entity.getContentLength() > Integer.MAX_VALUE) { throw new IllegalArgumentException(
			"HTTP entity too large to be buffered in memory"); }
		String charset = getContentCharSet(entity);
		if (charset == null) {
			charset = HTTP.DEFAULT_CONTENT_CHARSET;
		}
		Reader reader = new InputStreamReader(instream, charset);
		StringBuilder buffer = new StringBuilder();
		try {
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
	}

	public static String getContentCharSet(final HttpEntity entity) throws ParseException 
	{
		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
		String charset = null;
		if (entity.getContentType() != null) {
			HeaderElement values[] = entity.getContentType().getElements();
			if (values.length > 0) {
				NameValuePair param = values[0].getParameterByName("charset");
				if (param != null){
					charset = param.getValue();
				}
			}
		}
		return charset;
	}
	
	public static String getAttributeOfNode(String nodeName, String attributeName, Element f)
	{
		NodeList x1=f.getElementsByTagName(nodeName);
		NamedNodeMap attr=x1.item(0).getAttributes();
		if(attr.getNamedItem(attributeName).getNodeValue()!=null)
		{
			return attr.getNamedItem(attributeName).getNodeValue();
		}
		return null;
	}

}
