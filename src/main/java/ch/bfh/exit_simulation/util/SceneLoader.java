package ch.bfh.exit_simulation.util;

import ch.bfh.exit_simulation.GamePanel;
import ch.bfh.exit_simulation.model.Exit;
import ch.bfh.exit_simulation.model.IObstacle;
import ch.bfh.exit_simulation.model.ObstaclePoly;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Shylux on 03.01.2016.
 */
public class SceneLoader {
    InputStream xsd;

    Dimension windowDimension;
    ArrayList<IObstacle> obstacleList = new ArrayList<>();
    Exit exit = null;

    private static SceneLoader _instance;
    public static SceneLoader getInstance() {
        if (_instance == null)
            _instance = new SceneLoader();
        return _instance;
    }
    public SceneLoader() {
        xsd = GamePanel.class.getResourceAsStream("scene.xsd");
        load();
    }

    private InputStream getSceneXml() {
        try {
            String sceneFile = GamePanel.getProps().getProperty("scene");
            InputStream xml = null;
            if (sceneFile.startsWith("resource:")) {
                sceneFile = sceneFile.substring("resource:".length());
                xml = GamePanel.class.getResourceAsStream(sceneFile);
            } else {
                xml = new FileInputStream(sceneFile);
            }

            return xml;
        } catch (IOException e) {
            throw new Error(e.getMessage());
        }
    }

    private void load() {
        try {
            String xml = convertStreamToString(getSceneXml());

            validateXml(xml);

            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new StringReader(xml)));
            Document doc = parser.getDocument();

            Node scene = doc.getDocumentElement();

            int windowWidth = Integer.parseInt(GamePanel.getProps().getProperty("windowWidth"));
            int windowHeight = Integer.parseInt(GamePanel.getProps().getProperty("windowHeight"));

            double sceneWidthInM = Double.parseDouble(getNodeAttr("width", scene));
            double sceneHeightInM = Double.parseDouble(getNodeAttr("height", scene));

            if (windowWidth/sceneWidthInM < windowHeight/sceneHeightInM) {
                Converter.getInstance().setScaleFactor((int) (windowWidth / sceneWidthInM));
            }else{
                Converter.getInstance().setScaleFactor((int) (windowHeight / sceneHeightInM));
            }

            double sceneWidthInPx = sceneWidthInM * Converter.getInstance().getScaleFactor();
            double sceneHeightInPx = sceneHeightInM * Converter.getInstance().getScaleFactor();

            int sceneWidth = (int) sceneWidthInPx;
            int sceneHeight = (int) sceneHeightInPx;
            windowDimension = new Dimension(sceneWidth, sceneHeight);

            NodeList polygons = scene.getChildNodes();
            for (int i = 0; i < polygons.getLength(); i++) {
                Node polygon = polygons.item(i);
                if (polygon.getNodeType() != Node.ELEMENT_NODE) continue;

                ObstaclePoly polyObj = new ObstaclePoly();
                if (obstacleList.size() == 0 && exit == null) {
                    polyObj = new Exit();
                }
                NodeList points = polygon.getChildNodes();
                for (int j = 0; j < points.getLength(); j++) {
                    Node point = points.item(j);
                    if (point.getNodeType() != Node.ELEMENT_NODE) continue;
                    String x = getNodeAttr("x", point);
                    String y = getNodeAttr("y", point);
                    if (point.getLocalName() == "relpoint") { // check if the point is relative or absolute
                        polyObj.addPoint(new Double(Double.parseDouble(x)*sceneWidth).intValue(),
                                new Double(Double.parseDouble(y)*sceneHeight).intValue());
                    } else {
                        int xInPx = Converter.getInstance().getPixelFromMeter(Double.parseDouble(x));
                        int yInPx = Converter.getInstance().getPixelFromMeter(Double.parseDouble(y));
                        polyObj.addPoint(xInPx, yInPx);
                    }
                }

                if (obstacleList.size() == 0 && exit == null) { // first element is the exit
                    exit = (Exit) polyObj;
                } else {
                    obstacleList.add(polyObj);
                }
            }
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    private void validateXml(String xml) throws IOException {
        InputStream xsd = GamePanel.class.getResourceAsStream("scene.xsd");
        SchemaFactory factory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (SAXException e) {
            throw new Error("Invalid scene xml: " + e.getMessage());
        }
    }

    // from: http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    // from: http://www.drdobbs.com/jvm/easy-dom-parsing-in-java/231002580
    private String getNodeAttr(String attrName, Node node ) {
        NamedNodeMap attrs = node.getAttributes();
        for (int y = 0; y < attrs.getLength(); y++ ) {
            Node attr = attrs.item(y);
            if (attr.getNodeName().equalsIgnoreCase(attrName)) {
                return attr.getNodeValue();
            }
        }
        return "";
    }

    public Dimension getWindowDimension() {
        return windowDimension;
    }
    public ArrayList<IObstacle> getObstacles() {
        return obstacleList;
    }
    public Exit getExit() { return exit; }
}
