package com.example.activityEfemeride;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sqlite.R;
import com.mgl.beans.EfemerideBean;

public class EConsulActivity extends ActionBarActivity {
	
	ListView lsDetalle;
	ArrayList<EfemerideBean> lstEfe = new ArrayList<EfemerideBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_econsul);
		
		lsDetalle   = (ListView)findViewById(R.id.lsDetalle);	
        
		new PostAsync().execute();
	}
	
	//CLASE POSTASYNC 
	class PostAsync extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		
		@Override
  		protected void onPreExecute() 
  		{
  			pd = ProgressDialog.show(EConsulActivity.this, "Cargando Calendario", "Espere un momento...", true, false);
  		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Void... params) {
	     	
			Bundle bundleObject = getIntent().getExtras();          
			lstEfe = (ArrayList<EfemerideBean>) bundleObject.getSerializable("lstEfe");
	
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {	
			
			pd.dismiss();
			
			AdaptadorDetalle adapter = new AdaptadorDetalle(EConsulActivity.this);								
			lsDetalle.setAdapter(adapter);			
		}			
	}	
	
	//ADAPTADOR PARA LLENARA LA LISTA DE EL EFEMERIDES
	class AdaptadorDetalle extends ArrayAdapter<EfemerideBean> {
	    Activity context;    
    		    
	    AdaptadorDetalle(Activity context) {	    	
	        super(context, R.layout.efemerides_detalles,lstEfe);
	        this.context = context;
	    }	
	    
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	Log.d("TAMAÑO CURSOR ", "   TAMAÑO " + lstEfe.size());
	    	
	        LayoutInflater inflater = context.getLayoutInflater();	        
	        View item = inflater.inflate(R.layout.efemerides_detalles,null);	     
	        
	        TextView numMes = (TextView)item.findViewById(R.id.numMes);
	        numMes.setText(lstEfe.get(position).getNumMes().toString()); 
	    	Log.d("NUMMES", "NUMMES: " + lstEfe.get(position).getNumMes().toString());
	        	 
	        TextView nomMes = (TextView)item.findViewById(R.id.nomMes);
	        nomMes.setText(lstEfe.get(position).getNomMes().toString());
	        Log.d("NUMMES", "MES: " + lstEfe.get(position).getNomMes().toString());
	        
	        TextView dia = (TextView)item.findViewById(R.id.dia);
	        dia.setText(lstEfe.get(position).getNumDia().toString()); 
	        Log.d("NUMMES", "DIA: " + lstEfe.get(position).getNumDia().toString());

			WebView wvContenido = (WebView)item.findViewById(R.id.wvInfo);
			wvContenido.loadData(lstEfe.get(position).getContenido().toString(), "text/html; charset=utf-8", "utf-8");
	       
	      return(item);
	    }
	}
}
