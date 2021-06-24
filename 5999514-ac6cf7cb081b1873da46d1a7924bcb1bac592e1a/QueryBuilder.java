package org.deri.granatum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class QueryBuilder {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		// TODO Auto-generated method stub
		String sparqlQuery = "PREFIX schema:<http://schema.org/>" +
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" +
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/>" +
				"PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>" +
				"PREFIX owl:<http://www.w3.org/2002/07/owl#>" +
				"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
				"PREFIX steam:<http://steam.org/mini-project/>" +
				"SELECT DISTINCT * WHERE {" +
				"?x a steam:procedure ." +
				"?x schema:name ?procName ." +
				"?disease steam:hasProcedure ?x ." +
				"?disease steam:carriedBy ?hospital ." +
				"?disease steam:totalDischarge ?total ." +
				"?disease steam:averageCoveredCharge ?covered ." +
				"?disease steam:averageTotalPayment ?totalPayment ." +
				"?hospital schema:name ?hosp_name ." +
				"?hospital schema:address ?hosp_address ." +
				"?hospital schema:subtype ?type ." +
				"?hospital schema:ownedThrough ?owner ." +
				"?hospital schema:availableService ?service ." +
				"?hosp_address schema:streetAddress ?streetAddress ." +
				"?hosp_address schema:addressLocality ?hosp_local ." +
				"?hosp_address schema:addressRegion ?hosp_region ." +
				"?hosp_address schema:postalCode ?postalCode ." +
				"?hospital schema:telephone ?hosp_phone ." +
				"?hosp_address schema:longitude ?hosp_long ." +
				"?hosp_address schema:latitude ?hosp_lat ." +
				"FILTER regex(xsd:string(?procName), \"cardiac arrhythmia\", \"is\") " +
						"}";
		// Tried to fetch SPARQL Results in JSON Format initially but was not possible with SESAME, so ended up with fetching the query results in 
		// SPARQL -XML Bindings and doing Naive XML Parsing to JSON - Uncomment the below line to actually execute query
		
		//queryBuilder(sparqlQuery, null);
		JSONArray jsonarray = readXMLFile("min.xml");
		JSONObject finalObj = new JSONObject () ;
		finalObj.put("items", jsonarray);
		writeFile(finalObj.toString(), "minOuput.json");
	}
	
	
	public static JSONArray readXMLFile(String file) throws IOException, ParserConfigurationException, SAXException {
	//	FileInputStream fstream = new FileInputStream(file);
    	File xmlFile = new File(file);
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		JSONArray bindings = new JSONArray();
		NodeList nodes = doc.getElementsByTagName("result");
		for(int i = 0; i < nodes.getLength() ; i++) {
			Element result = (Element) nodes.item(i);
			String latitude = "", longitude = "";
			NodeList resultNodes = result.getElementsByTagName("binding");
			JSONObject resultObj = new JSONObject();
			for (int j = 0; j < resultNodes.getLength() ; j ++) {
				String key = resultNodes.item(j).getAttributes().getNamedItem("name").getNodeValue();
				Element resultElement = (Element) resultNodes.item(j);
				if(resultElement.getElementsByTagName("literal").getLength() > 0) {
					String value = resultElement.getElementsByTagName("literal").item(0).getChildNodes().item(0).getNodeValue();
					if(key.matches("hosp_name"))
						key = "label";
					resultObj.put(key, value);
					if(key.matches("hosp_long"))
						longitude = value;
					if(key.matches("hosp_lat"))
						latitude = value;
				}
			}
			if(latitude!= null && longitude != null)
				resultObj.put("latLng", latitude + "," + longitude);
			bindings.add(resultObj);
			System.out.println(resultNodes.getLength());
			
		}
		return bindings; 
	}
	
	public static void queryBuilder(String primeClass, String dataSet) throws IOException{
		String endpoint = "http://localhost:7080/openrdf-workbench/repositories/mini-project/query?query=";
		String query = URLEncoder.encode(primeClass, "UTF-8");
		String jsonResponse = httpGet(endpoint+query); // Tried to force accept JSON, via Query URL and Connection Request Property - Not possible with SESAME
		writeFile(jsonResponse, "min.xml");
	}
	
	public static String writeFile (String jsonStruct, String fileName) throws IOException {
		File titleFile =new File(fileName);
		if(!titleFile.exists()){
		  titleFile.createNewFile();
		}
		FileWriter file = new FileWriter(fileName);
		file.write(jsonStruct);
		file.flush();
		file.close();
		return "Output printed";
	}
	
	public static String httpGet(String urlStr) throws IOException {
		  URL url = new URL(urlStr);
		  HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();
		  conn.addRequestProperty("Accept", "application/sparql-results+json"); // Should return JSON but returns XML
		  if (conn.getResponseCode() != 200) {
			  return null;
		  }
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line + '\n');
		  }
		  rd.close();

		  conn.disconnect();
		  return sb.toString();
	}

}