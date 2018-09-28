package com.mgl.activityCal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.activityEfemeride.EQueryActivity;
import com.example.sqlite.R;
import com.mgl.beans.CalendarioBean;
import com.mgl.beans.EfemerideBean;
import com.mgl.service.CalBDHelper;
import com.mgl.service.CalXMLHandler;
import com.mgl.service.EXMLHandler;

public class MenuActivity extends ActionBarActivity {
	Button btnCalendario;
	
	Cursor cursor = null;
	CalBDHelper dbHelper;
	SQLiteDatabase db;
	
	ArrayList<CalendarioBean> ListCal = null;
	ArrayList<EfemerideBean> ListEfemeride = null;	
	
	String opr = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_menu);		

		View.OnClickListener handler = new View.OnClickListener() {
	            public void onClick(View v) {
	            	
	                switch (v.getId()) {
	 
	                	case R.id.btnCalendario:
	        				opr = "calendario";	        				
	        				new PostAsync().execute();		        				
	                	break;
		                case R.id.btnEfemerides:
		    				opr = "efemerides";		    				
		    				new PostAsync().execute();			    				               	
		                break;		               
	               }
	          }
	     };		 
	            
	    findViewById(R.id.btnCalendario).setOnClickListener(handler);
	    findViewById(R.id.btnEfemerides).setOnClickListener(handler);

	};
	
	class PostAsync extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;	
		String msj;
		Long status;
		
		@Override
  		protected void onPreExecute() 
  		{
  			pd = ProgressDialog.show(MenuActivity.this, "Cargando información", "Espere un momento...", true, false);
  		}

		@Override
		protected Void doInBackground(Void... params) {			
			try {					 
		     	
		     	//CUANDO SE DA CLICK EN CALENDARIO
		     	if(opr.compareTo("calendario") == 0) {
		     		    status = null;
		     		    
		     			//ABRIR BD 'CalendarioDB' EN MODO ESCRITURA
		     		    dbHelper = new CalBDHelper(getBaseContext(), "CalendarioDB", null, 1); 
					    db =dbHelper.getWritableDatabase();
					    
				     	cursor = dbHelper.getCalendario(db);
				     	
					     	if (cursor.getCount() <= 0) {		     		
					     		parseXmlCal();  //PARSEA XML Y LLENA LA LISTA  		
					     		
					     	    if (ListCal != null){
						     	    status = dbHelper.guardarCalendario(ListCal,db);
						     	    
						     	    if (status > 0) {
										msj = "Datos Almacenados";
					  		     	    Log.d("INSERTA DATOS ", " ");
						     	    } else {
								    	msj = "Error al Insertar los Datos";
							     	    Log.d("ERROR AL INSERTA DATOS ", " ");					
									}
					     	    }
					     	    
					     	} else {
					     		status = (long) 1;
					     		msj = "Datos Cargados";
					     		Log.d("LA INF YA EXISTE ", " ");
					     	}
				     	
				     	cursor.close();
			     	    db.close();

		     	}
		     	
		     	//CUANDO SE DA CLICK EN EFEMERIDES
		     	else if (opr.compareTo("efemerides") == 0) {
		     		
		     		dbHelper = new CalBDHelper(getBaseContext(),"CalendarioDB", null, 1);
		     		db =dbHelper.getWritableDatabase();
		     		
		     		cursor = dbHelper.getEfemerides(db);
		     		
		     		if(cursor.getCount() <= 0) {
			     		 parseXmlEfe();			     		 
			     		 
			     		 if (ListEfemeride != null){
				     		 status = dbHelper.guardaEfemerides(ListEfemeride, db);	
				     		 
					     	 if (status > 0) {
									msj = "Datos Almacenados";
					  		   	    Log.d("INSERTA DATOS ", " ");
						     } else {
							    	msj = "ERROR AL INSERTA DATOS";
							   	    Log.d("ERROR AL INSERTA DATOS ", " ");					
							 }				     	 
			     		 } 
			     		 
		     		} else {
		     			status = (long) 1;
			     		msj = "Datos Cargados";
			     		Log.d("LA INF YA EXISTE ", " ");		     			
		     		}		     		
		     	}
		       	
	        			
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}			
			return null;
		}
		
		@Override
  		protected void onPostExecute(Void result) {	

			//SI SE PULSA EL BOTON CALENDARIO
			if(opr.compareTo("calendario") == 0) {
				if(status != null) {  		     	    
					Intent i = new Intent(getBaseContext(),CalQueryActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);				
				} else {
	     			 msj = "ERROR AL LEER EL ARCHIVO";
	     		 } 
			
			//SI SE PULSA EL BOTON EFEMERIDES
			} else if(opr.compareTo("efemerides") == 0) {
				if(status != null) {					
					Intent i = new Intent(getBaseContext(),EQueryActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);				
				} else {
	     			 msj = "ERROR AL LEER EL ARCHIVO";
	     		 }
			}
			
		  pd.dismiss();    
		  Toast.makeText(getBaseContext(), msj , Toast.LENGTH_LONG).show();	   		  
  		}
	}
	
	
	//EXTRAE EL ARCHIVO XML DE CALENDARIO Y LO PARSEA 	
	public void parseXmlCal() throws IOException, ParserConfigurationException, SAXException{
		
		  InputStream is = getAssets().open("xml_calendario.xml");	
	         
          SAXParserFactory spf = SAXParserFactory.newInstance();
	      SAXParser sp = spf.newSAXParser();
	      XMLReader xr = sp.getXMLReader();
	     
	      CalXMLHandler myXMLHandler = new CalXMLHandler();
	      xr.setContentHandler(myXMLHandler);
	      InputSource inStream = new InputSource(is);
	      xr.parse(inStream);
	          
	      ListCal = myXMLHandler.getCalendarioList();		
	}	
	
	
	
	//EXTRAE EL ARCHIVO XML DE EFEMERIDES Y LO PARSEA 	
	public void parseXmlEfe() throws IOException, ParserConfigurationException, SAXException{
		
		 Log.d("METODO PARSE ", " ");
		 InputStream is = getAssets().open("xml_efemerides.xml");	
				 	
		  SAXParserFactory spf = SAXParserFactory.newInstance();
	      SAXParser sp = spf.newSAXParser();
	      XMLReader xr = sp.getXMLReader();
	     
	      EXMLHandler myXMLHandler = new EXMLHandler();
	      xr.setContentHandler(myXMLHandler);
	      InputSource inStream = new InputSource(is);
	      xr.parse(inStream);
	          
	      ListEfemeride = myXMLHandler.getLstEfemeride();	
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sqlite, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
