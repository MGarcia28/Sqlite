package com.example.activityEfemeride;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xml.sax.ContentHandler;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlite.R;
import com.mgl.beans.EfemerideBean;
import com.mgl.service.CalBDHelper;

public class EQueryActivity extends ActionBarActivity {
	
	Spinner spinnerE;
	TextView edtNumMes,edtDia,edtContenido;
	WebView wvContenido;
	Button btnConsulta, btnInsertar, btnActualizar,btnEliminar,btnGetEfe;
		
	String numMes, mes, msj, opr, msjVal;	
	boolean status;
	Long statusInsert = null;
	int statusMod = 0;

	CalBDHelper dbHelper = new CalBDHelper(EQueryActivity.this, "CalendarioDB", null, 1);
	SQLiteDatabase db;
	Cursor cursor = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_equery);
		
		spinnerE = (Spinner)findViewById(R.id.spinnerE);
		edtNumMes = (TextView)findViewById(R.id.edtNumMes);
		edtDia = (TextView)findViewById(R.id.edtDia);
		edtContenido = (TextView)findViewById(R.id.edtContenido);
				
		edtNumMes.setEnabled(false);
			
		//SPINNER LISTA DE MESES	
		spinnerE.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					
				edtNumMes.setText(String.valueOf(id));
					 
				numMes = String.valueOf(id);
				mes = parent.getItemAtPosition(pos).toString();
					 
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {			
			}			
		});		
		
		//ONCLICK DE BOTONES
		View.OnClickListener handler = new View.OnClickListener() {
			
			public void onClick(View v) {
				switch (v.getId()) {
				
				case R.id.btnConsulta:
					opr ="consulta";					
					status = valida();
					
					if (status == true) {
						new PostAsync().execute();		
					} else {
						Toast.makeText(getBaseContext(), msjVal , Toast.LENGTH_LONG).show();
					}
				break;
				
				case R.id.btnInsertar:
					opr ="inserta";
					status = valida();
					if(status == true) {
						new PostAsync().execute();
					} else {
						Toast.makeText(getBaseContext(), msjVal , Toast.LENGTH_LONG).show();
					}
				
				break;
				
				case R.id.btnActualizar:
					opr ="actualiza";
					status = valida();
					if(status == true) {
						new PostAsync().execute();
					} else {
						Toast.makeText(getBaseContext(), msjVal , Toast.LENGTH_LONG).show();
					}			
				break;
				
				case R.id.btnEliminar:
					opr ="elimina";
					status = valida();
					
					if(status ==true) {
						new PostAsync().execute();
					} else {
						Toast.makeText(getBaseContext(), msjVal , Toast.LENGTH_LONG).show();
					}
					
				break;
				
				case R.id.btnGetEfe:
					opr ="efemerides";
					new PostAsync().execute();					
				break;
				
				case R.id.btnLimpia:
					opr ="limpia";
					limpia();
				break;
					
				case R.id.btnJson:
					opr ="json";
					status = estadoSDCARD();
					if(status ==true) {
						new PostAsync().execute();
					} else {
						Toast.makeText(getBaseContext(), "MEMORIA EXTERNA NO DISPONIBLE" , Toast.LENGTH_LONG).show();
					}
					
				break;
				
				case R.id.btnXml:
					opr ="xml";
					status = estadoSDCARD();
					if(status ==true) {
						new PostAsync().execute();
					} else {
						Toast.makeText(getBaseContext(), "MEMORIA EXTERNA NO DISPONIBLE" , Toast.LENGTH_LONG).show();
					}
					
				break;
				}			
			}
		};
		
		findViewById(R.id.btnConsulta).setOnClickListener(handler);
		findViewById(R.id.btnInsertar).setOnClickListener(handler);
		findViewById(R.id.btnActualizar).setOnClickListener(handler);
		findViewById(R.id.btnEliminar).setOnClickListener(handler);
		findViewById(R.id.btnGetEfe).setOnClickListener(handler);
		findViewById(R.id.btnLimpia).setOnClickListener(handler);
		findViewById(R.id.btnJson).setOnClickListener(handler);
		findViewById(R.id.btnXml).setOnClickListener(handler);		
	}
	
	
	class PostAsync extends AsyncTask<Void, Void, Void>{
		ProgressDialog pd;
		ArrayList<EfemerideBean> lstEfe = new ArrayList<EfemerideBean>();
		
		public ArrayList<EfemerideBean> getLstEfe() {
			return lstEfe;
		}
		
		public void setLstEfe(ArrayList<EfemerideBean> lstEfe) {
			this.lstEfe = lstEfe;
		}

		

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(EQueryActivity.this, "Realizando Operación", "Espere un Momento...", true, false);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			db = dbHelper.getWritableDatabase();
			
			//SI SE PRECIONA EL BOTON CONSULTAR
			if (opr.compareTo("consulta") == 0 ) {
				cursor = dbHelper.buscaEfemeride(mes, edtDia.getText().toString(), db);
			
		    //SI SE PRECIONA EL BOTON INSERTAR
			} else if (opr.compareTo("inserta") == 0) {	
				
				//REMPLAZA EL SIGNO DE "<" POR "&lt; Y ">" POR "&gt;" 
				String cadena = edtContenido.getText().toString().replace("<","&lt;").replace(">","&gt;");
				
				//CONCATENA LAS ETIQUETAS DE HTML
				String formato = "<html><body align=\"justify\">\n" + cadena + "\n</body></html>";	
			
				statusInsert = dbHelper.guardaEfemeride(mes, numMes, edtDia.getText().toString(), formato, db);				
				db.close();
			
			//SI SE PRECIONA EL BOTON ACTUALIZAR
			} else if (opr.compareTo("actualiza") == 0) {	
				String cadena = edtContenido.getText().toString().replace("<html><body align=\"justify\">","<html><body align=\"justify\">\n").replace("</body></html>","\n</body></html>");	
				
				statusMod = dbHelper.actualizaEfe(mes, numMes,edtDia.getText().toString(), cadena, db);
				db.close();
			
			//SI SE PRESIONA EL BOTON ELIMINAR
			} else if(opr.compareTo("elimina") == 0) {				
				statusMod = dbHelper.eliminar(mes, edtDia.getText().toString(), db);
				db.close();
			
			//SI SE PRESIONA EL BOTON EFEMERIDES O SE PRESIONA EL BOTON JSON O SE PRESIONA EL BOTON XML
			} else if(opr.compareTo("efemerides") == 0 || opr.compareTo("json") == 0 || opr.compareTo("xml") == 0) {				
				cursor = dbHelper.getEfemerides(db);			
			} 
				
			return null;
		}	
		
		
		@Override
		protected void onPostExecute(Void result) {
			
			//SI SE PRECIONA EL BOTON CONSULTAR
			if(opr.compareTo("consulta") == 0) {				
				  
				if (cursor.getCount() > 0) {
			          cursor.moveToFirst();
			           
			         edtContenido.setText(cursor.getString(4).toString());			         
			         //wvContenido.loadData(cursor.getString(4).toString(), "text/html; charset=utf-8", "utf-8");
			         			         
			          msj="DATOS CARGADOS";	         
				} else {
				      msj="LA FECHA NO EXISTE";
				}					  
		       cursor.close();
		       db.close();
		    
		    //SI SE PRECIONA EL BOTON INSERTAR   
			} else if (opr.compareTo("inserta") == 0) {
				
				if(statusInsert > 0) {
					msj = "EFEMERIDE INSERTADA";
					limpia();
				} else {
					msj = "ERROR AL INSER DATOS";
				}
				
		    //SI SE PRECIONA EL BOTON ACTUALIZAR   
			} else if (opr.compareTo("actualiza") == 0) {
				
				msj = "EFEMERIDE(S) ACTUALIZADA(s) " + statusMod;
				
			//SI SE PRESIONA EL BOTON ELIMINAR
			}else if (opr.compareTo("elimina") == 0) {				
				msj = "CAMPOS ELIMINADOS " + statusMod;	
				limpia();
			
			//SI SE PRESIONA EL BOTON EFEMERIDES
			} else if (opr.compareTo("efemerides") == 0) {
				
				if(cursor.getCount() > 0 ){
					
					EfemerideBean efemeride = null;
				      
					if (cursor.moveToFirst()) {
				        do {    			          	
				        	efemeride = new EfemerideBean();
				        	
				          	efemeride.setIdDia(cursor.getString(0));
				          	efemeride.setNomMes(cursor.getString(1));
				          	efemeride.setNumMes(cursor.getString(2));
				        	efemeride.setNumDia(cursor.getString(3));
				        	efemeride.setContenido(cursor.getString(4));
				        				   			
				          	lstEfe.add(efemeride);
				        } while (cursor.moveToNext());
				      }
					
					 cursor.close();
					 db.close();
					  
					 msj ="EFEMERIDES";
										
    				Intent i = new Intent(EQueryActivity.this,EConsulActivity.class);   

	    				Bundle b = new Bundle();
	    				b.putSerializable("lstEfe", lstEfe);    				                 
	    				i.putExtras(b);
	    				   				
     				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);    				
    				startActivity(i);
    				     				
				} else {
					cursor.close();
					db.close();
					msj ="NO EXISTEN REGISTROS";
				}
				
			//SI SE PRESIONA EL BOTON JSON
			} else if (opr.compareTo("json") == 0) {		

			   if(cursor.getCount() > 0 ){	
					
					StringBuffer json = new StringBuffer();
					json.append("{ \n \"EFEMERIDES_SEP\": { \n\t \"EFEMERIDE\": [ \n");	
					
    			if (cursor.moveToFirst()) {
      	             do {    			          	
					        json.append("\t\t { \n");
						        json.append("\t\t\t \"ID_DIA\":\" "  + cursor.getString(0) + "\", \n" );
						        json.append("\t\t\t \"NOM_MES\":\" " + cursor.getString(1) + "\", \n" );
						        json.append("\t\t\t \"NUM_MES\":\" " + cursor.getString(2) + "\", \n" );
						        json.append("\t\t\t \"NUM_DIA\":\" " + cursor.getString(3) + "\", \n" );
						        json.append("\t\t\t \"CONTENIDO\":\" \t\t\t\t\t\t\t" + cursor.getString(4) + "\"" );
						    json.append("\n \t\t }");	
						    
						    if(!cursor.isLast()) {
						    	json.append(", \n");
						    }
						    
				      } while (cursor.moveToNext());
      	         }
			        json.append("\t\t\n]\n } \n}");
			        			        
					cursor.close();
					db.close();
					
					if(json != null || !json.equals("")) {
						Log.d("IMPRIME JSON", "" + json);
												
						//METODO PARA ESCRIBIR EL ARCHIVO EN LA MEMORIA EXTERNA
						msj = creaArchivo(json.toString(), "efemeridesJSON.json");
					} //else { msj ="ERROR AL CREAR EL ARCHIVO";}
					
			   } else {
				   msj ="NO EXISTEN DATOS";
			     }
			   
			 //SI SE PRESIONA EL BOTON XML
			} else if (opr.compareTo("xml") == 0) {		

			   if(cursor.getCount() > 0 ){	
					 XmlSerializer xmlS = Xml.newSerializer();									 
				     StringWriter writer = new StringWriter();
					 
				     try {
				    	 xmlS.setOutput(writer);
				    	 
				    	 xmlS.startDocument("UTF-8", true); 
					     xmlS.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
					     
           	    	     xmlS.startTag(null, "EFEMERIDE SEP");
           	    	     
    			        if (cursor.moveToFirst()) {
      	                    do {       	            	  
      	            	    	xmlS.startTag(null, "EFEMERIDE");
      	            	    	
	      	            	    	xmlS.startTag(null, "ID_DIA");
	      	            	    	xmlS.text(cursor.getString(0));
	      	            	    	xmlS.endTag(null, "ID_DIA");
				      	            	 
	      	            	    	xmlS.startTag(null, "NOM_MES");
	      	            	    	xmlS.text(cursor.getString(1));
	      	            	    	xmlS.endTag(null, "NOM_MES");
				      	            	
	      	            	    	xmlS.startTag(null, "NUM_MES");
	      	            	    	xmlS.text(cursor.getString(2));
	      	            	    	xmlS.endTag(null, "NUM_MES");
				      	            	 
	      	            	    	xmlS.startTag(null, "NUM_DIA");
	      	            	    	xmlS.text(cursor.getString(3));
	      	            	    	xmlS.endTag(null, "NUM_DIA");	
				      	            	
	      	            	    	xmlS.startTag(null, "CONTENIDO");
	      	            	    	xmlS.cdsect(cursor.getString(4));
	      	            	    	xmlS.endTag(null, "CONTENIDO"); 
	      	            	    xmlS.endTag(null, "EFEMERIDE");		      	            	              	    
			    
				          } while (cursor.moveToNext());
    			        }  
    			        
	            	    xmlS.endTag(null, "EFEMERIDE SEP");
	            	    xmlS.endDocument();
	            	    xmlS.flush();
	            	    
	            	    Log.d("IMPRIME XML", "" + writer.toString());
	            	    msj = creaArchivo(writer.toString(), "efemeridesXML.xml");
    			        
				     }catch(Exception e) {
						    msj = "Error al generar archivo!!!";
				     }
					cursor.close();
					db.close();    			    
					
			   } else { msj ="NO EXISTEN DATOS"; }
		  } 

			Toast.makeText(getBaseContext(), msj, Toast.LENGTH_LONG).show();
			pd.dismiss();	
		}		
	}
	
	
	//METODO PARA VALODACION DE DIAS
	public boolean valida() {
		status = true;
		
		//SI SE PRESIONA EL BOTON CONSULTAR O ELIMINAR
		if (opr.compareTo("consulta") == 0 || opr.compareTo("elimina") == 0) {
			
			if (edtDia.getText().toString().compareTo("") == 0 ||  mes.compareTo("SELECCIONA MES") == 0) {
				status = false;
				msjVal ="LLENAR CAMPO MES Y DIA";

			} else if((mes.compareTo("ABRIL") == 0 || mes.compareTo("JUNIO") == 0 || mes.compareTo("SEPTIEMBRE") == 0 || mes.compareTo("NOVIEMBRE") == 0) && 
					   Integer.valueOf(edtDia.getText().toString()) > 30) {
				msjVal = "DIA INVALIDA";
		    	status = false;
		    	
			} else if ((mes.compareTo("ENERO") == 0 || mes.compareTo("MARZO") == 0 || mes.compareTo("MAYO") == 0 || mes.compareTo("JULIO") == 0 ||
					   mes.compareTo("AGOSTO") == 0 || mes.compareTo("OCTUBRE") == 0 || mes.compareTo("DICIEMBRE") == 0 ) && Integer.valueOf(edtDia.getText().toString()) > 31 ){
				msjVal = "DIA INVALIDA";
		    	status = false;
		    	
			} else if (mes.compareTo("FEBRERO") == 0 && Integer.valueOf(edtDia.getText().toString()) > 29) {
				msjVal = "DIA INVALIDA";
		    	status = false;
			}		
		
	   //SI SE PRESIONA EL BOTON ACTUALIZAR � INSERTAR
	   } else if (opr.compareTo("actualiza") == 0 || opr.compareTo("inserta") == 0){

		   if (edtDia.getText().toString().compareTo("") == 0 ||  mes.compareTo("SELECCIONA MES") == 0 || edtContenido.getText().toString().compareTo("") == 0) {
				status = false;
				msjVal = "LLENAR TODOS CAMPOS";			
			
		   } else if((mes.compareTo("ABRIL") == 0 || mes.compareTo("JUNIO") == 0 || mes.compareTo("SEPTIEMBRE") == 0 || mes.compareTo("NOVIEMBRE") == 0) && 
					   Integer.valueOf(edtDia.getText().toString()) > 30) {
				msjVal = "DIA INVALIDA";
		    	status = false;
		    	
			} else if ((mes.compareTo("ENERO") == 0 || mes.compareTo("MARZO") == 0 || mes.compareTo("MAYO") == 0 || mes.compareTo("JULIO") == 0 ||
					   mes.compareTo("AGOSTO") == 0 || mes.compareTo("OCTUBRE") == 0 || mes.compareTo("DICIEMBRE") == 0 ) && Integer.valueOf(edtDia.getText().toString()) > 31 ){
				msjVal = "DIA INVALIDA";
		    	status = false;
		    	
			} else if (mes.compareTo("FEBRERO") == 0 && Integer.valueOf(edtDia.getText().toString()) > 29) {
				msjVal = "DIA INVALIDA";
		    	status = false;
			}		
	   }		
		return status;
	}

	
	//METODO LIMPIAR
	public void limpia() {
		edtDia.setText("");
		edtContenido.setText("");
		spinnerE.setSelection(0);
	}
	
	
	//METODO PARA COMPROBAR ESTADO DE LA MENMORIA SD PARA ESCRIBIR.
	public boolean estadoSDCARD(){
		//DEVUELVE EL DIRECTORIO PRIMARIO DE ALMACENAMIENTI DE LA MEMORIA SD
		String estado = Environment.getExternalStorageState();			
		
		//VERIFICA QUE LA MEMORIA ESTA PRESENTE Y MONTADA  CON EL ACCESO PARA LECTURA Y ESCRITURA
		if(estado.compareTo(Environment.MEDIA_MOUNTED) == 0) {
			return true;
		}		
		Log.d("METODO SDCARD", estado);
		
		return false;
	}
	
	//METODO PARA CREAR EL ARCHIVO EN MEMORIA EXTERNA
	public String creaArchivo(String archivo, String nombre) {
		Log.d("ESTADO DE MEMORIA ", "SD " + status);	
		String valor = null;
		
			Log.d("ENTRA A ESCRIBIR EL LA SD", "");
			try
			  {
				//OBTIENE EL DIRECTORIO PUBLICO(RAIZ) DEL TELEFONO
			    File ruta_sd = Environment.getExternalStorageDirectory();
			    //INSTANCIA UN OBJ PARA CREAR UN DIRECTORIO EN LA MEMORIA SD
			    File f = new File(ruta_sd.getAbsolutePath() + "/SQLite");	    
			    //SE CREA EL NUEVO DIRECTORIO
			    f.mkdirs();
			    
			    //SE CREA EL ARCHIVO EN EL DIRECTORIO CREADO 
			    f =new File(f,nombre);
			  
			    // CLASE QUE PERMITE GRABAR TEXTO EN UN ARCHIVO.
			    OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
			    fout.write(archivo);			    
			    fout.close();
			    valor = "ARCHIVO CREADO";
			  }
			  catch (Exception ex) {
			    Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
			    valor = "Error al escribir fichero a tarjeta SD";
			  }		
		
		return valor;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.equery, menu);
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
