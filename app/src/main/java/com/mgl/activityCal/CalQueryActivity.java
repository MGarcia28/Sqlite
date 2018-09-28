package com.mgl.activityCal;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlite.R;
import com.mgl.service.CalBDHelper;

public class CalQueryActivity extends ActionBarActivity {
	
	TextView edtNumMes,edtNomMes,edtDia,edtFondo,edtColor;
	Button btnConsulta,btnElimina,btnActualiza,btnInserta,btnGetCal,btnLimpiar;
	
	CalBDHelper dbHelper = new CalBDHelper(CalQueryActivity.this, "CalendarioDB", null, 1);

	SQLiteDatabase db; 
	Cursor cursor = null;
	Long statusInsert = null;
	int statusMod = 0;
	
	String opr, msj = null, msjVal;
	boolean status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_cal_query);
		
		edtNumMes = (TextView)findViewById(R.id.edtNumMes);
		edtNomMes = (TextView)findViewById(R.id.edtNomMes);
		edtDia = (TextView)findViewById(R.id.edtDia);
		edtFondo = (TextView)findViewById(R.id.edtFondo);
		edtColor = (TextView)findViewById(R.id.edtColor);
		
        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {
            	
                switch (v.getId()) {
 
                	case R.id.btnInserta:
        				opr = "inserta";
        				status = valida();
        				
        				if (status == true) {
        					new PostAsync().execute();	
        				} else {
        					Toast.makeText(getBaseContext(), msjVal , Toast.LENGTH_LONG).show();
        				}
                	break;
	                case R.id.btnConsulta:
	    				opr = "consulta";
	    				status = valida();
	    				
	    				if (status == true) {
	    					new PostAsync().execute();	
	    				}                	
	                break;
	                case R.id.btnElimina:
	                	opr = "elimina";
	    				status = valida();
	    				
	    				if (status == true) {
	    					new PostAsync().execute();	
	    				}
	    			break;	
	                case R.id.btnActualiza:
	        			opr = "actualiza";
	    				status = valida();

	    				if (status == true) {
	    					new PostAsync().execute();	
	    				} else {
	    					Toast.makeText(getBaseContext(), msjVal , Toast.LENGTH_LONG).show();
	    				}
	                break;
	                case R.id.btnGetCal:
	                	opr = "getCalendario";
	                	new PostAsync().execute();	
	                break;
		            case R.id.btnLimpiar:
		                	limpiar();
		            break;	               
                }
            }
        };
    	
		findViewById(R.id.btnLimpiar).setOnClickListener(handler);
        findViewById(R.id.btnConsulta).setOnClickListener(handler);
        findViewById(R.id.btnInserta).setOnClickListener(handler);
		findViewById(R.id.btnElimina).setOnClickListener(handler);
		findViewById(R.id.btnActualiza).setOnClickListener(handler);
		findViewById(R.id.btnGetCal).setOnClickListener(handler);
        
	}
		
	
	class PostAsync extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;		
		
		@Override
  		protected void onPreExecute() 
  		{
  			pd = ProgressDialog.show(CalQueryActivity.this, "Realizando Operación", "Espere un momento...", true, false);
  		}

		@Override
		protected Void doInBackground(Void... params) {				
			
			//SI SE PRECIONO EL BOTON INSERTAR
			if (opr.compareTo("inserta") == 0) {
				db = dbHelper.getWritableDatabase();
				
				statusInsert = dbHelper.guardarFecha(edtNomMes.getText().toString(),edtNumMes.getText().toString(),edtDia.getText().toString(),edtFondo.getText().toString(), edtColor.getText().toString(),db);	
		        db.close();	

			//SI SE PRECIONO EL BOTON CONSULTA
			} else if (opr.compareTo("consulta") == 0) {
				db = dbHelper.getWritableDatabase();
				
				cursor = dbHelper.consultaCal(edtNomMes.getText().toString(),edtDia.getText().toString(), db);
			
		    //SI SE PRECIONO EL BOTON ELIMINA
			}else if (opr.compareTo("elimina") == 0) {	
				db = dbHelper.getWritableDatabase();
				
				statusMod = dbHelper.deleteCal(edtNomMes.getText().toString(),edtDia.getText().toString(), db);				
				db.close();
			
		    //SI SE PRECIONO EL BOTON ACTUALIZA
			} else if (opr.compareTo("actualiza") == 0) {
				db = dbHelper.getWritableDatabase();
				
				statusMod = dbHelper.actualizaCal(edtNomMes.getText().toString(),edtNumMes.getText().toString(),edtDia.getText().toString(),edtFondo.getText().toString(), edtColor.getText().toString(), db);
				db.close();
				
			//SI SE PRECIONO EL BOTON CONSULTA GENERAL
			} else if (opr.compareTo("getCalendario") == 0) {
				db = dbHelper.getWritableDatabase();
				
				cursor = dbHelper.getCalendario(db);
				
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {	
			
			//SI SE PRECIONO EL BOTON INSERTAR
			if (opr.compareTo("inserta") == 0){
				if (statusInsert > 0) {
					msj ="FECHA INSERTADA";
				    limpiar();
				} else {
					msj ="ERROR AL INSERTAR FECHA";					
				}
				
			//SI SE PRECIONO EL BOTON CONSULTA
			} else if (opr.compareTo("consulta") == 0) {		
				
		        if (cursor.getCount() > 0) {
		            cursor.moveToFirst();
		            
		            edtNomMes.setText(cursor.getString(1).toString());
		            edtNumMes.setText(cursor.getString(2).toString());
		            edtDia.setText(cursor.getString(3).toString());
		            edtFondo.setText(cursor.getString(4).toString());
		            edtColor.setText(cursor.getString(5).toString());
		            
		            msj ="DATOS CARGADOS";     
		        } else {
		        	 msj ="LA FECHA NO EXISTE";
		        }
		        
		        cursor.close();
		        db.close();
		    
		    //SI SE PRECIONO EL BOTON ELIMINA
			} else if (opr.compareTo("elimina") == 0){
				
					msj ="REGISTROS ELIMINADOS " + statusMod;
					limpiar();
				
			//SI SE PRECIONO EL BOTON ACTUALIZA
			} else if (opr.compareTo("actualiza") == 0) {
				
					msj ="REGISTRO(S) ACTUALIZADO(S) " + statusMod;

		    //SI SE PRECIONO EL CALENDARIO
			} else if (opr.compareTo("getCalendario") == 0) {
				Log.d("CALENDARIO ", "CALENDARIO" + cursor.getCount());				
											
				if(cursor.getCount() > 0 ){
					cursor.close();
					db.close();
    				Intent i = new Intent(CalQueryActivity.this,CalConsulActivity.class);
    				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    				startActivity(i);
    				msj ="CALENDARIO";
    				
				} else {
					cursor.close();
					db.close();
					msj ="NO EXISTEN REGISTROS";
				}
		   }
						
			Toast.makeText(getBaseContext(), msj , Toast.LENGTH_LONG).show();
			pd.dismiss();	 		   
		}		
	}
	
	
	//VALIDA CAMPOS LLENOS
	public boolean valida() {		
		status = true;	
		String msj = null;
 
		if (opr.compareTo("consulta") == 0 || opr.compareTo("elimina") == 0) {
			
				if (edtNomMes.getText().toString().compareTo("") == 0 ||  edtDia.getText().toString().compareTo("") == 0) {
					Toast.makeText(getBaseContext(), "LLENAR CAMPO MES Y DIA" , Toast.LENGTH_LONG).show();
					status = false;
				}
				
		} else if (opr.compareTo("actualiza") == 0 || opr.compareTo("inserta") == 0){
				
				   if (edtNumMes.getText().toString().compareTo("") == 0 ||  edtNomMes.getText().toString().compareTo("") == 0 || edtDia.getText().toString().compareTo("") == 0 ||
					   edtFondo.getText().toString().compareTo("") == 0 ||  edtColor.getText().toString().compareTo("") == 0) {
					   msjVal = "LLENAR LOS CAMPOS";
						 status = false;
						 
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 1  && (Integer.valueOf(edtDia.getText().toString()) > 31 || edtNomMes.getText().toString().compareTo("ENERO") != 0)) {
					   msjVal = "FECHA INVALIDA";
				    	status = false; 
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 2  && (Integer.valueOf(edtDia.getText().toString()) > 29 || edtNomMes.getText().toString().compareTo("FEBRERO") != 0)) {
					   msjVal = "FECHA INVALIDA"; 	
					    status = false;	
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 3  && (Integer.valueOf(edtDia.getText().toString()) > 31 || edtNomMes.getText().toString().compareTo("MARZO") != 0)) {
					   msjVal = "FECHA INVALIDA";
					    status = false;
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 4  && (Integer.valueOf(edtDia.getText().toString()) > 30 || edtNomMes.getText().toString().compareTo("ABRIL") != 0)) {
					   msjVal = "FECHA INVALIDA";
					    status = false;
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 5  && (Integer.valueOf(edtDia.getText().toString()) > 31 || edtNomMes.getText().toString().compareTo("MAYO") != 0)) {
					   msjVal = "FECHA INVALIDA";	
					    status = false;
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 6  && (Integer.valueOf(edtDia.getText().toString()) > 30 || edtNomMes.getText().toString().compareTo("JUNIO") != 0)) {
					   msjVal = "FECHA INVALIDA";	
					    status = false;
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 7  && (Integer.valueOf(edtDia.getText().toString()) > 31 || edtNomMes.getText().toString().compareTo("JULIO") != 0)) {
					   msjVal = "FECHA INVALIDA";	
					    status = false;
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 8  && (Integer.valueOf(edtDia.getText().toString()) > 31 || edtNomMes.getText().toString().compareTo("AGOSTO") != 0)) {
					   msjVal = "FECHA INVALIDA";	
					    status = false;
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 9  && (Integer.valueOf(edtDia.getText().toString()) > 30 || edtNomMes.getText().toString().compareTo("SEPTIEMBRE") != 0)) {
					   msjVal = "FECHA INVALIDA";	
					    status = false;
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 10  && (Integer.valueOf(edtDia.getText().toString()) > 31 || edtNomMes.getText().toString().compareTo("OCTUBRE") != 0)) {
					   msjVal = "FECHA INVALIDA";	
					    status = false;
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 11  && (Integer.valueOf(edtDia.getText().toString()) > 30 || edtNomMes.getText().toString().compareTo("NOVIEMBRE") != 0)) {
					   msjVal = "FECHA INVALIDA";	
					    status = false;
				   }else if(Integer.valueOf(edtNumMes.getText().toString()) == 12  && (Integer.valueOf(edtDia.getText().toString()) > 31 || edtNomMes.getText().toString().compareTo("DICIEMBRE") != 0)) {
					   msjVal = "FECHA INVALIDA";	
					    status = false;		    	
			       } else if ( Integer.valueOf(edtDia.getText().toString()) >= 32 ||  Integer.valueOf(edtNumMes.getText().toString()) >= 13 || Integer.valueOf(edtDia.getText().toString()) <= 0 ||  Integer.valueOf(edtNumMes.getText().toString()) <= 0 ) {
			    	   msjVal = "FECHA INVALIDA";	
					    status = false;
				   }	
			}
		
		
		return status;
	}   
	
	//METODO PARA LIMPIAR CAMPOS
	public void limpiar() {
		edtNumMes.setText("");
		edtNomMes.setText("");
		edtDia.setText("");
		edtFondo.setText("");
		edtColor.setText("");
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cal_elimina, menu);
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
