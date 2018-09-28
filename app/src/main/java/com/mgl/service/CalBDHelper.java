package com.mgl.service;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mgl.beans.CalendarioBean;
import com.mgl.beans.EfemerideBean;

//Cursor interfaz que permite la lectura y escritura que devuelta por una consulta a bd  
//CursorFactory permite devolver subclases de cursor cuando se llama a la consulta.

//SQLiteOpenHelper Una clase de ayuda para gestionar la creación de bases de datos y gestión de versiones.
//SQLiteDatabase: Expone métodos para administrar una bd sqlite crear, eliminar, ejecutar comandos SQL y realizar otras tareas de administración de base de datos común.

public class CalBDHelper extends SQLiteOpenHelper  {
	
	Cursor cursor;
	Long status;
	int statusMod = 0;
	 
    //CONSTRUCTOR. SE INICIALIZA CON LOS PARAMETROS ESPECIFICADOS.
	public CalBDHelper(Context context, String name, CursorFactory factory, int version) { 
		super(context, name, factory, version);
	}
	
	//METODO QUE SE EJECUTA CUANDO SE CREAN POR PRIMERA VEZ LAS TABLAS.
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		Log.d(this.getClass().toString(), "Creando base de datos");
						
		arg0.execSQL("CREATE TABLE Calendario (IdCalendario INTEGER PRIMARY KEY AUTOINCREMENT, NomMesCalendario TEXT, NumMesCalendario TEXT, NumDiaCalendario TEXT, FondoCalendario TEXT, EsColor TEXT)");
		arg0.execSQL("CREATE TABLE Efemerides(IdDia INTEGER PRIMARY KEY AUTOINCREMENT, NomMes TEXT, NumMes TEXT, NumDia TEXT, Contenido TEXT)");
				
