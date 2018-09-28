package com.mgl.service;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.mgl.beans.CalendarioBean;



import android.util.Log;

public class CalXMLHandler extends  DefaultHandler{

	 boolean currentElement = false;
	 String currentValue = "";
	 
	 public CalendarioBean calendarioInfo = null;
	 public ArrayList<CalendarioBean> calendarioList = new ArrayList<CalendarioBean>();
	 
	public ArrayList<CalendarioBean> getCalendarioList() {
		return calendarioList;
	}
	 
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			  currentElement = true;
			  currentValue = "";
			  if (localName.equals("DIA")){
				  calendarioInfo = new CalendarioBean();
			  } 
	 }
	
	public void characters(char[] ch, int start, int length) throws SAXException {
			if(currentElement) {
				currentValue = currentValue + new String(ch, start, length);
				currentElement = false;
			}			 
    } 
	 	 
	 public void endElement(String uri, String localName, String qName) throws SAXException { 
		 
			  currentElement = false;			 
			  if (localName.equalsIgnoreCase("NOM_MES")) {
				  calendarioInfo.setNomMesCalendario(currentValue);
				  Log.d("endElement ", "NOM " + calendarioInfo.getNomMesCalendario());
			  } else if (localName.equalsIgnoreCase("NUM_MES")) {
				  calendarioInfo.setNumMesCalendario(currentValue);	
				  Log.d("endElement ", "NUM " + calendarioInfo.getNumMesCalendario());
			  } else if (localName.equalsIgnoreCase("NUM_DIA")) {
				  calendarioInfo.setNumDiaCalendario(currentValue);	
				  Log.d("endElement ", "DIA " + calendarioInfo.getNumDiaCalendario());
			  } else if (localName.equalsIgnoreCase("FONDO")) {
				  calendarioInfo.setFondoCalendario(currentValue);
				  Log.d("endElement ", "FONDO " + calendarioInfo.getFondoCalendario());
			  } else if (localName.equalsIgnoreCase("ES_COLOR")) {
				  calendarioInfo.setEsColor(currentValue);	
				  Log.d("endElement ", "COLOR " + calendarioInfo.getEsColor());
			  }else if (localName.equalsIgnoreCase("DIA")) {
				  calendarioList.add(calendarioInfo);
			  }
		}	 
}
