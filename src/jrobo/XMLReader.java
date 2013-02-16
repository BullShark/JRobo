/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Christopher Lemire>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jrobo;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLReader {

 public static void main(String argv[]) {

  try {
  File file = new File("MyXMLFile.xml");
  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  DocumentBuilder db = dbf.newDocumentBuilder();
  Document doc = db.parse(file);
  doc.getDocumentElement().normalize();
  System.out.println("Root element " + doc.getDocumentElement().getNodeName());
  NodeList nodeLst = doc.getElementsByTagName("employee");
  System.out.println("Information of all employees");

  for (int s = 0; s < nodeLst.getLength(); s++) {

    Node fstNode = nodeLst.item(s);
    
    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
  
      Element fstElmnt = (Element) fstNode;
      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("firstname");
      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
      NodeList fstNm = fstNmElmnt.getChildNodes();
      System.out.println("First Name : "  + ((Node) fstNm.item(0)).getNodeValue());
      NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("lastname");
      Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
      NodeList lstNm = lstNmElmnt.getChildNodes();
      System.out.println("Last Name : " + ((Node) lstNm.item(0)).getNodeValue());
    }

  }
  } catch (Exception e) {
    e.printStackTrace();
  }
 }
}