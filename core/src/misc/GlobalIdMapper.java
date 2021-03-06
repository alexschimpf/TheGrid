package misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import core.TheGame;

public final class GlobalIdMapper {

	private static final String ENTITY_FILE = "entity_mappings.txt";
	private static final String SCRIPT_FILE = "script_mappings.txt";
	private static final String SOURCE_ENTITY_DIR = "/Users/schimpf1/Desktop/libgdx/TheGrid/core/src/entity";
	private static final String SOURCE_SCRIPT_DIR = "/Users/schimpf1/Desktop/libgdx/TheGrid/core/src/script";
	private static final String OUTPUT_DIR = "/Users/schimpf1/Desktop/libgdx/TheGrid/android/assets/";	
	
	private static final HashMap<String, String> ENTITY_TYPE_CLASS_MAP = new HashMap<String, String>();
	private static final HashMap<String, String> SCRIPT_TYPE_CLASS_MAP = new HashMap<String, String>();
	
	public static HashMap<String, String> getEntityTypeClassMap() {
		return getMapFromFile(true);
	}
	
	public static HashMap<String, String> getScriptTypeClassMap() {
		return getMapFromFile(false);
	}
	
	public static void createGlobalIdMappings() {
		System.out.println("Creating global id mappings...");
		
		createEntityMappings();
		createScriptMappings();
		writeEntityMappings();
		writeScriptMappings();
	}
	
	private static void writeEntityMappings() {
		writeMappings(true);
	}
	
	private static void writeScriptMappings() {
		writeMappings(false);
	}
	
	private static void createEntityMappings() {
		createMappings(true);
	}
	
	private static void createScriptMappings() {
		createMappings(false);
	}
	
	private static void writeMappings(boolean entity) {
		try {
			String path = OUTPUT_DIR + (entity ? ENTITY_FILE : SCRIPT_FILE);
			HashMap<String, String> map = entity ? ENTITY_TYPE_CLASS_MAP : SCRIPT_TYPE_CLASS_MAP;
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			for(Entry<String, String> entry : map.entrySet()) {
				String type = entry.getKey();
				String className = entry.getValue();
				String prefix = entity ? "entity." : "script.";
				writer.println(type + "," + prefix + className);
			}
			
			if(entity) {
				writer.println("sensor,entity.sensor.SensorEntity");
			}		
			
			writer.close();
		} catch(Exception e) {
			Gdx.app.error("tendersaucer", "writeMappings", e);
		}
	}
	
	private static void createMappings(boolean entity) {
		String dirPath = entity ? SOURCE_ENTITY_DIR : SOURCE_SCRIPT_DIR;
		File dir = new File(dirPath);
		for(final File file : dir.listFiles()) {
	        if(file.isDirectory()) {
	        	continue;
	        }
	        
            String fileName = file.getName();
            if(!isFileValid(fileName)) {
            	System.out.println("SKIPPING " + fileName + "...");
            	continue;
            }

            String className = fileName.replace(".java", "");
            if(entity) {
            	String type = getEntityTypeFromClassName(className);
            	ENTITY_TYPE_CLASS_MAP.put(type, className);
            } else {
            	String type = getScriptTypeFromClassName(className);
            	SCRIPT_TYPE_CLASS_MAP.put(type, className);
            }
		}
	}
	
	private static String getEntityTypeFromClassName(String className) {
		return getTypeFromClassMap(true, className);
	}
	
	private static String getScriptTypeFromClassName(String className) {
		return getTypeFromClassMap(false, className);
	}
	
	private static String getTypeFromClassMap(boolean entity, String className) {
		String prefix = entity ? "entity." : "script."; 
		try {
			Class<?> c = Class.forName(prefix + className);
			return c.getDeclaredField("TYPE").get(null).toString();
		} catch(Exception e) {
			Gdx.app.error("tendersaucer", "getScriptFromClassName", e);
		}
		
		return null;
	}
	
	private static HashMap<String, String> getMapFromFile(boolean entity) {
		HashMap<String, String> map = new HashMap<String, String>();
		String filename = entity ? ENTITY_FILE : SCRIPT_FILE;
		
		try {
			if(TheGame.DEBUG) {
				BufferedReader reader = new BufferedReader(new FileReader(OUTPUT_DIR + filename));
				
				String line = reader.readLine();
				while (line != null) {
					String type = extractType(line);
					String className = extractClassName(line);
					map.put(type, className);
					
					line = reader.readLine();
				}
				
				reader.close();
			} else {
				FileHandle file = Gdx.files.internal(filename);
				String contents = file.readString();
				for(String line : contents.split("\n")) {
					String type = extractType(line);
					String className = extractClassName(line);
					map.put(type, className);
				}
			}
		} catch(Exception e) {
			Gdx.app.error("tendersaucer", "mapFromFile", e);
		}
		
		return map;
	}
	
	private static String extractType(String line) {
		String[] pieces = line.split(",");
		return pieces[0];
	}
	
	private static String extractClassName(String line) {
		String[] pieces = line.split(",");
		return pieces[1];
	}
	
	private static boolean isFileValid(String fileName) {
		return !((fileName.startsWith("I") && Character.isUpperCase(fileName.charAt(1))) || 
				fileName.startsWith(".") || fileName.equals("Entity.java") || fileName.equals("Script.java"));
	}
}
