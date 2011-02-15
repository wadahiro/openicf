/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml.query;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;
import net.sf.saxon.xqj.SaxonXQDataSource;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;

/**
 *
 * @author slogum
 */
public class XQueryHandler {

    private XQDataSource datasource;
    private XQConnection connection;
    private XQExpression expression;
    private String query;
    private Document document;

    public XQueryHandler(String query, Document document) {
        this.query = query;
        this.document = document;
        initialize();
    }

    private void initialize() {
        try {
            datasource = new SaxonXQDataSource();
            connection = datasource.getConnection();
            expression = connection.createExpression();
            DOMOutputter domOutputter = new DOMOutputter();
            org.w3c.dom.Document w3cDoc = domOutputter.output(document);
            expression.bindNode(XQConstants.CONTEXT_ITEM, w3cDoc, null);
        } catch (JDOMException ex) {
            Logger.getLogger(XQueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XQException ex) {
            Logger.getLogger(XQueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public XQResultSequence getResultSequence() throws XQException {
        return expression.executeQuery(query);
    }

    public void close() {
        try {
            connection.close();
            expression.close();
        } catch (XQException ex) {
            Logger.getLogger(XQueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
