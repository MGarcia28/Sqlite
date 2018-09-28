package com.mgl.beans;

import java.io.Serializable;
import java.util.List;

public class EfemerideBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	String idDia;
	String nomMes;
	String numMes;
	String numDia;
	String contenido;
	List<EfemerideBean> lstEfe;
	
	public String getIdDia() {
		return idDia;
	}
	public void setIdDia(String idDia) {
		this.idDia = idDia;
	}
	public String getNomMes() {
		return nomMes;
	}
	public void setNomMes(String nomMes) {
		this.nomMes = nomMes;
	}
	public String getNumMes() {
		return numMes;
	}
	public void setNumMes(String numMes) {
		this.numMes = numMes;
	}
	public String getNumDia() {
		return numDia;
	}
	public void setNumDia(String numDia) {
		this.numDia = numDia;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	
	
//	
//    public EfemerideBean() {
//
//    }
//	
//	/**
//	 * Constructor para crear el objeto a partir de un parcelable
//	 * @param in
//	 */
//	public EfemerideBean(Parcel in) {
//		lstEfe = new ArrayList<EfemerideBean>();
//		readFromParcel(in);
//	}
//
//	public int describeContents() {
//		return 0;
//	}
//
//	/**
//	 * Escribir a un parcel, OJO el orden es importante, es como escribir en un archivo binario
//	 * @param dest Parcel donde se va a escribir
//	 * @param flags ver documentacion de Parcelable.writeToParcel
//	 */
//	
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeString(idDia);
//		dest.writeString(nomMes);
//		dest.writeString(numMes);
//		dest.writeString(numDia);
//		dest.writeString(contenido);
//
//	//	dest.writeTypedList(lstEfe);
//	}
//	
//
//	/**
//	 * Clase para recuperar los datos de un parcel, IMPORTANTE leerlos en el mismo orden que se escribieron!
//	 * @param in Parcel con los datos a leer
//	 */
//	private void readFromParcel(Parcel in) {
//		idDia = in.readString();
//		nomMes = in.readString();
//		numMes = in.readString();
//		numDia = in.readString();
//		contenido = in.readString();
//		
//		in.readTypedList(lstEfe, CREATOR);
//	}
//
//	/**
//	 * Necesario para usarlo en arrays
//	 */
//	public static final Parcelable.Creator<EfemerideBean> CREATOR = new Parcelable.Creator<EfemerideBean>() {
//		public EfemerideBean createFromParcel(Parcel in) {
//			return new EfemerideBean(in);
//		}
//
//		public EfemerideBean[] newArray(int size) {
//			return new EfemerideBean[size];
//		}
//	};


}