		Log.d(this.getClass().toString(), "Tablas creadas");
		Log.d(this.getClass().toString(), "Base de datos creada");
	}

	//METODO QUE SE EJECUTA CUANDO ES MODIFICADA LA ESTRUCTURA DE LA BD. DE IDENTIFICA POR LA VERCION QUE VIENE COMO PARAMETRO
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	
		Log.d("Tabla creada", "onUpgrade");		
	}	
	
	
	
	//**************************************************************************************************	
	//****************************************** QUERY EFEMERIDES **************************************	
	//**************************************************************************************************
	
	//INSERTAR EFEMERIDES
	public Long guardaEfemerides(ArrayList<EfemerideBean> lsEfe, SQLiteDatabase db) {
		int tam = lsEfe.size();	
		
		for (int i = 0; i < tam; i++) {
			
			EfemerideBean item = lsEfe.get(i);
			
			if(db != null) {
				ContentValues efe = new ContentValues();
				
				efe.put("IdDia", item.getIdDia());
				efe.put("NomMes", item.getNomMes());
				efe.put("NumMes", item.getNumMes());
				efe.put("NumDia", item.getNumDia());
				efe.put("Contenido", item.getContenido());
				
				status = db.insert("Efemerides", null, efe);
			}	
		}		
	 return status;	
	}
	
	//INSERTAR EFEMERIDES POR FECHA
	public Long guardaEfemeride(String NomMes, String NumMes, String NumDia, String Contenido, SQLiteDatabase db) {
		
			if(db != null) {
				ContentValues efe = new ContentValues();
				
				//efe.put("IdDia", IdDia);
				efe.put("NomMes", NomMes);
				efe.put("NumMes", NumMes);
				efe.put("NumDia", NumDia);
				efe.put("Contenido", Contenido);
				
				status = db.insert("Efemerides", null, efe);
			}	
			
			Log.d("STATUS DE INSERCION", ""  + status);
		
	 return status;	
	}
	
	
	//CONSULTAR EFEMERIDES POR FECHA
	public Cursor buscaEfemeride(String nomMes, String numDia, SQLiteDatabase db){
		
		Log.d("doInBackground ", "nomMes " + nomMes);
		Log.d("doInBackground ", "numDia " + numDia);
		
		   if (db != null) {
			   cursor = db.query("Efemerides",
							      null,
							      "nomMes = ? AND numDia = ? ",
							      new String [] {nomMes, numDia}, 
							      null,
							      null,
							      null);			
		 }		
		return cursor;
	}		
	
	//ACTUALIZAR EFEMERIDES POR FECHA
	public int actualizaEfe(String NomMes, String NumMes, String NumDia, String Contenido, SQLiteDatabase db) {
		statusMod = 0;
		
		if (db != null) {
			ContentValues efe = new ContentValues();
			
				efe.put("NomMes", NomMes);
				efe.put("NumMes", NumMes);
				efe.put("NumDia", NumDia);
				efe.put("Contenido", Contenido);
				
				statusMod = db.update("Efemerides", efe, "nomMes=? AND numDia=?", new String[] {NomMes,NumDia});
		}
		
		return statusMod;
	}
	
	
	//ELIMINAR EFEMERIDES POR FECHA
	public int eliminar(String mes, String dia, SQLiteDatabase db) {		
		if (db != null) {		
			statusMod = db.delete("Efemerides", "nomMes = ? AND numDia = ? ", new String[] {mes, dia});			
		}	
		Log.d("ELIMINADOS ", "" + statusMod);
		
	return statusMod;
	}
	
	
	//CONSULTA DE EFEMERIDES
	public Cursor getEfemerides(SQLiteDatabase db) {
		
		Log.d("GET EFEMERIDES ", "");
		
		if (db != null) {
			cursor = db.query("Efemerides", null, null, null, null, null, null);
		}
		Log.d("EFEMERIDES EXISTENTES", "" + cursor.getCount());
		
		return cursor;		
	}
	
	
	
	//**************************************************************************************************	
	//****************************************** QUERY CALENDARIO **************************************	
	//**************************************************************************************************
	
	
	//METODO PARA INSERTAR TODOS LOS DATOS EN LA TABLA CALENDARIO
	public Long guardarCalendario(ArrayList<CalendarioBean> lsCal, SQLiteDatabase db){
				
		int size = lsCal.size();
		status = null;
		
        for (int i = 0; i < size; i++) {	            	
  	         CalendarioBean item = lsCal.get(i);
                        	
            if(db != null) {   //Si hemos abierto correctamente la base de datos 
  
        			ContentValues cl = new ContentValues();
        			cl.put("NomMesCalendario", item.getNomMesCalendario()); 
        			cl.put("NumMesCalendario", item.getNumMesCalendario());
        			cl.put("NumDiaCalendario", item.getNumDiaCalendario());
        			cl.put("FondoCalendario", item.getFondoCalendario());
        			cl.put("EsColor", item.getEsColor());			 
        			
        			status  = db.insert("Calendario", null, cl);             	            	
            }  
         } 
         
         Log.d("STATUS ", "INSERT " + status);

        return status;
	}	
	
	//METODO PARA INSERTAR FECHA
	public Long guardarFecha(String nomM, String numM, String numD, String fondo, String color, SQLiteDatabase db){		
		
		status = null;
		
        if(db != null) {   
			ContentValues cl = new ContentValues();
			//cl.put("IdCalendario", 1);
			cl.put("NomMesCalendario", nomM); 
			cl.put("NumMesCalendario", numM);
			cl.put("NumDiaCalendario", numD);
			cl.put("FondoCalendario", fondo);
			cl.put("EsColor", color);			 
			
			status  = db.insert("Calendario", //tabla
								null, //Columnas     
								cl);  //valores
			Log.d("STATUS ", "INSERT " + status);
        }
        return status;
	}	
	
	//CONSULTA GENERAL DE CALENDARIO
	public Cursor getCalendario(SQLiteDatabase db) {	
		
		if(db != null) {  		
			cursor = db.rawQuery("Select * from Calendario", null);
		}
		
        return cursor;		
	}	
	
	//CONSULTA POR FECHA
	public Cursor consultaCal(String mes, String dia, SQLiteDatabase db) {
				
		if(db != null) {  					
		    cursor = db.query("Calendario", // tabla
		             null, // Colunmas
		             "NomMesCalendario=? AND NumDiaCalendario=?", // condicion 
		             new String[] { mes,dia }, // parametros
		             null, // Group by
		             null, // Having
		             null, // Order by
		             null); // Limit			
		}
		
		return cursor;		
	}
	
	
	//ELIMINAR POR FECHA
	public int deleteCal(String mes, String dia, SQLiteDatabase db) {
		int statusMod = 0;
		
		if(db != null) {  
		  statusMod = db.delete("Calendario", "NomMesCalendario=? AND NumDiaCalendario=?", new String[] { mes,dia });
		}
		
		 Log.d("REGISTROS ", "ELIMINADOS " + statusMod);
	     
	   return statusMod;
    }	
	
	//ACTUALIZA
	public int actualizaCal(String nomM, String numM, String numD, String fondo, String color, SQLiteDatabase db) {		
		int statusMod = 0;
		
		if(db != null) {		
			ContentValues cl = new ContentValues();
			
			cl.put("NomMesCalendario", nomM); 
			cl.put("NumMesCalendario", numM);
			cl.put("NumDiaCalendario", numD);
			cl.put("FondoCalendario", fondo);
			cl.put("EsColor", color);
			 
			String[] args = new String[]{nomM, numD};
			statusMod = db.update("Calendario", cl, "NomMesCalendario=? AND NumDiaCalendario=?", args);
		}
		Log.d("REGISTROS  " , "ACTUALIZADOS: " + statusMod);
		
		return statusMod;
	}
	
}
