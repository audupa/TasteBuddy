package com.example;

import java.io.Serializable;

public class FoodItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int hashCode() {
		return 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		FoodItem fo=(FoodItem)obj;
		return id.equals(fo.getId());
	}
	
	@Override
	public String toString() {
		return "[name="+name+", id="+id+"]";
	}

}
