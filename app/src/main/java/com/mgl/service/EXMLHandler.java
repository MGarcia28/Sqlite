package com.mgl.service;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.mgl.beans.EfemerideBean;

public class EXMLHandler extends DefaultHandler{
	
	 boolean currentElement = false;
	 String currentValue = "";
	 String currentValue2 = "";
	 private StringBuffer buffer;
	 
	 public EfemerideBean  efemeridesInfo = null;
	 public ArrayList<EfemerideBean> lstEfemeride =   new ArrayList<EfemerideBean>();
	  	
	 public ArrayList<EfemerideBean> getLstEfemeride() {
		return lstEfemeride;
	 }
	 
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {	
		  currentElement = true;
		  currentValue = "";
		  currentValue2 = localName;
		  if (localName.equals("EFEMERIDE")){
			  efemeridesInfo = new EfemerideBean();
			  buffer = new StringBuffer();
		  } 
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		if(currentElement) {
			
	         if(currentValue2.equals("CONTENIDO"))
             {
	        	 currentValue = new String(ch, start, length);
	 	         if(buffer != null) buffer.append(currentValue);

             } else {
            	 currentValue = currentValue + new String(ch, start, length);
            	 currentElement = false;
             }
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
						
		  currentElement = false;			 
		  if (localName.equalsIgnoreCase("ID_DIA")) {
			  efemeridesInfo.setIdDia(currentValue);
			  Log.d("endElement ", "ID_DIA " + efemeridesInfo.getIdDia());			  
		  } else if (localName.equalsIgnoreCase("NOM_MES")) {
			  efemeridesInfo.setNomMes(currentValue);	
			  Log.d("endElement ", "NOM_MES " + efemeridesInfo.getNomMes());			  
		  } else if (localName.equalsIgnoreCase("NUM_MES")) {
			  efemeridesInfo.setNumMes(currentValue);	
			  Log.d("endElement ", "NUM_MES " + efemeridesInfo.getNumMes());			  
		  } else if (localName.equalsIgnoreCase("NUM_DIA")) {
			  efemeridesInfo.setNumDia(currentValue);
			  Log.d("endElement ", "NUM_DIA " + efemeridesInfo.getNumDia());			  
		  } else if (localName.equalsIgnoreCase("CONTENIDO")) {			  
			  efemeridesInfo.setContenido(buffer.toString());
			  Log.d("endElement ", "CONTENIDO " + efemeridesInfo.getContenido());			  
		  }else if (localName.equalsIgnoreCase("EFEMERIDE")) {
			  lstEfemeride.add(efemeridesInfo);
		  }
	}	
}
