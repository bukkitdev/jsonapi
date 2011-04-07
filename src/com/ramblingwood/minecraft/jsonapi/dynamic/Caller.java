package com.ramblingwood.minecraft.jsonapi.dynamic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.json.simpleForBukkit.JSONArray;
import org.json.simpleForBukkit.JSONObject;
import org.json.simpleForBukkit.parser.JSONParser;
import org.json.simpleForBukkit.parser.ParseException;

import com.ramblingwood.minecraft.jsonapi.JSONAPI;

public class Caller {
	public HashMap<String, Method> methods = new HashMap<String, Method>();
	
	public Caller () {
	}
	
	public Object call(String method, Object[] params) throws Exception {
		Call c = methods.get(method).getCall();
		
		if(params.length < c.getNumberOfExpectedArgs()) {
			throw new Exception("Incorrect number of args: gave "+params.length+" ("+Arrays.asList(params).toString()+"), expected "+c.getNumberOfExpectedArgs());
		}
		
		return c.call(params);
	}
	
	public boolean methodExists (String name) {
		return methods.containsKey(name);
	}
	
	public void loadFile (File methodsFile) {
		JSONParser p = new JSONParser();
		JSONArray methods;
		try {
			methods = (JSONArray)p.parse(new FileReader(methodsFile));
		
			for(Object o : methods) {
				if(o instanceof JSONObject) {
					String name = ((JSONObject)o).get("name").toString();
					
					if(this.methods.containsKey(name)) {
						Logger.getLogger("Minecraft").warning("[JSONAPI] The method " + name + " already exists! It is being overridden.");
					}
					
					this.methods.put(name, new Method((JSONObject)o));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}