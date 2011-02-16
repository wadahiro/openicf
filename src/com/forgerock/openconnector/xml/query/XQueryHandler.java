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
import org.identityconnectors.common.logging.Log;
import org.w3c.dom.Document;

public class XQueryHandler {

    private XQDataSource datasource;
    private XQConnection connection;
    private XQExpression expression;
    private String query;
    private Document document;

    private static final Log log = Log.getLog(XQueryHandler.class);

    public XQueryHandler(String query, Document document) {
        this.query = query;
        this.document = document;
        initialize();
    }

    private void initialize() {
        final String method = "initialize";
        log.info("Entry {0}", method);
        try {
            datasource = new SaxonXQDataSource();
            connection = datasource.getConnection();
            expression = connection.createExpression();
            expression.bindNode(XQConstants.CONTEXT_ITEM, document, null);
        } catch (XQException ex) {
            log.warn("XQEexception while initializing: {0}", ex);
        }
        log.info("Exit {0}", method);
    }

    public XQResultSequence getResultSequence() {
        final String method = "getResultSequence";
        log.info("Entry {0}", method);
        XQResultSequence sequence = null;
        try {
            sequence = expression.executeQuery(query);
        } catch (XQException ex) {
            Logger.getLogger(XQueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info("Exit {0}", method);
        return sequence;
    }

    public void close() {
        final String method = "close";
        log.info("Entry {0}", method);
        try {
            connection.close();
            expression.close();
        } catch (XQException ex) {
            log.warn("Exception while closing: {0}", ex);
        }
        log.info("Exit {0}", method);
    }
}
