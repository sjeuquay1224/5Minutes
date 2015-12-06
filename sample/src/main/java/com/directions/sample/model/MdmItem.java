package com.directions.sample.model;

import java.io.Serializable;

public class MdmItem implements Serializable{
	
	public int type;
	public int methodType;
	public String methodName;
	public String desc;
	
	public MdmItem(int type, int methodType, String methodName, String desc) {
		this.type = type;
		this.methodType = methodType;
		this.methodName = methodName;
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
        builder.append("(type = ").append(type);
        builder.append(", methodType = ").append(methodType);
        builder.append(", methodName = ").append(methodName);
        builder.append(", desc = ").append(desc).append(")");
        return builder.toString();
	}
}
