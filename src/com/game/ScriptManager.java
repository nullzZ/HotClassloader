package com.game;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author nullzZ
 *
 */
public class ScriptManager {

	private static final Logger logger = Logger.getLogger(ScriptManager.class);
	private ConcurrentHashMap<Integer, IScript> scripts = new ConcurrentHashMap<>();

	private static ScriptManager instance = new ScriptManager();

	private ScriptManager() {
	}

	public static ScriptManager getInstance() {
		return instance;
	}

	public void putScript(IScript script) {
		this.scripts.put(script.getId(), script);
	}

	@SuppressWarnings("unchecked")
	public <T> T getScript(int sid) {
		return (T) this.scripts.get(sid);
	}

	/**
	 * 加载所有脚本
	 * 
	 * @param scriptXMLPath
	 *            script.xml文件位置
	 * @throws Exception
	 */
	public void load(String scriptXMLPath) throws Exception {
		ecute(scriptXMLPath);
	}

	private void ecute(String scriptXMLPath) throws Exception {
		File xmlFile = new File(scriptXMLPath);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document doc = builder.parse(xmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("script");
		for (int i = 0; i < nList.getLength(); i++) {

			Node node = nList.item(i);

			// System.out.println("Node name: " + node.getNodeName());
			Element ele = (Element) node;

			if (node.getNodeType() == Element.ELEMENT_NODE) {
				String className = ele.getElementsByTagName("class").item(0).getTextContent();
				int sId = Integer.parseInt(ele.getElementsByTagName("id").item(0).getTextContent());
				Object obj = createScriptObj(className);
				if (this.scripts.containsKey(sId)) {
					logger.error("脚本id冲突" + sId + "|" + className + "|" + this.scripts.get(sId).toString());
					throw new Exception("脚本id冲突");
				}
				this.scripts.put(sId, (IScript) obj);
				// System.out.println("scriptId: " +
				// ele.getElementsByTagName("id").item(0).getTextContent());
				// System.out.println("class :" +
				// ele.getElementsByTagName("class").item(0).getTextContent());
			}
		}
	}

	private Object createScriptObj(String className) throws Exception {
		Class<?> c = Class.forName(className);
		return c.newInstance();
	}

}
