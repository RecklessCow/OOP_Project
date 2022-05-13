package src.encyclopedia.database.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import src.encyclopedia.Article;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.*;

import javax.xml.xpath.*;


public class XML implements ControllerInterface {
    
    private String path;
    private Document doc;
    private File xmlFile;
    
    public XML(String _path){
        this.path = _path;
        xmlFile = new File(this.path);
        try {
            docBuilder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    /** 
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    /** 
     * @return String
     */
    public String getPath() {
        return path;
    }

    /**
     * method to initialize the document, and document builders
     * @NOT_FOR_USE
     * @throws Exception
     */
    public void docBuilder() throws Exception{
        DocumentBuilderFactory docBuilderfactory = DocumentBuilderFactory.newInstance();

        //parsing the encyclopedia.XML file here
        DocumentBuilder docBuilder = docBuilderfactory.newDocumentBuilder();
        try{
            doc = docBuilder.parse(xmlFile);
        }catch(FileNotFoundException f){
            System.out.println("file not found, enter new path");
        }
        
        //normalizing to get proper formatting
        doc.getDocumentElement().normalize();
    }

    /**
     * @NOT_FOR_USE
     * was made to create more xml nodes if the number of objects is bigger than the quantitiy of encyclopedia.XML article's
     */
    public void createElements(){
        //String _id, String _nameOfArticle, String _author, String _publishDate, String _text
        Element rootArticleSet = doc.getDocumentElement();

        Element article = doc.createElement("article");

        rootArticleSet.appendChild(article);

        Element id = doc.createElement("id");
        Element title = doc.createElement("title");
        Element author = doc.createElement("author");
        Element publishDate = doc.createElement("publishDate");
        Element content = doc.createElement("content");

        article.appendChild(id);
        article.appendChild(title);
        article.appendChild(author);
        article.appendChild(publishDate);
        article.appendChild(content);
    }

    public void deleteElements(String id) {
		try {
			NodeList articleList = doc.getElementsByTagName("article");
			for (int i = 0; i < articleList.getLength(); i++) {
				Element articleELEM = (Element) articleList.item(i);
				Element idTag = (Element) articleELEM.getElementsByTagName("id").item(0);
				if (idTag.getTextContent().equalsIgnoreCase(id)) {
					idTag.getParentNode().getParentNode().removeChild(articleList.item(i));
					break;
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
    /**
     * reads and return 2d array from given encyclopedia.XML
     * probably consumes a lot of memory
     * but i cant come up with a better method
     * maybe if i knew oop better i would of done that
     * @return String 2d array, with rows as articles, and columns as article elements
     */
    public String[][] readArticle(){

        if(this.doc == null){
            System.out.println("Wrong path to the file");
            System.exit(0);
        }else{
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        }

        NodeList list = doc.getElementsByTagName("article");

        String[][] args = new String[list.getLength()][4];

        for (int i = 0; i < list.getLength(); i++) {

            Node node = list.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE){

                Element e = (Element) node;

                args[i][0] = (e.getElementsByTagName("title").item(0).getTextContent());
                args[i][1] = (e.getElementsByTagName("author").item(0).getTextContent());
                args[i][2] = (e.getElementsByTagName("publishDate").item(0).getTextContent());
                args[i][3] = (e.getElementsByTagName("content").item(0).getTextContent());
            }
        }
        return args;
    }   


    private void removeEmptyWhitespaces(){
        //deleteing the whitespaces after each itration of overriding
        try{
            XPath xp = XPathFactory.newInstance().newXPath();
            NodeList nl = (NodeList) xp.evaluate("//text()[normalize-space(.)='']", doc, XPathConstants.NODESET);

            for (int i=0; i < nl.getLength(); ++i) {
                Node node = nl.item(i);
                node.getParentNode().removeChild(node);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    /**
     * Given the modified arraylist, saves the changes into the file
     * @param articles arraylist containing articles
     */
    public void writeArticle(ArrayList<Article> articles){
        System.out.println("Appending document with modified content");

        NodeList list = doc.getElementsByTagName("article");

        if(list.getLength() < articles.size()){
            for (int i = list.getLength(); i < articles.size(); i++) {
                createElements();
            }
        }

        for (int i = 0; i < list.getLength(); i++) {

            Node node = list.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE){

                Element e = (Element) node;

                e.getElementsByTagName("id").item(0).setTextContent(String.valueOf(i));
                e.getElementsByTagName("title").item(0).setTextContent(articles.get(i).getTitle());
                e.getElementsByTagName("author").item(0).setTextContent(articles.get(i).getAuthor());
                e.getElementsByTagName("publishDate").item(0).setTextContent(articles.get(i).getPublishDate());
                e.getElementsByTagName("content").item(0).setTextContent(articles.get(i).getContent());
            }
        }

        try{

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            removeEmptyWhitespaces();

            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8"); 
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSc = new DOMSource(doc);
            FileOutputStream fOut = new FileOutputStream(this.xmlFile);
            transformer.transform(domSc, new StreamResult(fOut));

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
