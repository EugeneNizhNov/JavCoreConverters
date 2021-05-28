/**
 * В данном проекте решение трех задач:
 * CSV - JSON парсер;
 * XML - JSON парсер;
 * JSON парсер (задание со звездочкой *).
 **/

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";
        List<Employee> listEmployeeCSV = parsCSV(columnMapping, fileNameCSV);
        String jsonString = listToJson(listEmployeeCSV);
        String fileJSON = "dataJSON";
        writeString(jsonString, fileJSON);
        String fileNameXML = "data.xml";
        List<Employee> listEmployeeXML = parsXML(fileNameXML);
        String jsonStringXML = listToJson(listEmployeeXML);
        fileJSON = "dataXML";
        writeString(jsonStringXML, fileJSON);
        String jsonFileString = readString(fileJSON);
        List<Employee> listEmployeeJSON = jsonToList(jsonFileString);
        for (Employee employee : listEmployeeJSON) {
            System.out.println(employee);
        }
    }

    public static List<Employee> parsCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static List<Employee> parsXML(String fileNameXML) {
        List<Employee> listXML = new ArrayList<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File("data.xml"));
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("employee");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getElementsByTagName("id").item(0).getTextContent();
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    String age = element.getElementsByTagName("age").item(0).getTextContent();
                    listXML.add(new Employee(Integer.parseInt(id), firstName, lastName, country, Integer.parseInt(age)));
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            ex.printStackTrace();
        }
        return listXML;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String jsonString, String fileName) {
        try (FileWriter file = new
                FileWriter(fileName)) {
            file.write(jsonString);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readString(String jsonFile) {
        String jsonFileString = null;
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
            jsonFileString = br.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return jsonFileString;
    }

    public static List<Employee> jsonToList(String jsonFileString) {
        List<Employee> list = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonFileString);
        GsonBuilder builder = new GsonBuilder();
        for (int i = 0; i < jsonArray.size(); i++) {
            Gson gson = builder.create();
            Employee employee = gson.fromJson(jsonArray.get(i), Employee.class);
            list.add(employee);
        }
        return list;
    }
}
