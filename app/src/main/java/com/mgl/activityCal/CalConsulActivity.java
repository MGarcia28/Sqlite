package com.mgl.activityCal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sqlite.R;
import com.mgl.beans.CalendarioBean;
import com.mgl.service.CalBDHelper;

public class CalConsulActivity extends ActionBarActivity {
	
	ListView lsDetalle;
	
	CalBDHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_muestra);
		
		lsDetalle   = (ListView)findViewById(R.id.lsDetalle);
		
		new PostAsync().execute();
	}	

	class PostAsync extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		
		@Override
  		protected void onPreExecute() 
  		{
  			pd = ProgressDialog.show(CalConsulActivity.this, "Cargando Calendario", "Espere un momento...", true, false);
  		}

		@Override
		protected Void doInBackground(Void... params) {
			
			dbHelper = new CalBDHelper(CalConsulActivity.this, "CalendarioDB", null, 1); 
			db =dbHelper.getReadableDatabase();
			
	     	cursor = dbHelper.getCalendario(db); 
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {	
			
			pd.dismiss();
							      
			AdaptadorDetalle adapter = new AdaptadorDetalle(CalConsulActivity.this);								
			lsDetalle.setAdapter(adapter);
			
		}	
		
	}	
	
	//ADAPTADOR PARA LLENARA LA LISTA DE EL CALENDARIO
	class AdaptadorDetalle extends ArrayAdapter<CalendarioBean> {
	    Activity context;    
    		    
	    AdaptadorDetalle(Activity context) {	    	
	        super(context, R.layout.calendario_detalles);
	        this.context = context;
	        cursor.moveToFirst();
	    }	
	    
	    public int  getCount () {
	    	return cursor.getCount();
	    }
	    	
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	Log.d("TAMAÑO CURSOR ", "CONSULTA GRAL " + cursor.getCount());
	    	Log.d("LLENA LA LISTA", "INFLA LA LISTA: " + cursor.getPosition());
	    	    	
	        LayoutInflater inflater = context.getLayoutInflater();	        
	        View item = inflater.inflate(R.layout.calendario_detalles,null);	        
	 
	        TextView numMes = (TextView)item.findViewById(R.id.numMes);
	        numMes.setText(cursor.getString(1).toString());
	    	Log.d("NUMMES", "NUMMES: " + cursor.getString(1).toString());
	        	 
	        TextView nomMes = (TextView)item.findViewById(R.id.nomMes);
	        nomMes.setText(cursor.getString(2).toString());
	        Log.d("NUMMES", "NUMMES: " + cursor.getString(2).toString());
	        
	        TextView dia = (TextView)item.findViewById(R.id.dia);
	        dia.setText(cursor.getString(3).toString());  
	        Log.d("NUMMES", "NUMMES: " + cursor.getString(3).toString());
	        
	        TextView fondo = (TextView)item.findViewById(R.id.fondo);
	        fondo.setText(cursor.getString(4).toString());
	 
	        TextView color = (TextView)item.findViewById(R.id.color);
	        color.setText(cursor.getString(5).toString());
	        	        
	        cursor.moveToNext();      
	        return(item);
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.muestra, menu);
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
