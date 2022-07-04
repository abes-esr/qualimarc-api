package fr.abes.qualimarc.core.configuration;

import oracle.xdb.XMLType;
import org.hibernate.dialect.Oracle12cDialect;

@BaseXMLConfiguration
public class OracleXmlDialect extends Oracle12cDialect {
    public OracleXmlDialect() {
        registerHibernateType(XMLType._SQL_TYPECODE, "XMLTYPE");
        registerColumnType(XMLType._SQL_TYPECODE, "XMLTYPE");
    }

    @Override
    public boolean useInputStreamToInsertBlob() {
        //This forces the use of CLOB binding when inserting
        return false;
    }
}