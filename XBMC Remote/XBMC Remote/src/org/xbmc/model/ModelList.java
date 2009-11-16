package org.xbmc.model;

import java.util.ArrayList;
import java.util.Observable;

/** An observable list to hold the data asynchronous filled by the backend.
 * Register yourself as an observer to get notified on every element that is being added.
 * 
 * @author till.essers
 *
 * @param <T>
 */
public class ModelList<E> extends Observable {

	private ArrayList<E> list = new ArrayList<E>();
	
	public void addItem(E element) {
		list.add(element);
		notifyObservers(element);
	}
	
	public ArrayList<E> getList() {
		return list;
	}
}
