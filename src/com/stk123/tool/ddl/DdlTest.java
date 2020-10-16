package com.stk123.tool.ddl;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ddlutils.DatabaseOperationException;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.PlatformUtils;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.CreationParameters;
import org.apache.ddlutils.platform.oracle.Oracle9Platform;
import org.apache.ddlutils.task.PlatformConfiguration;
import org.apache.ddlutils.task.TableSpecificParameter;

import javax.sql.DataSource;
import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class DdlTest {

    private static Properties _testProps;

    /** The name of the property that specifies properties file with the settings for the connection to test against. */
    public static final String JDBC_PROPERTIES_PROPERTY = "./jdbc.properties";
    /** The prefix for properties of the datasource. */
    public static final String DATASOURCE_PROPERTY_PREFIX = "datasource.";
    /** The prefix for properties for ddlutils. */
    public static final String DDLUTILS_PROPERTY_PREFIX = "ddlutils.";
    /** The property for specifying the platform. */
    public static final String DDLUTILS_PLATFORM_PROPERTY = DDLUTILS_PROPERTY_PREFIX + "platform";
    /** The property specifying the catalog for the tests. */
    public static final String DDLUTILS_CATALOG_PROPERTY = DDLUTILS_PROPERTY_PREFIX + "catalog";
    /** The property specifying the schema for the tests. */
    public static final String DDLUTILS_SCHEMA_PROPERTY = DDLUTILS_PROPERTY_PREFIX + "schema";

    StringWriter _writer;
    Platform _platform;
    DataSource _dataSource;
    String _databaseName;

    public static void main(String[] args) throws IOException {
        DdlTest test = new DdlTest();
        test.init();
        Database model = test.readModelFromDatabase("TWP4T1");
        System.out.println(model);
        System.out.println(model.toVerboseString());

        Platform           platform        = test.getPlatform();
        boolean            isCaseSensitive = platform.isDelimitedIdentifierModeOn();
        CreationParameters params          = null;//getFilteredParameters(model, platform.getName(), isCaseSensitive);
        platform.getSqlBuilder().setWriter(new FileWriter(new File("./ddl.sql")));
        platform.getSqlBuilder().createTables(model);
        platform.getSqlBuilder().getWriter().flush();

        FileWriter outputWriter = new FileWriter(new File("./ddl.xml"));
        DatabaseIO dbIO         = new DatabaseIO();
        dbIO.write(model, outputWriter);
        outputWriter.close();
    }

    public void init() throws IOException {
        Properties props = getTestProperties();

        try
        {
            String dataSourceClass = props.getProperty(DATASOURCE_PROPERTY_PREFIX + "class", BasicDataSource.class.getName());

            _dataSource = (DataSource)Class.forName(dataSourceClass).newInstance();

            for (Iterator it = props.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry    = (Map.Entry)it.next();
                String    propName = (String)entry.getKey();

                if (propName.startsWith(DATASOURCE_PROPERTY_PREFIX) && !propName.equals(DATASOURCE_PROPERTY_PREFIX +"class"))
                {
                    BeanUtils.setProperty(_dataSource,
                            propName.substring(DATASOURCE_PROPERTY_PREFIX.length()),
                            entry.getValue());
                }
            }
        }
        catch (Exception ex)
        {
            throw new DatabaseOperationException(ex);
        }

        _databaseName = props.getProperty(DDLUTILS_PLATFORM_PROPERTY);
        if (_databaseName == null)
        {
            // property not set, then try to determine
            _databaseName = new PlatformUtils().determineDatabaseType(_dataSource);
            if (_databaseName == null)
            {
                throw new DatabaseOperationException("Could not determine platform from datasource, please specify it in the jdbc.properties via the ddlutils.platform property");
            }
        }

        _writer   = new StringWriter();
        _platform = PlatformFactory.createNewPlatformInstance(getDatabaseName());
        System.out.println("Platform:"+_platform.getClass().getName());
        _platform.getSqlBuilder().setWriter(new FileWriter(new File("./ddl.sql")));
        _platform.setDataSource(_dataSource);
    }


    protected Database readModelFromDatabase(String databaseName)
    {
        String     catalog = _testProps.getProperty(DDLUTILS_CATALOG_PROPERTY);
        String     schema  = _testProps.getProperty(DDLUTILS_SCHEMA_PROPERTY);

        _platform.getModelReader().setDefaultTablePattern("ADMLOCAL");
        return getPlatform().readModelFromDatabase(databaseName, catalog, schema,  new String[]{"TABLE"});
    }

    public Platform getPlatform(){
        return _platform;
    }

    protected String getDatabaseName()
    {
        return _databaseName;
    }

    protected Properties getTestProperties()
    {
        if (_testProps == null)
        {
            try
            {
                _testProps = new Properties();
                InputStream propStream = getClass().getResourceAsStream(JDBC_PROPERTIES_PROPERTY);
                _testProps.load(propStream);
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }
        return _testProps;
    }
}
